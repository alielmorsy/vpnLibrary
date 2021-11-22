package aie.vpnLibrary.messages;

import java.nio.ByteBuffer;

public interface IMessage {

    ByteBuffer buildMessage();

    ByteBuffer buildSubMessage();

    void construct(ByteBuffer buffer);
}
