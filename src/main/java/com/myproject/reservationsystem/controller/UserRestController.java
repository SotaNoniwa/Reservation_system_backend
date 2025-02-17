package com.myproject.reservationsystem.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserRestController {

    @GetMapping
    public List<String> showExampleData() {
        return List.of("Hello", "This is", "Example", "Data");
    }
}
