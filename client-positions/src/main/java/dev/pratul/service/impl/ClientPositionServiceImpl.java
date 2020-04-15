package dev.pratul.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.pratul.dao.ClientPositionRepository;
import dev.pratul.entity.Accounts;
import dev.pratul.entity.ClientPosition;
import dev.pratul.service.api.AccountService;
import dev.pratul.service.api.ClientPositionService;

@Service
public class ClientPositionServiceImpl implements ClientPositionService {

	@Autowired
	private ClientPositionRepository clientPositionRepository;

	@Autowired
	private AccountService accountService;

	@Transactional
	public List<ClientPosition> getClientPositions(String clientId) {
		Accounts accounts = accountService.getAccountById(clientId);
		return clientPositionRepository.findByAssetId(Long.valueOf(1));
	}

}
