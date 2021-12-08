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

    ConnectionManagerProxy(String clientName) throws UserNotFoundException {
        for (SocketChild child : ServerBootstrap.clients) {
            if (child.getName().equals(clientName)) {
                client = child;
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
        return null;
    }

    @Override
    public String requestPOST(String url, List<NameValuePair> data) throws UserNotFoundException, RequestException {
        return null;
    }

    @Override
    public String requestPOST(String url, String data) throws UserNotFoundException, RequestException {
        return null;
    }

}
