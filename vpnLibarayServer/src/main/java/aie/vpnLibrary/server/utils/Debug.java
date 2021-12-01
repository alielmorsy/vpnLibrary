package aie.vpnLibrary.server.utils;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class Debug {
    public static  PrintStream VPN_EXCEPTION_DEBUG = null;
    static {
        try {
            VPN_EXCEPTION_DEBUG=new PrintStream("VPN_SERVER_EXCEPTION.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
