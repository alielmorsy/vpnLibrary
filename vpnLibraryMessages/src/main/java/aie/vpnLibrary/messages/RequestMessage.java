package aie.vpnLibrary.messages;

import aie.vpnLibrary.messages.enums.MethodType;
import aie.vpnLibrary.messages.enums.PostType;
import aie.vpnLibrary.messages.models.Cookie;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RequestMessage extends BaseMessage {
    private final Map<String, String> additionalHeaders = new HashMap<>();
    private final List<Cookie> cookies = new ArrayList<>();

    private MethodType method = MethodType.GET;
    private String url;
    private PostType postType;
    private byte[] postContent = new byte[0];


    private int id;

    public RequestMessage() {
        super(BaseMessage.REQUEST_MESSAGE);
    }


    @Override
    public IMessage construct(ByteBuffer buffer) {


        int method = buffer.get();
        this.method = MethodType.values()[method];

        buffer.get();
        byte[] headersBytes = new byte[Short.toUnsignedInt(buffer.getShort())];
        buffer.get(headersBytes);
        parseHeaders(headersBytes);

        byte[] urlBytes = new byte[Short.toUnsignedInt(buffer.getShort())];
        buffer.get(urlBytes);

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
        return this;
    }


    @Override
    public ByteBuffer buildSubMessage() {
        byte[] headers = parseHeaders();

        byte[] cookies = parseCookies();
        int totalSize = 4 + headers.length + 2 + url.length() + 1 + cookies.length + 1;
        if (method == MethodType.POST) {
            totalSize += 2 + postContent.length;
        }

        ByteBuffer buffer = ByteBuffer.allocate(totalSize);
        buffer.put((byte) method.ordinal()).put((byte) 0).putShort((short) headers.length);
        buffer.put(headers);
        buffer.putShort((short) url.length()).put(url.getBytes());

        buffer.put((byte) cookies.length);

        buffer.put(cookies);

        if (method == MethodType.POST) {
            buffer.put((byte) postType.ordinal());
            buffer.putShort((short) postContent.length).put(postContent);
        }

        return buffer;

    }


    private void parseHeaders(byte[] bytes) {
        String s = new String(bytes);
        if (s.length() == 0) return;

        String[] a = s.split(",");
        for (String header : a) {
            System.out.println("Header: " + header);
            String[] hh = header.replace('\0', ',').split(":");
            additionalHeaders.put(hh[0], hh[1].replace('\1', ':'));
        }
    }

    private void parseCookies(byte[] cookiesBytes) {
        String s = new String(cookiesBytes);

        String[] a = s.split(",");
        if (s.length() == 0) {
            return;
        }

        for (String header : a) {

            String[] hh = header.split(":");
            setCookie(hh[0], hh[1]);
        }
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
        value = value.replace(':', '\1');
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


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<String, String> getAdditionalHeaders() {
        return additionalHeaders;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }
}
