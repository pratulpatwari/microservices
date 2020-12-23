package dev.pratul.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssetService {

	private final JdbcTemplate jdbcTemplate;
	final String SELECT_BY_ID = "select count(*) from asset";

	public AssetService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Transactional
	public void loadAssets() {
		int assets = jdbcTemplate.queryForObject(SELECT_BY_ID, Integer.class);
		System.out.println(assets);
	}

}
