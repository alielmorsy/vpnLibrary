package aie.vpnLibrary.server;

public class ServerConfiguration {
    private int port;
    private int numberOfPools;
    private boolean clientAuth;

    public static ServerConfiguration createConfiguration() {
        return new ServerConfiguration();
    }

    public int getPort() {
        return port;
    }

    public ServerConfiguration setPort(int port) {
        this.port = port;
        return this;
    }

    public boolean isClientAuth() {
        return clientAuth;
    }

    public ServerConfiguration setClientAuth(boolean clientAuth) {
        this.clientAuth = clientAuth;
        return this;
    }

    public int getNumberOfPools() {
        return numberOfPools;
    }

    public ServerConfiguration setNumberOfPools(int numberOfPools) {
        this.numberOfPools = numberOfPools;
        return this;
    }
}
