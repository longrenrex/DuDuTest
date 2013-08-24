package com.baidu.dudu.plugin.httpserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.dudu.framework.command.DuDuCommand;
import com.baidu.dudu.framework.exception.DuDuException;
import com.baidu.dudu.framework.handler.IncomingMessageHandler;
import com.baidu.dudu.framework.interaction.TestInteraction;
import com.baidu.dudu.framework.message.DuDuMessage;
import com.baidu.dudu.framework.message.DuDuSessionMessage;
import com.baidu.dudu.framework.plugin.Plugin;

public class HttpServerPlugin implements Plugin {

	private static Logger logger = LoggerFactory.getLogger(HttpServerPlugin.class);

	private TestInteraction testInteraction;
	private IncomingMessageHandler incomingMessageHandler;

	private DuDuHttpServer httpServer;

	public void start() {
		logger.info("HTTP server start...");
		incomingMessageHandler.setTestInteraction(testInteraction);
		httpServer.setIncomingMessageHandler(incomingMessageHandler);

		try {
			httpServer.start();
		} catch (Exception e) {
			logger.error("start HTTP Server error!", e);
			throw new DuDuException("start HTTP Server error!", e);
		}
	}

	public void send(DuDuMessage message) {
		send(message, null);
	}

	public void send(DuDuMessage message, Object sessionToken) {
		if (message == null) {
			throw new AssertionError("DuDuMessage is null.");
		}
		logger.info("------> HTTP server sends {} message------>", message.getClass());

		httpServer.send(message, sessionToken);
	}

	public void execute(DuDuCommand command) {
		// TODO Auto-generated method stub

	}

	public DuDuSessionMessage receive() {
		return incomingMessageHandler.receive();
	}

	public DuDuSessionMessage receive(DuDuMessage message) {
		return incomingMessageHandler.receive(message);
	}

	public void stop() {
		httpServer.stop();
	}

	public void setIncomingMessageHandler(IncomingMessageHandler handler) {
		this.incomingMessageHandler = handler;
	}

	public void setTestInteraction(TestInteraction testInteraction) {
		this.testInteraction = testInteraction;
	}

	public void setHttpServer(DuDuHttpServer httpServer) {
		this.httpServer = httpServer;
	}

}
