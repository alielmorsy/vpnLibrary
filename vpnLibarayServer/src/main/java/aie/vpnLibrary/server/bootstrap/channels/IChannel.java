package aie.vpnLibrary.server.bootstrap.channels;

import aie.vpnLibrary.messages.IMessage;
import aie.vpnLibrary.server.bootstrap.SocketChild;

import java.io.IOException;

public interface IChannel {

    void runChannel(SocketChild child) throws IOException;

    void writeMessage(IMessage message) throws IOException;

    IMessage getMessage() throws IOException;
}
