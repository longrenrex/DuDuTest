package com.baidu.dudu.framework.message;


/**
 * @author rzhao
 */
public class DuDuStartCycleMessage implements DuDuMessage {

    private DuDuMessage message;

    private int interval;

    /**
     * @param message
     * @param interval
     */
    public DuDuStartCycleMessage(DuDuMessage message, int interval) {
        super();
        this.message = message;
        this.interval = interval;
    }

    public DuDuMessage getMessage() {
        return message;
    }

    public void setMessage(DuDuMessage message) {
        this.message = message;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

}
