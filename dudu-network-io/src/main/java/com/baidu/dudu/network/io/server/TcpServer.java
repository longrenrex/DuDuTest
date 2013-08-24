package com.baidu.dudu.network.io.server;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.dudu.network.io.TcpSocketConnection;

/**
 * @author rzhao
 */
public abstract class TcpServer implements Runnable {

	private final static Logger logger = LoggerFactory
			.getLogger(TcpServer.class);

	protected String hostname;

	protected int port;

	protected ServerSocket serverSocket;

	protected CopyOnWriteArrayList<TcpSocketConnection> openConnections;

	private boolean isRunning;

	public TcpServer(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
		this.serverSocket = null;
		this.openConnections = new CopyOnWriteArrayList<TcpSocketConnection>();
		this.isRunning = false;
	}

	public String getHostname() {
		return hostname;
	}

	public int getPort() {
		return port;
	}

	public void start() throws IOException {
		if (serverSocket != null)
			return;

		if (logger.isDebugEnabled())
			logger.debug("Creating TcpServer");
		serverSocket = new ServerSocket();
		serverSocket.setReuseAddress(true);

		if (logger.isDebugEnabled())
			logger.debug("Starting TcpServer on " + getHostname() + ":"
					+ getPort());
		SocketAddress endpoint = new InetSocketAddress(
				InetAddress.getByName(getHostname()), getPort());
		serverSocket.bind(endpoint);

		isRunning = true;
		new Thread(this).start();
	}

	public void stop() throws IOException {
		logger.info("Stopping TcpServer");
		isRunning = false;
		try {
			teardownConnections();
		} finally {
			try {
				serverSocket.close();
			} finally {
				serverSocket = null;
			}
		}
	}

	public boolean isRunning() {
		return isRunning;
	}

	/*
	 * Runnable interface
	 */
	public final void run() {
		try {
			logger.info("Starting TcpServer");
			while (isRunning) {
				try {
					Socket socket = serverSocket.accept();
					logger.info("Incomming connection to TcpServer");
					TcpSocketConnection connection = createSocketConnection(socket);
					if (connection != null) {
						// TODO: Thread pool or non-blocking IO
						// new Thread(ThreadHelpers.assignExceptionHandler(
						// new ConnectionReceiverExecutor(connection),
						// new IRHUncaughtExceptionHandler())).start();

						Thread thread = new Thread(
								new ConnectionReceiverExecutor(connection));
						thread.setUncaughtExceptionHandler(new IRHUncaughtExceptionHandler());
						thread.start();
					}
				} catch (IOException e) {
					if (isRunning) {
						logger.warn(
								"Error while accepting incomming connection", e);
					} else {
						// Ignore IOException on shutdown
					}
				}
			}
		} finally {
			isRunning = false;
		}
	}

	protected abstract TcpSocketConnection createSocketConnection(Socket socket)
			throws IOException;

	private void teardownConnections() {
		for (TcpSocketConnection connection : openConnections) {
			try {
				if (connection != null) {
					connection.shutdown();
				}
			} catch (Exception ignored) {
			}
		}

		openConnections.clear();
	}

	private class ConnectionReceiverExecutor implements Runnable {

		private TcpSocketConnection connection;

		public ConnectionReceiverExecutor(TcpSocketConnection connection) {
			this.connection = connection;
		}

		public void run() {
			openConnections.add(connection);
			try {
				connection.receive();
			} finally {
				try {
					// Always tear down connections if receive fails.
					connection.shutdown();
				} catch (IOException e) {
					logger.warn(
							"Error while shutting down incomming connection after receive failed",
							e);
				} finally {
					openConnections.remove(connection);
				}
			}
		}
	}

	public static class IRHUncaughtExceptionHandler implements
			UncaughtExceptionHandler {

		private final static Logger logger = LoggerFactory
				.getLogger(IRHUncaughtExceptionHandler.class);

		private static ThreadLocal logExceptions = new ThreadLocal() {

			protected synchronized Object initialValue() {
				return new Boolean(true);
			}
		};

		public void uncaughtException(Thread t, Throwable e) {
			if (((Boolean) logExceptions.get()).booleanValue()) {
				System.err.println("UNCAUGHT EXCEPTION by " + t.getName());
				e.printStackTrace();
			}
		}

		/**
		 * With this function logging of exceptions can be turned on and off.
		 * Use it with care.
		 * 
		 * @param logExceptions
		 */
		public static void setLogExceptions(boolean logExceptions) {
			if (logger.isDebugEnabled()) {
				logger.debug("Setting logExceptions in thread "
						+ Thread.currentThread().getName() + " to "
						+ logExceptions + ".");
			}
			IRHUncaughtExceptionHandler.logExceptions.set(new Boolean(
					logExceptions));
		}
	}

}
