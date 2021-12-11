package aie.vpnLibrary.server;

import aie.vpnLibrary.messages.BaseMessage;
import aie.vpnLibrary.messages.RequestMessage;
import aie.vpnLibrary.messages.ResponseMessage;
import aie.vpnLibrary.messages.enums.MethodType;
import aie.vpnLibrary.messages.enums.PostType;
import aie.vpnLibrary.server.connnectionManager.ConnectionManager;
import aie.vpnLibrary.server.exceptions.OfficeInUse;
import aie.vpnLibrary.server.exceptions.RequestException;
import aie.vpnLibrary.server.exceptions.UserNotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class Test {
    public static void main(String[] args) {
        VPNServer server = new VPNServer(new ServerConfiguration().setClientAuth(false).setPort(9999).setNumberOfPools(6));
        server.bind();

    }
}
