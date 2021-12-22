package aie.vpnLibrary.messages;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public abstract class BaseMessage implements IMessage {

    public final static int REQUEST_MESSAGE = 21;
    public final static int RESPONSE_MESSAGE = 22;
    public final static int KEEP_ALIVE = 0;
    public final static int GET_NAME_MESSAGE = 1;
    public final static int NAME_MESSAGE = 10;
    public final static int ERROR_MESSAGE = -1;
    public final static int DISCONNECT_MESSAGE = -2;
    public final static int GET_IP_MESSAGE = 2;

    public int getMessageType() {
        return messageType;
    }

    private final int messageType;

    protected BaseMessage(int messageType) {
        this.messageType = messageType;
    }

    public static IMessage createMessage(ByteBuffer byteBuffer) {
        if (byteBuffer == null) return null;
        ((Buffer) byteBuffer).position(0);
        switch ((int) byteBuffer.get()) {
            case REQUEST_MESSAGE:
                return new RequestMessage().construct(byteBuffer);
            case RESPONSE_MESSAGE:
                return new ResponseMessage().construct(byteBuffer);
            case KEEP_ALIVE:
                return new KeepAliveMessage().construct(byteBuffer);
            case GET_NAME_MESSAGE:
                return new GetNameMessage().construct(byteBuffer);
            case NAME_MESSAGE:
                return new NameMessage().construct(byteBuffer);
            case ERROR_MESSAGE:
                return new ErrorMessage().construct(byteBuffer);
            case DISCONNECT_MESSAGE:
                return new DisconnectMessage().construct(byteBuffer);
            case GET_IP_MESSAGE:
                return new GetIPMessage().construct(byteBuffer);
        }
        return null;
    }

    @Override
    public ByteBuffer buildMessage() {
        ByteBuffer subMessage = buildSubMessage();
        ((Buffer) subMessage).position(0);
        ByteBuffer message = ByteBuffer.allocate(1 + subMessage.capacity());
        message.put((byte) messageType);
        message.put(subMessage);

        return message;
    }

    public abstract ByteBuffer buildSubMessage();

    @Override
    public int compareTo(IMessage o) {
        return 0;
    }
}
