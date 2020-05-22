package dev.pratul;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import dev.pratul.controller.AccountResourceController;

@SpringBootTest
class AccountServiceApplicationTests {

	@Autowired
	private AccountResourceController controller;

	@Test
	public void contexLoads() throws Exception {
		assertNotNull(controller);
	}
	
}
