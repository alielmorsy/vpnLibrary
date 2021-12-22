package aie.vpnLibrary.messages;

import java.nio.ByteBuffer;
import java.util.Map;

public class GetIPMessage extends BaseMessage {
    public static final int GET = 0;
    public static final int SET = 1;

    private int state;

    private String ip;


    public GetIPMessage() {
        super(GET_IP_MESSAGE);
    }

    @Override
    public ByteBuffer buildSubMessage() {
        int size = 1;
        if (state == SET) {
            size += ip.length();
        }
        ByteBuffer buffer = ByteBuffer.allocate(size);
        buffer.put((byte) state);
        if (state == SET) {
            buffer.put(ip.getBytes());
        }
        return buffer;
    }

    @Override
    public IMessage construct(ByteBuffer buffer) {
        state = buffer.get();
        if (state == SET) {
            byte[] bytes = new byte[buffer.capacity() - buffer.position()];
            buffer.get(bytes);
            ip = new String(bytes);
        }
        return this;
    }

    public GetIPMessage setState(int state) {
        this.state = state;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public GetIPMessage setIp(String ip) {
        this.ip = ip;
        return this;
    }

}
