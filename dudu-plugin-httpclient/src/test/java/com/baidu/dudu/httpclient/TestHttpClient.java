package com.baidu.dudu.httpclient;

import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.apache.http.message.BasicStatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.dudu.framework.DuDuTest;
import com.baidu.dudu.framework.interaction.TestInteraction;
import com.baidu.dudu.framework.message.DuDuSessionMessage;
import com.baidu.dudu.framework.plugin.Plugin;
import com.baidu.dudu.network.http.message.response.DuDuHttpStringResponse;
import com.baidu.dudu.network.http.message.resquest.DuDuHttpRequest;
import com.baidu.dudu.network.http.message.resquest.DuDuHttpStringRequest;

public class TestHttpClient extends DuDuTest {

	private static final Logger logger = LoggerFactory.getLogger(TestHttpClient.class);

	Plugin httpclient;

	public void setUp(TestInteraction testInteraction) {
		logger.info("TestHttpClient setUp");
		httpclient = testInteraction.getPlugin("httpclient");
	}

	@Override
	public void testStart(TestInteraction testInteraction) {
		logger.info("TestPing testStart ");

		// create request
		DuDuHttpRequest httpRequest = new DuDuHttpStringRequest();
		httpRequest.setMethod(HttpGet.METHOD_NAME);
		httpRequest.setUrl("http://www.baidu.com");
//		httpRequest.setUrl("http://localhost:8080");
		// send request
		httpclient.send(httpRequest);

		// create response
		DuDuHttpStringResponse duduHttpStringResponse = null;
		
		BasicStatusLine statusLine = new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_ACCEPTED, EnglishReasonPhraseCatalog.INSTANCE.getReason(HttpStatus.SC_OK, null));
		duduHttpStringResponse = new DuDuHttpStringResponse(statusLine);

		// receive response
		DuDuSessionMessage sessionMessage = httpclient.receive(duduHttpStringResponse);
		duduHttpStringResponse = (DuDuHttpStringResponse) sessionMessage.getMessage();
		logger.info(duduHttpStringResponse.getStatusLine().toString());
		logger.info(duduHttpStringResponse.getContent());
	}

	@Override
	public void tearDown(TestInteraction testInteraction) {
		logger.info("TestPing tearDown ");
	}

}
