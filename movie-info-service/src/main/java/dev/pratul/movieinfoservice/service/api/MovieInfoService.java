package dev.pratul.movieinfoservice.service.api;

import dev.pratul.movieinfoservice.modal.Movie;

public interface MovieInfoService {

	public Movie getMovieInformation(String movieId);
}
