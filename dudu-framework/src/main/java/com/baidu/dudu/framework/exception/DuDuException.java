package com.baidu.dudu.framework.exception;

/**
 * @author rzhao
 */
public class DuDuException extends RuntimeException {

	private static final long serialVersionUID = 7811306475361913638L;

	private StringBuffer messagePrefix = new StringBuffer();

	public DuDuException(String string) {
		super(string);
	}

	public DuDuException(String string, Exception e) {
		super(string, e);
	}

	public void prependMessage(String message) {
		messagePrefix.append(message);
	}

	public String getMessage() {
		return messagePrefix.toString() + super.getMessage();
	}
}