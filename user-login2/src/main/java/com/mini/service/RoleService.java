package com.mini.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mini.model.Role;
import com.mini.repo.RoleRepo;

@Service
public class RoleService {

	@Autowired
	private RoleRepo rolerepo;
	
	public Role createNewRole(Role role) {
		return rolerepo.save(role);
	}
}
