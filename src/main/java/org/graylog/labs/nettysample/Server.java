package org.graylog.labs.nettysample;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {
  private static final Logger LOG = LoggerFactory.getLogger(Server.class);

  public static void main(String[] args) throws InterruptedException {
    EventLoopGroup bossGroup = new NioEventLoopGroup(2);
    EventLoopGroup workerGroup = new NioEventLoopGroup(2);

    ScheduledExecutorService pingExecutor = Executors.newScheduledThreadPool(2);
    try {
      final ServerBootstrap server = new ServerBootstrap();
      server
          .group(bossGroup, workerGroup)
          .childOption(ChannelOption.TCP_NODELAY, true)
          .channel(NioServerSocketChannel.class)
          .childHandler(
              new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                  final ChannelPipeline pipeline = ch.pipeline();
                  // this handles encoding/decoding out protocol to wire format, called on both
                  // incoming and outgoing messages
                  pipeline.addLast(new FooMessageCodecHandler());
                  // this does whatever we want to do on the application level (in terms of received
                  // FooMessages)
                  pipeline.addLast(new ServerProtocolHandler(pingExecutor));
                }
              });

      final ChannelFuture bind = server.bind("127.0.0.1", 9999);
      bind.addListener(
          future -> {
            LOG.info("Server listening for requests: {}", future.isSuccess());
          });
      bind.channel().closeFuture().sync();
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }
}
