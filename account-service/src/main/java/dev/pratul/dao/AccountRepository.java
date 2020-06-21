package dev.pratul.dao;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.pratul.entity.Accounts;
import dev.pratul.entity.Users;

@Repository
public interface AccountRepository extends JpaRepository<Accounts, Long> {

	
	public Optional<Accounts> findByAccountId(String accountId);

	public Set<Accounts> findByUsersAndStatusTrue(Users user);
	
	public Set<Accounts> findByUsers(Users user);
	
}
