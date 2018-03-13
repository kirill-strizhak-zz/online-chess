package ks3.oc.conn.handlers;

import ks3.oc.MainWindow;
import ks3.oc.Protocol;

import java.io.BufferedReader;
import java.io.IOException;

public class ColorHandler implements MessageHandler {

    private final MainWindow main;

    public ColorHandler(MainWindow main) {
        this.main = main;
    }

    @Override
    public void handle(BufferedReader reader) throws IOException {
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
