package ks3.oc.conn.handlers;

import ks3.oc.Protocol;
import ks3.oc.chat.ChatDisplay;
import ks3.oc.MainWindow;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;

public class NameHandler implements MessageHandler {

    private static final Logger LOGGER = Logger.getLogger(NameHandler.class);

    private final MainWindow main;
    private final ChatDisplay chat;
    private final BufferedReader reader;

    public NameHandler(MainWindow main, ChatDisplay chat, BufferedReader reader) {
        this.main = main;
        this.chat = chat;
        this.reader = reader;
    }

    @Override
    public void handle() throws IOException {
        LOGGER.info("Got NameID");
        String name = reader.readLine();
        main.setOpponentName(name);
        LOGGER.info("Name received");
        chat.addChatLine("* " + name + " connected", Protocol.SYSTEM);
    }
}
