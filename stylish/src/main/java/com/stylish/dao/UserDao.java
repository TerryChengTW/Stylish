package com.stylish.dao;

import com.stylish.model.User;

import java.util.List;

public interface UserDao {
    void insertUser(User user);
    User getUserByEmail(String email);
    List<String> getUserRoles(int userId);
}