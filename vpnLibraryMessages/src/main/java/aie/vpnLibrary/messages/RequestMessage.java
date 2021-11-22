package aie.vpnLibrary.messages;

import aie.vpnLibrary.messages.enums.MethodType;
import aie.vpnLibrary.messages.enums.PostType;
import aie.vpnLibrary.messages.models.Cookie;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestMessage extends BaseMessage {
    private final Map<String, String> additionalHeaders = new HashMap<>();
    private final List<Cookie> cookies = new ArrayList<>();

    private MethodType method = MethodType.GET;
    private String url;
    private PostType postType;
    private byte[] postContent = new byte[0];

    public RequestMessage() {
        super(BaseMessage.REQUEST_MESSAGE);
    }

    public RequestMessage(ByteBuffer byteBuffer) {
        super(BaseMessage.REQUEST_MESSAGE);
        construct(byteBuffer);
    }

    @Override
    public void construct(ByteBuffer buffer) {
        ((Buffer) buffer).position(0);

        System.out.println(new String(buffer.array()));
        int method = buffer.get();
        this.method = MethodType.values()[method];

        buffer.get();
        buffer.get();
        byte[] headersBytes = new byte[Byte.toUnsignedInt(buffer.get())];
        buffer.get(headersBytes);
        parseHeaders(headersBytes);

        byte[] urlBytes = new byte[Byte.toUnsignedInt(buffer.get())];
        buffer.get(urlBytes);
        System.out.println(urlBytes.length);
        url = new String(urlBytes, StandardCharsets.UTF_8);

        byte[] cookiesBytes = new byte[Byte.toUnsignedInt(buffer.get())];

        buffer.get(cookiesBytes);

        parseCookies(cookiesBytes);

        if (this.method == MethodType.POST) {
            postType = PostType.values()[buffer.get()];
            byte[] postContent = new byte[buffer.getShort()];

            buffer.get(postContent);
            this.postContent = postContent;
        }
    }

    private void parseHeaders(byte[] bytes) {
        String s = new String(bytes);
        if (s.length() == 0) return;
        String[] a = s.split(",");
        for (String header : a) {
            String[] hh = header.split(":");
            additionalHeaders.put(hh[0], hh[1]);
        }
    }

    private void parseCookies(byte[] cookiesBytes) {
        String s = new String(cookiesBytes);

        String[] a = s.split(",");
        if (s.length() == 0) {
            return;
        }

        for (String header : a) {
            System.out.println("Cookie: " + header);
            String[] hh = header.split(":");
            setCookie(hh[0], hh[1]);
        }
    }

    @Override
    public ByteBuffer buildSubMessage() {
        byte[] headers = parseHeaders();
        byte[] cookies = parseCookies();
        int totalSize = 4 + headers.length + 1 + url.length() + 1 + cookies.length + 1;
        if (method == MethodType.POST) {
            totalSize += 2 + postContent.length;
        }

        ByteBuffer buffer = ByteBuffer.allocate(totalSize);
        buffer.put((byte) method.ordinal()).put((byte) 0).put((byte) 0).put((byte) headers.length);
        buffer.put(headers);
        buffer.put((byte) url.length()).put(url.getBytes());

        buffer.put((byte) cookies.length);

        buffer.put(cookies);

        if (method == MethodType.POST) {
            buffer.put((byte) postType.ordinal());
            buffer.putShort((short) postContent.length).put(postContent);
        }

        return buffer;

    }


    private byte[] parseHeaders() {
        StringBuilder s = new StringBuilder();
        for (Map.Entry<String, String> entry : additionalHeaders.entrySet()) {
            s.append(entry.getKey()).append(':').append(entry.getValue()).append(',');
        }
        return s.toString().getBytes();
    }

    private byte[] parseCookies() {
        StringBuilder builder = new StringBuilder();

        for (Cookie cookie : cookies) {

            builder.append(cookie.getKey()).append(":").append(cookie.getValue()).append(",");
        }
        return builder.toString().getBytes();
    }


    public void setHeader(String name, String value) {
        additionalHeaders.put(name, value);
    }

    public void setCookie(String name, String value) {
        cookies.add(new Cookie(name, value));
    }


    public MethodType getMethod() {
        return method;
    }

    public void setMethod(MethodType method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public PostType getPostType() {
        return postType;
    }

    public void setPostType(PostType postType) {
        this.postType = postType;
    }

    public byte[] getPostContent() {
        return postContent;
    }

    public void setPostContent(byte[] postContent) {
        this.postContent = postContent;
    }


}
