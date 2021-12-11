package aie.vpnLibrary.messages;

import java.nio.ByteBuffer;

public class ErrorMessage extends BaseMessage {
    private int errorCode;
    private String errorMessage;

    public ErrorMessage() {
        super(ERROR_MESSAGE);
    }

    @Override
    public ByteBuffer buildSubMessage() {
        ByteBuffer buffer = ByteBuffer.allocate(1 + errorMessage.length());
        buffer.put((byte) errorCode).put(errorMessage.getBytes());
        return buffer;
    }

    @Override
    public IMessage construct(ByteBuffer buffer) {
        errorCode = buffer.get();
        errorMessage = new String(buffer.slice().array());
        return this;
    }


    public int getErrorCode() {
        return errorCode;
    }

    public ErrorMessage setErrorCode(int errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public ErrorMessage setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }
}
