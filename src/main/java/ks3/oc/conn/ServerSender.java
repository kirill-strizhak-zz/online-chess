package ks3.oc.conn;

import ks3.oc.chat.ChatDisplay;
import ks3.oc.main.MainWindow;
import ks3.oc.Protocol;
import ks3.oc.board.BoardState;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

public class ServerSender extends Sender {

    private static final Logger LOGGER = Logger.getLogger(ServerSender.class);

    public ServerSender(MainWindow main, BoardState board, ChatDisplay chat, String host, int port) {
        super(main, board, chat, host, port);
    }

    @Override
    protected Socket openConnection(String host, int port) throws IOException {
        LOGGER.info("Waiting for connection...");
        Socket socket = getSocketFactory().createServer(port);
        int header = socket.getInputStream().read();
        if (header != Protocol.IDENT) {
            socket.close();
            throw new IOException("Invalid identification token received");
        }
        socket.getOutputStream().write(Protocol.IDENT);
        return socket;
    }
}
