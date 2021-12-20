package aie.vpnLibrary.messages;

import java.nio.ByteBuffer;

public class DisconnectMessage extends BaseMessage {
    private String errorMessage;

    public DisconnectMessage() {
        super(DISCONNECT_MESSAGE);
    }

    @Override
    public ByteBuffer buildSubMessage() {

        return ByteBuffer.wrap(errorMessage.getBytes());
    }

    @Override
    public IMessage construct(ByteBuffer buffer) {
        errorMessage = new String(buffer.array());
        return this;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public DisconnectMessage setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }
}
