package aie.vpnLibraryClient;

public class Test {
    public static void main(String[] args) {
        ClientThread clientThread = new ClientThread(false, "127.0.0.1", 9999, "a");
        clientThread.start();
    }
}
