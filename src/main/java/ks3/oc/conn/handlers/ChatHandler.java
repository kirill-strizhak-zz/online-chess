package ks3.oc.conn.handlers;

import ks3.oc.ChatPanel;
import ks3.oc.MainWindow;

import java.io.BufferedReader;
import java.io.IOException;

public class ChatHandler implements MessageHandler {

    private final MainWindow main;
    private final ChatPanel chat;

    public ChatHandler(MainWindow main, ChatPanel chat) {
        this.main = main;
        this.chat = chat;
    }

    @Override
    public void handle(BufferedReader reader) throws IOException {
        chat.addChatLine(reader.readLine(), main.getOpponentName());
    }
}
