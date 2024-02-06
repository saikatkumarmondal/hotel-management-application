package com.lakesidehotel.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lakesidehotel.exception.UserAlreadyExistsException;
import com.lakesidehotel.model.User;
import com.lakesidehotel.service.IUserService;

import lombok.RequiredArgsConstructor;
@RestController
@RequiredArgsConstructor
public class AuthController {

	private final IUserService userService;
	@PostMapping("/register-user")
	public ResponseEntity<?> registerUser(User user){
		try {
		 this.userService.registerUser(user);
		return ResponseEntity.ok("Registration successfully..!!");
		} catch (UserAlreadyExistsException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}
}
