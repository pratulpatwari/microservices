package dev.pratul.moviecatalogservice.modal;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRatings {

	private String userId;
	private List<Rating> userRating;
}
