package ks3.oc.conn.handlers;

import ks3.oc.main.MainWindow;
import ks3.oc.Protocol;

import java.io.BufferedReader;
import java.io.IOException;

public class ColorHandler implements MessageHandler {

    private final MainWindow main;
    private final BufferedReader reader;

    public ColorHandler(MainWindow main, BufferedReader reader) {
        this.main = main;
        this.reader = reader;
    }

    @Override
    public void handle() throws IOException {
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
