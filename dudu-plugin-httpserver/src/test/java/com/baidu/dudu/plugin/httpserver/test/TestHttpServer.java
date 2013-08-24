package com.baidu.dudu.plugin.httpserver.test;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.apache.http.message.BasicHeader;
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

public class TestHttpServer extends DuDuTest {

	private static final Logger logger = LoggerFactory.getLogger(TestHttpServer.class);

	Plugin httpServer;

	@Override
	public void setUp(TestInteraction testInteraction) {
		logger.info("TestHttpServer setUp");
		httpServer = testInteraction.getPlugin("httpserver");
	}

	@Override
	public void testStart(TestInteraction testInteraction) {
		logger.info("TestHttpServer testStart ");
		// create request
		DuDuHttpRequest httpRequest = new DuDuHttpStringRequest();
		httpRequest.setMethod(HttpGet.METHOD_NAME);
		httpRequest.setUri("/");

		//receive http request
		DuDuSessionMessage sessionMessage = httpServer.receive(httpRequest);
		
		BasicStatusLine statusLine = new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, EnglishReasonPhraseCatalog.INSTANCE.getReason(HttpStatus.SC_OK, null));
		DuDuHttpStringResponse httpResponse = new DuDuHttpStringResponse(statusLine);
		httpResponse.addHeader(new BasicHeader(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8"));
		httpResponse.setContent("It is just a test.");
		
    	//send http response
		httpServer.send(httpResponse, sessionMessage);
		
		
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void tearDown(TestInteraction testInteraction) {
		// TODO Auto-generated method stub

	}

}
