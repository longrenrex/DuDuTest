package com.baidu.dudu.network.io.client;


import java.io.IOException;
import java.net.Socket;

import com.baidu.dudu.network.io.TcpSocketConnection;
import com.baidu.dudu.network.io.TcpSocketConnectionFactory;

/**
 * @author rzhao
 */
public class FactoryTcpClient extends TcpClient {

    private TcpSocketConnectionFactory connectionFactory;

    public FactoryTcpClient(String hostname, int port, TcpSocketConnectionFactory connectionFactory) {
        super(hostname, port);
        this.connectionFactory = connectionFactory;
    }

    protected TcpSocketConnection createSocketConnection(Socket socket) throws IOException {
        return connectionFactory.createSocketConnection(socket);
    }

}
