package com.stylish.service;

import com.stylish.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User registerUser(String name, String email, String password);
    User processOAuthPostLogin(String email, String name, String picture, String provider);
}