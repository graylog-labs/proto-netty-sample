package org.graylog.labs.nettysample.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class Ping implements FooMessage {

  private final int randomNumber;

  public Ping(int randomNumber) {
    this.randomNumber = randomNumber;
  }

  public static Ping decode(ByteBuf in) {
    // TODO this should use error handling and sanity checks
    return new Ping(in.readInt());
  }

  @Override
  public ByteBuf encode() {
    return Unpooled.buffer().writeByte(FooMessage.PING_TYPE).writeInt(randomNumber);
  }

  @Override
  public String toString() {
    return "Ping " + randomNumber;
  }
}
