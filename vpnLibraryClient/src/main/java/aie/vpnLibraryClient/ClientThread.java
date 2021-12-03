package aie.vpnLibraryClient;

import aie.vpnLibrary.messages.BaseMessage;
import aie.vpnLibrary.messages.IMessage;
import aie.vpnLibrary.messages.utils.Debug;
import aie.vpnLibrary.messages.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ClientThread extends Thread {
    private boolean withCharles;

    private String ip;
    private int port;

    private InputStream is;
    private OutputStream os;

    public ClientThread(boolean withCharles, String ip, int port) {
        this.withCharles = withCharles;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(ip, port);
            socket.setKeepAlive(true);
            socket.setSoTimeout(5 * 60 * 1000);
            is = socket.getInputStream();
            os = socket.getOutputStream();
            while (true) {
                ByteBuffer buffer = readData();
                if (buffer == null) {
                    if (socket.isClosed() || !socket.isConnected()) {
                        return;
                    } else {
                        Thread.sleep(100);
                        continue;
                    }
                }
                BaseMessage message = (BaseMessage) BaseMessage.createMessage(buffer);
                if (message == null) {
                    writeData(ByteBuffer.allocate(0)); //TODO: Create Error Message
                    continue;
                }
                if (message.getMessageType() == BaseMessage.KEEP_ALIVE) {
                    continue;
                } else if (message.getMessageType() == BaseMessage.REQUEST_MESSAGE) {
                    //TODO:  implement http connection thread
                    
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
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
            return buffer;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void writeData(ByteBuffer byteBuffer) {
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
                    return;
                }
            }
        } catch (Exception e) {

        }
    }

}
