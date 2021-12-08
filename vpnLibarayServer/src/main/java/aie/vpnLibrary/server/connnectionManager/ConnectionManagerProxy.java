package aie.vpnLibrary.server.connnectionManager;

import aie.vpnLibrary.server.bootstrap.ServerBootstrap;
import aie.vpnLibrary.server.bootstrap.SocketChild;
import aie.vpnLibrary.server.exceptions.RequestException;
import aie.vpnLibrary.server.exceptions.UserNotFoundException;
import aie.vpnLibrary.server.model.NameValuePair;

import java.util.List;

public class ConnectionManagerProxy implements IConnection {

    private IConnection connection;

    private SocketChild client;

    private final Object lockObject;

    ConnectionManagerProxy(String clientName, Object lockObject) throws UserNotFoundException {
        this.lockObject = lockObject;

        for (SocketChild child : ServerBootstrap.clients) {
            if (child.getName().equals(clientName)) {
                client = child;
                this.connection = new ConnectionManager(client, lockObject);
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
    public String requestPOST(String url, String data, String contentType) throws UserNotFoundException, RequestException {
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

}
