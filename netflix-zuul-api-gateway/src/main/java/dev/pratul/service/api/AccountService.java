package dev.pratul.service.api;

import java.util.Set;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import dev.pratul.model.Account;

@FeignClient("account-service")
public interface AccountService {
	
	@GetMapping("/api/user/{id}")
	@CrossOrigin
	Set<Account> getAccount(@PathVariable("id") String id);
}
