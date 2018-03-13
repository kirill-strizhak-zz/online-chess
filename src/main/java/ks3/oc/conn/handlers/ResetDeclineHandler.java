package ks3.oc.conn.handlers;

import ks3.oc.ChatPanel;

import java.io.BufferedReader;
import java.io.IOException;

public class ResetDeclineHandler implements MessageHandler {

    private final ChatPanel chat;

    public ResetDeclineHandler(ChatPanel chat) {
        this.chat = chat;
    }

    @Override
    public void handle(BufferedReader reader) throws IOException {
        chat.addChatLine("* Reset declined", "sys_&^_tem");
    }
}
