package com.info.service;

import com.info.dto.ShoppingCarQueryDto;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface ShoppingService {
    List<ShoppingCarQueryDto> selectSeckillGoodsList(Map<String ,Object> map) throws Exception;
    void saveSeckillRecord(Long userId,Long goodsId) throws ParseException;
    void updateSeckillRecord(String type,String goodsId);
    void deleteSeckillRecord(Long goodsId);
}
