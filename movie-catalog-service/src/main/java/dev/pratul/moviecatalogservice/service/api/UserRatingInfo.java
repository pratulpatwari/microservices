package dev.pratul.moviecatalogservice.service.api;

import dev.pratul.moviecatalogservice.modal.UserRatings;

public interface UserRatingInfo {

	public UserRatings getUserRating(String userId);
}
