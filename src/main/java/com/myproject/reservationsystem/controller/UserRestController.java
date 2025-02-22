package com.myproject.reservationsystem.controller;

import com.myproject.reservationsystem.dto.CourseDTO;
import com.myproject.reservationsystem.entity.Course;
import com.myproject.reservationsystem.entity.RestaurantTable;
import com.myproject.reservationsystem.service.ReservationSystemService;
import com.myproject.reservationsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private ReservationSystemService reservationSystemService;

    @Autowired
    public UserRestController(ReservationSystemService reservationSystemService) {
        this.reservationSystemService = reservationSystemService;
    }

    @GetMapping("/courses")
    public List<CourseDTO> getCourses() {
        List<Course> courses = reservationSystemService.findAllCourses();
        return courses.stream()
                .map(course -> new CourseDTO(course.getId(), course.getName(), course.getPrice()))
                .collect(Collectors.toList());
    }
}
