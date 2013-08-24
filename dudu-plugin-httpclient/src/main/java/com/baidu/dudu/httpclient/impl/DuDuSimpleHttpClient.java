package com.baidu.dudu.httpclient.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.dudu.framework.exception.DuDuException;
import com.baidu.dudu.framework.handler.IncomingMessageHandler;
import com.baidu.dudu.framework.message.DuDuMessage;
import com.baidu.dudu.framework.message.DuDuSessionMessage;
import com.baidu.dudu.httpclient.DuDuHttpClient;
import com.baidu.dudu.network.http.message.response.DuDuHttpStringResponse;
import com.baidu.dudu.network.http.message.resquest.DuDuHttpByteArrayRequest;
import com.baidu.dudu.network.http.message.resquest.DuDuHttpRequest;
import com.baidu.dudu.network.http.message.resquest.DuDuHttpStringRequest;

/**
 * @author rzhao
 */
public class DuDuSimpleHttpClient implements DuDuHttpClient {

	private static Logger logger = LoggerFactory.getLogger(DuDuSimpleHttpClient.class);

	private IncomingMessageHandler incomingMessageHandler;

	private static int timeout = 12000;

	public void setTestInteraction() {
	}

	public void setIncomingMessageHandler(IncomingMessageHandler incomingMessageHandler) {
		this.incomingMessageHandler = incomingMessageHandler;
	}

	public void send(final DuDuHttpRequest request, Object sessionToken) {

		new Thread() {
			public void run() {
				try {
					if (HttpGet.METHOD_NAME.equalsIgnoreCase(request.getMethod())) {
						doGet(request);
						return;
					}

					if (HttpPost.METHOD_NAME.equalsIgnoreCase(request.getMethod())) {
						doPost(request);
						return;
					}

					if (HttpDelete.METHOD_NAME.equalsIgnoreCase(request.getMethod())) {
						doDelete(request);
						return;
					}

					if (HttpPut.METHOD_NAME.equalsIgnoreCase(request.getMethod())) {
						doPut(request);
						return;
					}

					throw new DuDuException("Unknown HttpRequest method.");

				} catch (Exception e) {
					logger.error("Send HttpRequest error!", e);
					throw new DuDuException("Send HttpRequest error!", e);
				}
			}
		}.start();
	}

	public void receive(DuDuMessage msg) {

	}

