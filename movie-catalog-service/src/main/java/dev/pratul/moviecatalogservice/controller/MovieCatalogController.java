package dev.pratul.moviecatalogservice.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.pratul.moviecatalogservice.modal.CatalogItem;
import dev.pratul.moviecatalogservice.modal.UserRatings;
import dev.pratul.moviecatalogservice.service.api.MovieInfo;
import dev.pratul.moviecatalogservice.service.api.UserRatingInfo;

/*
 * This is acting as a client application consuming 
 * rating data and movie info service.
 * */

@RestController
@RequestMapping("/catalog")
public class MovieCatalogController {

	@Autowired
	private MovieInfo movieInfo;

	@Autowired
	private UserRatingInfo userRatingInfo;

	/*
	 * 1. get all the movie id's
	 * 
	 * 2. For each movie id, call movie info service.
	 * 
	 * 3. put them all together
	 * 
	 * 
	 * 4. Hystrix will implement circuit breaker pattern. This annotation is placed
	 * where multiple micro-services are called where chances of fault are high.
	 * fallbackMethod is the one which will be called when there is any fault
	 * identified by the Hystrix in the called micro-service.
	 * 
	 * Always put the @HystrixCommand in the service method to make sure that
	 * Hystrix proxy is able to call the fallback method.
	 * 
	 */

	@RequestMapping("/{userId}")
	public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

		UserRatings userRating = userRatingInfo.getUserRating(userId);

		return userRating.getUserRating().stream().map(rating -> movieInfo.getCatalogItem(rating))
				.collect(Collectors.toList());
	}

}
