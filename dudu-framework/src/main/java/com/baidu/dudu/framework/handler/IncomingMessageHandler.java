package com.baidu.dudu.framework.handler;


import com.baidu.dudu.framework.interaction.TestInteraction;
import com.baidu.dudu.framework.mapper.DuDuMapper;
import com.baidu.dudu.framework.message.DuDuMessage;
import com.baidu.dudu.framework.message.DuDuSessionMessage;

/**
 * @author rzhao
 */
public interface IncomingMessageHandler {

    void incomingMessage(final DuDuSessionMessage message);

    DuDuSessionMessage receive();

    DuDuSessionMessage receive(DuDuMessage msg);

    DuDuMapper getMapper();

    void setTestInteraction(TestInteraction testInteraction);
}