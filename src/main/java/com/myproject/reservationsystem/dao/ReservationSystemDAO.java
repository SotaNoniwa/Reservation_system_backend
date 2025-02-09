package com.myproject.reservationsystem.dao;

import com.myproject.reservationsystem.entity.*;

import java.util.List;

public interface ReservationSystemDAO {

    void saveAvailableTimeSlot(AvailableTimeSlot availableTimeSlot);

    void deleteAvailableTimeSlot(int id);

    void updateAvailableTimeSlot(AvailableTimeSlot availableTimeSlot);

//    void clearTableFromAvailableTimeSlot(AvailableTimeSlot availableTimeSlot);

    List<RestaurantTable> findAllTables();

//    RestaurantTable findFreeTable(int capacity);

    RestaurantTable findTableByCapacity(int capacity);

    int getMaxCapacityOfTable();

    List<RestaurantTable> findTablesByCapacity(int capacity);

    User findUserById(int id);

    void saveReservation(Reservation reservation);

//    Course findCourseByName(String courseName);

    List<Course> findAllCourses();

    Course findCourseById(int id);
}
