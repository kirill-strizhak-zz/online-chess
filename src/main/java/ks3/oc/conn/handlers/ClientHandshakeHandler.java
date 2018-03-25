package ks3.oc.conn.handlers;

import ks3.oc.Protocol;
import ks3.oc.chat.ChatDisplay;
import ks3.oc.main.MainWindow;

import java.io.BufferedReader;
import java.io.IOException;

public class ClientHandshakeHandler extends HandshakeHandler {

    public ClientHandshakeHandler(MainWindow main, ChatDisplay chat, BufferedReader reader) {
        super(reader, chat, main);
    }

    @Override
    public void handle() throws IOException {
        readColor();
        readName();
        main.opponentConnected();
    }

    private void readColor() throws IOException {
        int myColor = reader.read();
        main.setMyColor(myColor);
        if (myColor == Protocol.WHITE) {
            main.setMyTurn(true);
            main.setOppColor(Protocol.BLACK);
        } else {
            main.setOppColor(Protocol.WHITE);
        }
    }
}
