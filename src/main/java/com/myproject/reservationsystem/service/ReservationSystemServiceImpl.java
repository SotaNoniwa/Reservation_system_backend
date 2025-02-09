package com.myproject.reservationsystem.service;

import com.myproject.reservationsystem.dao.ReservationSystemDAO;
import com.myproject.reservationsystem.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReservationSystemServiceImpl implements ReservationSystemService {

    private ReservationSystemDAO reservationSystemDAO;

    @Autowired
    public ReservationSystemServiceImpl(ReservationSystemDAO reservationSystemDAO) {
        this.reservationSystemDAO = reservationSystemDAO;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAvailableTimeSlot(AvailableTimeSlot availableTimeSlot) {
        reservationSystemDAO.saveAvailableTimeSlot(availableTimeSlot);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteAvailableTimeSlot(int id) {
        reservationSystemDAO.deleteAvailableTimeSlot(id);
    }

    @Override
    @Transactional
    public void updateAvailableTimeSlot(AvailableTimeSlot availableTimeSlot) {
        reservationSystemDAO.updateAvailableTimeSlot(availableTimeSlot);
    }

    @Override
    public List<RestaurantTable> findAllTables() {
        return reservationSystemDAO.findAllTables();
    }

    @Override
    public RestaurantTable findTableByCapacity(int capacity) {
        return reservationSystemDAO.findTableByCapacity(capacity);
    }

    @Override
    public List<RestaurantTable> findTablesByCapacity(int capacity) {
        return reservationSystemDAO.findTablesByCapacity(capacity);
    }

    @Override
    public int getMaxCapacityOfTable() {
        return reservationSystemDAO.getMaxCapacityOfTable();
    }

    @Override
    public User findUserById(int id) {
        return reservationSystemDAO.findUserById(id);
    }

    @Override
    @Transactional
    public void saveReservation(Reservation reservation) {
        reservationSystemDAO.saveReservation(reservation);
    }

//    @Override
//    public Course findCourseByName(String courseName) {
//        return reservationSystemDAO.findCourseByName(courseName);
//    }

    @Override
    public List<Course> findAllCourses() {
        return reservationSystemDAO.findAllCourses();
    }

    @Override
    public Course findCourseById(int id) {
        return reservationSystemDAO.findCourseById(id);
    }
}
