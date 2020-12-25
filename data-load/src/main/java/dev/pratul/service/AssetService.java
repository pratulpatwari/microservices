package dev.pratul.service;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import dev.pratul.entity.Asset;
import dev.pratul.entity.Quote;
import dev.pratul.entity.Stock;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class AssetService {

	private static final String FINNHUB_URL = "https://finnhub.io/api/v1/";
	private static final String STOCKS = "/stock/symbol?exchange=US&";
	private static final String QUOTE = "/quote?symbol=";
	final String SELECT_BY_ID = "select * from asset";
	private final JdbcTemplate jdbcTemplate;
	private final WebClient webClient;

	public AssetService(JdbcTemplate jdbcTemplate, WebClient.Builder webClientBuilder) {
		this.jdbcTemplate = jdbcTemplate;
		this.webClient = webClientBuilder.baseUrl(FINNHUB_URL)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultUriVariables(Collections.singletonMap("url", FINNHUB_URL)).build();
	}

	@Transactional
	public List<Asset> loadAssetsFromDB() {
		List<Asset> assets = jdbcTemplate.query(SELECT_BY_ID, (rs, rowNum) -> new Asset(rs.getLong("id"),
				rs.getString("symbol"), rs.getString("description"), rs.getDouble("market_value")));
		for (Asset asset : assets) {
			log.info("Asset: id: {}, symbol: {}, description: {}, market_value: {}", asset.getId(), asset.getSymbol(),
					asset.getDescription(), asset.getMarketValue());
		}
		return assets;
	}

	public Flux<Stock> supportedStock() {
		return this.webClient.get().uri(STOCKS + "token=bvfrvcn48v6p6kjqkkh0").exchange()
				.flatMapMany(response -> response.bodyToFlux(Stock.class));
	}
	
	public Flux<Quote> stockPrice() {
		return this.webClient.get().uri(QUOTE + "AAPL" + "&token=bvfrvcn48v6p6kjqkkh0").exchange()
				.flatMapMany(response -> response.bodyToFlux(Quote.class));
	}
}
