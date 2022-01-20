package com.info.controller;

import com.info.dto.GoodsDto;
import com.info.dto.ShoppingCarQueryDto;
import com.info.entity.ShoppingCarEntity;
import com.info.impl.GoodsServiceImpl;
import com.info.mapper.ShoppingCarEntityMapper;
import com.info.service.SeckillService;
import com.info.service.ShoppingService;
import com.rabbitmq.client.Channel;
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
    @Resource
    private ShoppingCarEntityMapper shoppingCarEntityMapper;

    @RequestMapping("send")
    public ModelAndView send(ModelAndView mv, Channel channel) throws Exception {
        String msg = "success";
        amqpTemplate.convertAndSend("spring.test.exchange", "a.b", msg);
        // 等待10秒后再结束
//        Thread.sleep(3000);
        mv.setViewName("test");
        return mv;
    }

    /**
     * 开始秒杀
     */
    @RequestMapping(value = "startSeckill", method = RequestMethod.GET)
    @ResponseBody
    public int startSeckill(
            @RequestParam(required = false, value = "goodsId") long goodsId,
            @RequestParam(required = false, value = "userId") long userId
    ) throws Exception {
        int goodsNumber = seckillService.deductionInventory(goodsId);
        if (goodsNumber >= 0) {
            seckillService.saveUserInfo(goodsId,userId);
            return 0;
        }
        return 1;
    }

    /**
     * 结束秒杀
     */
    @RequestMapping(value = "endSeckill")
    @ResponseBody
    public void endSeckil(){
        amqpTemplate.convertAndSend("seckill.exchange", "seckillGoods", "success");
    }

    /**
     * 根据条件查询商品
     */
    @RequestMapping(value = "selectSeckillGoodsList")
    public ModelAndView selectSeckillGoodsList(
            @RequestParam(required = false, value = "userId") Long userId,
            ModelAndView mv) throws Exception {
        if (!Objects.equals(userId, "") && !Objects.equals(userId, null)) {
            Map<String, Object> map = new HashMap<>();
            map.put("status", "P");
            List<GoodsDto> goodsDtos = new ArrayList<>();
            for (GoodsDto goodsDto : goodsService.selectByCondition(map, 1)) {
                map.clear();
                map.put("userId", userId);
                map.put("goodsId", goodsDto.getGoodsId());
                if (shoppingCarEntityMapper.selectByCondition(map).size() == 0) {
                    int goodsNumber = (int) Objects.requireNonNull((Map<String, Object>) redisTemplate.boundHashOps("秒杀商品").get(goodsDto.getGoodsId())).get("goodsNumber");
                    if (goodsNumber > 0) {
                        goodsDtos.add(goodsDto);
                    }
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