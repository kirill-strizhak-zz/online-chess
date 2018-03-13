package ks3.oc.conn.handlers;

import ks3.oc.ChatPanel;
import ks3.oc.MainWindow;

import java.io.BufferedReader;
import java.io.IOException;

public class MateHandler implements MessageHandler {

    private final MainWindow main;
    private final ChatPanel chat;

    public MateHandler(MainWindow main, ChatPanel chat) {
        this.main = main;
        this.chat = chat;
    }

    @Override
    public void handle(BufferedReader reader) throws IOException {
        main.setMyTurn(false);
        chat.addChatLine("* You win! Check and mate", "sys_&^_tem");
    }
}
