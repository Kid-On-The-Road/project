package com.info.service;

import com.info.dto.UserDto;

import java.util.List;

public interface LoginService {
    //验证登录信息
    List<UserDto> verifyInfo(String userName, String password, String role) throws Exception;
}
