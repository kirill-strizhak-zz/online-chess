package ks3.oc.conn.handlers;

import ks3.oc.ChatPanel;

public class ResetDeclineHandler implements MessageHandler {

    private final ChatPanel chat;

    public ResetDeclineHandler(ChatPanel chat) {
        this.chat = chat;
    }

    @Override
    public void handle() {
        chat.addChatLine("* Reset declined", "sys_&^_tem");
    }
}
