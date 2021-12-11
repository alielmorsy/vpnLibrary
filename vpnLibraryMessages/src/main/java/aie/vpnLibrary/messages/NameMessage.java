package aie.vpnLibrary.messages;

import java.nio.ByteBuffer;

public class NameMessage extends BaseMessage {
    private String name;

    public NameMessage() {
        super(NAME_MESSAGE);

    }

    @Override
    public ByteBuffer buildSubMessage() {

        return ByteBuffer.allocate(name.length()).put(name.getBytes());
    }

    @Override
    public IMessage construct(ByteBuffer buffer) {
        byte[] bytes = new byte[buffer.capacity()-buffer.position()];
        buffer.get(bytes);
        name = new String(bytes, 0, bytes.length);
        System.out.println(name);
        return this;
    }

    public String getName() {
        return name;
    }

    public IMessage setName(String name) {
        this.name = name;
        return this;
    }
}
