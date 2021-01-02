package dev.pratul.service;

public abstract class Dataload {

	abstract void fetchData(String param);

	abstract void processData();

	abstract void saveDataToDB();

	public final void dataload(String param) {
		fetchData(param);
		processData();
		saveDataToDB();
	}
}
