package dev.pratul.moviecatalogservice.service.impl;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import dev.pratul.moviecatalogservice.modal.Rating;
import dev.pratul.moviecatalogservice.modal.UserRatings;
import dev.pratul.moviecatalogservice.service.api.UserRatingInfo;

@Service
public class UserRatingInfoImpl implements UserRatingInfo {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${movie.ratings.service.uri}")
	private String ratingsUri;

	/*
	 * It is important to note that @HystrixCommand method has to be in separate
	 * service class and not in the Controller class. If this method is part of
	 * Controller class then Hystrix will not call this method as the method is
	 * called by the proxy created by Hystrix and not by the actual instance.
	 * 
	 * 
	 */
	@Override
	@HystrixCommand(fallbackMethod = "getFallbackUserRating", commandProperties = {
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
			@HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
			@HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000"), })
	public UserRatings getUserRating(String userId) {
		UserRatings ratings = restTemplate.getForObject(ratingsUri + userId, UserRatings.class);
		return ratings;
	}

	/*
	 * In the real world application, make a call to cache to return previously
	 * cached default values.
	 */
	@SuppressWarnings("unused")
	private UserRatings getFallbackUserRating(@PathVariable("userId") String userId) {
		Rating rating = new Rating("0", 0);
		UserRatings userRating = new UserRatings();
		userRating.setUserId(userId);
		userRating.setRatings(Arrays.asList(rating));
		return userRating;
	}
}
