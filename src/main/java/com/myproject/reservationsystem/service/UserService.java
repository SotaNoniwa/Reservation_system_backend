package com.myproject.reservationsystem.service;

import com.myproject.reservationsystem.entity.Role;
import com.myproject.reservationsystem.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends UserDetailsService {

    User findUserById(int id);

    int saveUser(User user);

    Role findRoleByName(String name);

    User findUserByEmail(String email);

    User findUserByPhone(String phone);
}
