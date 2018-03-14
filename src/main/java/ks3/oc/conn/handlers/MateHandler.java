package ks3.oc.conn.handlers;

import ks3.oc.ChatPanel;
import ks3.oc.MainWindow;

public class MateHandler implements MessageHandler {

    private final MainWindow main;
    private final ChatPanel chat;

    public MateHandler(MainWindow main, ChatPanel chat) {
        this.main = main;
        this.chat = chat;
    }

    @Override
    public void handle() {
        main.setMyTurn(false);
        chat.addChatLine("* You win! Check and mate", "sys_&^_tem");
    }
}
