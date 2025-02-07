package com.myproject.reservationsystem.service;

import com.myproject.reservationsystem.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    User findUserById(int id);

    User findUserByUserName(String userName);
}
