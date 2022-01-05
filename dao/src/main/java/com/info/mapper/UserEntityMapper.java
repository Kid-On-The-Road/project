package com.info.mapper;

import com.info.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserEntityMapper {
    /**
     * 根据商品ID删除商品
     */
    int deleteByPrimaryKey(Long userId);

    /**
     * 添加商品
     */
    int insert(UserEntity userEntity);

    /**
     * 更新商品信息
     */
    int updateById(UserEntity userEntity);

    /**
     * 根据条件查询商品
     */
    List<UserEntity> selectByCondition(Map<String,Object> map);

    /**
     * 根据商品ID查询商品
     */
    UserEntity selectByPrimaryKey(Long userId);
}
