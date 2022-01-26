package com.info.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
import java.text.SimpleDateFormat;
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
    @Resource
    private GoodsEntityMapper goodsEntityMapper;

    /**
     * 加入购物车
     */
    @RequestMapping(value = "addShoppingCar")
    @ResponseBody
    public int addShoppingCar(
            @RequestParam(required = false, value = "goodsId") long goodsId,
            @RequestParam(required = false, value = "userId") long userId,
            @RequestParam(required = false, value = "orderNumber") int orderNumber
    ) throws Exception {
        try {
            RedissonLock.acquire("key");
            return orderService.saveUserInfo(goodsId, userId, orderNumber);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            RedissonLock.release("key");
        }
        return 0;
    }

    /**
     * 根据条件查询商品
     */
    @RequestMapping(value = "selectOrderGoodsList")
    public ModelAndView selectOrderGoodsList(
            @RequestParam(required = false, value = "userId") Long userId,
            @RequestParam(required = false, value = "goodsName") String goodsName,
            @RequestParam(required = false, value = "goodsCategory") String goodsCategory,
            @RequestParam(required = false, value = "productionTime") String productionTime,
            @RequestParam(required = false, defaultValue = "1", value = "pageNum") int pageNum,
            @RequestParam(required = false, defaultValue = "10", value = "pageSize") int pageSize,
            ModelAndView mv) throws Exception {
        //判断userId 是否为空,为空则返回登录界面
        if (!Objects.equals(userId, "") && !Objects.equals(userId, null)) {
            Page<GoodsDto> page = PageHelper.startPage(pageNum, pageSize);
            Map<String, Object> map = new HashMap<>();
            map.put("userId",userId);
            if (!Objects.isNull(goodsName) && goodsName.length() > 0) {
                map.put("goodsName", goodsName);
            }
            if (!Objects.isNull(goodsCategory) && goodsCategory.length() > 0) {
                map.put("goodsCategory", goodsCategory);
            }
            if (!Objects.isNull(productionTime) && productionTime.length() > 0) {
                map.put("productionTime", new SimpleDateFormat("yyyy-MM-dd").parse(productionTime));
            }
            map.put("status","P");
            List<GoodsDto> goodsDtos = orderService.selectByCondition(map, pageNum);
            PageInfo<GoodsDto> pageInfo = page.toPageInfo();
            mv.addObject("pageInfo", pageInfo);
            mv.addObject("userId", userId);
            mv.setViewName("order");
        } else {
            mv.setViewName("index");
        }
        return mv;
    }

    /**
     * 根据ID查询商品
     *
     * @param goodsId 商品ID
     */
    @RequestMapping(value = "selectOrderGoods", method = RequestMethod.POST)
    @ResponseBody
    public GoodsDto selectGoods(
            @RequestParam(required = false, value = "goodsId") Long goodsId) throws Exception {
        return orderService.selectByGoodsId(goodsId);
    }
}