package dev.pratul.movieinfoservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.pratul.movieinfoservice.modal.Movie;
import dev.pratul.movieinfoservice.service.api.MovieInfoService;

@RestController
@RequestMapping("/movies")
public class MovieInfoController {
	
	@Autowired
	private MovieInfoService movieInforService;

	@RequestMapping("/{movieId}")
	public Movie getMovieInfor(@PathVariable("movieId") String movieId) {
		return movieInforService.getMovieInformation(movieId);
	}
}
