package com.info.controller;

import com.info.dto.GoodsDto;
import com.info.impl.GoodsServiceImpl;
import com.info.mapper.ShoppingCarEntityMapper;
import com.info.redis.RedissonLock;
import com.info.service.OrderService;
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
import java.text.ParseException;
import java.util.*;

@Controller
public class OrderController {
    @Resource
    private GoodsServiceImpl goodsService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private OrderService orderService;
    @Resource
    private ShoppingCarEntityMapper shoppingCarEntityMapper;
    /**
     * 下单
     */
    @RequestMapping(value = "startOrder", method = RequestMethod.GET)
    @ResponseBody
    public int startOrder(
            @RequestParam(required = false, value = "goodsId") long goodsId,
            @RequestParam(required = false, value = "userId") long userId
    ) throws Exception {
        try {
            RedissonLock.acquire("key");
            int goodsNumber = orderService.deductionInventory(goodsId);
            if (goodsNumber >= 0) {
                orderService.saveUserInfo(goodsId, userId);
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            RedissonLock.release("key");
        }
        return 1;
    }

    /**
     * 根据条件查询商品
     */
    @RequestMapping(value = "selectOrderGoodsList")
    public ModelAndView selectOrderGoodsList(
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
                    int goodsNumber = (int) Objects.requireNonNull((Map<String, Object>) redisTemplate.boundHashOps("上架商品").get(goodsDto.getGoodsId())).get("goodsNumber");
                    if (goodsNumber > 0) {
                        goodsDtos.add(goodsDto);
                    }
                }
            }
            mv.addObject("goodsDtos", goodsDtos);
            mv.addObject("userId", userId);
            mv.setViewName("order");
        } else {
            mv.setViewName("index");
        }
        return mv;
    }
}