package com.info.service;

import com.info.dto.GoodsDto;

import java.util.List;
import java.util.Map;

public interface GoodsService {
    /**
     * 保存和修改
     *
     * @return
     */
    int save(GoodsDto goodsDto) throws Exception;

    /**
     * 根据商品ID查询商品
     *
     */
    GoodsDto selectByGoodsId(Long goodsId) throws Exception;

    /**
     * 根据条件查询商品
     */
    List<GoodsDto> selectByCondition(Map<String,Object> map,int pageNum) throws Exception;

    /**
     * 根据商品ID删除商品
     *
     */
    int deleteByGoodsId(Long goodsId);

}
