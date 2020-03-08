package com.biyesheji.code.service.impl;

/*
* 用户Service实现类*/

import com.biyesheji.code.entity.User;
import com.biyesheji.code.repository.UserRepository;
import com.biyesheji.code.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public User getById(Integer id) {
        return userRepository.getOne(id);
    }
}
