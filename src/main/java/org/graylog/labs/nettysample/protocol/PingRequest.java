package org.graylog.labs.nettysample.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/** This is the first message in the Foo protocol: It asks the server to ping us every n seconds. */
public class PingRequest implements FooMessage {

  private final int secondsInterval;

  public PingRequest(int secondsInterval) {
    if (secondsInterval < 1) {
      throw new IllegalArgumentException("Interval must be positive");
    }
    this.secondsInterval = secondsInterval;
  }

  public static PingRequest decode(ByteBuf buf) {
    // the surrounding decoder consumes the actual "type", we only read what we are responsible for
    // TODO this should really check if enough data is available, and other sanity checks
    final int interval = buf.readInt();
    return new PingRequest(interval);
  }

  @Override
  public String toString() {
    return "PingRequest: Every " + secondsInterval + " seconds.";
  }

  @Override
  public ByteBuf encode() {
    return Unpooled.buffer().writeByte(FooMessage.PING_REQUEST_TYPE).writeInt(secondsInterval);
  }

  public int getInterval() {
    return secondsInterval;
  }
}
