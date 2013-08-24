package com.baidu.dudu.network.io;

import java.io.IOException;
import java.net.Socket;

/**
 * @author rzhao
 */
public interface TcpSocketConnectionFactory {
	
	TcpSocketConnection createSocketConnection(Socket socket) throws IOException;
	
}