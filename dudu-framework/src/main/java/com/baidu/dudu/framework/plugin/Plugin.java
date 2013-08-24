package com.baidu.dudu.framework.plugin;


import com.baidu.dudu.framework.command.DuDuCommand;
import com.baidu.dudu.framework.handler.IncomingMessageHandler;
import com.baidu.dudu.framework.interaction.TestInteraction;
import com.baidu.dudu.framework.message.DuDuMessage;
import com.baidu.dudu.framework.message.DuDuSessionMessage;

/**
 * @author rzhao
 */
public interface Plugin {

    void setIncomingMessageHandler(IncomingMessageHandler handler);

    void setTestInteraction(TestInteraction testInteraction);

    void start();

    void send(DuDuMessage message);

    void send(DuDuMessage message, Object sessionToken);

    void execute(DuDuCommand command);

    DuDuSessionMessage receive();

    DuDuSessionMessage receive(DuDuMessage message);

    void stop();

}