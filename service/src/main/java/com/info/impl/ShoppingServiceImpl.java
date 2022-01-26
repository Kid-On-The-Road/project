package com.info.impl;

import com.info.convert.ConvertUtil;
import com.info.dto.GoodsDto;
import com.info.dto.ShoppingCarQueryDto;
import com.info.entity.GoodsEntity;
import com.info.entity.ShoppingCarEntity;
import com.info.mapper.GoodsEntityMapper;
import com.info.mapper.ShoppingCarEntityMapper;
import com.info.service.ShoppingService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;

@Service
public class ShoppingServiceImpl implements ShoppingService {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private GoodsServiceImpl goodsService;
    @Resource
    private GoodsEntityMapper goodsEntityMapper;
    @Resource
    private ShoppingCarEntityMapper shoppingCarEntityMapper;

    /**
     * 查询订单信息
     */
    @Override
    public List<GoodsEntity> selectOrderGoodsList(Map<String, Object> map) throws Exception {
        List<GoodsEntity> goodsEntityList = new ArrayList<>();
        Map<Object, Object> userInfos = redisTemplate.boundHashOps("用户信息").entries();
        for (Object userinfo : userInfos.keySet()) {
            Map<String, Object> user = (Map<String, Object>) userInfos.get(userinfo);
            if (Long.valueOf((String) map.get("userId")) == Long.valueOf(String.valueOf(user.get("userId"))).longValue()) {
                GoodsEntity goodsEntity = goodsEntityMapper.selectByPrimaryKey(Long.valueOf(String.valueOf(user.get("goodsId"))).longValue());
                goodsEntity.setGoodsNumber((Integer) user.get("orderNumber"));
                goodsEntity.setStatus((String) user.get("status"));
                goodsEntityList.add(goodsEntity);
            }
        }
        return goodsEntityList;
    }

    /**
     * 付款
     */
    @Override
    public void payment(String type, Long goodsId, Long userId) {
        Map<String, Object> userInfo = (Map<String, Object>) redisTemplate.boundHashOps("用户信息").get(userId + goodsId);
        userInfo.put("status", type);
        redisTemplate.boundHashOps("用户信息").put(goodsId + userId, userInfo);
    }

    /**
     * 保存编辑
     */
    @Override
    public void saveOrder(Long userId, Long goodsId, int orderNumber) {
        Map<String, Object> userInfo = (Map<String, Object>) redisTemplate.boundHashOps("用户信息").get(userId + goodsId);
        redisTemplate.boundHashOps("上架商品").put(goodsId, (int)redisTemplate.boundHashOps("上架商品").get(goodsId)-(orderNumber-(int)userInfo.get("orderNumber")));
        userInfo.put("orderNumber", orderNumber);
        redisTemplate.boundHashOps("用户信息").put(goodsId + userId, userInfo);
    }


    @Override
    public void deleteOrderRecord(Long goodsId, Long userId) throws Exception {
        //恢复缓存中商品的库存
        Map<String, Object> goodsInfo = (Map<String, Object>)redisTemplate.boundHashOps("用户信息").get(goodsId + userId);
        redisTemplate.boundHashOps("上架商品").put(goodsId, (int)goodsInfo.get("orderNumber")+(int)redisTemplate.boundHashOps("上架商品").get(goodsId));
        //删除订单信息
        redisTemplate.boundHashOps("用户信息").delete(goodsId + userId);
    }


}
