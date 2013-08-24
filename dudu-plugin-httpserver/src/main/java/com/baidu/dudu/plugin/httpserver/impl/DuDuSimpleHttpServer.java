package com.baidu.dudu.plugin.httpserver.impl;

import javax.net.ssl.SSLContext;

import org.apache.http.protocol.HttpRequestHandler;

import com.baidu.dudu.framework.handler.IncomingMessageHandler;
import com.baidu.dudu.framework.message.DuDuMessage;
import com.baidu.dudu.network.http.Authenticator;
import com.baidu.dudu.network.http.HttpServer;
import com.baidu.dudu.plugin.httpserver.DuDuHttpServer;
import com.baidu.dudu.plugin.httpserver.handler.DuDuSimpleHttpHandler;

public class DuDuSimpleHttpServer extends HttpServer implements DuDuHttpServer {

	private DuDuSimpleHttpHandler httpRequestHandler;

	public DuDuSimpleHttpServer(String host, int port, boolean secure, SSLContext sslContext, boolean blocking, int threadPoolSize, HttpRequestHandler handler, Authenticator authenticator, int soTimeout, int soBufferSizeInBytes) throws Exception {
		super(host, port, secure, sslContext, blocking, threadPoolSize, handler, authenticator, soTimeout, soBufferSizeInBytes);
		// TODO Auto-generated constructor stub
	}

	public void start() throws Exception {
		super.start();
	}

	public void stop() {
		super.stop();
	}

	public void setIncomingMessageHandler(IncomingMessageHandler incomingMessageHandler) {
		httpRequestHandler.setIncomingMessageHandler(incomingMessageHandler);
	}

	@Override
	public void send(DuDuMessage message, Object sessionToken) {
		// TODO Auto-generated method stub
		
	}
}
