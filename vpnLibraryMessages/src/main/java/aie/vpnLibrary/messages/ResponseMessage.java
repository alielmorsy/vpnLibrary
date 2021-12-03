package aie.vpnLibrary.messages;

import aie.vpnLibrary.messages.models.Cookie;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ResponseMessage extends BaseMessage {
    private List<Cookie> cookies = new ArrayList<>();
    private byte[] data;
    private boolean isSuccess;
    private String message = "";

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ResponseMessage() {
        super(RESPONSE_MESSAGE);
    }

    public ResponseMessage(ByteBuffer byteBuffer) {
        super(RESPONSE_MESSAGE);
        construct(byteBuffer);
    }

    @Override
    public void construct(ByteBuffer buffer) {
        ((java.nio.Buffer) buffer).position(0);
        id = buffer.get();
        boolean isSuccess = buffer.get() == 1;
        this.isSuccess = isSuccess;
        if (!isSuccess) {
            byte[] message = new byte[buffer.capacity() - buffer.position()];
            buffer.get(message);
            this.message = new String(message);
            return;
        }

        byte[] bytes = new byte[Byte.toUnsignedInt(buffer.get())];
        buffer.get(bytes);
        parseCookies(bytes);
        bytes = new byte[buffer.getInt()];
        buffer.get(bytes);
        data = bytes;
    }

    private void parseCookies(byte[] bytes) {

        String s = new String(bytes);
        if (s.length() == 0) {
            return;

        }
        String[] split = s.split(",");
        for (String ss : split) {
            System.out.println("Cookie: " + ss);
            String[] cookie = ss.split(":");
            cookies.add(new Cookie(cookie[0], cookie[1]));
        }
    }

    @Override
    public ByteBuffer buildSubMessage() {
        int totaleSize = 2;

        if (!isSuccess) {
            totaleSize += message.length();
            ByteBuffer buffer = ByteBuffer.allocate(totaleSize);
            buffer.put((byte) id);
            buffer.put((byte) 0);
            buffer.put(message.getBytes());
            return buffer;
        } else {

            byte[] bb = parseCookies();
            totaleSize += bb.length + 1 + 4 + data.length;
            ByteBuffer buffer = ByteBuffer.allocate(totaleSize);
            buffer.put((byte) id);
            buffer.put((byte) 1);
            buffer.put((byte) bb.length);
            buffer.put(bb);
            buffer.putInt(data.length);
            buffer.put(data);

            return buffer;
        }

    }

    private byte[] parseCookies() {
        StringBuilder builder = new StringBuilder();
        for (Cookie c : cookies) {
            builder.append(c.getKey()).append(":").append(c.getValue()).append(",");
        }

        return builder.toString().getBytes();
    }


    public List<Cookie> getCookies() {
        return cookies;
    }

    public void setCookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
