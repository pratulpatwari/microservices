package dev.pratul.service.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import dev.pratul.model.Account;

@FeignClient("account-service")
public interface AccountService {
	
	@GetMapping("/api")
	@CrossOrigin
	Account getAccount(String id);
}
