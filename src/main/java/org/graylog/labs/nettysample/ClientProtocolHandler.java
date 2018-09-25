package org.graylog.labs.nettysample;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.graylog.labs.nettysample.protocol.FooMessage;
import org.graylog.labs.nettysample.protocol.Ping;
import org.graylog.labs.nettysample.protocol.PingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientProtocolHandler extends SimpleChannelInboundHandler<FooMessage> {
  private static final Logger LOG = LoggerFactory.getLogger(ClientProtocolHandler.class);

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, FooMessage msg) throws Exception {
    // there's really nothing to do for the client, just print that we received the ping from the
    // server
    LOG.info("Received response: {}", msg);
  }
}
