package org.graylog.labs.nettysample;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.graylog.labs.nettysample.protocol.PingRequest;

public class Client {
  public static void main(String[] args) {
    final EventLoopGroup group = new NioEventLoopGroup();

    final Bootstrap client = new Bootstrap();
    client
        .group(group)
        .channel(NioSocketChannel.class)
        .handler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) throws Exception {
            final ChannelPipeline pipeline = ch.pipeline();
            // this handles encoding/decoding out protocol to wire format, called on both incoming and
            // outgoing messages
            pipeline.addLast(new FooMessageCodecHandler());
            // this does whatever we want to do on the application level (in terms of received
            // FooMessages)
            pipeline.addLast(new ClientProtocolHandler());
          }
        });

    final ChannelFuture connect = client
        .connect("127.0.0.1", 9999);
    final Channel channel = connect.channel();
    connect
        .addListener(
            future -> {
              if (future.isSuccess()) {
                // this starts the communication and sends the object through the pipeline.
                channel.writeAndFlush(new PingRequest(2));
              } else {
                throw new IllegalStateException("Please start the server on port 9999 first");
              }
            });

  }
}
