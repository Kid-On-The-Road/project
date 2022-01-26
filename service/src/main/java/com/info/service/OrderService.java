package com.info.service;

import com.info.dto.GoodsDto;

import java.util.List;
import java.util.Map;

public interface OrderService {
    //redis中添加库存
    void addInventory(GoodsDto goodsDto);

    //扣减库存
    int deductionInventory(Long goodsId);

    //删除库存
    void deleteInventory(GoodsDto goodsDto);

    //保存用户信息
    int saveUserInfo(Long goodsId,Long userId,int orderNumber);

    //删除用户信息
    void deleteUserInfo(Long goodsId);

    //自定义查询
    List<GoodsDto> selectByCondition(Map<String, Object> map, int pageNum) throws Exception;

    /**
     * 根据商品ID查询商品
     *
     */
    GoodsDto selectByGoodsId(Long goodsId) throws Exception;

}
