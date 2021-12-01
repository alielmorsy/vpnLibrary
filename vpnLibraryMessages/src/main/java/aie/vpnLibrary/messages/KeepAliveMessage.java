package aie.vpnLibrary.messages;

import java.nio.ByteBuffer;

public class KeepAliveMessage extends BaseMessage {
    protected KeepAliveMessage(ByteBuffer byteBuffer) {
        super(KEEP_ALIVE);
    }

    @Override
    public ByteBuffer buildSubMessage() {
        return ByteBuffer.allocate(1);
    }

    @Override
    public void construct(ByteBuffer buffer) {

    }
}
