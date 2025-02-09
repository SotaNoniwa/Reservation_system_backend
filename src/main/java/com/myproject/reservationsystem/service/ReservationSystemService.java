package com.myproject.reservationsystem.service;

import com.myproject.reservationsystem.entity.*;

import java.util.List;
import java.util.Optional;

public interface ReservationSystemService {

    void saveAvailableTimeSlot(AvailableTimeSlot availableTimeSlot);

    void deleteAvailableTimeSlot(int id);

    void updateAvailableTimeSlot(AvailableTimeSlot availableTimeSlot);

    List<RestaurantTable> findAllTables();

    RestaurantTable findTableByCapacity(int capacity);

    List<RestaurantTable> findTablesByCapacity(int capacity);

    int getMaxCapacityOfTable();

    User findUserById(int id);

    void saveReservation(Reservation reservation);

//    Course findCourseByName(String courseName);

    List<Course> findAllCourses();

    Course findCourseById(int id);
}
