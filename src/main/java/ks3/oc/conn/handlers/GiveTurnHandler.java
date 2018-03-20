package ks3.oc.conn.handlers;

import ks3.oc.main.MainWindow;

public class GiveTurnHandler implements MessageHandler {

    private final MainWindow main;

    public GiveTurnHandler(MainWindow main) {
        this.main = main;
    }

    @Override
    public void handle() {
        main.setMyTurn(true);
    }
}
