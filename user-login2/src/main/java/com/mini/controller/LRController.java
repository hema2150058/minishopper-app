package com.mini.controller;

import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;


import com.mini.dto.JwtRequest;
import com.mini.dto.JwtResponse;
import com.mini.dto.RegisterDto;
import com.mini.dto.UserDto;
import com.mini.dto.ValidateStatusDto;
import com.mini.exception.UserAlreadyExistException;
import com.mini.model.Address;
import com.mini.model.Role;
import com.mini.model.User;
import com.mini.repo.AddressRepo;
import com.mini.repo.UserRepo;
import com.mini.service.LRService;
import com.mini.util.JwtUtil;

@RestController
public class LRController {

	//login register and validate code

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private LRService userService;

	@Autowired
	private JwtUtil jwtTokenUtil;

	@Autowired
	private UserRepo userRepo;

	private ValidateStatusDto validatingDTO = new ValidateStatusDto();
	

	@jakarta.annotation.PostConstruct
    public void initRoleAndUser() {
		userService.initRoleAndUser();
    }

	@PostMapping(path = "/register")
	public ResponseEntity<Object> registerUser(@RequestBody UserDto userDTO) throws UserAlreadyExistException {
		boolean b = (this.userService.existsByUserEmail(userDTO.getUserEmail()) || this.userService.existsByUserName(userDTO.getUserName()));
		if (b) {
			throw new UserAlreadyExistException("User Already Exist: " + userDTO.getUserEmail()+" " +userDTO.getUserName());
		} else {
			User users = new User();
			users.setUserName(userDTO.getUserName());
			users.setUserPassword(passwordEncoder.encode(userDTO.getUserPassword()));
			users.setUserFirstName(userDTO.getUserFirstName());
			users.setUserLastName(userDTO.getUserLastName());
			users.setUserEmail(userDTO.getUserEmail());
			users.setCreatedDate(userDTO.getCreatedDate());
			
			//address
			Address address = new Address();
			address.setAddressId(users.getUserName());
			address.setAddressLine(userDTO.getAddress().getAddressLine());
			address.setStreet(userDTO.getAddress().getStreet());
			address.setCity(userDTO.getAddress().getCity());
			address.setState(userDTO.getAddress().getState());
			address.setPincode(userDTO.getAddress().getPincode());
			users.setAddress(address);
			
			//users.setAddress(userDTO.getAddress());
			
			//role
			HashSet<Role> roles = new HashSet<>();
			Role role = new Role();
			role.setRoleName("CUSTOMER");
			roles.add(role);
			users.setRole(roles);

			this.userService.saveUsers(users);
			return new ResponseEntity<Object>(
					new RegisterDto( users.getUserName(), users.getUserEmail()),
					HttpStatus.CREATED);
		}
	}

	@PostMapping(path= "/signin")
	public ResponseEntity<Object> createAuthorizationToken(@RequestBody JwtRequest jwtRequest)
			throws UsernameNotFoundException {

		boolean b = this.userService.existsByUserEmail(jwtRequest.getEmailId());
		if (!b) {
			throw new UsernameNotFoundException("User Not Found : " + jwtRequest.getEmailId());
		} else {

			User users = new User();
			users.setUserEmail(jwtRequest.getEmailId());
			users.setUserPassword(jwtRequest.getUserPassword());

			final UserDetails userDetails = userService.loadUserByUsername(users.getUserEmail());

			User r = userRepo.findByUserEmail(users.getUserEmail());
			String username = r.getUserName();
			String email = r.getUserEmail();
			String password = r.getUserPassword();

			System.out.println(userRepo.findByUserEmail(email).getRole().toArray()[0]);

			// if (userDetails.getPassword().equals(users.getPassword())) {
			if (passwordEncoder.matches(users.getUserPassword(), userDetails.getPassword())) {
				return new ResponseEntity<>(
						new JwtResponse(username, email, jwtTokenUtil.generateToken(userDetails),
								jwtTokenUtil.getCurrentTime(), jwtTokenUtil.getExpirationTime()),
						HttpStatus.OK);
			}

			return new ResponseEntity<>(
					new JwtResponse(email, username, jwtTokenUtil.generateToken(userDetails),
							jwtTokenUtil.getCurrentTime(), jwtTokenUtil.getExpirationTime()),
					HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@GetMapping(path = "/validate", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ValidateStatusDto> validatingAuthorizationToken(
			@RequestHeader(name = "Authorization") String tokenDup) {
		String token = tokenDup.substring(7);
		try {
			UserDetails user = userService.loadUserByUsername(jwtTokenUtil.extractUsername(token));
			if (Boolean.TRUE.equals(jwtTokenUtil.validateToken(token, user))) {
				validatingDTO.setStatus(true);
				return new ResponseEntity<>(validatingDTO, HttpStatus.OK);
			} else {
				throw new Exception("Invalid token");
			}
		} catch (Exception e) {
			validatingDTO.setStatus(false);
			return new ResponseEntity<>(validatingDTO, HttpStatus.BAD_REQUEST);
		}

	}

	@GetMapping(path = "/health-check")
	public ResponseEntity<String> healthCheck() {

		return new ResponseEntity<>("OK", HttpStatus.OK);
	}
}



