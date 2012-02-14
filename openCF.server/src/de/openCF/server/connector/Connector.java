package de.openCF.server.connector;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import org.apache.log4j.Logger;

public class Connector implements Runnable {

	private Logger				logger						= Logger.getLogger(Connector.class);
	private ServerSocket		serverSocket				= null;
	private ServerSocketFactory	serverSocketFactory			= null;
	private ExecutorService		executorService				= null;

	private int					port						= 41191;
	private boolean				needsClientAuth				= false;
	private boolean				useSSL						= false;
	private int					connectionPoolSize			= 1024;
	private Connection			connectionImplementation	= null;

	public Connector() {
		logger.trace("new");
	}

	@Override
	public void run() {
		logger.trace("run");

		logger.debug("using port: " + port);
		logger.debug("use ssl: " + useSSL);
		logger.debug("require clientAuth: " + needsClientAuth);
		logger.debug("using connection pool size: " + connectionPoolSize);

		executorService = Executors.newFixedThreadPool(this.connectionPoolSize);

		String[] supportedCipherSuites = null;

		if (useSSL) {
			SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			serverSocketFactory = sslServerSocketFactory;

			supportedCipherSuites = sslServerSocketFactory.getSupportedCipherSuites();

			logger.debug("supported ciphersuites: " + Arrays.toString(supportedCipherSuites));
		} else {
			serverSocketFactory = ServerSocketFactory.getDefault();
		}

		try {
			serverSocket = serverSocketFactory.createServerSocket(port, this.connectionPoolSize);
			logger.info("server socket open");
		} catch (IOException e) {
			logger.error("cant open server socket", e);
			return;
		}

		if (useSSL) {
			((SSLServerSocket) serverSocket).setNeedClientAuth(needsClientAuth);
			((SSLServerSocket) serverSocket).setEnabledCipherSuites(supportedCipherSuites);
		}

		while (!serverSocket.isClosed()) {
			Socket socket = null;
			try {
				socket = serverSocket.accept();
				if (useSSL) {
					SSLSocket sslSocket = (SSLSocket) socket;
					sslSocket.startHandshake();
					logger.debug("active ciphersuite: " + sslSocket.getSession().getCipherSuite());
				}

				logger.info("got new agent connection");
			} catch (IOException e) {
				logger.error("accept failed", e);
				continue;
			}
			Connection serverConnection = getConnectionImplementation();
			serverConnection.setSocket(socket);
			executorService.execute(serverConnection);
		}

	}

	public boolean isUseSSL() {
		return useSSL;
	}

	public void setUseSSL(boolean useSSL) {
		this.useSSL = useSSL;
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

	public Connection getConnectionImplementation() {
		return connectionImplementation;
	}

	public void setConnectionImplementation(Connection connectionImplementation) {
		this.connectionImplementation = connectionImplementation;
	}
}
