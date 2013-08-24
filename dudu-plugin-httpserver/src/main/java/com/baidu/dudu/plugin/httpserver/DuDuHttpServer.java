package com.baidu.dudu.plugin.httpserver;

import com.baidu.dudu.framework.handler.IncomingMessageHandler;
import com.baidu.dudu.framework.message.DuDuMessage;

public interface DuDuHttpServer {

	void setIncomingMessageHandler(IncomingMessageHandler incomingMessageHandler);

	void send(final DuDuMessage message, final Object sessionToken);

	void start() throws Exception;

	void stop();
}
