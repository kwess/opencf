package de.openCF.server;

import java.util.concurrent.Executors;

public class Starter {
	public static void main(String[] args) {
		Executors.newSingleThreadExecutor().execute(new Server());
	}
}
