package com.baidu.dudu.httpclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.dudu.framework.command.DuDuCommand;
import com.baidu.dudu.framework.exception.DuDuException;
import com.baidu.dudu.framework.handler.IncomingMessageHandler;
import com.baidu.dudu.framework.interaction.TestInteraction;
import com.baidu.dudu.framework.message.DuDuMessage;
import com.baidu.dudu.framework.message.DuDuSessionMessage;
import com.baidu.dudu.framework.plugin.Plugin;
import com.baidu.dudu.network.http.message.resquest.DuDuHttpRequest;

public class HttpClientPlugin implements Plugin {

	private static Logger logger = LoggerFactory.getLogger(HttpClientPlugin.class);
	private TestInteraction testInteraction;
	private IncomingMessageHandler incomingMessageHandler;
	private DuDuHttpClient httpClient;

	public void send(DuDuMessage message) {
		send(message, null);
	}

	public void send(DuDuMessage message, Object sessionToken) {
		if (message == null) {
			throw new AssertionError("DuDuMessage is null.");
		}

		logger.info(" ------> DuDuHttpClient is sending : {} message------>", message.getClass());
		if (message instanceof DuDuHttpRequest) {
			DuDuHttpRequest request = (DuDuHttpRequest) message;
			httpClient.send(request, sessionToken);
		} else {
			throw new DuDuException("Only DuDuHttpRequest are accepted by DuDuHttpClient!");
		}
	}

	public DuDuSessionMessage receive(DuDuMessage message) {
		if (message == null) {
			throw new AssertionError("DuDuMessage is null.");
		}

		return incomingMessageHandler.receive(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dudu.free.framework.plugin.Plugin#recevie()
	 */
	public DuDuSessionMessage receive() {
		return incomingMessageHandler.receive();
	}

	public void start() {
		incomingMessageHandler.setTestInteraction(testInteraction);
		httpClient.setIncomingMessageHandler(incomingMessageHandler);
	}

	public void stop() {
		// TODO Auto-generated method stub
	}

	public DuDuHttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(DuDuHttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public void setIncomingMessageHandler(IncomingMessageHandler incomingMessageHandler) {
		this.incomingMessageHandler = incomingMessageHandler;
	}

	public void setTestInteraction(TestInteraction testInteraction) {
		this.testInteraction = testInteraction;
	}

	public void execute(DuDuCommand command) {
		// TODO Auto-generated method stub

	}

}
