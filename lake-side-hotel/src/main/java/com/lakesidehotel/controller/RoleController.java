package com.lakesidehotel.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lakesidehotel.exception.RoleAlreadyExists;
import com.lakesidehotel.exception.UserAlreadyExistsException;
import com.lakesidehotel.model.Role;
import com.lakesidehotel.model.User;
import com.lakesidehotel.service.IRoleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roles")
public class RoleController {

	private IRoleService roleService;
	 @GetMapping("/all-roles")
	    public ResponseEntity<List<Role>> getAllRoles(){
	        return new ResponseEntity<>(roleService.getRoles(), HttpStatus.FOUND);
	    }

	    @PostMapping("/create-new-role")
	    public ResponseEntity<String> createRole(@RequestBody Role theRole){
	        try{
	            roleService.createRole(theRole);
	            return ResponseEntity.ok("New role created successfully!");
	        }catch(RoleAlreadyExists re){
	            return ResponseEntity.status(HttpStatus.CONFLICT).body(re.getMessage());

	        }
	    }
	    @DeleteMapping("/delete/{roleId}")
	    public void deleteRole(@PathVariable("roleId") Long roleId){
	        roleService.deleteRole(roleId);
	    }
	    @PostMapping("/remove-all-users-from-role/{roleId}")
	    public Role removeAllUsersFromRole(@PathVariable("roleId") Long roleId){
	        return roleService.removeAllUsersFromRole(roleId);
	    }

	    @PostMapping("/remove-user-from-role")
	    public User removeUserFromRole(
	            @RequestParam("userId") Long userId,
	            @RequestParam("roleId") Long roleId){
	        return roleService.removeUserFromRole(userId, roleId);
	    }
	    @PostMapping("/assign-user-to-role")
	    public User assignUserToRole(
	            @RequestParam("userId") Long userId,
	            @RequestParam("roleId") Long roleId) throws UserAlreadyExistsException{
	        return roleService.assignRoleToUser(userId, roleId);
	    }
}
