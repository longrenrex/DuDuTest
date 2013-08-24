package com.baidu.dudu.network.http.message.resquest;

/**
 * @author rzhao
 */
public class DuDuHttpStringRequest extends DuDuHttpRequest {

	private static final long serialVersionUID = -4953417104391938132L;
	private String bodyContent;

	public String getBodyContent() {
		return bodyContent;
	}

	public void setBodyContent(String bodyContent) {
		this.bodyContent = bodyContent;
	}

}
