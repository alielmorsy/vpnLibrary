package aie.vpnLibrary.server.connnectionManager;

import aie.vpnLibrary.messages.BaseMessage;
import aie.vpnLibrary.messages.GetIPMessage;
import aie.vpnLibrary.messages.RequestMessage;
import aie.vpnLibrary.messages.ResponseMessage;
import aie.vpnLibrary.messages.enums.MethodType;
import aie.vpnLibrary.messages.enums.PostType;
import aie.vpnLibrary.messages.models.Cookie;
import aie.vpnLibrary.messages.utils.Debug;
import aie.vpnLibrary.server.bootstrap.ServerBootstrap;
import aie.vpnLibrary.server.bootstrap.SocketChild;
import aie.vpnLibrary.server.bootstrap.channels.MainChannel;
import aie.vpnLibrary.server.exceptions.OfficeInUse;
import aie.vpnLibrary.server.exceptions.RequestException;
import aie.vpnLibrary.server.exceptions.UserNotFoundException;
import aie.vpnLibrary.server.model.NameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public abstract class ConnectionManager implements IConnection {

    protected List<Cookie> cookies = new ArrayList<>();

    private SocketChild client;

    private String ip;


    public ConnectionManager(String clientName) throws UserNotFoundException, OfficeInUse {


        for (SocketChild child : ServerBootstrap.clients) {
            if (child.getName().equals(clientName)) {
                client = child;
                if (child.iSBlocked()) {
                    throw new OfficeInUse(clientName);
                }
                MainChannel c = (MainChannel) client.getMainChannel();
                c.setLockObject(new Object());
                child.block();
                return;
            }
        }
        throw new UserNotFoundException(clientName);
        //TODO: Create ConnectionManager Object
    }

    @Override
    public byte[] requestGET(String url) throws UserNotFoundException, RequestException {
        if (!client.isConnected()) {
            ServerBootstrap.clients.remove(client);
            throw new UserNotFoundException(client.getName());
        }
        RequestMessage message = new RequestMessage();
        message.setUrl(url);
        message.setMethod(MethodType.GET);
        editRequest(message);
        return extractResponse(runRequest(message));
    }

    @Override
    public byte[] requestPOST(String url, List<NameValuePair> data) throws UserNotFoundException, RequestException {
        if (!client.isConnected()) {
            ServerBootstrap.clients.remove(client);
            throw new UserNotFoundException(client.getName());
        }
        return requestPOST(url, convertToFormData(data), PostType.FORM_DATA);
    }


    @Override
    public byte[] requestPOST(String url, String data, PostType contentType) throws UserNotFoundException, RequestException {
        if (!client.isConnected()) {
            ServerBootstrap.clients.remove(client);
            throw new UserNotFoundException(client.getName());
        }


        return requestPOST(url, data.getBytes(), contentType);
    }

    @Override
    public byte[] requestPOST(String url, byte[] data, PostType contentType) throws UserNotFoundException, RequestException {
        if (!client.isConnected()) {
            ServerBootstrap.clients.remove(client);
            throw new UserNotFoundException(client.getName());
        }
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setMethod(MethodType.POST);
        requestMessage.setUrl(url);
        requestMessage.setPostType(contentType);
        requestMessage.setPostContent(data);
        editRequest(requestMessage);
        ResponseMessage m = runRequest(requestMessage);
        return extractResponse(m);
    }

    private String convertToFormData(List<NameValuePair> data) {
        StringBuilder builder = new StringBuilder();
        for (NameValuePair pair : data) {
            try {
                builder.append(pair.getKey()).append('=').append(URLEncoder.encode(pair.getValue(), "utf-8")).append("&");
            } catch (UnsupportedEncodingException e) {

            }
        }
        return builder.substring(0, builder.length() - 1);
    }

    @Override
    public void releaseClient() throws UserNotFoundException {
        if (!client.isConnected()) {
            throw new UserNotFoundException(client.getName());
        }
        client.release();
    }

    public abstract void editRequest(RequestMessage requestMessage);

    private ResponseMessage runRequest(RequestMessage message) {
        try {
            message.getCookies().addAll(cookies);
            System.out.println("Write Message");
            client.getMainChannel().writeMessage(message);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                try {
                    releaseClient();
                } catch (UserNotFoundException userNotFoundException) {
                    userNotFoundException.printStackTrace(Debug.VPN_EXCEPTION_DEBUG);
                }
            }

            return (ResponseMessage) client.getMainChannel().getMessage();
        } catch (IOException e) {
            e.printStackTrace(Debug.VPN_EXCEPTION_DEBUG);
            client.release();
        }
        return null;
    }

    private byte[] extractResponse(ResponseMessage m) throws RequestException {
        if (m == null) {
            throw new RequestException(client.getName(), "Connection With Office Is dead", -1);
        }
        if (!m.isSuccess()) {
            throw new RequestException(client.getName(), m.getMessage(), m.getErrorCode());
        }
        if (m.getCookies().size() != 0) {
            cookies.clear();
            cookies.addAll(m.getCookies());
        }


        return m.getData();
    }

    @Override
    public String getIP() throws UserNotFoundException, RequestException {
        if (ip != null) {
            return ip;
        }
        if (!client.isConnected()) {
            throw new UserNotFoundException(client.getName());
        }
        GetIPMessage message = new GetIPMessage().setState(GetIPMessage.GET);
        try {
            client.getMainChannel().writeMessage(message);
            BaseMessage message1 = (BaseMessage) client.getMainChannel().getMessage();
            if (!(message1 instanceof GetIPMessage)) {
                throw new RequestException(client.getName(), "Requests entered in each other", -500);
            }
            message = (GetIPMessage) message1;
            ip = message.getIp();
            return ip;
        } catch (IOException e) {
            e.printStackTrace(Debug.VPN_EXCEPTION_DEBUG);

        }
        return null;
    }


}
