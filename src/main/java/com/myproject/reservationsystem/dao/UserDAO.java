package com.myproject.reservationsystem.dao;

import com.myproject.reservationsystem.entity.User;

public interface UserDAO {

    User findUserById(int id);

    User findUserByUserName(String username);
}
