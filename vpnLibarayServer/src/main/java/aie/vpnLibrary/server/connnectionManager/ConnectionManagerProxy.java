package aie.vpnLibrary.server.connnectionManager;

import aie.vpnLibrary.messages.enums.PostType;
import aie.vpnLibrary.server.bootstrap.ServerBootstrap;
import aie.vpnLibrary.server.bootstrap.SocketChild;
import aie.vpnLibrary.server.exceptions.RequestException;
import aie.vpnLibrary.server.exceptions.UserNotFoundException;
import aie.vpnLibrary.server.model.NameValuePair;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class ConnectionManagerProxy implements IConnection {

    private IConnection connection;

    private SocketChild client;


    public ConnectionManagerProxy(String clientName, Class<IConnection> connectionClass) throws UserNotFoundException {


        for (SocketChild child : ServerBootstrap.clients) {
            if (child.getName().equals(clientName)) {
                client = child;
                createIConnectionObject(connectionClass);
                return;
            }
        }
        throw new UserNotFoundException(clientName);
        //TODO: Create ConnectionManager Object
    }

    @Override
    public String requestGET(String url) throws UserNotFoundException, RequestException {
        if (!client.isConnected()) {
            ServerBootstrap.clients.remove(client);
            throw new UserNotFoundException(client.getName());
        }
        return connection.requestGET(url);
    }

    @Override
    public String requestPOST(String url, List<NameValuePair> data) throws UserNotFoundException, RequestException {
        if (!client.isConnected()) {
            ServerBootstrap.clients.remove(client);
            throw new UserNotFoundException(client.getName());
        }
        return connection.requestPOST(url, data);
    }

    @Override
    public String requestPOST(String url, String data, PostType contentType) throws UserNotFoundException, RequestException {
        if (!client.isConnected()) {
            ServerBootstrap.clients.remove(client);
            throw new UserNotFoundException(client.getName());
        }
        return connection.requestPOST(url, data, contentType);
    }

    @Override
    public void releaseClient() throws UserNotFoundException {
        if (!client.isConnected()) {
            throw new UserNotFoundException(client.getName());
        }
        connection.releaseClient();
    }

    private void createIConnectionObject(Class<IConnection> clazz) {
        Constructor<?> c = clazz.getDeclaredConstructors()[0];
        try {
            connection = (IConnection) c.newInstance(client);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Unknown Connection Class");
        }

    }

}
