package dev.pratul.movieinfoservice.modal;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MovieSummary {
	private boolean adult;

	@JsonProperty("backdrop_path")
	private String backdropPath;

	@JsonProperty("belongs_to_collection")
	private String belongsToCollection;

	private int budget;

	private List<Genres> genres;

	private String homepage;

	private int id;

	@JsonProperty("imdb_id")
	private String imdbId;
	
	@JsonProperty("original_language")
	private String originalLanguage;

	@JsonProperty("original_title")
	private String originalTitle;

	private String overview;

	private double popularity;

	@JsonProperty("poster_path")
	private String posterPath;

	@JsonProperty("production_companies")
	private List<ProductionCompanies> productionCompanies;

	@JsonProperty("production_countries")
	private List<ProductionCountries> productionCountries;

	@JsonProperty("release_date")
	private String releaseDate;

	private int revenue;

	private int runtime;

	@JsonProperty("spoken_languages")
	private List<SpokenLanguages> spokenLanguages;

	private String status;

	private String tagline;

	private String title;

	private boolean video;

	@JsonProperty("vote_average")
	private double voteAverage;

	@JsonProperty("vote_count")
	private int voteCount;

	@Getter
	@Setter
	@NoArgsConstructor
	public static class Genres {
		private int id;
		private String name;

	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class ProductionCompanies {
		private int id;

		@JsonProperty("logo_path")
		private String logoPath;

		private String name;

		@JsonProperty("origin_country")
		private String originCountry;

	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class ProductionCountries {
		private String iso_3166_1;
		private String name;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class SpokenLanguages {
		private String iso_639_1;
		private String name;
	}

}
