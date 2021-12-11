package aie.vpnLibrary.messages;

import java.nio.ByteBuffer;

public interface IMessage extends Comparable<IMessage>{

    ByteBuffer buildMessage();

    ByteBuffer buildSubMessage();

    IMessage construct(ByteBuffer buffer);
}
