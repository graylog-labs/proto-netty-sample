package org.graylog.labs.nettysample;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import java.util.List;
import org.graylog.labs.nettysample.protocol.FooMessage;
import org.graylog.labs.nettysample.protocol.Ping;
import org.graylog.labs.nettysample.protocol.PingRequest;
import org.graylog.labs.nettysample.protocol.PingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This handler knows how to convert (or delegate to convert) a {@link FooMessage} to {@link
 * ByteBuf} and vice versa.
 */
public class FooMessageCodecHandler extends ByteToMessageCodec<FooMessage> {
  private static final Logger LOG = LoggerFactory.getLogger(FooMessageCodecHandler.class);

  @Override
  protected void encode(ChannelHandlerContext ctx, FooMessage msg, ByteBuf out) throws Exception {
    // simply copy everything over, could be more efficient by passing the "out" to the encoder
    LOG.info("Sending message {}", msg);
    out.writeBytes(msg.encode());
    ctx.flush();
  }

  // TODO this should do proper error checking and handling (and possibly close the connection)
  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    // first find out what actual type this is:
    final byte type = in.readByte();
    LOG.info("Received a message of type {}", type);
    switch (type) {
      case FooMessage.PING_REQUEST_TYPE:
        out.add(PingRequest.decode(in));
        break;
      case FooMessage.PING_RESPONSE_TYPE:
        out.add(PingResponse.decode(in));
        break;
      case FooMessage.PING_TYPE:
        out.add(Ping.decode(in));
        break;
      default:
        throw new IllegalStateException("Unknown message type: " + type);
    }
  }
}
