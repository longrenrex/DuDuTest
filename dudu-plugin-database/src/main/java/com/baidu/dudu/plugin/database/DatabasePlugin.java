package com.baidu.dudu.plugin.database;


import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.baidu.dudu.framework.command.DuDuCommand;
import com.baidu.dudu.framework.exception.DuDuException;
import com.baidu.dudu.framework.handler.IncomingMessageHandler;
import com.baidu.dudu.framework.interaction.TestInteraction;
import com.baidu.dudu.framework.message.DuDuMessage;
import com.baidu.dudu.framework.message.DuDuSessionMessage;
import com.baidu.dudu.framework.plugin.Plugin;
import com.baidu.dudu.plugin.database.message.request.DuDuCountReq;
import com.baidu.dudu.plugin.database.message.request.DuDuExcuteReq;
import com.baidu.dudu.plugin.database.message.request.DuDuQueryReq;
import com.baidu.dudu.plugin.database.message.response.DuDuCountResp;
import com.baidu.dudu.plugin.database.message.response.DuDuQueryResp;

/**
 * @author rzhao
 */
public class DatabasePlugin implements Plugin {

    private static final Logger logger = LoggerFactory.getLogger(DatabasePlugin.class);

    private TestInteraction testInteraction;
    private IncomingMessageHandler incomingMessageHandler;
    private JdbcTemplate jdbcTemplate;
    private DataSource dataSource;

    @Override
    public void start() {
        incomingMessageHandler.setTestInteraction(testInteraction);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Map<String, Object>> getList(String sql) {
        return this.jdbcTemplate.queryForList(sql);
    }

    public void doExecute(String sql) {
        this.jdbcTemplate.execute(sql);
    }

    public int getCount(String sql) {
        return this.jdbcTemplate.queryForInt(sql);
    }

    @Override
    public void send(DuDuMessage message) {
        send(message, null);
    }

    @Override
    public void send(DuDuMessage message, Object sessionToken) {
        if (message instanceof DuDuCountReq) {
            logger.info("------> DatabasePlugin sends DuDuCountReq ------>");
            DuDuCountReq countReq = (DuDuCountReq) message;
            DuDuCountResp resp = new DuDuCountResp();
            resp.setCount(getCount(countReq.getSql()));
            logger.info("<------ DatabasePlugin recevied DuDuCountResp <------");
            DuDuSessionMessage sessionMessage = new DuDuSessionMessage(resp, dataSource);
            incomingMessageHandler.incomingMessage(sessionMessage);
            return;
        }

        if (message instanceof DuDuQueryReq) {
            logger.info("------> DatabasePlugin sends DuDuQueryReq ------>");
            DuDuQueryReq queryReq = (DuDuQueryReq) message;
            DuDuQueryResp resp = new DuDuQueryResp();
            resp.setList(getList(queryReq.getSql()));
            logger.info("<------ DatabasePlugin recevied DuDuQueryResp <------");
            DuDuSessionMessage sessionMessage = new DuDuSessionMessage(resp, dataSource);
            incomingMessageHandler.incomingMessage(sessionMessage);
            return;
        }

        if (message instanceof DuDuExcuteReq) {
            logger.info("------> DatabasePlugin sends DuDuExcuteReq ------>");
            DuDuExcuteReq excuteReq = (DuDuExcuteReq) message;
            this.doExecute(excuteReq.getSql());
            return;
        }

        throw new DuDuException("DatabasePlugin can send DatabaseMessage only!");

    }

    @Override
    public void execute(DuDuCommand command) {
        // TODO Auto-generated method stub

    }

    @Override
    public DuDuSessionMessage receive() {
        return incomingMessageHandler.receive();
    }

    @Override
    public DuDuSessionMessage receive(DuDuMessage message) {
        if (message == null) {
            throw new AssertionError("DuDuMessage DatabasePlugin received is null!");
        }

        return incomingMessageHandler.receive(message);
    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setIncomingMessageHandler(IncomingMessageHandler handler) {
        this.incomingMessageHandler = handler;
    }

    @Override
    public void setTestInteraction(TestInteraction testInteraction) {
        this.testInteraction = testInteraction;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

}
