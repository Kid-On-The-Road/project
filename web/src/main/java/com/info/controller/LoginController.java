package com.info.controller;

import com.info.dto.UserDto;
import com.info.service.LoginService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class LoginController {
    @Resource
    private LoginService loginService;

    @RequestMapping(value = "login", method = RequestMethod.GET)
    @ResponseBody
    public List<UserDto> login(
            @RequestParam(required = false, value = "userName") String userName,
            @RequestParam(required = false, value = "password") String password,
            @RequestParam(required = false, value = "role") String role
    ) throws Exception {
        return loginService.verifyInfo(userName, password,role);
    }
}
