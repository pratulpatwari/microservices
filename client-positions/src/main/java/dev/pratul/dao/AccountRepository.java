package dev.pratul.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.pratul.entity.Accounts;

@Repository
public interface AccountRepository extends JpaRepository<Accounts, Long> {

	public Accounts findByAccountId(String accountId);
}
