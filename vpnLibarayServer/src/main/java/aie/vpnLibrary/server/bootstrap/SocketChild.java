package aie.vpnLibrary.server.bootstrap;

import aie.vpnLibrary.server.bootstrap.channels.IChannel;
import aie.vpnLibrary.messages.utils.Debug;
import aie.vpnLibrary.messages.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public class SocketChild {
    private Socket socket;

    private InputStream is;
    private OutputStream os;

    private OnUserDisconnected observer;

    private boolean connected = true;

    private IChannel mainChannel;

    private String name;

    private boolean used = false;

    public SocketChild(Socket socket) {
        this.socket = socket;

        init();
    }

    private void init() {
        try {
            is = socket.getInputStream();
            os = socket.getOutputStream();

        } catch (IOException e) {
            e.printStackTrace(Debug.VPN_EXCEPTION_DEBUG);
        }
    }

    public ByteBuffer readData() {
        try {
            byte[] bb = new byte[4];

            int c = is.read(bb);
            if (bb[0] != 0) {
                System.out.println("The Fuck Fatal Error");

                if (c == -1) {

                    throw new IOException("Failed To Read");
                }
                System.exit(-1);
            }
            int size = Utils.convertByteArrayToInt(bb);

            System.out.println("Data Size: +" + size);
            ByteBuffer buffer = ByteBuffer.allocate(size);
            if (size > 1024) {
                byte[] b = new byte[1024];

                int read;
                while ((read = is.read(b)) > 0) {
                    size -= read;
                    buffer.put(b, 0, read);
                    System.out.printf("Size: %d, read: %d, available:%d%n", size, read, is.available());
                    if (size <= 0) {
                        break;
                    }
                }
            } else {
                byte[] bytes = new byte[size];

                int read = is.read(bytes);
                buffer.put(bytes, 0, read);
            }
            System.out.println(new String(buffer.array()));
            ((Buffer) buffer).position(0);
            return buffer;
        } catch (Exception e) {

            e.printStackTrace(Debug.VPN_EXCEPTION_DEBUG);
            if (!socket.isConnected() || socket.isClosed()) {
                //observer.onDisconnected(this);
                connected = false;
            }
        }

        return null;
    }

    public boolean writeData(ByteBuffer byteBuffer) {
        try {
            os.write(Utils.intToBytes(byteBuffer.capacity()));
            int sent = 0;
            int total = byteBuffer.capacity();
            ((java.nio.Buffer) byteBuffer).position(0);
            byte[] bytes;
            while (true) {
                int size = Math.abs(total - sent);


                if (size > 1024) {


                    bytes = new byte[1024];
                    byteBuffer.get(bytes);

                    sent += 1024;
                    os.write(bytes);
                } else if (size > 0) {
                    bytes = new byte[size];
                    byteBuffer.get(bytes);
                    os.write(bytes);
                    bytes = null;
                    byteBuffer = null;
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace(Debug.VPN_EXCEPTION_DEBUG);
            if (!socket.isConnected() || socket.isClosed()) {
                //observer.onDisconnected(this);
                connected = false;
            }
        }
        return false;
    }

    public void setObserver(OnUserDisconnected observer) {
        this.observer = observer;
    }

    public IChannel getMainChannel() {
        return mainChannel;
    }

    public void setMainChannel(IChannel mainChannel) throws IOException {
        this.mainChannel = mainChannel;
        mainChannel.runChannel(this);
    }

    public boolean isConnected() {
        return connected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean iSBlocked() {
        return used;
    }

    public void block() {
        used = true;
    }

    public void release() {
        used = false;
    }
}
