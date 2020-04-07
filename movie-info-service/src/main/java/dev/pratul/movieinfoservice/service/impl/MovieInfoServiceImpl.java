package dev.pratul.movieinfoservice.service.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.pratul.movieinfoservice.modal.Movie;
import dev.pratul.movieinfoservice.modal.MovieSummary;
import dev.pratul.movieinfoservice.service.api.MovieInfoService;

@Service
public class MovieInfoServiceImpl implements MovieInfoService {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${movie.external.api.key}")
	private String apiKey;

	@Value("${movie.external.uri}")
	private String movieDbUri;

	@Override
	public Movie getMovieInformation(String movieId) {
		String movieSummary = restTemplate.getForObject(movieDbUri + movieId + "?api_key=" + apiKey, String.class);
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
