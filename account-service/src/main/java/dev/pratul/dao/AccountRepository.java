package dev.pratul.dao;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.pratul.entity.Accounts;
import dev.pratul.entity.User;

@Repository
public interface AccountRepository extends JpaRepository<Accounts, Long> {

	
	public Accounts findByAccountId(String accountId);

	public Set<Accounts> findByUserAndStatusTrue(User user);
	
	public Set<Accounts> findByUser(User user);
}
