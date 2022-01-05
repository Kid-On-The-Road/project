package com.info.service;

import com.info.dto.UserDto;

import java.util.List;
import java.util.Map;

public interface UserService {
    /**
     * 保存和修改
     *
     * @return
     */
    int save(UserDto userDto) throws Exception;

    /**
     * 根据商品ID查询商品
     *
     */
    UserDto selectByUserId(Long userId) throws Exception;

    /**
     * 根据条件查询商品
     */
    List<UserDto> selectByCondition(Map<String,Object> map,int pageNum) throws Exception;

    /**
     * 根据商品ID删除商品
     *
     */
    int deleteByUserId(Long userId);

}
