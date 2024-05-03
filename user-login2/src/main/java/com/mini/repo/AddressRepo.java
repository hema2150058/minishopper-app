package com.mini.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mini.model.Address;

public interface AddressRepo extends JpaRepository<Address, Integer>{

}
