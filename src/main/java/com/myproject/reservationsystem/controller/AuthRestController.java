package com.myproject.reservationsystem.controller;

import com.myproject.reservationsystem.dto.UserLoginDTO;
import com.myproject.reservationsystem.dto.UserRegisterDTO;
import com.myproject.reservationsystem.entity.Role;
import com.myproject.reservationsystem.entity.User;
import com.myproject.reservationsystem.security.JwtService;
import com.myproject.reservationsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthRestController {

    private UserService userService;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private JwtService jwtService;

    private AuthenticationManager authenticationManager;

    @Autowired
    public AuthRestController(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public int register(@RequestBody UserRegisterDTO userRegisterDTO) {
        Role role = userService.findRoleByName("ROLE_USER");

        User user = new User(
                userRegisterDTO.getUsername(),
                userRegisterDTO.getEmail(),
                userRegisterDTO.getPhone(),
                userRegisterDTO.getPassword(),
                true,
                role
        );

        return userService.saveUser(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody UserLoginDTO userLoginDTO) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginDTO.getEmail(), userLoginDTO.getPassword()));

        User user = userService.findUserByEmail(userLoginDTO.getEmail());
        if (user == null) {
            throw new UsernameNotFoundException("User doesn't exits");
        }

        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(user);
        } else {
            throw new UsernameNotFoundException("Invalid user request");
        }
    }
}
