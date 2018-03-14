package ks3.oc.conn.handlers;

import ks3.oc.MainWindow;

public class ResetAcceptHandler implements MessageHandler {

    private final MainWindow main;

    public ResetAcceptHandler(MainWindow main) {
        this.main = main;
    }

    @Override
    public void handle() {
        main.reset();
    }
}
