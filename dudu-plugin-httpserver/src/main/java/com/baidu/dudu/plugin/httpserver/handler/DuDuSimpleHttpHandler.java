package com.baidu.dudu.plugin.httpserver.handler;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.dudu.framework.handler.IncomingMessageHandler;
import com.baidu.dudu.framework.message.DuDuSessionMessage;

public class DuDuSimpleHttpHandler extends SimpleChannelUpstreamHandler {

	private static final Logger logger = LoggerFactory.getLogger(DuDuSimpleHttpHandler.class);

	private IncomingMessageHandler incomingMessageHandler;

	public IncomingMessageHandler getIncomingMessageHandler() {
		return incomingMessageHandler;
	}

	public void setIncomingMessageHandler(IncomingMessageHandler incomingMessageHandler) {
		this.incomingMessageHandler = incomingMessageHandler;
	}

	/**
	 * handle http request and give response
	 */
	public DuDuSessionMessage handle(final HttpRequest request, final HttpResponse response, final HttpContext context) throws HttpException, IOException {
		return null;

	}

}
