package dev.pratul.service;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import dev.pratul.DataLoadConfig;
import dev.pratul.entity.Asset;
import dev.pratul.entity.Quote;
import dev.pratul.entity.Stock;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class DataloadService {

	private final DataLoadConfig dataLoadConfig;
	final String SELECT_BY_ID = "select * from asset";
	private final JdbcTemplate jdbcTemplate;
	private final WebClient webClient;

	public DataloadService(JdbcTemplate jdbcTemplate, DataLoadConfig dataLoadConfig,
			WebClient.Builder webClientBuilder) {
		this.jdbcTemplate = jdbcTemplate;
		this.dataLoadConfig = dataLoadConfig;
		this.webClient = webClientBuilder.baseUrl(dataLoadConfig.getUri())
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultUriVariables(Collections.singletonMap("url", dataLoadConfig.getUri())).build();
	}

	@Transactional
	public List<Asset> loadAssetsFromDB() {
		List<Asset> assets = jdbcTemplate.query(SELECT_BY_ID,
				(rs, rowNum) -> new Asset(rs.getLong("id"), rs.getString("symbol"), rs.getString("description"),
						rs.getString("figi"), rs.getString("mic"), rs.getString("currency"), rs.getString("type")));
		for (Asset asset : assets) {
			log.info("Asset: id: {}, symbol: {}, description: {}", asset.getId(), asset.getSymbol(),
					asset.getDescription());
		}
		return assets;
	}

	public Flux<Stock> supportedStock() {
		return this.webClient.get().uri(dataLoadConfig.getStocks() + "token=" + dataLoadConfig.getToken()).exchange()
				.flatMapMany(response -> response.bodyToFlux(Stock.class));
	}

	public Flux<Quote> stockPrice(String ticker) {
		String url = dataLoadConfig.getQuote();
		if (ticker != null && !ticker.isBlank()) {
			url += ticker;
		}
		Flux<Quote> quotes = this.webClient.get().uri(url + "&token=" + dataLoadConfig.getToken()).exchange()
				.flatMapMany(response -> response.bodyToFlux(Quote.class));
		return quotes;
	}

}
