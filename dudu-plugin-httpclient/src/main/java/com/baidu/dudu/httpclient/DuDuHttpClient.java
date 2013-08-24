package com.baidu.dudu.httpclient;

import com.baidu.dudu.framework.handler.IncomingMessageHandler;
import com.baidu.dudu.network.http.message.resquest.DuDuHttpRequest;

public interface DuDuHttpClient {

	void send(final DuDuHttpRequest request, Object sessionToken);

	void setIncomingMessageHandler(IncomingMessageHandler incomingMessageHandler);
}
