package ks3.oc.conn.handlers;

import ks3.oc.Protocol;
import ks3.oc.chat.ChatDisplay;
import ks3.oc.main.MainWindow;

import java.io.BufferedReader;
import java.io.IOException;

abstract class HandshakeHandler implements MessageHandler {
    protected final MainWindow main;
    protected final ChatDisplay chat;
    protected final BufferedReader reader;

    HandshakeHandler(BufferedReader reader, ChatDisplay chat, MainWindow main) {
        this.reader = reader;
        this.chat = chat;
        this.main = main;
    }

    void readName() throws IOException {
        String name = reader.readLine();
        main.setOpponentName(name);
        chat.addChatLine("* " + name + " connected", Protocol.SYSTEM);
    }
}
