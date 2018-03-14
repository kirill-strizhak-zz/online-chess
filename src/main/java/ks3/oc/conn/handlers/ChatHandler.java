package ks3.oc.conn.handlers;

import ks3.oc.ChatPanel;
import ks3.oc.MainWindow;

import java.io.BufferedReader;
import java.io.IOException;

public class ChatHandler implements MessageHandler {

    private final MainWindow main;
    private final ChatPanel chat;
    private final BufferedReader reader;

    public ChatHandler(MainWindow main, ChatPanel chat, BufferedReader reader) {
        this.main = main;
        this.chat = chat;
        this.reader = reader;
    }

    @Override
    public void handle() throws IOException {
        chat.addChatLine(reader.readLine(), main.getOpponentName());
    }
}
