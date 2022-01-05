package com.info.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.info.dto.UserDto;
import com.info.impl.UserServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Controller
public class UserManageController {
    @Resource
    private UserServiceImpl userService;

    /**
     * 测试页面
     * @param userId 用户ID
     */
    @GetMapping("test")
    @ResponseBody
    public String test(@RequestParam(required = false, value = "userId") Long userId) {
        return "selectUserList";
    }

    /**
     * 保存/修改用户信息
     *
     * @param userDto 需要保存的用户对象
     */
    @RequestMapping(value = "saveUser", method = RequestMethod.POST)
    @ResponseBody
    public int save(@ModelAttribute(value = "save") UserDto userDto) throws Exception {
        return userService.save(userDto);
    }

    /**
     * 删除用户
     *
     * @param userId 用户ID
     */
    @GetMapping("deleteUser")
    @ResponseBody
    public int delete(@RequestParam(required = false, value = "userId") Long userId) {
        return userService.deleteByUserId(userId);
    }

    /**
     * 根据ID查询用户
     * @param userId 用户ID
     */
    @RequestMapping(value = "selectUser", method = RequestMethod.POST)
    @ResponseBody
    public UserDto selectUser(
            @RequestParam(required = false, value = "userId") Long userId) throws Exception {
        return userService.selectByUserId(userId);
    }

    /**
     * 根据条件查询用户
     *
     * @param userName      用户名称
     * @param createTime 创建时间
     * @param pageNum        当前页
     * @param pageSize       每页显示的数据条数
     */
    @RequestMapping(value = "selectUserList", method = RequestMethod.GET)
    public ModelAndView selectUserList(
            @RequestParam(required = false, value = "userName") String userName,
            @RequestParam(required = false, value = "createTime") String createTime,
            @RequestParam(required = false, defaultValue = "1", value = "pageNum") int pageNum,
            @RequestParam(required = false, defaultValue = "10", value = "pageSize") int pageSize, ModelAndView mv) throws Exception {
        Page<UserDto> page = PageHelper.startPage(pageNum, pageSize);
        Map<String, Object> map = new HashMap<>();
        if (!Objects.isNull(userName) && userName.length() > 0) {
            map.put("userName", userName);
        }
        if (!Objects.isNull(createTime) && createTime.length() > 0) {
            map.put("createTime", new SimpleDateFormat("yyyy-MM-dd").parse(createTime));
        }
//        Thread.sleep(1000);
        userService.selectByCondition(map, pageNum);
        PageInfo<UserDto> pageInfo = page.toPageInfo();
        mv.addObject("pageInfo", pageInfo);
        mv.setViewName("userManage");
        return mv;
    }


}