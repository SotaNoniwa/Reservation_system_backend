package com.myproject.reservationsystem.service;

import com.myproject.reservationsystem.dao.RoleDAO;
import com.myproject.reservationsystem.dao.UserDAO;
import com.myproject.reservationsystem.entity.Role;
import com.myproject.reservationsystem.entity.User;
import com.myproject.reservationsystem.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private UserDAO userDAO;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserDAO userDAO, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDAO = userDAO;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User findUserById(int id) {
        return userDAO.findUserById(id);
    }

    @Override
    @Transactional
    public int saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userDAO.saveUser(user);
    }

    @Override
    public Role findRoleByName(String name) {
        return userDAO.findRoleByName(name);
    }

    @Override
    public User findUserByEmail(String email) {
        return userDAO.findUserByEmail(email);
    }

    @Override
    public User findUserByPhone(String phone) {
        return userDAO.findUserByPhone(phone);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userDAO.findUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with address: " + email);
        }

        return new CustomUserDetails(user);
    }
}
