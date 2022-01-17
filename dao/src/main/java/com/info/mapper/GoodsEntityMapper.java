package com.info.mapper;

import com.info.dto.GoodsDto;
import com.info.entity.GoodsEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
@Mapper
public interface GoodsEntityMapper {
    /**
     * 根据商品ID删除商品
     */
    int deleteByPrimaryKey(Long goodsId);

    /**
     * 添加商品
     */
    int insert(GoodsEntity goodsEntity);

    /**
     * 更新商品信息
     */
    int updateById(GoodsEntity goodsEntity);

    /**
     * 根据条件查询商品
     */
    List<GoodsEntity> selectByCondition(Map<String,Object> map);

    /**
     * 根据商品ID查询商品
     */
    GoodsEntity selectByPrimaryKey(Long goodsId);

    /**
     * 更新商品状态
     */
    void updateStatus(GoodsDto goodsDto);
    /**
     * 扣减库存
     */
    void deductionInventory(@Param("type") String type,@Param("goodsId") String goodsId);
}
