package com.lakesidehotel.service;

import java.util.Collections;
import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import com.lakesidehotel.exception.UserAlreadyExistsException;
import com.lakesidehotel.model.Role;
import com.lakesidehotel.model.User;
import com.lakesidehotel.repository.RoleRepository;
import com.lakesidehotel.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class UserService implements IUserService{

	private final UserRepository userRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	private final RoleRepository roleRepository;
	@Override
	public User registerUser(User user) throws UserAlreadyExistsException {
	
		if(this.userRepository.existsByEmail(user.getEmail())) {
			throw new UserAlreadyExistsException(user.getEmail()+" already exist");
		}
		user.setPassword(this.passwordEncoder.encode(user.getPassword()));
		Role roleUser = this.roleRepository.findByName("ROLE_USER").get();
		user.setRoles(Collections.singletonList(roleUser));
		return this.userRepository.save(user);
	}

	@Override
	public List<User> getUsers() {
		
		return this.userRepository.findAll();
	}
	@Transactional
	@Override
	public void deleteUser(String email) {
		User theUser = getUser(email);
		if(theUser != null) {
			this.userRepository.deleteByEmail(email);
		}
		
	}

	@Override
	public User getUser(String email) {
	
		return this.userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User  not found...!!"));
	}

}
