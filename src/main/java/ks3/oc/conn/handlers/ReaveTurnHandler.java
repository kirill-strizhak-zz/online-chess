package ks3.oc.conn.handlers;

import ks3.oc.MainWindow;

public class ReaveTurnHandler implements MessageHandler {

    private final MainWindow main;

    public ReaveTurnHandler(MainWindow main) {
        this.main = main;
    }

    @Override
    public void handle() {
        main.setMyTurn(false);
    }
}
