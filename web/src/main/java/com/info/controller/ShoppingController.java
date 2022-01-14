package com.info.controller;

import com.info.dto.ShoppingCarQueryDto;
import com.info.service.GoodsService;
import com.info.service.SeckillService;
import com.info.service.ShoppingService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
public class ShoppingController {
    @Resource
    private GoodsService goodsService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private SeckillService seckillService;
    @Resource
    private ShoppingService shoppingService;

    //查询购物车商品
    @RequestMapping(value = "selectShoppingGoods")
    public ModelAndView selectShop(
            @RequestParam(required = false, value = "userId") String userId,
            @RequestParam(required = false, value = "goodsName") String goodsName,
            @RequestParam(required = false, value = "goodsCategory") String goodsCategory,
            @RequestParam(required = false, value = "payStatus") String status,
            ModelAndView mv) throws Exception {
        Map<String, Object> map = new HashMap<>();
        if (!Objects.isNull(goodsName) && goodsName.length() > 0) {
            map.put("goodsName", goodsName);
        }
        if (!Objects.isNull(userId) && userId.length() > 0) {
            map.put("userId", userId);
        }
        if (!Objects.isNull(goodsCategory) && goodsCategory.length() > 0) {
            map.put("goodsCategory", goodsCategory);
        }
        if (!Objects.isNull(status) && status.length() > 0) {
            map.put("status", status);
        }
        mv.addObject("goodsList", shoppingService.selectSeckillGoodsList(map));
        mv.addObject("userId", userId);
        mv.setViewName("shopping");
        return mv;
    }

    //支付
    @RequestMapping(value = "payment")
    @ResponseBody
    public void payment(
            @RequestParam(required = false, value = "goodsId") String goodsId,
            @RequestParam(required = false, value = "type") String type
    ) {
        shoppingService.updateSeckillRecord(type, goodsId);
    }

    //删除
    @RequestMapping(value = "delRecord")
    @ResponseBody
    public void delRecord(
            @RequestParam(required = false,value = "goodsId")Long goodsId
    ){
        shoppingService.deleteSeckillRecord(goodsId);
    }
}