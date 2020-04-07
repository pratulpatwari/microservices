package dev.pratul.moviecatalogservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import dev.pratul.moviecatalogservice.modal.CatalogItem;
import dev.pratul.moviecatalogservice.modal.Movie;
import dev.pratul.moviecatalogservice.modal.Rating;
import dev.pratul.moviecatalogservice.service.api.MovieInfo;

@Service
public class MovieInfoImpl implements MovieInfo {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${movie.info.service.uri}")
	private String movieInfoUri;

	@Override
	@HystrixCommand(fallbackMethod = "getFallbackCatalogItem", commandProperties = {
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
			@HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
			@HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000"), })
	public CatalogItem getCatalogItem(Rating rating) {
		Movie movie = restTemplate.getForObject(movieInfoUri + rating.getMovieId(), Movie.class);
		return new CatalogItem(movie.getName(), movie.getDesc(), rating.getRating());
	}

	@SuppressWarnings("unused")
	private CatalogItem getFallbackCatalogItem(Rating rating) {
		return new CatalogItem("Movie name not found", "", rating.getRating());
	}

}
