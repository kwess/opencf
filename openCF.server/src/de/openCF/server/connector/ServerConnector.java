package de.openCF.server.connector;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import org.apache.log4j.Logger;

public class ServerConnector implements Runnable {

	private Logger					logger						= Logger.getLogger(ServerConnector.class);
	private SSLServerSocket			sslServerSocket				= null;
	private SSLServerSocketFactory	sslServerSocketFactory		= null;
	private ExecutorService			executorService				= null;

	private int						port						= 41191;
	private boolean					needsClientAuth				= false;
	private int						connectionPoolSize			= 1024;
	private ServerConnection		connectionImplementation	= null;

	public ServerConnector() {
		logger.trace("new");
	}

	@Override
	public void run() {
		logger.trace("run");

		logger.debug("using port: " + port);
		logger.debug("require clientAuth: " + needsClientAuth);
		logger.debug("using connection pool size: " + connectionPoolSize);

		executorService = Executors.newFixedThreadPool(this.connectionPoolSize);

		sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

		String[] supportedCipherSuites = sslServerSocketFactory.getSupportedCipherSuites();

		logger.debug("supported ciphersuites: " + Arrays.toString(supportedCipherSuites));


		try {
			sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port, this.connectionPoolSize);
			sslServerSocket.setEnabledCipherSuites(supportedCipherSuites);
			logger.info("server socket open");
		} catch (IOException e) {
			logger.error("cant open server socket", e);
			return;
		}
		sslServerSocket.setNeedClientAuth(needsClientAuth);

		while (!sslServerSocket.isClosed()) {
			SSLSocket sslSocket = null;
			try {
				sslSocket = (SSLSocket) sslServerSocket.accept();
				sslSocket.startHandshake();
				logger.debug("active ciphersuite: " + sslSocket.getSession().getCipherSuite());

				logger.info("got new agent connection");
			} catch (IOException e) {
				logger.error("accept failed", e);
				continue;
			}
			ServerConnection serverConnection = getConnectionImplementation();
			serverConnection.setSocket(sslSocket);
			executorService.execute(serverConnection);
		}

	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isNeedsClientAuth() {
		return needsClientAuth;
	}

	public void setNeedsClientAuth(boolean needsClientAuth) {
		this.needsClientAuth = needsClientAuth;
	}

	public int getConnectionPoolSize() {
		return connectionPoolSize;
	}

	public void setConnectionPoolSize(int connectionPoolSize) {
		this.connectionPoolSize = connectionPoolSize;
	}

	public ServerConnection getConnectionImplementation() {
		return connectionImplementation;
	}

	public void setConnectionImplementation(ServerConnection connectionImplementation) {
		this.connectionImplementation = connectionImplementation;
	}
}
