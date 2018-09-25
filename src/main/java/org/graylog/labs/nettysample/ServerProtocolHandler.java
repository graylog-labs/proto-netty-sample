package org.graylog.labs.nettysample;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.DefaultChannelGroup;
import java.net.SocketAddress;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.graylog.labs.nettysample.protocol.FooMessage;
import org.graylog.labs.nettysample.protocol.Ping;
import org.graylog.labs.nettysample.protocol.PingRequest;
import org.graylog.labs.nettysample.protocol.PingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerProtocolHandler extends SimpleChannelInboundHandler<FooMessage> {
  private static final Logger LOG = LoggerFactory.getLogger(ServerProtocolHandler.class);

  private static final Map<Channel, ScheduledFuture<?>> clients = new ConcurrentHashMap<Channel, ScheduledFuture<?>>();
  private final ScheduledExecutorService pingExecutor;

  public ServerProtocolHandler(ScheduledExecutorService pingExecutor) {
    this.pingExecutor = pingExecutor;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, FooMessage msg) throws Exception {
    // we could have created a specific handler for PingRequest objects only, too.
    if (msg instanceof PingRequest) {
      final Channel channel = ctx.channel();
      // remove our state if the client disconnects
      channel
          .closeFuture()
          .addListener(
              clientDisconnected -> {
                LOG.info("Client {} disconnected", channel.remoteAddress());
                clients.get(channel).cancel(true);
              });

      // schedule the pings
      final int interval = ((PingRequest) msg).getInterval();
      LOG.info("Scheduling ping to {} for every {} seconds.", channel.remoteAddress(), interval);
      final ScheduledFuture<?> scheduledFuture =
          pingExecutor.scheduleAtFixedRate(
              () -> {
                LOG.info("Sending ping to {}", channel.remoteAddress());
                channel.writeAndFlush(new Ping(new Random().nextInt()));
                LOG.info("Write done to {} ", channel.remoteAddress());
              },
              interval,
              interval,
              TimeUnit.SECONDS);

      // remember the future, so we can cancel it later
      clients.putIfAbsent(channel, scheduledFuture);

      final SocketAddress socketAddress = channel.remoteAddress();
      // we can write an application level object here, the FooMessageCodecHandler will convert as
      // necessary
      ctx.writeAndFlush(new PingResponse(socketAddress.toString()));
    }
  }
}
