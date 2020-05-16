package dev.pratul.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.pratul.entity.Accounts;
import dev.pratul.entity.ClientPosition;

@Repository
public interface ClientPositionRepository extends JpaRepository<ClientPosition, Long> {

	public List<ClientPosition> findByAccountsIn(List<Accounts> accounts);

	public List<ClientPosition> findByAssetId(Long assetId);

	public List<ClientPosition> findByCreateDateBetween(String startDate, String endDate);
}
