package aie.vpnLibrary.server.connnectionManager;

import aie.vpnLibrary.messages.enums.PostType;
import aie.vpnLibrary.server.exceptions.RequestException;
import aie.vpnLibrary.server.exceptions.UserNotFoundException;
import aie.vpnLibrary.server.model.NameValuePair;

import java.util.List;

public interface IConnection {

    /**
     * @param url the http url
     * @return http response
     */
    String requestGET(String url) throws UserNotFoundException, RequestException;

    String requestPOST(String url, List<NameValuePair> data) throws UserNotFoundException, RequestException;


    String requestPOST(String url, String data, PostType contentType) throws UserNotFoundException, RequestException;

    void releaseClient() throws UserNotFoundException;
}
