package com.baidu.dudu.network.http.message.response;

import org.apache.http.StatusLine;

/**
 * @author rzhao
 */
public class DuDuHttpStringResponse extends DuDuHttpResponse {

	private static final long serialVersionUID = 1108220325893945583L;

	private String content;
	
	public DuDuHttpStringResponse(StatusLine statusLine){
		this.statusLine = statusLine;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
