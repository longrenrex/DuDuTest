package com.baidu.dudu.plugin.httpserver.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.dudu.framework.exception.DuDuException;
import com.baidu.dudu.framework.handler.IncomingMessageHandler;
import com.baidu.dudu.framework.message.DuDuMessage;
import com.baidu.dudu.framework.message.DuDuSessionMessage;
import com.baidu.dudu.network.netty.NettyServer;
import com.baidu.dudu.plugin.httpserver.DuDuHttpServer;
import com.baidu.dudu.plugin.httpserver.netty.HttpDefaultServerPipelineFactory;

public class DuDuNettyHttpServer extends NettyServer implements DuDuHttpServer {

	private static Logger logger = LoggerFactory.getLogger(NettyServer.class);

	public DuDuNettyHttpServer(int port) {
		super(port);

	}

	@Override
	public void setIncomingMessageHandler(IncomingMessageHandler incomingMessageHandler) {
		this.setChannelPipelineFactory(new HttpDefaultServerPipelineFactory(incomingMessageHandler));
	}

	@Override
	public void start() throws Exception {
		this.run();
	}

	@Override
	public void stop() {
		super.stop();
	}

	@Override
	public void send(DuDuMessage message, Object sessionToken) {
		DuDuSessionMessage sessionMessage = (DuDuSessionMessage) sessionToken;
		try {
			sessionMessage.sendingMessage(message);
		} catch (InterruptedException e) {
			final String errorInfo = "HTTP Server sends message error!";
			logger.error(errorInfo, e);
			throw new DuDuException(errorInfo, e);
		}
	}

}
