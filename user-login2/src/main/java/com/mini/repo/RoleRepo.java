package com.mini.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mini.model.Role;

public interface RoleRepo extends JpaRepository<Role, String> {

}
