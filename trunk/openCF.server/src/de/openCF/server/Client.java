package de.openCF.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.log4j.Logger;

public class Client implements Runnable {

	private Logger logger = Logger.getLogger(Client.class);

	@Override
	public void run() {

		try {
			SSLSocket socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket("localhost", 5678);
			PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
			logger.debug("Client -> sending...");
			for (int i = 0; i < 100; i++) {
				String message = "Hallo: " + i;
				logger.info("Client sent: " + message);
				printWriter.println(message);
				printWriter.flush();
				TimeUnit.SECONDS.sleep(1);
			}
			socket.close();
		} catch (UnknownHostException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}

	}

	public static void main(String[] args) {

		Executors.newSingleThreadExecutor().execute(new Client());
	}

}
