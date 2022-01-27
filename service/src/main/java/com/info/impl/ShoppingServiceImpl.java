package com.info.impl;

import com.info.entity.GoodsEntity;
import com.info.mapper.GoodsEntityMapper;
import com.info.service.ShoppingService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ShoppingServiceImpl implements ShoppingService {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private GoodsEntityMapper goodsEntityMapper;

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
        if (redisTemplate.boundHashOps("上架商品").get(goodsId) != null) {
            redisTemplate.boundHashOps("上架商品").put(goodsId, (int) redisTemplate.boundHashOps("上架商品").get(goodsId) - (orderNumber - (int) userInfo.get("orderNumber")));
        } else {
            GoodsEntity goodsEntity = goodsEntityMapper.selectByPrimaryKey(goodsId);
            goodsEntity.setGoodsNumber(goodsEntity.getGoodsNumber() - (orderNumber - (int)userInfo.get("orderNumber")));
            goodsEntityMapper.updateById(goodsEntity);
        }
        userInfo.put("orderNumber", orderNumber);
        redisTemplate.boundHashOps("用户信息").put(goodsId + userId, userInfo);
    }

    /**
     * 删除订单信息
     */
    @Override
    public void deleteOrderRecord(Long goodsId, Long userId) throws Exception {
        //恢复缓存中商品的库存
        Map<String, Object> userInfo = (Map<String, Object>) redisTemplate.boundHashOps("用户信息").get(goodsId + userId);
        if (redisTemplate.boundHashOps("上架商品").get(goodsId) != null) {
            redisTemplate.boundHashOps("上架商品").put(goodsId, (int) userInfo.get("orderNumber") + (int) redisTemplate.boundHashOps("上架商品").get(goodsId));
        }else{
            GoodsEntity goodsEntity = goodsEntityMapper.selectByPrimaryKey(goodsId);
            goodsEntity.setGoodsNumber(goodsEntity.getGoodsNumber() + (int)userInfo.get("orderNumber"));
            goodsEntityMapper.updateById(goodsEntity);
        }
        //删除订单信息
        redisTemplate.boundHashOps("用户信息").delete(goodsId + userId);
    }


}
