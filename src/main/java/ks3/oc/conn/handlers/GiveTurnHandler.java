package ks3.oc.conn.handlers;

import ks3.oc.MainWindow;

import java.io.BufferedReader;
import java.io.IOException;

public class GiveTurnHandler implements MessageHandler {

    private final MainWindow main;

    public GiveTurnHandler(MainWindow main) {
        this.main = main;
    }

    @Override
    public void handle(BufferedReader reader) throws IOException {
        main.setMyTurn(true);
    }
}
