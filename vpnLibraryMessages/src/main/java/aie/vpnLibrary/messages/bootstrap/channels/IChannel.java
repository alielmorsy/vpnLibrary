package aie.vpnLibrary.messages.bootstrap.channels;

import aie.vpnLibrary.messages.bootstrap.SocketChild;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface IChannel {

    void runChannel(SocketChild child) throws IOException;

    void putData(ByteBuffer buffer) throws IOException;

    ByteBuffer getData() throws IOException;
}
