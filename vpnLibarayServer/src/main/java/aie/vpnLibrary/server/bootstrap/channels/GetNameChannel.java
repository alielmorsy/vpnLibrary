package aie.vpnLibrary.server.bootstrap.channels;

import aie.vpnLibrary.messages.*;
import aie.vpnLibrary.server.bootstrap.ServerBootstrap;
import aie.vpnLibrary.server.bootstrap.SocketChild;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

public class GetNameChannel implements IChannel {
    private SocketChild child;

    @Override
    public void runChannel(SocketChild child) throws IOException {
        this.child = child;
        writeMessage(new GetNameMessage());
        if (child == null) return;

        NameMessage message = (NameMessage) getMessage();
        String name = message.getName();
        if (checkIfThereClientConnectedAlready(name)) {
            writeMessage(new DisconnectMessage().setErrorMessage("Office with name: "+name+" Already Connected"));
        } else
            child.setName(name);
    }

    @Override
    public void writeMessage(IMessage message) throws IOException {
        if (!child.writeData(message.buildMessage())) {
            ServerBootstrap.clients.remove(child);
            child = null;
            return;
        }
    }

    @Override
    public IMessage getMessage() throws IOException {
        ByteBuffer buffer = child.readData();

        return BaseMessage.createMessage(buffer);
    }

    private boolean checkIfThereClientConnectedAlready(String name) {
        for (SocketChild c : ServerBootstrap.clients) {
            if (Objects.equals(c.getName(), name)) {
                if (c.isConnected()) {
                    return true;
                }
                ServerBootstrap.clients.remove(c);
            }
        }
        return false;
    }

}
