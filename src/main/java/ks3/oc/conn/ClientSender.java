package ks3.oc.conn;

import ks3.oc.MainWindow;
import ks3.oc.Protocol;
import ks3.oc.swing.SwingMainWindow;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

public class ClientSender extends Sender {

    private static final Logger LOGGER = Logger.getLogger(ClientSender.class);

    public ClientSender(MainWindow main, String host, int port) {
        super(main, host, port);
    }

    @Override
    protected Socket openConnection(String host, int port) throws IOException {
        LOGGER.info("Connecting to server");
        Socket sock = getSocketFactory().createClient(host, port);
        sock.getOutputStream().write(Protocol.IDENT);
        int header = sock.getInputStream().read();
        if (header != Protocol.IDENT) {
            throw new IOException("Invalid identification token received");
        }
        return sock;
    }
}
