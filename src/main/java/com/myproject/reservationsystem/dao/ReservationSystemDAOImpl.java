package com.myproject.reservationsystem.dao;

import com.myproject.reservationsystem.entity.*;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReservationSystemDAOImpl implements ReservationSystemDAO {

    private EntityManager entityManager;

    @Autowired
    public ReservationSystemDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void saveAvailableTimeSlot(AvailableTimeSlot availableTimeSlot) {
        entityManager.persist(availableTimeSlot);
    }

    @Override
    public void deleteAvailableTimeSlot(int id) {
        entityManager
                .createQuery("delete from AvailableTimeSlot a where a.id=:data")
                .setParameter("data", id)
                .executeUpdate();
    }

    @Override
    public void updateAvailableTimeSlot(AvailableTimeSlot availableTimeSlot) {
        entityManager.merge(availableTimeSlot);
    }

    @Override
    public List<RestaurantTable> findAllTables() {
        return entityManager
                .createQuery("from RestaurantTable", RestaurantTable.class)
                .getResultList();
    }

    @Override
    public List<RestaurantTable> findTablesByCapacity(int capacity) {
        List<RestaurantTable> tables = entityManager
                .createQuery(
                        "from RestaurantTable where capacity >= :data order by capacity asc",
                        RestaurantTable.class
                )
                .setParameter("data", capacity)
                .getResultList();

        return tables.isEmpty() ? null : tables;
    }

    @Override
    public RestaurantTable findTableByCapacity(int capacity) {
        List<RestaurantTable> tables = entityManager
                .createQuery(
                        "from RestaurantTable where capacity >= :data order by capacity asc",
                        RestaurantTable.class
                )
                .setParameter("data", capacity)
                .setMaxResults(1)
                .getResultList();

        return tables.isEmpty() ? null : tables.getFirst();
    }

    @Override
    public User findUserById(int id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public void saveReservation(Reservation reservation) {
        entityManager.persist(reservation);
    }

//    @Override
//    public Course findCourseByName(String courseName) {
//        return entityManager
//                .createQuery("from Course where name=:data", Course.class)
//                .setParameter("data", courseName)
//                .getSingleResult();
//    }


    @Override
    public List<Course> findAllCourses() {
        return entityManager
                .createQuery("from Course", Course.class)
                .getResultList();
    }

    @Override
    public Course findCourseById(int id) {
        return entityManager.find(Course.class, id);
    }
}
