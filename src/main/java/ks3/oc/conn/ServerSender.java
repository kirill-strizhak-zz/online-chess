package ks3.oc.conn;

import ks3.oc.Protocol;
import ks3.oc.swing.SwingMainWindow;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

public class ServerSender extends Sender {

    private static final Logger LOGGER = Logger.getLogger(ServerSender.class);

    public ServerSender(SwingMainWindow own, String host, int port) {
        super(own, host, port);
    }

    @Override
    protected Socket openConnection(String host, int port) throws IOException {
        LOGGER.info("Waiting for connection...");
        Socket socket = getSocketFactory().createServer(port);
        byte b = (byte) socket.getInputStream().read();
        if (b != Protocol.IDENT) {
            socket.close();
            throw new IOException("Invalid identification token received");
        }
        socket.getOutputStream().write(Protocol.IDENT);
        return socket;
    }
}
