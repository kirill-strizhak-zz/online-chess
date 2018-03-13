package ks3.oc.conn.handlers;

import ks3.oc.conn.Receiver;
import ks3.oc.conn.Sender;

import java.io.BufferedReader;
import java.io.IOException;

public class CloseHandler implements MessageHandler {

    private final Receiver receiver;
    private final Sender sender;

    public CloseHandler(Receiver receiver, Sender sender) {
        this.receiver = receiver;
        this.sender = sender;
    }

    @Override
    public void handle(BufferedReader reader) throws IOException {
        receiver.deactivate();
        sender.deactivate("Receiver: client disconnected");
    }
}
