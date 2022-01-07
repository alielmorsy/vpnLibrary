package aie.vpnLibraryClient;

import aie.vpnLibrary.messages.*;
import aie.vpnLibrary.messages.enums.MethodType;
import aie.vpnLibrary.messages.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ClientThread extends Thread {
    private boolean withCharles;

    private String ip;
    private int port;

    private InputStream is;
    private OutputStream os;

    private String name;
    private Socket socket;

    public ClientThread(boolean withCharles, String ip, int port, String name) {
        this.withCharles = withCharles;
        this.ip = ip;
        this.port = port;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(ip, port);
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
                    writeData(new ErrorMessage().setErrorCode(-1).setErrorMessage("Null Message").buildMessage()); //TODO: Create Error Message
                    continue;
                }
                if (message.getMessageType() == BaseMessage.GET_NAME_MESSAGE) {
                    writeData(new NameMessage().setName(name).buildMessage());
                } else if (message.getMessageType() == BaseMessage.KEEP_ALIVE) {
                    continue;
                } else if (message.getMessageType() == BaseMessage.REQUEST_MESSAGE) {
                    Files.write(Paths.get(++ConnectionManager.counter + "request.txt"), buffer.array());

                    RequestMessage requestMessage = (RequestMessage) message;

                    ResponseMessage rm;
                    if (requestMessage.getMethod() == MethodType.GET) {
                        rm = ConnectionManager.getInstance(withCharles).requestGET(requestMessage);
                    } else {
                        rm = ConnectionManager.getInstance(withCharles).requestPOST(requestMessage);
                    }
                    writeData(rm.buildMessage());
                } else if (message.getMessageType() == BaseMessage.DISCONNECT_MESSAGE) {
                    DisconnectMessage dm = (DisconnectMessage) message;
                    System.out.println(dm.getErrorMessage());
                    socket.close();
                    System.exit(0);
                } else if (message.getMessageType() == BaseMessage.GET_IP_MESSAGE) {
                    GetIPMessage ip = new GetIPMessage();
                    ip.setState(GetIPMessage.SET);
                    ip.setIp(ConnectionManager.getInstance(false).getIP());
                    writeData(ip.buildMessage());
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

            ((Buffer) buffer).position(0);
            return buffer;
        } catch (Exception e) {
            if (!socket.isConnected()) {
                System.out.println("Server Disconnected");
                System.exit(0);
            }
            e.printStackTrace();
        }

        return null;
    }

    public void writeData(ByteBuffer byteBuffer) {
        try {
            Files.write(Paths.get(ConnectionManager.counter + "response.txt"), byteBuffer.array());
            ((java.nio.Buffer) byteBuffer).position(0);
            os.write(Utils.intToBytes(byteBuffer.capacity()));
            int sent = 0;
            int total = byteBuffer.capacity();

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
                    Thread.sleep(50);
                    os.flush();
                    return;
                }
            }
        } catch (Exception e) {
            if (!socket.isConnected()) {
                System.out.println("Server Disconnected");
                System.exit(0);
            }
            e.printStackTrace();

        }
    }

}
