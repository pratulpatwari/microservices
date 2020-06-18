package dev.pratul.service.impl;

import java.util.Set;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.pratul.model.Account;
import dev.pratul.service.api.AccountService;
import lombok.Getter;

@Component
public class ServiceCallables implements Callable<Set<Account>> {

	@Autowired
	private AccountService accountService;

	@Getter
	private int counter;

	@Override
	public Set<Account> call() throws Exception {
		counter++;
		System.out.println("Thread : " + Thread.currentThread() + "    Current value: " + counter);
		return accountService.getAccountByUserId("1");
	}
	/*
	 * private Set<Account> multiThreadForLoadTesting() { Set<Account> accounts =
	 * new HashSet<>(); ExecutorService executor = null; try { executor =
	 * Executors.newFixedThreadPool(10); List<Future<Set<Account>>> list = new
	 * LinkedList<Future<Set<Account>>>(); List<ServiceCallables> task = new
	 * LinkedList<>(); for (int i = 0; i < 5; i++) { task.add(serviceCall); } list =
	 * executor.invokeAll(task); for (int i = 0; i < list.size(); i++) {
	 * 
	 * log.info("Counter value for thread: {}", serviceCall.getCounter());
	 * Future<Set<Account>> future = list.get(i); try { Set<Account> result =
	 * future.get(); accounts.addAll(result); } catch (InterruptedException |
	 * ExecutionException e) { e.printStackTrace(); } } } catch
	 * (InterruptedException e) { e.printStackTrace(); } finally {
	 * executor.shutdown(); } return accounts; }
	 */
}
