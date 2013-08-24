package com.baidu.dudu.network.mina;


import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoEventType;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.filter.statistic.ProfilerTimerFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.dudu.framework.exception.DuDuException;
import com.baidu.dudu.network.mina.ssl.TrustManagerFactoryImpl;

/**
 * @author rzhao
 */
public class MinaFactory {

    private static final Logger logger = LoggerFactory.getLogger(MinaFactory.class);

    private static MinaFactory instance = null;

    public static final String DEFAULT_SSL_PROTOCOL = "TLS";

    public static synchronized MinaFactory getInstance() {
        if (instance == null) {
            instance = new MinaFactory();
        }
        return instance;
    }

    public NioSocketAcceptor createAcceptor(ProtocolCodecFactory protocolCodecFactory, IoHandler handler, boolean isSecure) {

        if (logger.isDebugEnabled()) {
            logger.debug("Create Acceptor instance...");
        }

        // initialize
        NioSocketAcceptor acceptor = new NioSocketAcceptor();
        //measure the time it takes for a method in the IoFilterAdapter class to execute.
        ProfilerTimerFilter profilerFilter = new ProfilerTimerFilter(TimeUnit.MILLISECONDS, IoEventType.MESSAGE_RECEIVED, IoEventType.MESSAGE_SENT);
        acceptor.getFilterChain().addLast("Profiler", profilerFilter);
        // set protocolCodecFactory
        acceptor.getFilterChain().addLast("protocolFilter", new ProtocolCodecFilter(protocolCodecFactory));

        acceptor.setReuseAddress(true);
        DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();

        // Add SSL filter if SSL is enabled.
        if (isSecure) {
            try {
                addSSLSupport(chain);
            }
            catch (Exception e) {
                throw new DuDuException("fail to add SSL support for server !");
            }
        }

        // set handler
        if (handler != null)
            acceptor.setHandler(handler);
        else
            throw new DuDuException("no handler for server");

        return acceptor;
    }

    public NioSocketConnector createConnector(IoHandler handler, ProtocolCodecFactory protocolCodecFactory) {
        logger.debug("Create Connector instance...");
        NioSocketConnector connector = new NioSocketConnector();
        if (handler != null) {
            connector.setHandler(handler);
        }

        ProfilerTimerFilter profilerFilter = new ProfilerTimerFilter(TimeUnit.MILLISECONDS, IoEventType.MESSAGE_RECEIVED, IoEventType.MESSAGE_SENT);
        connector.getFilterChain().addLast("profiler", profilerFilter);

        if (protocolCodecFactory != null) {
            ProtocolCodecFilter filter = new ProtocolCodecFilter(protocolCodecFactory);
            connector.getFilterChain().addLast("codec", filter);
        }

        return connector;
    }

    private void addSSLSupport(DefaultIoFilterChainBuilder chain) throws Exception {
        SSLContext context = SSLContext.getInstance(DEFAULT_SSL_PROTOCOL);
        context.init(null, TrustManagerFactoryImpl.X509_MANAGERS, null);
        SslFilter sslFilter = new SslFilter(context);
        chain.addLast("sslFilter", sslFilter);
        logger.info("SSL ON");
    }

}
