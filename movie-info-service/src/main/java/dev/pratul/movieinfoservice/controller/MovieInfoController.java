package dev.pratul.movieinfoservice.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.pratul.movieinfoservice.modal.Movie;
import dev.pratul.movieinfoservice.modal.MovieSummary;

@RestController
@RequestMapping("/movies")
public class MovieInfoController {

	@Value("${api.key}")
	private String apiKey;

	@Autowired
	private RestTemplate restTemplate;

	@RequestMapping("/{movieId}")
	public Movie getMovieInfor(@PathVariable("movieId") String movieId) {
		String movieSummary = restTemplate
				.getForObject("https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + apiKey, String.class);
		try {
			ObjectMapper mapper = new ObjectMapper();
			MovieSummary movieSummaryObject = mapper.readValue(movieSummary, MovieSummary.class);
			return new Movie(movieId, movieSummaryObject.getTitle(), movieSummaryObject.getOverview());
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
