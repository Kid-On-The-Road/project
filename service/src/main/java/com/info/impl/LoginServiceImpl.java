package com.info.impl;

import com.info.dto.UserDto;
import com.info.service.LoginService;
import com.info.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {
    @Resource
    private UserService userService;

    @Override
    public List<UserDto> verifyInfo(String userName, String password,String role) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("userName",userName);
        map.put("password", password);
        map.put("role", role);
        return userService.selectByCondition(map,1);
    }
}
