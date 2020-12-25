package dev.pratul.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import dev.pratul.entity.UserAccount;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

	
	@Query("update UserAccount u set u.status = ?3 where u.user.id = ?2 and u.account.id = ?1")
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	public int updateUserAccountStatus(Long accountId, Long userId, boolean status);
}
