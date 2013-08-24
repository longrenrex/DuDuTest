package com.baidu.dudu.network.http;


import javax.net.ssl.SSLContext;

import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTTP server
 */
public class HttpServer {

    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    private String m_host;
    private int m_port;
    private boolean m_secure;
    private int m_workerCount;
    private boolean m_blocking;
    private HttpRequestHandler m_handler;
    private Authenticator m_authenticator;
    private HttpParams m_params;
    private SSLContext m_sslContext;

    private int m_soTimeout = 5000;
    private int m_soBufferSizeInBytes = 8 * 1024;

    private boolean m_started = false;

    public HttpServer(String host, int port, boolean secure, SSLContext sslContext, boolean blocking, int threadPoolSize, HttpRequestHandler handler,
            Authenticator authenticator, int soTimeout, int soBufferSizeInBytes) throws Exception {
        m_params = new BasicHttpParams();
        m_params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, m_soTimeout)
                .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, m_soBufferSizeInBytes)
                .setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false).setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
                .setParameter(CoreProtocolPNames.ORIGIN_SERVER, "Seven Ping server 1.2");

        this.m_host = host;
        this.m_port = port;
        if (sslContext == null) {
            this.m_secure = false;
        }
        else {
            this.m_secure = secure;
        }

        this.m_workerCount = threadPoolSize;
        this.m_blocking = blocking;
        this.m_handler = handler;
        this.m_authenticator = authenticator;
        this.m_sslContext = sslContext;
        this.m_soTimeout = soTimeout;
        this.m_soBufferSizeInBytes = soBufferSizeInBytes;
    }

    public void start() throws Exception {
        if (m_started) {
            if (logger.isInfoEnabled())
                logger.info("Http server on " + this + " has already been started up");
            return;
        }
        else {
            if (logger.isInfoEnabled())
                logger.info("Starting http server on " + this);
        }

        BasicHttpProcessor httpproc = new BasicHttpProcessor();

        if (m_authenticator != null) {
            new AuthenticationHelper(m_authenticator).addAuthenticationInterceptors(httpproc);
        }

        httpproc.addInterceptor(new ResponseDate());
        httpproc.addInterceptor(new ResponseServer());
        httpproc.addInterceptor(new ResponseContent());
        httpproc.addInterceptor(new ResponseConnControl());

        if (m_blocking)
            BlockingHttpServer.startup(m_host, m_port, m_secure, m_sslContext, m_params, httpproc, m_handler);
        else {
            NonblockingHttpServer.startup(m_host, m_port, m_secure, m_sslContext, m_params, httpproc, m_handler, m_workerCount);
        }
    }

    public void stop() {

    }

    public String toString() {
        return "[http" + (m_secure ? "s" : "") + "://" + m_host + ":" + m_port + "] (" + (m_blocking ? "blocking" : "nio") + ", "
                + (m_authenticator != null && m_authenticator.isAuthEnabled() ? "auth" : "nonauth") + ")";
    }

    public int getSoBufferSizeInBytes() {
        return m_soBufferSizeInBytes;
    }

    public void setSoBufferSizeInBytes(int bufferSizeInBytes) {
        m_soBufferSizeInBytes = bufferSizeInBytes;
    }

    public int getSoTimeout() {
        return m_soTimeout;
    }

    public void setSoTimeout(int timeout) {
        m_soTimeout = timeout;
    }
}
