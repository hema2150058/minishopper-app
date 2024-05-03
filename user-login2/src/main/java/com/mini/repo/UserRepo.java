package com.mini.repo;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mini.model.Role;
import com.mini.model.User;

public interface UserRepo extends JpaRepository<User, String>{


	boolean existsByUserEmail(String email);
	boolean existsByUserName(String userName);
	List<User>  findByRoles(Role role);

	User findByUserEmail(String email);
	User findByUserName(String username);
}
