package com.info.mapper;

import com.info.dto.ShoppingCarQueryDto;
import com.info.entity.ShoppingCarEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ShoppingCarEntityMapper {
    /**
     * 根据商品ID删除信息
     */
    int deleteByPrimaryKey(Long goodsId);

    /**
     * 添加信息
     */
    int insert(ShoppingCarEntity shoppingCarEntity);

    /**
     * 批量插入
     */
    int insertBatchWithAll(List<ShoppingCarEntity> shoppingCarEntities);

    /**
     * 更新信息
     */
    int updateByGoodsId(Map<String, Object> map);

    /**
     * 根据条件查询记录
     */
    List<ShoppingCarQueryDto> selectByCondition(Map<String, Object> map);

    /**
     * 根据商品ID查询记录
     */
    ShoppingCarQueryDto selectByPrimaryKey(Long userId);
}
