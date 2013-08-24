package com.baidu.dudu.network.http;


import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpException;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.nio.DefaultServerIOEventDispatch;
import org.apache.http.impl.nio.SSLServerIOEventDispatch;
import org.apache.http.impl.nio.reactor.DefaultListeningIOReactor;
import org.apache.http.nio.NHttpConnection;
import org.apache.http.nio.protocol.BufferingHttpServiceHandler;
import org.apache.http.nio.protocol.EventListener;
import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.nio.reactor.ListeningIOReactor;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HttpCore 4.0 based nio HTTP 1.1 server
 */
public class NonblockingHttpServer {

    private static final Logger m_logger = LoggerFactory.getLogger(NonblockingHttpServer.class);
    private static volatile int IOWORKER_COUNT = 0;

    public static void startup(String host, int port, boolean secure, SSLContext sslContext, HttpParams params, HttpProcessor httpproc,
            HttpRequestHandler reqhandler, int threadPoolSize) throws Exception {
        final boolean isSecure = secure && sslContext != null;

        BufferingHttpServiceHandler handler = new BufferingHttpServiceHandler(httpproc, new DefaultHttpResponseFactory(),
                new DefaultConnectionReuseStrategy(), params);

        // Set up request handlers
        HttpRequestHandlerRegistry reqistry = new HttpRequestHandlerRegistry();
        reqistry.register("*", reqhandler);

        handler.setHandlerResolver(reqistry);

        // Provide an event logger
        handler.setEventListener(new EventLogger());

        IOEventDispatch ioEventDispatch = null;
        if (!isSecure) {
            ioEventDispatch = new DefaultServerIOEventDispatch(handler, params);
        }
        else {
            ioEventDispatch = new SSLServerIOEventDispatch(handler, sslContext, params);
        }

        final IOEventDispatch ioEventDispatcher = ioEventDispatch;

        ThreadFactory workertf = new ThreadFactory() {

            public Thread newThread(final Runnable r) {
                return new Thread(r, (isSecure ? "NSWorker-" : "NWorker-") + (++IOWORKER_COUNT));
            }
        };

        final ListeningIOReactor ioReactor = new DefaultListeningIOReactor(threadPoolSize, workertf, params);

        // listen and start up
        m_logger.info("Starting HTTP" + (isSecure ? "S" : "") + " server at " + host + ":" + port);
        ioReactor.listen(new InetSocketAddress(host, port));
        new Thread("Nio-Selector") {

            public void run() {
                try {
                    ioReactor.execute(ioEventDispatcher);
                }
                catch (InterruptedIOException ex) {
                    m_logger.error("HTTP server is interrupted");
                }
                catch (IOException e) {
                    m_logger.error("I/O error: " + e.getMessage());
                    if (m_logger.isDebugEnabled()) {
                        m_logger.debug("I/O error: ", e);
                    }
                }
                m_logger.warn("Shuting down HTTP server");
            }
        }.start();
    }

    static class EventLogger implements EventListener {

        public void connectionOpen(final NHttpConnection conn) {
            if (m_logger.isDebugEnabled())
                m_logger.debug("Connection open: " + conn);
        }

        public void connectionTimeout(final NHttpConnection conn) {
            if (m_logger.isDebugEnabled())
                m_logger.debug("Connection timed out: " + conn);
        }

        public void connectionClosed(final NHttpConnection conn) {
            if (m_logger.isDebugEnabled())
                m_logger.debug("Connection closed: " + conn);
        }

        public void fatalIOException(final IOException ex, final NHttpConnection conn) {
            m_logger.error("I/O error: " + ex.getMessage() + " on connection: " + conn);
            if (m_logger.isDebugEnabled()) {
                m_logger.debug("I/O error", ex);
            }
        }

        public void fatalProtocolException(final HttpException ex, final NHttpConnection conn) {
            m_logger.error("HTTP error: " + ex.getMessage() + " on connection: " + conn);
            if (m_logger.isDebugEnabled()) {
                m_logger.debug("HTTP error ", ex);
            }
        }

    }

}
