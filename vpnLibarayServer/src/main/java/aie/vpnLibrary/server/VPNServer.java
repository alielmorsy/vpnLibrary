package aie.vpnLibrary.server;

import aie.vpnLibrary.server.bootstrap.ServerBootstrap;
import aie.vpnLibrary.server.bootstrap.channels.GetNameChannel;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class VPNServer implements IServer {
    private ServerConfiguration configuration;
    private Executor threadPool;

    public VPNServer(ServerConfiguration configuration) {
        this.configuration = configuration;
        threadPool = Executors.newFixedThreadPool(configuration.getNumberOfPools());
    }

    @Override
    public boolean bind() {
        ServerBootstrap bootstrap = new ServerBootstrap(threadPool);
        bootstrap.addChannel(ServerBootstrap.Client_Connected, new GetNameChannel());
        bootstrap.bind(configuration.getPort());

        return false;
    }
}
