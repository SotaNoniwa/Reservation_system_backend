package com.myproject.reservationsystem.dto;

import org.springframework.beans.factory.annotation.Autowired;

public class UserLoginDTO {

    private String email;
    private String password;

    @Autowired
    public UserLoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
