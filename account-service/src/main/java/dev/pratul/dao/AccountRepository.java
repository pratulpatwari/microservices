package dev.pratul.dao;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dev.pratul.entity.Accounts;
import dev.pratul.entity.Users;

@Repository
public interface AccountRepository extends CrudRepository<Accounts, Long> {

	public Optional<Accounts> findByAccountId(String accountId);

	public Set<Accounts> findByUserAndStatusTrue(Users user);

	public Set<Accounts> findByUser(Users user);

	public Set<Accounts> findByUserAndStatusTrueAndUserAccount_StatusTrue(Users user);

}
