package com.baidu.dudu.network.mina;


import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.dudu.framework.exception.DuDuException;
import com.baidu.dudu.framework.message.DuDuMessage;
import com.baidu.dudu.network.mina.handler.MessageIoHandler;

/**
 * @author rzhao
 */
public abstract class MinaServer {

    private static Logger logger = LoggerFactory.getLogger(MinaServer.class);

    private NioSocketAcceptor acceptor = null;

    private MinaFactory factory = MinaFactory.getInstance();

    private String ip;

    private int port = 8080;

    private MessageIoHandler handler;

    public MinaServer(String ip, int port, boolean isSecure, MessageIoHandler handler, ProtocolCodecFactory codecFactory) {
        this.ip = ip;
        this.port = port;
        this.handler = handler;
        this.acceptor = factory.createAcceptor(codecFactory, handler, isSecure);
    }

    public abstract void send(DuDuMessage message, Object sessionToken);

    public void start() {
        try {
            if (logger.isInfoEnabled()) {
                logger.info("Server bind ip: " + ip + ", listen port:" + port);
            }
            this.acceptor.bind(new InetSocketAddress(ip, port));
        }
        catch (IOException e) {
            logger.error("Server bind ip: " + ip + ", port:" + port + " IOException:" + e);
            throw new DuDuException("Server bind port:" + port + " IOException:", e);
        }
    }

    public void stop() {
        if (acceptor != null) {
            acceptor.dispose();
            acceptor.unbind();
        }
    }

    public NioSocketAcceptor getAcceptor() {
        return acceptor;
    }
}
