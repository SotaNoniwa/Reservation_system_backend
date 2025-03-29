package com.myproject.reservationsystem.dao;

import com.myproject.reservationsystem.entity.Role;
import com.myproject.reservationsystem.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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

    /**
     * @param user
     * @return {@code int}
     * @apiNote Return a user id if passed user already exists in database, otherwise return -1.
     */
    @Override
    public int  saveUser(User user) {
        User fetchedUser = findUserByEmail(user.getEmail());

        if (fetchedUser!=null) {
            System.out.println("User id " + fetchedUser.getId() + " exists in DB");
            return fetchedUser.getId();
        }

        User foundUserByPhone = findUserByPhone(user.getPhone());

        System.out.println("Saving the user in DB...");
        entityManager.persist(user);
        return -1;
    }

    @Override
    public Role findRoleByName(String name) {
        TypedQuery<Role> query = entityManager
                .createQuery("from Role where name=:name", Role.class)
                .setParameter("name", name);

        Role role;
        try {
            role = query.getSingleResult();
        } catch (Exception e) {
            role = null;
        }

        return role;
    }

    @Override
    public User findUserByEmail(String email) {
        TypedQuery<User> query = entityManager
                .createQuery("from User where email=:email", User.class)
                .setParameter("email", email);

        User user;
        try {
            user = query.getSingleResult();
        } catch (Exception e) {
            user = null;
        }

        return user;
    }

    @Override
    public User findUserByPhone(String phone) {
        TypedQuery<User> query = entityManager
                .createQuery("from User where phone=:phone", User.class)
                .setParameter("phone", phone);

        User user;
        try {
            user = query.getSingleResult();
        } catch (Exception  e) {
            user = null;
        }

        return user;
    }
}
