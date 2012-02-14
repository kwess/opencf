package de.openCF.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.log4j.Logger;

public class Test {
	
	private static Logger logger = Logger.getLogger(Test.class);

	public static void main(String[] args) {
		logger.debug("test");

		int port = 1234;
		String hostName = "HostName";
		SSLSocketFactory sslFact = (SSLSocketFactory) SSLSocketFactory
				.getDefault();
		SSLSocket socket;
		try {
			socket = (SSLSocket) sslFact.createSocket(hostName, port);
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			// Nun sicher lesen und schreiben
			in.close();
			out.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
