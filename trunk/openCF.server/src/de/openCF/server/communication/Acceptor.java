package de.openCF.server.communication;

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

import de.openCF.protocol.Connection;
import de.openCF.protocol.ConnectionFactory;

public class Acceptor implements Runnable {

	private Logger				logger				= Logger.getLogger(Acceptor.class);
	private ServerSocket		serverSocket		= null;
	private ServerSocketFactory	serverSocketFactory	= null;
	private ExecutorService		executorService		= null;

	private int					port				= 41191;
	private boolean				needsClientAuth		= false;
	private boolean				useSSL				= false;
	private int					connectionPoolSize	= 1024;
	private ConnectionFactory	connectionFactory	= null;

	public Acceptor() {
		logger.trace("new");
	}

	@Override
	public void run() {
		logger.trace("run");

		logger.info("using port: " + port);
		logger.info("use ssl: " + useSSL);
		logger.info("require clientAuth: " + needsClientAuth);
		logger.info("using connection pool size: " + connectionPoolSize);
		logger.info("using connection factory: " + connectionFactory.getClass());

		executorService = Executors.newFixedThreadPool(this.connectionPoolSize);

		String[] supportedCipherSuites = null;

		if (useSSL) {
			logger.info("creating ssl session factory");
			SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			serverSocketFactory = sslServerSocketFactory;

			supportedCipherSuites = sslServerSocketFactory.getSupportedCipherSuites();

			logger.info("supported ciphersuites: " + Arrays.toString(supportedCipherSuites));
		} else {
			logger.info("creating session factory");
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
				logger.info("waiting for new incomming connection");
				socket = serverSocket.accept();
				if (useSSL) {
					SSLSocket sslSocket = (SSLSocket) socket;
					logger.info("starting ssl handshake");
					sslSocket.startHandshake();
					logger.info("active ciphersuite: " + sslSocket.getSession().getCipherSuite());
				}

				logger.info("got new connection: " + socket.getLocalPort() + " --> " + socket.getRemoteSocketAddress());
			} catch (IOException e) {
				logger.error("accept failed", e);
				continue;
			}

			Connection serverConnection = this.connectionFactory.createConnection();
			serverConnection.setSocket(socket);

			logger.info("running worker for connection");
			executorService.execute(serverConnection);
		}

	}

	public boolean useSSL() {
		logger.trace("useSSL");
		return useSSL;
	}

	public void setUseSSL(boolean useSSL) {
		logger.trace("setUseSSL(boolean)");
		this.useSSL = useSSL;
		logger.debug("useSSL set to: " + useSSL);
	}

	public int getPort() {
		logger.trace("getPort");
		return port;
	}

	public void setPort(int port) {
		logger.trace("setPort(int)");
		this.port = port;
		logger.debug("port set to: " + port);
	}

	public boolean needsClientAuth() {
		logger.trace("needsClientAuth");
		return needsClientAuth;
	}

	public void setNeedsClientAuth(boolean needsClientAuth) {
		logger.trace("setNeedsClientAuth(boolean)");
		this.needsClientAuth = needsClientAuth;
		logger.debug("needsClientAuth set to: " + needsClientAuth);
	}

	public int getConnectionPoolSize() {
		logger.trace("getConnectionPoolSize");
		return connectionPoolSize;
	}

	public void setConnectionPoolSize(int connectionPoolSize) {
		logger.trace("setConnectionPoolSize(int)");
		this.connectionPoolSize = connectionPoolSize;
		logger.debug("connectionPoolSize set to: " + connectionPoolSize);
	}

	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		logger.trace("setConnectionFactory(ConnectionFactor)");
		this.connectionFactory = connectionFactory;
	}
}
