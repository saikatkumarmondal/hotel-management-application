package com.lakesidehotel.service;

import java.util.List;

import com.lakesidehotel.exception.UserAlreadyExistsException;
import com.lakesidehotel.model.User;

public interface IUserService {

	User registerUser(User user) throws UserAlreadyExistsException;
	
	List<User> getUsers();
	
	void deleteUser(String email);
	
	User getUser(String email);
}
