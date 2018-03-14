package ks3.oc.conn.handlers;

import ks3.oc.dialogs.DialogWindow;

public class ResetOfferHandler implements MessageHandler {

    private final DialogWindow newGameConfirmation;

    public ResetOfferHandler(DialogWindow newGameConfirmation) {
        this.newGameConfirmation = newGameConfirmation;
    }

    @Override
    public void handle() {
        newGameConfirmation.open();
    }
}
