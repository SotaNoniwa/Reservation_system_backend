package com.myproject.reservationsystem.dao;

import com.myproject.reservationsystem.entity.Role;
import com.myproject.reservationsystem.entity.User;

import java.util.Optional;

public interface UserDAO {

    User findUserById(int id);

    int saveUser(User user);

    Role findRoleByName(String name);

    User findUserByEmail(String email);

    User findUserByPhone(String phone);
}
