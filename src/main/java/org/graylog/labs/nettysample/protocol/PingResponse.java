package org.graylog.labs.nettysample.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.nio.charset.StandardCharsets;

/**
 * The server answers a {@link PingRequest} with this, basically just acknowledging that it got the request.
 */
public class PingResponse implements FooMessage {

  private final String clientAddress;

  public PingResponse(String clientAddress) {
    this.clientAddress = clientAddress;
  }

  public static PingResponse decode(ByteBuf in) {
    // no payload, nothing to decode
    final int strLen = in.readInt();
    final CharSequence charSequence = in.readCharSequence(strLen, StandardCharsets.UTF_8);
    return new PingResponse(charSequence.toString());
  }

  @Override
  public ByteBuf encode() {
    final ByteBuf buffer = Unpooled.buffer();
    buffer.writeByte(FooMessage.PING_RESPONSE_TYPE);
    buffer.writeInt(clientAddress.length());
    buffer.writeCharSequence(clientAddress, StandardCharsets.UTF_8);
    return buffer;
  }

  @Override
  public String toString() {
    return "PingResponse (to client at " + clientAddress + ")";
  }
}
