package aie.vpnLibrary.server.exceptions;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(String userName) {
        super("User: " + userName + " Not Connected");
    }
}
