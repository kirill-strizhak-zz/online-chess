package ks3.oc.conn.handlers;

import ks3.oc.conn.Receiver;
import ks3.oc.conn.Sender;

import java.io.BufferedReader;

public class CloseHandler implements MessageHandler {

    private final Receiver receiver;
    private final Sender sender;

    public CloseHandler(Receiver receiver, Sender sender, BufferedReader reader) {
        this.receiver = receiver;
        this.sender = sender;
    }

    @Override
    public void handle() {
        receiver.deactivate();
        sender.deactivate("Receiver: client disconnected");
    }
}
