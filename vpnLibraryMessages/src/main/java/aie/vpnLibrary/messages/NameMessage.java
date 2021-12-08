package aie.vpnLibrary.messages;

import java.nio.ByteBuffer;

public class NameMessage extends BaseMessage {
    private String name;

    protected NameMessage() {
        super(NAME_MESSAGE);
    }

    @Override
    public ByteBuffer buildSubMessage() {

        return ByteBuffer.allocate(name.length()).put(name.getBytes());
    }

    @Override
    public void construct(ByteBuffer buffer) {
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);
        name = new String(bytes, 0, bytes.length);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
