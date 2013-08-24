package com.baidu.dudu.network.http.message.response;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.StatusLine;

import com.baidu.dudu.framework.message.DuDuMessage;

/**
 * @author rzhao
 */
public abstract class DuDuHttpResponse implements DuDuMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4532737296891455202L;
	protected StatusLine statusLine;
	protected List headerList;

	public StatusLine getStatusLine() {
		return statusLine;
	}

	public void setStatusLine(StatusLine statusLine) {
		this.statusLine = statusLine;
	}

	public List getHeaderList() {
		return headerList;
	}

	public void setHeaderList(List headerList) {
		this.headerList = headerList;
	}

	public void addHeader(Header header) {
		if (headerList == null) {
			headerList = new ArrayList();
		}
		
		headerList.add(header);
	}
}
