package dev.pratul.moviedataservice.modal;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRatings {

	private List<Rating> userRating;
}
