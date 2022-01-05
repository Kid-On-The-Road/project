package com.info.dto;

public class UserDto {
    //用户ID
    private Long userId;
    //用户名
    private String userName;
    //密码
    private String password;
    //创建时间
    private String createTime;
    //是否有效
    private String validity;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }
}
