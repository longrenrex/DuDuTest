package com.baidu.dudu.network.io.client;


import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.dudu.network.io.TcpSocketConnection;

/**
 * @author rzhao
 */
public abstract class TcpClient {

    private final static Logger logger = LoggerFactory.getLogger(TcpClient.class);

    protected String hostname;

    protected int port;

    protected TcpSocketConnection connection;

    public TcpClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        this.connection = null;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public TcpSocketConnection getConnection() {
        try {
            assertConnected();
        }
        catch (IOException e) {
            logger.error("Error while creating connection towards " + hostname + ":" + port, e);
        }
        return connection;
    }

    public void send(byte[] message) throws IOException {
        assertConnected();
        connection.send(message);
    }

    /* Synchronized only on first part in method for optimization? */
    protected synchronized void assertConnected() throws IOException {
        if ((connection == null) || (connection.isConnected() == false)) {
            if (connection != null)
                connection.shutdown();

            connection = createSocketConnection(createSocket());

            // TODO: Thread pool or non-blocking IO
            new Thread() {

                public void run() {
                    connection.receive();
                }
            }.start();
        }
    }

    protected Socket createSocket() throws IOException {
        logger.info("Connecting towards " + hostname + ":" + port);
        return new Socket(hostname, port);
    }

    protected abstract TcpSocketConnection createSocketConnection(Socket socket) throws IOException;

    public synchronized void shutdown() throws IOException {
        logger.info("Shutdown of connection towards " + getHostname() + ":" + getPort());
        try {
            if (connection != null) {
                connection.shutdown();
            }
        }
        finally {
            connection = null;
        }
    }

}