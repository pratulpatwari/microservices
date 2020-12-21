package dev.pratul.dao;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.pratul.entity.Account;
import dev.pratul.entity.Position;

@Repository
public interface ClientPositionRepository extends JpaRepository<Position, Long> {

	/*
	 * Fetch the client-positions between startDate and endDate for the given set of
	 * accounts. The start date is considered as 7 days back from the today's date
	 * with time as 00:00 midnight. The end date will have current time of the day.
	 */
	public List<Position> findByAccountsInAndCreateDateBetween(List<Account> accounts, ZonedDateTime startDate,
			ZonedDateTime endDate);

	/*
	 * Fetch the client-positions for the given account for last 7 days. This call
	 * is for a single account
	 */
	public List<Position> findByAccountsAndCreateDateBetween(Account accounts, ZonedDateTime startDate,
			ZonedDateTime endDate);

	/*
	 * Fetch the client-positions for the given account and the assetId held by the
	 * account.
	 */
	public List<Position> findByAccountsAndAssetId(Account account, Long assetId);

}
