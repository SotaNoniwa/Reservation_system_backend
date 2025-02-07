package com.myproject.reservationsystem.dao;

import com.myproject.reservationsystem.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDAOImpl implements UserDAO {

    private EntityManager entityManager;

    @Autowired
    public UserDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public User findUserById(int id) {
//        User user = null;
//        try {
//            user = entityManager.find(User.class, id);
//        } catch (Exception e) {
//            user = null;
//        }
//
//        return user;

        return entityManager.find(User.class, id);
    }

    @Override
    public User findUserByUserName(String username) {
        TypedQuery<User> query = entityManager
                .createQuery("from User where username=:username and enabled=true", User.class)
                .setParameter("username", username);

        User user = null;
        try {
            user = query.getSingleResult();
        } catch (Exception e) {
            user = null;
        }

        return user;
    }
}
