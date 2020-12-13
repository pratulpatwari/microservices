package dev.pratul.dao;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import dev.pratul.entity.Accounts;
import dev.pratul.entity.User;

@Repository
public interface AccountRepository extends JpaRepository<Accounts, Long> {

	public Set<Accounts> findByIdIn(Set<Long> ids);
	
	public Set<Accounts> findByIdIn(Long[] ids);

	public Optional<Accounts> findByAccountId(String accountId);
	
	public Set<Accounts> findByAccountIdIn(Collection<String> accountId);

	public Set<Accounts> findByUserAndStatusTrue(User user);

	public Set<Accounts> findByUser(User user);

	public Set<Accounts> findByUserAndStatusTrueAndUserAccountStatusTrue(User user);

	public Set<Accounts> findByUserIdAndStatusTrueAndUserAccountStatusTrue(Long userId);

	public Set<Accounts> findByAccountIdInAndUserIdIn(List<String> accountId, List<Long> userId);

	@Modifying
	@Query("update Accounts acc set acc.status=:status where acc.id=:accountId")
	public void updateAccountStatus(Long accountId, boolean status);
}
