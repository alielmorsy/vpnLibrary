package aie.vpnLibrary.server.bootstrap.channels;

import aie.vpnLibrary.messages.BaseMessage;
import aie.vpnLibrary.messages.GetNameMessage;
import aie.vpnLibrary.messages.IMessage;
import aie.vpnLibrary.messages.NameMessage;
import aie.vpnLibrary.server.bootstrap.ServerBootstrap;
import aie.vpnLibrary.server.bootstrap.SocketChild;

import java.io.IOException;
import java.nio.ByteBuffer;

public class GetNameChannel implements IChannel {
    private SocketChild child;

    @Override
    public void runChannel(SocketChild child) throws IOException {
        this.child = child;
        writeMessage(new GetNameMessage());
        if (child == null) return;

        NameMessage message = (NameMessage) getMessage();
        child.setName(message.getName());
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


}
