package com.baidu.dudu.network.io;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rzhao
 */
public abstract class TcpSocketConnection {

    private static Logger log = LoggerFactory.getLogger(TcpSocketConnection.class);

    protected Socket socket;

    protected String hostname;

    protected int port;

    protected InputStream inputStream;

    protected OutputStream outputStream;

    private boolean receiving;

    private Thread receiverThread;

    /**
     * TODO Implement non-blocking IO here
     * 
     * @throws IOException
     */

    // private static BlockingQueue newConnections;
    // static {
    // startSocketThread();
    // }
    // private static void startSocketThread() {
    // newConnections = new LinkedBlockingQueue();
    //		
    // // Thread 1: Select thread
    // // Select for 500 ms
    // // Any new connections? Create sockets and add to selector
    // // Any new connect? Kick in handler
    // // Any new available, read and hand off to byte thread
    //		
    // // Thread 2: Read thread
    // // Get bytes from read queue
    // // Kick in handler for that byte stream
    // }
    public TcpSocketConnection(Socket socket) throws IOException, NullPointerException {
        this(socket, socket.getInetAddress().getHostAddress(), socket.getPort(), new BufferedInputStream(socket.getInputStream(), 128 * 1024),
                new BufferedOutputStream(socket.getOutputStream()));
    }

    private TcpSocketConnection(Socket socket, String hostname, int port, InputStream inputStream, OutputStream outputStream) {
        this.socket = socket;
        this.hostname = hostname;
        this.port = port;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public String getLocalHost() {
        return socket.getLocalAddress().getHostAddress();
    }

    public int getLocalPort() {
        return socket.getLocalPort();
    }

    public String getRemoteHost() {
        return hostname;
    }

    public int getRemotePort() {
        return port;
    }

    public void send(byte[] message) throws IOException {
        send(message, true);
    }

    public void send(byte[] message, boolean flush) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("Sending to " + hostname + ":" + port + ":\n" + new String(message));
        }
        outputStream.write(message);
        if (flush) {
            flush();
        }
    }

    public void flush() throws IOException {
        outputStream.flush();
    }

    public boolean isReceiving() {
        return receiving;
    }

    public void receive() {
        if (receiverThread != null) {
            return;
        }
        receiverThread = Thread.currentThread();
        receiving = true;
        while (receiving) {
            try {
                waitForFirstIncomingByte(inputStream);
            }
            catch (IOException e) {
                if (receiving) {

                    try {
                        shutdown();
                    }
                    catch (IOException e1) {
                    }
                }
                // Break if we fail to receive first byte, indicating a
                // communications problem
                break;
            }
            try {
                receivedBytes(inputStream);
            }
            catch (IOException e) {
                if (receiving) {
                    log.error("Got IOException while receiving from " + hostname + ":" + port, e);
                }
            }
        }
    }

    protected abstract void receivedBytes(InputStream inputStream) throws IOException;

    public boolean isConnected() {
        return (inputStream != null) && (outputStream != null);
    }

    public synchronized void shutdown() throws IOException {
        if (receiving) {
            if (log.isInfoEnabled()) {
                log.info("Shutdown of tcp connection to " + hostname + ":" + port);
            }
            receiving = false;
            if (receiverThread != null) {
                receiverThread.interrupt();
            }
            receiverThread = null;
            teardownSocket();
        }
    }

    private void waitForFirstIncomingByte(InputStream inputStream) throws IOException {
        inputStream.mark(1);
        int result = inputStream.read();
        if (result == -1) {
            // log.info("");
            throw new IOException("End of stream reached ");
        }
        inputStream.reset();
    }

    private void teardownSocket() {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
        catch (IOException ignored) {
        }
        finally {
            inputStream = null;
            outputStream = null;
            try {
                if (socket != null) {
                    socket.close();
                }
            }
            catch (IOException ignored) {
            }
            finally {
                socket = null;
            }
        }
    }
}
