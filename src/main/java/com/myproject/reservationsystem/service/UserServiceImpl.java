package com.myproject.reservationsystem.service;

import com.myproject.reservationsystem.dao.RoleDAO;
import com.myproject.reservationsystem.dao.UserDAO;
import com.myproject.reservationsystem.entity.Role;
import com.myproject.reservationsystem.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserDAO userDAO;

    private RoleDAO roleDAO;

    @Autowired
    public UserServiceImpl(UserDAO userDAO, RoleDAO roleDAO) {
        this.userDAO = userDAO;
        this.roleDAO = roleDAO;
    }

    @Override
    public User findUserByUserName(String userName) {
        return userDAO.findUserByUserName(userName);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDAO.findUserByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }
}
