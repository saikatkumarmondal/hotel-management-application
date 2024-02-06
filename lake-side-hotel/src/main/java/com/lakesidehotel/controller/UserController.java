package com.lakesidehotel.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lakesidehotel.model.User;
import com.lakesidehotel.service.IUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

	private final IUserService userService;
	@GetMapping("/all")
	public ResponseEntity<List<User>> getUsers(){
		return new ResponseEntity<List<User>>(this.userService.getUsers(),HttpStatus.FOUND);
	}
	@GetMapping("/{email}")
	public ResponseEntity<?> getUserByEmail(@PathVariable("email")String email){
	
		try {
			User user = this.userService.getUser(email);
			return ResponseEntity.ok(user);

		} catch (UsernameNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching user..!!");
		}
	
	}
	
	@DeleteMapping("/delete/{email}")
	public ResponseEntity<String> deleteUser(@PathVariable("email")String email){
		try {
			this.userService.deleteUser(email);
			return ResponseEntity.ok("User deleted successfully...");
		} catch (UsernameNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting user...!!");
		}
	}
}
