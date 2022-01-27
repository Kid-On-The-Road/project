package com.info.service;

import com.info.entity.GoodsEntity;

import java.util.List;
import java.util.Map;

public interface ShoppingService {
    List<GoodsEntity> selectOrderGoodsList(Map<String ,Object> map) throws Exception;
    void deleteOrderRecord(Long goodsId,Long userId) throws Exception;
    void payment(String type, Long goodsId,Long userId) throws Exception;
    int saveOrder(Long userId, Long goodsId, int orderNumber);
}
