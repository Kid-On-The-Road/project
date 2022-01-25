package com.info.controller;

import com.info.convert.ConvertUtil;
import com.info.dto.GoodsDto;
import com.info.impl.GoodsServiceImpl;
import com.info.mapper.GoodsEntityMapper;
import com.info.mapper.ShoppingCarEntityMapper;
import com.info.redis.RedissonLock;
import com.info.service.OrderService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    @Resource
    private GoodsEntityMapper goodsEntityMapper;
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
        //判断userId 是否为空,为空则返回登录界面
        if (!Objects.equals(userId, "") && !Objects.equals(userId, null)) {
            List lists = redisTemplate.boundHashOps("上架商品").values();
            List<GoodsDto> goodsDtoList = new ArrayList<GoodsDto>();
            for (Object list : lists) {
                Map<Object, Object> map = (Map<Object, Object>) list;
                goodsDtoList.add((GoodsDto) ConvertUtil.mapToObject(map, GoodsDto.class));
            }
            mv.addObject("goodsDtos",goodsDtoList);
            mv.addObject("userId", userId);
            mv.setViewName("order");
        } else {
            mv.setViewName("index");
        }
        return mv;
    }
}