package com.lakesidehotel.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lakesidehotel.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long>{
	
	Optional<Role> findByName(String name);

	boolean existsByName(Role role);

	boolean existsByName(String roleName);
}
