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

	Set<Account> findByIdIn(Set<Long> ids);
	
	Set<Account> findByIdIn(long[] ids);
	
	Set<Account> findByIdIn(Long[] ids);

	Optional<Account> findByAccountId(String accountId);
	
	Set<Account> findByAccountIdIn(Collection<String> accountId);

	Set<Account> findByUserAndStatusTrue(User user);

	Set<Account> findByUser(User user);
	
	Set<Account> findByUserAccountUserId(Long userId);

	Set<Account> findByUserAndStatusTrueAndUserAccountStatusTrue(User user);

	Set<Account> findByUserIdAndStatusTrueAndUserAccountStatusTrue(Long userId);
	
	Set<Account> findByUserIdAndStatusTrueAndUserAccountStatus(Long userId, boolean status);

	Set<Account> findByAccountIdInAndUserIdIn(List<String> accountId, List<Long> userId);

	@Modifying
	@Query("update Account acc set acc.status=:status where acc.id=:accountId")
	void updateAccountStatus(Long accountId, boolean status);

}
