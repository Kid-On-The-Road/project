package com.info.service;

import com.info.dto.GoodsDto;

import java.util.Map;

public interface SeckillService {
    //redis中添加库存
    void addInventory(GoodsDto goodsDto);

    //扣减库存
    int deductionInventory(Long goodsId);

    //删除库存
    void deleteInventory(GoodsDto goodsDto);

    //保存用户信息
//    void saveUserInfo(Long goodsId,Long userId);

    //删除用户信息
//    void deleteUserInfo(Long goodsId);
}
