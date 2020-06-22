package dev.pratul.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import dev.pratul.entity.Accounts;
import dev.pratul.entity.UserAccount;
import dev.pratul.entity.Users;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

	public List<UserAccount> findByAccounts(Accounts account);

	public List<UserAccount> findByUsersAndStatusTrueAndAccountsIn(Users users, Set<Accounts> accounts);

	public List<UserAccount> findByUsersAndAccountsIn(Users users, Set<Accounts> accounts);
	
	public List<UserAccount> findByAccountsIn(Set<Accounts> accounts);

	@Modifying
	@Query("update UserAccount u set u.status = false where u.users.id = :userId and u.accounts.id = :accountId")
	public int deactivateUserAccount(Long userId, Long accountId);
}
