package com.baidu.dudu.framework.message;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author rzhao
 */
public class DuDuSessionMessage implements DuDuMessage {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4636297606839629666L;
	
	private DuDuMessage message;
    private Object sessionToken;
    private BlockingQueue<DuDuMessage> messageQueue;
    private BlockingQueue<Object> receiptQueue;
    private Throwable throwable;

    public DuDuSessionMessage(DuDuMessage message) {
        this(message, null);
    }

    public DuDuSessionMessage(DuDuMessage message, Object sessionToken) {
        this.message = message;
        this.sessionToken = sessionToken;
        this.messageQueue = new LinkedBlockingQueue<DuDuMessage>();
        this.receiptQueue = new LinkedBlockingQueue<Object>();
    }

    public DuDuMessage getMessage() {
        return message;
    }

    public void setMsg(DuDuMessage msg) {
        this.message = msg;
    }

    public Object getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(Object sessionToken) {
        this.sessionToken = sessionToken;
    }

    public DuDuMessage getSendingMessage(long timeout, TimeUnit unit) throws InterruptedException {
        DuDuMessage message = messageQueue.poll(timeout, unit);
        return message;
    }

    public void sendingMessage(DuDuMessage message) throws InterruptedException {
        sendingMessage(message, null, null);
    }

    public void sendingMessage(DuDuMessage message, Long timeout, TimeUnit unit) throws InterruptedException {
        messageQueue.put(message);
        if (timeout != null) {
            Object o = receiptQueue.poll(timeout, unit);
            o = null;
        }
    }

    public void addReceipt(Object receipt) throws InterruptedException {
        receiptQueue.put(receipt);
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}
