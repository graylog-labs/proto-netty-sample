package org.graylog.labs.nettysample.protocol;

import io.netty.buffer.ByteBuf;

/**
 * Our protocol messages all implement this.
 */
public interface FooMessage {

  byte PING_REQUEST_TYPE = 0x00;
  byte PING_RESPONSE_TYPE = 0x01;
  byte PING_TYPE = 0x02;

  ByteBuf encode();

}
