package ks3.oc.conn.handlers;

import ks3.oc.ChatDisplay;

public class ResetDeclineHandler implements MessageHandler {

    private final ChatDisplay chat;

    public ResetDeclineHandler(ChatDisplay chat) {
        this.chat = chat;
    }

    @Override
    public void handle() {
        chat.addChatLine("* Reset declined", "sys_&^_tem");
    }
}
