package aie.vpnLibrary.server.exceptions;

public class RequestException extends Exception {
    private int errorCode;

    public RequestException(String user, String message, int errorCode) {
        super("Request Exception from office: " + user + " With Message: " + message);
        this.errorCode = errorCode;
    }
}

