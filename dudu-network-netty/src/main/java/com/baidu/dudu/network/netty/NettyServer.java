package com.baidu.dudu.network.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.dudu.framework.exception.DuDuException;

public abstract class NettyServer {

	private static Logger logger = LoggerFactory.getLogger(NettyServer.class);

	private final int port;

	protected ServerBootstrap bootstrap;

	private ChannelPipelineFactory channelPipelineFactory;

	public NettyServer(int port) {
		this.port = port;
	}

	public void run() {

		// check ChannelPipelineFactory
		if (channelPipelineFactory == null) {
			throw new DuDuException("ChannelPipelineFactory is null !");
		}

		// Configure the server.
		bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

		// Enable TCP_NODELAY to handle pipelined requests without latency.
		bootstrap.setOption("child.tcpNoDelay", true);

		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(channelPipelineFactory);

		// Bind and start to accept incoming connections.
		bootstrap.bind(new InetSocketAddress(port));

	}

	protected void stop() {
		if (bootstrap != null) {
			bootstrap.shutdown();
		}

		bootstrap = null;
	}

	public void setChannelPipelineFactory(ChannelPipelineFactory channelPipelineFactory) {
		this.channelPipelineFactory = channelPipelineFactory;
	}

}
