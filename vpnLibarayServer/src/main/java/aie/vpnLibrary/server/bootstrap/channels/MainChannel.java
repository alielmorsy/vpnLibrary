package aie.vpnLibrary.server.bootstrap.channels;

import aie.vpnLibrary.messages.BaseMessage;
import aie.vpnLibrary.messages.IMessage;
import aie.vpnLibrary.messages.KeepAliveMessage;
import aie.vpnLibrary.messages.utils.Debug;
import aie.vpnLibrary.server.bootstrap.SocketChild;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class MainChannel implements IChannel {
    private Queue<IMessage> messageQueue;

    private final Object lockObject = new Object();


    private SocketChild client;

    public MainChannel() {
        messageQueue = new PriorityQueue<>();

    }

    @Override
    public void runChannel(SocketChild child) throws IOException {
        this.client = child;

        Timer timer = new Timer();
        synchronized (lockObject) {
            while (client.isConnected()) {
                IMessage message = messageQueue.poll();
                if (message == null) {
                    synchronized (lockObject) {
                        try {
                            lockObject.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace(Debug.VPN_EXCEPTION_DEBUG);
                        }
                        message = messageQueue.poll();
                        if (message == null) {
                            break;
                        }
                        client.writeData(message.buildMessage());
                    }
                }
            }
        }
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                messageQueue.add(new KeepAliveMessage());
            }
        };
        timer.schedule(task, 40000, 40000);
    }

    @Override
    public void writeMessage(IMessage message) throws IOException {
        messageQueue.add(message);
        lockObject.notifyAll();

    }

    @Override
    public IMessage getMessage() throws IOException {
        ByteBuffer buffer = client.readData();
        if (buffer == null) {
            return null;
        }
        return BaseMessage.createMessage(buffer);

    }

}
