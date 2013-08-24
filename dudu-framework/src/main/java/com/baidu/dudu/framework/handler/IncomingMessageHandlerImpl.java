package com.baidu.dudu.framework.handler;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.dudu.framework.exception.DuDuException;
import com.baidu.dudu.framework.interaction.TestInteraction;
import com.baidu.dudu.framework.mapper.DuDuMapper;
import com.baidu.dudu.framework.message.DuDuMessage;
import com.baidu.dudu.framework.message.DuDuSessionMessage;
import com.baidu.dudu.framework.message.DuDuTimeoutMessage;

/**
 * @author rzhao
 */
public class IncomingMessageHandlerImpl implements IncomingMessageHandler {

    private static Logger logger = LoggerFactory.getLogger(IncomingMessageHandlerImpl.class);

    private DuDuMapper mapper;

    private BlockingQueue<DuDuSessionMessage> receviedMessageQueue;

    private TestInteraction testInteraction;

    private long timeout;

    public IncomingMessageHandlerImpl(BlockingQueue<DuDuSessionMessage> messageQueue, DuDuMapper mapper, long timeout) {
        this.receviedMessageQueue = messageQueue;
        this.mapper = mapper;
        this.timeout = timeout;
    }

    @Override
    public void incomingMessage(DuDuSessionMessage sessionMessage) {
        try {
            receviedMessageQueue.put(sessionMessage);
        }
        catch (InterruptedException e) {
            logger.error("put message into messageQueue error!");
            throw new DuDuException("put message into messageQueue error!");
        }
    }

    /*
     * (non-Javadoc)
     * @see com.dudu.free.framework.handler.IncomingMessageHandler#receive()
     */
    @Override
    public DuDuSessionMessage receive() {
        return receive(null);
    }

    public DuDuSessionMessage receive(DuDuMessage message) {
        DuDuSessionMessage receivedMessage = null;
        try {
            if (message == null) {
                receivedMessage = receviedMessageQueue.poll(timeout, TimeUnit.MILLISECONDS);
                return receivedMessage;
            }

            if (message instanceof DuDuTimeoutMessage) {
                DuDuTimeoutMessage timeoutMessage = (DuDuTimeoutMessage) message;
                receivedMessage = receviedMessageQueue.poll(timeoutMessage.getTimeout(), timeoutMessage.getTimeunit());
                if (receivedMessage == null) {
                    final String errorInfo = "No message recevied! Expect:" + timeoutMessage.getMessage().getClass();
                    logger.error(errorInfo);
                    throw new DuDuException(errorInfo);
                }
                testInteraction.analyticResult(timeoutMessage.getMessage(), mapper.compare(timeoutMessage.getMessage(), receivedMessage.getMessage()));
            }
            else {
                receivedMessage = receviedMessageQueue.poll(timeout, TimeUnit.MILLISECONDS);
                if (receivedMessage == null) {
                    final String errorInfo = "No message recevied! Expect:" + message.getClass();
                    logger.error(errorInfo);
                    throw new DuDuException(errorInfo);
                }
                
                logger.info("<------ recevied message: {} <------", message.getClass());
                testInteraction.analyticResult(message, mapper.compare(message, receivedMessage.getMessage()));
            }

            return receivedMessage;

        }
        catch (InterruptedException e) {
            throw new DuDuException("get message from receive queue error!", e);
        }
        catch (DuDuException e) {
            throw new DuDuException("get message from receive queue error!", e);
        }
        finally {
            //TODO:
        }
    }

    public DuDuMapper getMapper() {
        return mapper;
    }

    public void setMapper(DuDuMapper mapper) {
        this.mapper = mapper;
    }

    public void setTestInteraction(TestInteraction testInteraction) {
        this.testInteraction = testInteraction;
    }

}
