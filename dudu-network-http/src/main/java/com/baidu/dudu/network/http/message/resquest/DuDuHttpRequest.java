package com.baidu.dudu.network.http.message.resquest;

import java.util.Map;

import org.apache.http.Header;

import com.baidu.dudu.framework.message.DuDuMessage;

/**
 * @author rzhao
 */
public abstract class DuDuHttpRequest implements DuDuMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6336644684665658905L;
	protected String method;

	// url
	protected String url;

	// if url == null
	protected String host;
	protected Integer port;
	protected String uri;

	protected Map<String, String> paramsMap;

	// http or https
	protected Boolean isSecure;

	protected Header[] headers;
	
	protected Boolean isfollowRedirect;

	public Header[] getHeaders() {
		return headers;
	}

	public void setHeaders(Header[] headers) {
		this.headers = headers;
	}

	public Boolean getIsfollowRedirect() {
		return isfollowRedirect;
	}

	public void setIsfollowRedirect(Boolean isfollowRedirect) {
		this.isfollowRedirect = isfollowRedirect;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Boolean getIsSecure() {
		return isSecure;
	}

	public void setIsSecure(Boolean isSecure) {
		this.isSecure = isSecure;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String path) {
		this.uri = path;
	}

	public Map<String, String> getParamsMap() {
		return paramsMap;
	}

	public void setParamsMap(Map<String, String> paramsMap) {
		this.paramsMap = paramsMap;
	}
}
