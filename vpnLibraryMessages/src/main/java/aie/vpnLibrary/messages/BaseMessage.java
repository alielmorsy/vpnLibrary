package aie.vpnLibrary.messages;

import java.nio.ByteBuffer;

public abstract class BaseMessage implements IMessage {

    public final static int REQUEST_MESSAGE = 21;
    public final static int RESPONSE_MESSAGE = 22;
    public final static int KEEP_ALIVE = 0;

    public int getMessageType() {
        return messageType;
    }

    private final int messageType;

    protected BaseMessage(int messageType) {
        this.messageType = messageType;
    }

    public static IMessage createMessage(ByteBuffer byteBuffer) {
        switch ((int) byteBuffer.get()) {
            case REQUEST_MESSAGE:
                return new RequestMessage(byteBuffer);
            case RESPONSE_MESSAGE:
                return new ResponseMessage(byteBuffer);
            case KEEP_ALIVE:
                return new KeepAliveMessage();
        }
        return null;
    }

    @Override
    public ByteBuffer buildMessage() {
        ByteBuffer subMessage = buildSubMessage().position(0);
        ByteBuffer message = ByteBuffer.allocate(1 + subMessage.capacity());
        message.put((byte) messageType);
        message.put(subMessage);

        return message;
    }

    public abstract ByteBuffer buildSubMessage();
}
