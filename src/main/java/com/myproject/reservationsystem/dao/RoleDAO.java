package com.myproject.reservationsystem.dao;


import com.myproject.reservationsystem.entity.Role;
import jakarta.persistence.EntityManager;

public interface RoleDAO {

    Role findRoleByName(String roleName);
}
