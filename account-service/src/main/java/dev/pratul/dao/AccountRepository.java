package dev.pratul.dao;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import dev.pratul.entity.Account;
import dev.pratul.entity.User;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

	public Set<Account> findByIdIn(Set<Long> ids);
	
	public Set<Account> findByIdIn(long[] ids);
	
	public Set<Account> findByIdIn(Long[] ids);

	public Optional<Account> findByAccountId(String accountId);
	
	public Set<Account> findByAccountIdIn(Collection<String> accountId);

	public Set<Account> findByUserAndStatusTrue(User user);

	public Set<Account> findByUser(User user);
	
	public Set<Account> findByUserAccountUserId(Long userId);

	public Set<Account> findByUserAndStatusTrueAndUserAccountStatusTrue(User user);

	public Set<Account> findByUserIdAndStatusTrueAndUserAccountStatusTrue(Long userId);
	
	public Set<Account> findByUserIdAndStatusTrueAndUserAccountStatus(Long userId, boolean status);

	public Set<Account> findByAccountIdInAndUserIdIn(List<String> accountId, List<Long> userId);

	@Modifying
	@Query("update Account acc set acc.status=:status where acc.id=:accountId")
	public void updateAccountStatus(Long accountId, boolean status);

}
