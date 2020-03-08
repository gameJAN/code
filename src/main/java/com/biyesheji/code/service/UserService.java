package com.biyesheji.code.service;

import com.biyesheji.code.entity.User;

/*用户service接口
* */
public interface UserService {

    public User findByUserName(String userName);

    public User findByEmail(String email);

    /*
    * 添加或修改用户信息*/

    public void save(User user);

    /*根据ID获取用户信息*/

    public User getById(Integer id);

}
