package dev.pratul.moviecatalogservice.service.api;

import dev.pratul.moviecatalogservice.modal.CatalogItem;
import dev.pratul.moviecatalogservice.modal.Rating;

public interface MovieInfo {
	public CatalogItem getCatalogItem(Rating rating);
}
