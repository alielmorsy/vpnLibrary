package aie.vpnLibrary.server.bootstrap.channels;

import aie.vpnLibrary.messages.IMessage;
import aie.vpnLibrary.server.bootstrap.SocketChild;

import java.io.IOException;
import java.nio.ByteBuffer;

public class GetNameChannel implements IChannel{
    @Override
    public void runChannel(SocketChild child) throws IOException {

    }

    @Override
    public void writeMessage(IMessage message) throws IOException {

    }

    @Override
    public IMessage getMessage() throws IOException {
        return null;
    }

  
}
