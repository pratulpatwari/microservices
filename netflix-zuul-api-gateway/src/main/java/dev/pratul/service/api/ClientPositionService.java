package dev.pratul.service.api;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("client-position")
public interface ClientPositionService {
	
	
}
