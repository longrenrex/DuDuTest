package com.baidu.dudu.plugin.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.util.CharsetUtil;

import com.baidu.dudu.network.http.message.resquest.DuDuHttpStringRequest;

public class HttpMessageUtil {

	/**
	 * convert org.jboss.netty.handler.codec.http.HttpRequest to
	 * com.baidu.dudu.network.http.message.resquest.DuDuHttpStringRequest
	 * 
	 * @param nettyHttpRequest
	 * @return
	 */
	public static com.baidu.dudu.network.http.message.resquest.DuDuHttpStringRequest convert(org.jboss.netty.handler.codec.http.HttpRequest nettyHttpRequest) {

		DuDuHttpStringRequest httpStringRequest = new DuDuHttpStringRequest();
		httpStringRequest.setMethod(nettyHttpRequest.getMethod().getName());
		httpStringRequest.setUri(nettyHttpRequest.getUri());

		// http header
		List<Map.Entry<String, String>> headList = nettyHttpRequest.getHeaders();
		if (!headList.isEmpty()) {
			List<Header> list = new ArrayList<Header>();

			for (Entry<String, String> entry : headList) {
				Header header = new BasicHeader(entry.getKey(), entry.getValue() + "\r\n");
				list.add(header);
			}

			httpStringRequest.setHeaders(list.toArray(new Header[headList.size()]));
		}

		// http body
		httpStringRequest.setBodyContent(nettyHttpRequest.getContent().toString(CharsetUtil.UTF_8));

		return httpStringRequest;
	}

	/**
	 * 
	 * convert com.baidu.dudu.network.http.message.response.DuDuHttpStringResponse to org.jboss.netty.handler.codec.http.DefaultHttpResponse
	 * 
	 * @param duduHttpStringResponse
	 * @return
	 */
	public static org.jboss.netty.handler.codec.http.DefaultHttpResponse convert(com.baidu.dudu.network.http.message.response.DuDuHttpStringResponse duduHttpStringResponse) {
		if (duduHttpStringResponse == null) {
			return null;
		}
		
		// set http repsonse status
		HttpResponseStatus httpResponseStatus = new HttpResponseStatus(duduHttpStringResponse.getStatusLine().getStatusCode(), duduHttpStringResponse.getStatusLine().getReasonPhrase());
		DefaultHttpResponse httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, httpResponseStatus);

		//set header
		List<Header> headerList = duduHttpStringResponse.getHeaderList();
		if (headerList != null) {
			for (Header h : headerList) {
				httpResponse.addHeader(h.getName(), h.getValue());
			}
		}

		// set body content
		httpResponse.setContent(ChannelBuffers.copiedBuffer(duduHttpStringResponse.getContent(), CharsetUtil.UTF_8));

		return httpResponse;
	}
}
