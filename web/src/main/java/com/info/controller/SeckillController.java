package com.info.controller;

import com.info.dto.GoodsDto;
import com.info.entity.ShoppingCarEntity;
import com.info.impl.GoodsServiceImpl;
import com.info.mapper.ShoppingCarEntityMapper;
import com.info.service.SeckillService;
import com.info.service.ShoppingService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import java.util.*;

@Controller
public class SeckillController {
    @Resource
    private GoodsServiceImpl goodsService;
    @Resource
    private AmqpTemplate amqpTemplate;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private SeckillService seckillService;
    @Resource
    private ShoppingService shoppingService;
    @RequestMapping("send")
    public ModelAndView send(ModelAndView mv) throws InterruptedException {
        String msg = "hello, Spring boot amqp";
        amqpTemplate.convertAndSend("spring.test.exchange", "a.b", msg);
        // 等待10秒后再结束
        Thread.sleep(10000);
        mv.setViewName("seckill");
        return mv;
    }

    /**
     * 开始秒杀
     */
    @RequestMapping(value = "startSeckill", method = RequestMethod.GET)
    @ResponseBody
    public int deductionInventory(
            @RequestParam(required = false, value = "goodsId") long goodsId,
            @RequestParam(required = false, value = "userId") long userId
    ) throws Exception {
        int goodsNumber = seckillService.deductionInventory(goodsId);
        shoppingService.saveSeckillRecord(userId,goodsId);
        if (goodsNumber <= 0) {
            return 0;
        }
        return 1;
    }

    /**
     * 根据条件查询商品
     */
    @RequestMapping(value = "selectSeckillGoodsList")
    public ModelAndView selectSeckillGoodsList(
            @RequestParam(required = false, value = "userId") String userId,
            ModelAndView mv) throws Exception {
        if (!Objects.equals(userId, "") && !Objects.equals(userId, null)) {
            Map<String, Object> map = new HashMap<>();
            map.put("status", "P");
            List<GoodsDto> goodsDtos = new ArrayList<>();
            for (GoodsDto goodsDto : goodsService.selectByCondition(map, 1)) {
                int goodsNumber = (int) Objects.requireNonNull((Map<String, Object>) redisTemplate.boundHashOps("秒杀商品").get(goodsDto.getGoodsId())).get("goodsNumber");
                if (goodsNumber>0) {
                    goodsDtos.add(goodsDto);
                }
            }
            mv.addObject("goodsDtos", goodsDtos);
            mv.addObject("userId", userId);
            mv.setViewName("seckill");
        } else {
            mv.setViewName("index");
        }
        return mv;
    }
}