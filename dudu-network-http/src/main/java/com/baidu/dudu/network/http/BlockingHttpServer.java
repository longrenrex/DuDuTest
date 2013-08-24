package com.baidu.dudu.network.http;


import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.HttpServerConnection;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HttpCore 4.0 based blocking http server
 */
public class BlockingHttpServer {

    private static final Logger logger = LoggerFactory.getLogger(BlockingHttpServer.class);

    public static void startup(String host, int port, boolean secure, SSLContext sslContext, HttpParams params, HttpProcessor httpproc,
            HttpRequestHandler handler) throws Exception {
        Thread t = new RequestListenerThread(host, port, secure, sslContext, params, httpproc, handler);
        t.setDaemon(false);
        t.start();
    }

    static class RequestListenerThread extends Thread {

        private final ServerSocket serversocket;
        private final HttpParams params;
        private final HttpService httpService;
        private final boolean secure;

        public RequestListenerThread(String host, int port, boolean secure, SSLContext sslContext, HttpParams params, HttpProcessor httpproc,
                HttpRequestHandler handler) throws Exception {
            this.secure = secure && sslContext != null;
            this.serversocket = getServerSocket(host, port, secure, sslContext, false);
            this.params = params;

            // Set up request handlers
            HttpRequestHandlerRegistry reqistry = new HttpRequestHandlerRegistry();
            reqistry.register("*", handler);

            // Set up the HTTP service
            this.httpService = new HttpService(httpproc, new DefaultConnectionReuseStrategy(), new DefaultHttpResponseFactory(), reqistry, params);
        }

        public void run() {
            if (logger.isInfoEnabled())
                logger.info("HTTP server is listening on " + this.serversocket);
            while (!Thread.interrupted()) {
                try {
                    // Set up HTTP connection
                    Socket socket = this.serversocket.accept();
                    DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
                    if (logger.isDebugEnabled())
                        logger.debug("Incoming HTTP connection from " + socket.getInetAddress());
                    conn.bind(socket, this.params);

                    // Start worker thread
                    Thread t = new WorkerThread(secure, this.httpService, conn);
                    t.setDaemon(true);
                    t.start();
                }
                catch (InterruptedIOException ex) {
                    break;
                }
                catch (IOException e) {
                    logger.error("I/O error initialising HTTP connection thread: ", e.getMessage());
                    break;
                }
            }
        }

        protected ServerSocket getServerSocket(String host, int port, boolean secure, SSLContext sslContext, boolean requireClientAuth)
                throws Exception {
            if (secure && sslContext != null) {
                SSLServerSocket serverSocket = (SSLServerSocket) sslContext.getServerSocketFactory().createServerSocket();
                serverSocket.setNeedClientAuth(requireClientAuth);
                serverSocket.bind(new InetSocketAddress(host, port));
                return serverSocket;
            }
            else {
                ServerSocket serversocket = new ServerSocket();
                serversocket.bind(new InetSocketAddress(host, port));
                return serversocket;
            }
        }
    }

    static class WorkerThread extends Thread {

        private final HttpService httpservice;
        private final HttpServerConnection conn;
        /* For autonumbering anonymous threads. */
        private static volatile int COUNT = 0;

        public WorkerThread(final boolean secure, final HttpService httpservice, final HttpServerConnection conn) {
            super((secure ? "Worker-" : "SWorker-") + (++COUNT));
            this.httpservice = httpservice;
            this.conn = conn;
        }

        public void run() {
            if (logger.isDebugEnabled())
                logger.debug("New connection thread");
            HttpContext context = new BasicHttpContext(null);
            try {
                while (!Thread.interrupted() && this.conn.isOpen()) {
                    this.httpservice.handleRequest(this.conn, context);
                }
            }
            catch (ConnectionClosedException ex) {
                if (logger.isDebugEnabled())
                    logger.debug("Client closed connection: " + ex.getMessage());
            }
            catch (IOException ex) {
                logger.error("I/O error: {}", ex.getMessage());
            }
            catch (HttpException ex) {
                logger.error("Unrecoverable HTTP protocol violation: {}", ex.getMessage());
            }
            finally {
                try {
                    this.conn.shutdown();
                }
                catch (IOException ignore) {
                }
            }
        }

    }

}
