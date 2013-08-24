package com.baidu.dudu.framework.message;


import java.util.concurrent.TimeUnit;

/**
 * @author rzhao
 */
public class DuDuTimeoutMessage implements DuDuMessage {

    private long timeout;

    private DuDuMessage message;

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public DuDuMessage getMessage() {
        return message;
    }

    public void setMessage(DuDuMessage message) {
        this.message = message;
    }

    public TimeUnit getTimeunit() {
        return timeunit;
    }

    public void setTimeunit(TimeUnit timeunit) {
        this.timeunit = timeunit;
    }

    private TimeUnit timeunit;

    public DuDuTimeoutMessage(DuDuMessage message, long timeout, TimeUnit timeunit) {
        this.message = message;
        this.timeout = timeout;
        this.timeunit = timeunit;
    }
}
