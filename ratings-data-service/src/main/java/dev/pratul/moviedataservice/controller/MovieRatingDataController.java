package dev.pratul.moviedataservice.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.pratul.moviedataservice.modal.Rating;
import dev.pratul.moviedataservice.modal.UserRatings;

@RestController
@RequestMapping("/ratingsdata")
public class MovieRatingDataController {

	@RequestMapping("/{userId}")
	public UserRatings getRating(@PathVariable("userId") String userId) {
		List<Rating> ratings = Arrays.asList(new Rating("550", 4), new Rating("5678", 3));
		UserRatings userRating = new UserRatings(userId, ratings);
		return userRating;
	}
}
