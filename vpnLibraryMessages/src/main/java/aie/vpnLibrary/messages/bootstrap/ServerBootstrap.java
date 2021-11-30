package aie.vpnLibrary.messages.bootstrap;

import aie.vpnLibrary.messages.bootstrap.channels.IChannel;
import aie.vpnLibrary.messages.utils.Debug;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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
            new Thread(() -> {
                Thread currentThread = Thread.currentThread();
                while (true) {
                    try {
                        Socket socket = serverSocket.accept();
                        executor.execute(() -> {
                            SocketChild child = new SocketChild(socket);
                            IChannel channel = channels.get(ServerBootstrap.Client_Connected);
                            if (channel != null) {
                                try {
                                    channel.runChannel(child);
                                } catch (IOException e) {

                                    e.printStackTrace(Debug.VPN_EXCEPTION_DEBUG);
                                }
                            }
                            IChannel mainChannel = channels.get(MAIN_CHANNEL);
                            if (mainChannel == null) {
                                Debug.VPN_EXCEPTION_DEBUG.println("Main Channel Not Implemented...Stopping VPN");
                                try {
                                    serverSocket.close();
                                } catch (IOException e) {
                                    currentThread.interrupt();

                                }

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
