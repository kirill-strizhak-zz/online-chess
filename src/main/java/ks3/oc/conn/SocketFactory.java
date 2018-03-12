package ks3.oc.conn;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketFactory {

    public Socket createServer(int port) throws IOException {
        ServerSocket server = new ServerSocket(port);
        return server.accept();
    }

    public Socket createClient(String host, int port) throws IOException {
        return new Socket(host, port);
    }
}
