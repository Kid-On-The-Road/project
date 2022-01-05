package com.info.impl;

import com.info.convert.ConvertUtil;
import com.info.dto.UserDto;
import com.info.entity.UserEntity;
import com.info.mapper.UserEntityMapper;
import com.info.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserEntityMapper userEntityMapper;

    @Override
    public int save(UserDto userDto) throws Exception {
        UserEntity userEntity = ConvertUtil.convert(userDto, UserEntity.class);
        userEntity.setCreateTime(new Date());
        if (!Objects.isNull(userEntity.getUserId()) && userEntity.getUserId() > 0) {
            return userEntityMapper.updateById(userEntity);
        }
        return userEntityMapper.insert(userEntity);
    }

    @Override
    public UserDto selectByUserId(Long userId) throws Exception {
        UserEntity userEntity = userEntityMapper.selectByPrimaryKey(userId);
        return ConvertUtil.convert(userEntity, UserDto.class);
    }

    @Override
    public List<UserDto> selectByCondition(Map<String, Object> map, int pageNum) throws Exception {

        List<UserEntity> userEntities = userEntityMapper.selectByCondition(map);
        List<UserDto> userDtos = new ArrayList<>();
        for (UserEntity userEntity : userEntities) {
            UserDto userDto = ConvertUtil.convert(userEntity, UserDto.class);
            userDtos.add(userDto);
        }
        return userDtos;
    }

    @Override
    public int deleteByUserId(Long userId) {
        return userEntityMapper.deleteByPrimaryKey(userId);
    }
}
