package aie.vpnLibrary.server.exceptions;

public class RequestException extends Exception {
    public RequestException(String user, String message) {
        super("Request Exception from office: " + user + " With Message: " + message);

    }
}

