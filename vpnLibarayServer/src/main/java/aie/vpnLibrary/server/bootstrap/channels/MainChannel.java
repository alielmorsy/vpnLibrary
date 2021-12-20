package aie.vpnLibrary.server.bootstrap.channels;

import aie.vpnLibrary.messages.BaseMessage;
import aie.vpnLibrary.messages.IMessage;
import aie.vpnLibrary.messages.KeepAliveMessage;
import aie.vpnLibrary.messages.utils.Debug;
import aie.vpnLibrary.server.bootstrap.ServerBootstrap;
import aie.vpnLibrary.server.bootstrap.SocketChild;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class MainChannel implements IChannel {
    private Queue<IMessage> messageQueue;

    private Object lockObject;


    private SocketChild client;

    private boolean write = true;
    private Timer timer;

    public MainChannel() {
        messageQueue = new PriorityQueue<>();
    }

    @Override
    public void runChannel(SocketChild child) throws IOException {
        this.client = child;

        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("AddTasked");
                if (write) {
                    if (!client.writeData(new KeepAliveMessage().buildMessage())) {
                        timer.cancel();
                        timer = null;
                        System.gc();
                    }
                }

            }
        };
        timer.schedule(task, 20000, 40000);

    }

    @Override
    public void writeMessage(IMessage message) throws IOException {
        write = false;
        if (!client.writeData(message.buildMessage())) {
            timer.cancel();
            timer = null;
            System.gc();
        } else

            write = true;

    }

    @Override
    public IMessage getMessage() throws IOException {
        ByteBuffer buffer = client.readData();
        if (buffer == null) {
            ServerBootstrap.clients.remove(client);
            return null;
        }
        return BaseMessage.createMessage(buffer);

    }

    private void startLock() {
        synchronized (lockObject) {
            while (client.isConnected()) {
                IMessage message = messageQueue.poll();
                if (message == null) {
                    synchronized (lockObject) {
                        try {
                            System.out.println("Waited");
                            lockObject.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace(Debug.VPN_EXCEPTION_DEBUG);
                        }
                        System.out.println("End Waiting");
                        message = messageQueue.poll();
                        if (message == null) {
                            break;
                        }
                        System.out.println("Writing A Message");
                        write = false;
                        boolean result = client.writeData(message.buildMessage());
                        if (!result)
                            ServerBootstrap.clients.remove(client);
                        write = true;
                    }
                }
            }
        }

    }

    public MainChannel setLockObject(Object lockObject) {
        this.lockObject = lockObject;

        return this;
    }
}
