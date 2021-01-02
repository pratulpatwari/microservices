package dev.pratul.service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import dev.pratul.DataLoadConfig;
import dev.pratul.entity.Quote;
import dev.pratul.entity.Stock;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AssetService extends Dataload {

	private final DataLoadConfig dataLoadConfig;
	final String SELECT_BY_ID = "select * from asset";
	private final WebClient webClient;
	private List<Stock> stocks = new LinkedList<>();

	public AssetService(DataLoadConfig dataLoadConfig, WebClient.Builder webClientBuilder) {
		this.dataLoadConfig = dataLoadConfig;
		this.webClient = webClientBuilder.baseUrl(dataLoadConfig.getUri())
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultUriVariables(Collections.singletonMap("url", dataLoadConfig.getUri())).build();
	}

	@Override
	void fetchData(String ticker) {
		log.debug("Fetching data for asset service");
		this.stocks = this.webClient.get().uri(dataLoadConfig.getStocks() + "token=" + dataLoadConfig.getToken())
				.exchange().flatMapMany(response -> response.bodyToFlux(Stock.class)).collectList().block();
		log.debug("Data fetch for asset service completed");
	}

	@Override
	void processData() {
		log.debug("Entering process asset data");
		String stockPriceUrl = dataLoadConfig.getQuote();
		if (!stocks.isEmpty()) {
			for (Stock stock : stocks) {
				if (stock.getSymbol() != null && !stock.getSymbol().isBlank()) {
					stockPriceUrl += stock.getSymbol().trim();
					Quote quote = this.webClient.get().uri(stockPriceUrl + "&token=" + dataLoadConfig.getToken())
							.exchange().flatMap(response -> response.bodyToMono(Quote.class)).block();
				}
			}
		}
		log.debug("Leaving process asset data");
	}

	@Override
	void saveDataToDB() {
		log.debug("Saving Asset data to DB started ...");
		log.debug("Saving Asset data to DB completed ...");
	}
}
