package com.info.service;

import com.info.dto.ShoppingCarQueryDto;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface ShoppingService {
    List<ShoppingCarQueryDto> selectOrderGoodsList(Map<String ,Object> map) throws Exception;
    int saveOrderRecord(Map map) throws ParseException;
    void updateOrderRecord(String type,String goodsId);
    void deleteOrderRecord(Long goodsId,Long userId) throws Exception;
}
