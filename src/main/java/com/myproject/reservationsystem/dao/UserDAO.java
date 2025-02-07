package com.myproject.reservationsystem.dao;

import com.myproject.reservationsystem.entity.User;

public interface UserDAO {

    User findUserByUserName(String username);
}
