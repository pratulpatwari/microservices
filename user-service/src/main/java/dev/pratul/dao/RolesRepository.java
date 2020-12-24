package dev.pratul.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.pratul.entity.Role;

@Repository
public interface RolesRepository extends JpaRepository<Role,Integer>{
	
	Role[] findByIdIn(Integer[] ids);
}
