package aie.vpnLibrary.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class User {
    private String name;

    private String userIP;


    private InputStream is;
    private OutputStream os;

    public static User importUser(Socket socket) {
        User user = new User();
        user.userIP = socket.getInetAddress().getHostName();
        return user;
    }
}
