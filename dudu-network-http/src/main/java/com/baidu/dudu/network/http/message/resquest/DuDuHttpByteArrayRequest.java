package com.baidu.dudu.network.http.message.resquest;

/**
 * @author rzhao
 */
public class DuDuHttpByteArrayRequest extends DuDuHttpRequest {

	private static final long serialVersionUID = -8195424755876513250L;

	private byte[] bodyContent;

	public byte[] getBodyContent() {
		return bodyContent;
	}

	public void setBodyContent(byte[] bodyContent) {
		this.bodyContent = bodyContent;
	}
}
