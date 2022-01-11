package com.info.service;

import com.info.dto.GoodsDto;

public interface SeckillService {
    //redis中添加库存
    void addInventory(GoodsDto goodsDto);

    //更新秒杀数据
    void updateSeckillData(GoodsDto goodsDto);

    //扣减库存
    void deductionInventory(GoodsDto goodsDto);

    //删除库存
    void deleteInventory(GoodsDto goodsDto);
}
