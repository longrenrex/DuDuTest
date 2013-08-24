package com.baidu.dudu.test.scripts;

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

public class TestJunitHttp extends DuDuTest {

	private static final Logger logger = LoggerFactory.getLogger(TestJunitHttp.class);

	Plugin httpServer;
	
	Plugin httpClient;

	@Override
	public void setUp(TestInteraction testInteraction) {
		logger.info("TestHttpServer setUp");
		httpServer = testInteraction.getPlugin("httpserver");
		httpClient = testInteraction.getPlugin("httpclient");
	}

	@Override
	public void testStart(TestInteraction testInteraction) {
		logger.info("TestHttp testStart ");
		
		
		// create request
		DuDuHttpRequest clientHttpRequest = new DuDuHttpStringRequest();
		clientHttpRequest.setMethod(HttpGet.METHOD_NAME);
		clientHttpRequest.setUrl("http://localhost:8080");
		// send request
		httpClient.send(clientHttpRequest);
		
		// create request
		DuDuHttpRequest httpRequest = new DuDuHttpStringRequest();
		httpRequest.setMethod(HttpGet.METHOD_NAME);
		httpRequest.setUri("/");
		
		//server receive http request
		DuDuSessionMessage sessionMessage = httpServer.receive(httpRequest);
	
		//create server http reponse
		BasicStatusLine statusLine = new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, EnglishReasonPhraseCatalog.INSTANCE.getReason(HttpStatus.SC_OK, null));
		DuDuHttpStringResponse duduHttpStringResponse = new DuDuHttpStringResponse(statusLine);
		duduHttpStringResponse.addHeader(new BasicHeader(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8"));
		duduHttpStringResponse.setContent("It is just a test.\r\n\r\n");
		
    	//server send http response
		httpServer.send(duduHttpStringResponse, sessionMessage);

		//Client receive response
		sessionMessage = httpClient.receive(duduHttpStringResponse);
		duduHttpStringResponse = (DuDuHttpStringResponse) sessionMessage.getMessage();
		logger.info(duduHttpStringResponse.getStatusLine().toString());
		logger.info(duduHttpStringResponse.getContent());
	}

	@Override
	public void tearDown(TestInteraction testInteraction) {
		// TODO Auto-generated method stub

	}

}
