package dev.pratul.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.pratul.entity.Roles;

@Repository
public interface RolesRepository extends JpaRepository<Roles,Long>{
	
	Roles[] findByIdIn(Long[] ids);
}
