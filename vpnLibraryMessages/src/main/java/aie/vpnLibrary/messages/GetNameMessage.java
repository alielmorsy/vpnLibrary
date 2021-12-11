package aie.vpnLibrary.messages;

import java.nio.ByteBuffer;

public class GetNameMessage extends BaseMessage {
    public GetNameMessage() {
        super(GET_NAME_MESSAGE);
    }

    @Override
    public ByteBuffer buildSubMessage() {
        return ByteBuffer.allocate(0);
    }

    @Override
    public IMessage construct(ByteBuffer buffer) {
        return this;
    }
}