	/**
	 * 
	 * @param request
	 * @throws HttpException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public void doGet(final DuDuHttpRequest request) throws HttpException, IOException, URISyntaxException {
		final HttpClient httpClient = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), timeout);

		// create HttpGet
		HttpGet httpGet = null;
		if (request.getUrl() == null) {
			httpGet = new HttpGet(buildUri(request));
		} else {
			httpGet = new HttpGet(request.getUrl());
		}
		// set headers
		httpGet.setHeaders(request.getHeaders());

		// execute
		HttpResponse httpResponse = httpClient.execute(httpGet);
		// create response
		buildResponse(httpResponse);
	}

	/**
	 * 
	 * @param request
	 * @throws URISyntaxException
	 * @throws HttpException
	 * @throws IOException
	 */
	public void doPost(final DuDuHttpRequest request) throws URISyntaxException, HttpException, IOException {

		final HttpClient httpClient = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), timeout);

		// create HttpPost
		HttpPost httpPost = null;
		if (request.getUrl() == null) {
			httpPost = new HttpPost(buildUri(request));
		} else {
			httpPost = new HttpPost(request.getUrl());
		}
		// set headers
		httpPost.setHeaders(request.getHeaders());
		// set body
		if (request instanceof DuDuHttpStringRequest) {
			DuDuHttpStringRequest duduHttpStringRequest = (DuDuHttpStringRequest) request;
			StringEntity entity = new StringEntity(duduHttpStringRequest.getBodyContent(), "UTF-8");
			httpPost.setEntity(entity);
		} else {
			DuDuHttpByteArrayRequest duduHttpByteArrayRequest = (DuDuHttpByteArrayRequest) request;
			ByteArrayEntity entity = new ByteArrayEntity(duduHttpByteArrayRequest.getBodyContent());
			httpPost.setEntity(entity);
		}

		httpPost.getParams().setBooleanParameter("http.protocol.expect-continue", false);

		// execute
		HttpResponse httpResponse = httpClient.execute(httpPost);
		// create response
		buildResponse(httpResponse);
	}

	/**
	 * 
	 * @param request
	 * @throws URISyntaxException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public void doPut(final DuDuHttpRequest request) throws URISyntaxException, ClientProtocolException, IOException {
		final HttpClient httpClient = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), timeout);

		// create HttpPut
		HttpPut httpPut = null;
		if (request.getUrl() == null) {
			httpPut = new HttpPut(buildUri(request));
		} else {
			httpPut = new HttpPut(request.getUrl());
		}
		// set headers
		httpPut.setHeaders(request.getHeaders());
		// set body
		if (request instanceof DuDuHttpStringRequest) {
			DuDuHttpStringRequest duduHttpStringRequest = (DuDuHttpStringRequest) request;
			StringEntity entity = new StringEntity(duduHttpStringRequest.getBodyContent(), "UTF-8");
			httpPut.setEntity(entity);
		} else {
			DuDuHttpByteArrayRequest duduHttpByteArrayRequest = (DuDuHttpByteArrayRequest) request;
			ByteArrayEntity entity = new ByteArrayEntity(duduHttpByteArrayRequest.getBodyContent());
			httpPut.setEntity(entity);
		}

		httpPut.getParams().setBooleanParameter("http.protocol.expect-continue", false);

		// execute
		HttpResponse httpResponse = httpClient.execute(httpPut);
		// create response
		buildResponse(httpResponse);
	}

	/**
	 * 
	 * @param request
	 * @throws HttpException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public void doDelete(final DuDuHttpRequest request) throws HttpException, IOException, URISyntaxException {
		final HttpClient httpClient = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), timeout);

		// create HttpDelete
		HttpDelete httpDelete = null;
		if (request.getUrl() == null) {
			httpDelete = new HttpDelete(buildUri(request));
		} else {
			httpDelete = new HttpDelete(request.getUrl());
		}
		// set headers
		httpDelete.setHeaders(request.getHeaders());
		// execute
		HttpResponse httpResponse = httpClient.execute(httpDelete);
		// create response
		buildResponse(httpResponse);
	}

	/**
	 * 
	 * @param httpResponse
	 * @throws ParseException
	 * @throws IOException
	 */
	private void buildResponse(final HttpResponse httpResponse) throws ParseException, IOException {
		// create response
		DuDuHttpStringResponse response = new DuDuHttpStringResponse(httpResponse.getStatusLine());

		// covert BufferedHeader to BasiceHeader, WTF why apache http client designs two Header types ?
		Header[] headers = httpResponse.getAllHeaders();
		List<Header> headerList = new ArrayList<Header>();
		for (Header header : headers) {
			headerList.add(new BasicHeader(header.getName(), header.getValue()));
		}
		response.setHeaderList(headerList);
		
		// set response body
		HttpEntity entity = httpResponse.getEntity();
		response.setContent(EntityUtils.toString(entity));

		// convert to DuDuSessionMessage
		DuDuSessionMessage sessionMessage = new DuDuSessionMessage(response);
		incomingMessageHandler.incomingMessage(sessionMessage);
	}

	/**
	 * 
	 * @param request
	 * @return
	 * @throws URISyntaxException
	 */
	private static URI buildUri(final DuDuHttpRequest request) throws URISyntaxException {
		URIBuilder builder = new URIBuilder();
		if (request.getIsSecure()) {
			builder.setScheme("https");
		} else {
			builder.setScheme("http");
		}
		builder.setHost(request.getHost()).setPath(request.getUri());

		if (request.getParamsMap() != null) {
			for (Map.Entry<String, String> entry : request.getParamsMap().entrySet()) {
				builder.setParameter(entry.getKey(), entry.getValue());
			}
		}
		return builder.build();
	}
}
