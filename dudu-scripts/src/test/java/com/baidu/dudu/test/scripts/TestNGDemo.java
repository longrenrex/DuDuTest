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
import org.testng.annotations.Test;

import com.baidu.dudu.framework.DuDuTest2;
import com.baidu.dudu.framework.interaction.TestInteraction;
import com.baidu.dudu.framework.message.DuDuSessionMessage;
import com.baidu.dudu.framework.plugin.Plugin;
import com.baidu.dudu.network.http.message.response.DuDuHttpStringResponse;
import com.baidu.dudu.network.http.message.resquest.DuDuHttpRequest;
import com.baidu.dudu.network.http.message.resquest.DuDuHttpStringRequest;
import com.baidu.dudu.plugin.database.message.request.DuDuCountReq;
import com.baidu.dudu.plugin.database.message.response.DuDuCountResp;

public class TestNGDemo extends DuDuTest2 {

	private static final Logger logger = LoggerFactory.getLogger(TestNGDemo.class);

	Plugin httpServer;
	
	Plugin httpClient;
	
	Plugin dbClient;

	@Override
	public void setUp(TestInteraction testInteraction) {
		logger.info("TestHttpServer setUp");
		httpServer = testInteraction.getPlugin("httpserver");
		httpClient = testInteraction.getPlugin("httpclient"); 
		dbClient = testInteraction.getPlugin("database");
	}

	@Test
	public void testStart() {
		logger.info("TestHttp testStart ");
		interaciton.beforeTest(interaciton);
		
		// create request
		DuDuHttpRequest clientHttpRequest = new DuDuHttpStringRequest();
		clientHttpRequest.setMethod(HttpGet.METHOD_NAME);
		clientHttpRequest.setUrl("http://localhost:8080");
		
		toJson(clientHttpRequest);
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
		duduHttpStringResponse.setContent("It is just a test.");
		
    	//server send http response
		httpServer.send(duduHttpStringResponse, sessionMessage);
		
		
		 DuDuCountReq duduCountReq = new DuDuCountReq();
		 duduCountReq.setSql("select count(*) from city");
		 dbClient.send(duduCountReq);
		
		 DuDuCountResp resp = new DuDuCountResp();
		 resp.setCount(Integer.valueOf(600));
		 dbClient.receive(resp);

		//Client receive response
		sessionMessage = httpClient.receive(duduHttpStringResponse);
		duduHttpStringResponse = (DuDuHttpStringResponse) sessionMessage.getMessage();
		logger.info(duduHttpStringResponse.getStatusLine().toString());
		logger.info(duduHttpStringResponse.getContent());
		
		interaciton.afterTest(interaciton);
	}

	@Override
	public void tearDown(TestInteraction testInteraction) {
		// TODO Auto-generated method stub

	}

}
