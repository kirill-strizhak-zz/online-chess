package ks3.oc.conn.handlers;

import ks3.oc.ChatPanel;
import ks3.oc.MainWindow;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;

public class NameHandler implements MessageHandler {

    private static final Logger LOGGER = Logger.getLogger(NameHandler.class);

    private final MainWindow main;
    private final ChatPanel chat;

    public NameHandler(MainWindow main, ChatPanel chat) {
        this.main = main;
        this.chat = chat;
    }

    @Override
    public void handle(BufferedReader reader) throws IOException {
        LOGGER.info("Got NameID");
        String name = reader.readLine();
        main.setOpponentName(name);
        LOGGER.info("Name received");
        chat.addChatLine("* " + name + " connected", "sys_&^_tem");
    }
}
