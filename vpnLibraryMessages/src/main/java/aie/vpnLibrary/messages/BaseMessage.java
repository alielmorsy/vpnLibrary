package aie.vpnLibrary.messages;

import java.nio.ByteBuffer;

public abstract class BaseMessage implements IMessage {

    public final static int REQUEST_MESSAGE = 21;
    public final static int RESPONSE_MESSAGE = 22;
    private int messageType;

    protected BaseMessage(int messageType) {
        this.messageType = messageType;
    }

    public static BaseMessage createMessage(ByteBuffer byteBuffer) {
        switch ((int) byteBuffer.get()) {
            case REQUEST_MESSAGE:
                return new RequestMessage(byteBuffer);
            case RESPONSE_MESSAGE:
                return new ResponseMessage(byteBuffer);
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
