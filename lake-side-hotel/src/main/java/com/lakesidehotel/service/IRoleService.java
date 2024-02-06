package com.lakesidehotel.service;

import java.util.List;

import com.lakesidehotel.exception.UserAlreadyExistsException;
import com.lakesidehotel.model.Role;
import com.lakesidehotel.model.User;

public interface IRoleService {
	 List<Role> getRoles();
	    Role createRole(Role theRole);

	    void deleteRole(Long id);
	    Role findByName(String name);

	    User removeUserFromRole(Long userId, Long roleId);
	    User assignRoleToUser(Long userId, Long roleId) throws UserAlreadyExistsException;
	    Role removeAllUsersFromRole(Long roleId);
}
