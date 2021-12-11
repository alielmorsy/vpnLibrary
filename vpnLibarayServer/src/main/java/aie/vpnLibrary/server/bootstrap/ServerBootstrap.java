package aie.vpnLibrary.server.bootstrap;

import aie.vpnLibrary.messages.RequestMessage;
import aie.vpnLibrary.messages.enums.PostType;
import aie.vpnLibrary.server.bootstrap.channels.IChannel;
import aie.vpnLibrary.server.bootstrap.channels.MainChannel;
import aie.vpnLibrary.messages.utils.Debug;
import aie.vpnLibrary.server.connnectionManager.ConnectionManager;
import aie.vpnLibrary.server.exceptions.OfficeInUse;
import aie.vpnLibrary.server.exceptions.RequestException;
import aie.vpnLibrary.server.exceptions.UserNotFoundException;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public class ServerBootstrap {
    public static final int Client_Connected = 0;
    public static final int MAIN_CHANNEL = 1;
    public static final int Send_Message_To_Client = 2;
    public static final int Receive_Message_From_Client = 3;

    public static final List<SocketChild> clients = new ArrayList<>();
    private Map<Integer, IChannel> channels;
    private Executor executor;

    public ServerBootstrap(Executor executor) {
        this.executor = executor;
        if (executor == null) {
            throw new NullPointerException("Executor Can't be null");
        }
        channels = new HashMap<>();
    }

    public void addChannel(int type, IChannel channel) {
        channels.put(type, channel);
    }

    public boolean bind(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(5 * 60 * 1000);

            new Thread(() -> {

                while (true) {
                    try {
                        Socket socket = serverSocket.accept();

                        executor.execute(() -> {
                            try {
                                socket.setKeepAlive(true);
                            } catch (SocketException e) {

                            }
                            SocketChild child = new SocketChild(socket);
                            IChannel channel = channels.get(ServerBootstrap.Client_Connected);
                            System.out.println("Client Connected");
                            if (channel != null) {
                                try {
                                    channel.runChannel(child);
                                } catch (IOException e) {

                                    e.printStackTrace(Debug.VPN_EXCEPTION_DEBUG);
                                }
                            }

                            IChannel mainChannel = new MainChannel();
                            ServerBootstrap.clients.add(child);
                            try {
                                child.setMainChannel(mainChannel);
                            } catch (IOException e) {
                                e.printStackTrace(Debug.VPN_EXCEPTION_DEBUG);
                            }
                            try {
                                ConnectionManager manager = new ConnectionManager("a") {
                                    @Override
                                    public void editRequest(RequestMessage requestMessage) {

                                    }
                                };
                                System.out.println(new String(manager.requestGET("https://xero.gg")));
                                System.out.println(new String(manager.requestPOST("https://xero.gg", "a=1", PostType.FORM_DATA)));
                            } catch (UserNotFoundException | OfficeInUse | RequestException e) {
                                e.printStackTrace();
                            }
                        });
                    } catch (IOException e) {

                        Debug.VPN_EXCEPTION_DEBUG.println("Exception In Bind Looping");
                        e.printStackTrace(Debug.VPN_EXCEPTION_DEBUG);
                    }
                }
            }).start();

            return true;
        } catch (Exception e) {
            Debug.VPN_EXCEPTION_DEBUG.println("Exception In Bind Method");
            e.printStackTrace(Debug.VPN_EXCEPTION_DEBUG);
        }
        return false;
    }
}
