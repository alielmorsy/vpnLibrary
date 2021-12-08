package aie.vpnLibrary.server.connnectionManager;

import aie.vpnLibrary.server.bootstrap.SocketChild;
import aie.vpnLibrary.server.exceptions.RequestException;
import aie.vpnLibrary.server.exceptions.UserNotFoundException;
import aie.vpnLibrary.server.model.NameValuePair;

import java.util.List;

public class ConnectionManager implements IConnection {

    private SocketChild client;
    private Object lockObject;

    public static IConnection createConnectionManager(String uerName, Object lockObject) throws UserNotFoundException {
        return new ConnectionManagerProxy(uerName, lockObject);
    }


    ConnectionManager(SocketChild client, Object lockObject) {
        this.client = client;
        this.lockObject = lockObject;
        client.block();
    }

    @Override
    public String requestGET(String url) throws UserNotFoundException, RequestException {
        return null;
    }

    @Override
    public String requestPOST(String url, List<NameValuePair> data) throws UserNotFoundException, RequestException {
        return null;
    }

    @Override
    public String requestPOST(String url, String data, String contentType) throws UserNotFoundException, RequestException {
        return null;
    }

    @Override
    public void releaseClient() throws UserNotFoundException {
        client.release();
    }


}
