package ks3.oc.conn.handlers;

import ks3.oc.chat.ChatDisplay;
import ks3.oc.main.MainWindow;

import java.io.BufferedReader;
import java.io.IOException;

public class ServerHandshakeHandler extends HandshakeHandler {

    public ServerHandshakeHandler(MainWindow main, ChatDisplay chat, BufferedReader reader) {
        super(reader, chat, main);
    }

    @Override
    public void handle() throws IOException {
        readName();
        main.opponentConnected();
    }
}
