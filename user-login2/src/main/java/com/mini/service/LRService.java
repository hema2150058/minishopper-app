package com.mini.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mini.model.Address;
import com.mini.model.Role;
import com.mini.model.User;
import com.mini.repo.AddressRepo;
import com.mini.repo.RoleRepo;
import com.mini.repo.UserRepo;

@Service
public class LRService implements UserDetailsService {

	@Autowired
	private RoleRepo roleRepo;
	
	@Autowired
	private AddressRepo addressRepo;
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	public void initRoleAndUser() {

        Role shopperRole = new Role();
        shopperRole.setRoleName("SHOPPER");
        shopperRole.setRoleDescription("Shopper role");
        roleRepo.save(shopperRole);

        Role customerRole = new Role();
        customerRole.setRoleName("CUSTOMER");
        customerRole.setRoleDescription("Default role for newly created record");
        roleRepo.save(customerRole);
        
        Address shopperAddress = new Address();
        shopperAddress.setAddressId("Shopper123");
        shopperAddress.setAddressLine("RK Nagar");
        shopperAddress.setStreet("Madhapur");
        shopperAddress.setCity("Vizag");
        shopperAddress.setState("TS");
        shopperAddress.setPincode(2873990);
        addressRepo.save(shopperAddress);
        
        Address customerAddress = new Address();
        customerAddress.setAddressId("raj123");
        customerAddress.setAddressLine("Mn Nagar");
        customerAddress.setStreet("IS Sadan");
        customerAddress.setCity("Hyderabad");
        customerAddress.setState("AS");
        customerAddress.setPincode(7979889);
        addressRepo.save(customerAddress);
        
        User shopperUser = new User();
        shopperUser.setUserName("Shopper123");
        shopperUser.setUserPassword(getEncodedPassword("shopper@pass"));
        shopperUser.setUserEmail("shopper@mini.com");
        shopperUser.setUserFirstName("shop");
        shopperUser.setUserLastName("per");
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(shopperRole);
        shopperUser.setRole(adminRoles);
        shopperUser.setAddress(shopperAddress);
        userRepo.save(shopperUser);

        User customer = new User();
        customer.setUserName("raj123");
        customer.setUserPassword(getEncodedPassword("raj@123"));
        customer.setUserEmail("raj123@gmail.com");
        customer.setUserFirstName("raja");
        customer.setUserLastName("sharma");
        Set<Role> userRoles = new HashSet<>();
        userRoles.add(customerRole);
        customer.setRole(userRoles);
        customer.setAddress(customerAddress);
        userRepo.save(customer);
        
//        User customer1 = new User();
//        customer.setUserName("hea123");
//        customer.setUserPassword(getEncodedPassword("hea@123"));
//        customer.setUserFirstName("hema");
//        customer.setUserLastName("sharma");
//        Set<Role> userRoles1 = new HashSet<>();
//        userRoles.add(customerRole);
//        customer.setRole(userRoles);
//        ShopperUser.setAddress(customerAddress);
//        userRepo.save(customer);
        
    }
	
	public String getEncodedPassword(String password) {
        return passwordEncoder.encode(password);
    }

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		User user = userRepo.findByUserEmail(email);
		
		if(user==null) {
			throw new UsernameNotFoundException("User not found for email: "+email);
		}
		return new org.springframework.security.core.userdetails.User
				(user.getUserEmail(), user.getUserPassword(), getAuthority(user));
	}
	
	private Set<SimpleGrantedAuthority> getAuthority(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        user.getRole().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName()));
        });
        return authorities;
    }

	public User saveUsers(User users) {
		try {
		User value = userRepo.save(users);
		return value;
		}
		catch(DataIntegrityViolationException e) {
			throw new RuntimeException("Email or username is already in use. Please choose a different one");
		}
		
	}

	public boolean existsByUserEmail(String username) {
		return userRepo.existsByUserEmail(username);
	}
	
	public boolean existsByUserName(String userName) {
		return userRepo.existsByUserName(userName);
	}
	
}
