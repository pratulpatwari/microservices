package dev.pratul.userservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.pratul.userservice.entity.Roles;

@Repository
public interface RolesRepository extends JpaRepository<Roles,Long>{

}
