package ks3.oc.conn.handlers;

import ks3.oc.dialogs.DialogWindow;

import java.io.BufferedReader;
import java.io.IOException;

public class ResetOfferHandler implements MessageHandler {

    private final DialogWindow newGameConfirmation;

    public ResetOfferHandler(DialogWindow newGameConfirmation) {
        this.newGameConfirmation = newGameConfirmation;
    }

    @Override
    public void handle(BufferedReader reader) throws IOException {
        newGameConfirmation.open();
    }
}
