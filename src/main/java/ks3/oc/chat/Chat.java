package ks3.oc.chat;

import ks3.oc.Protocol;
import ks3.oc.conn.Sender;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Date;
import java.util.StringTokenizer;

public abstract class Chat implements ChatDisplay {

    private static final Logger LOGGER = Logger.getLogger(Chat.class);

    private static final String TIMESTAMP_FORMAT = "[%1$tR]";
    private static final String SIMPLE_MESSAGE_FORMAT = TIMESTAMP_FORMAT + " <%2$s> %3$s";
    private static final String EMOTE_MESSAGE_FORMAT = TIMESTAMP_FORMAT + " * %2$s %3$s";
    private static final String SYSTEM_MESSAGE_FORMAT = TIMESTAMP_FORMAT + " %2$s";

    private final String playerName;

    private Sender sender;

    public Chat(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public void sendChat(String message) {
        LOGGER.info("Waiting to send chat");
        try {
            sender.send(Protocol.CHAT);
            sender.send(message);
            addChatLine(message, playerName);
        } catch (IOException ex) {
            LOGGER.error("Failed to send chat", ex);
            addChatLine("* Cannot send chat: connection lost", Protocol.SYSTEM);
        }
    }

    @Override
    public void addChatLine(String message, String senderName) {
        StringTokenizer getParam = new StringTokenizer(message, " ", false);
        String param = getParam.nextToken();
        if (param.equals("*") && senderName.equals(Protocol.SYSTEM)) {
            appendLine(String.format(SYSTEM_MESSAGE_FORMAT, new Date(), message));
        } else {
            if (param.equals("/me")) {
                appendLine(String.format(EMOTE_MESSAGE_FORMAT, new Date(), senderName, message.substring(4)));
            } else {
                appendLine(String.format(SIMPLE_MESSAGE_FORMAT, new Date(), senderName, message));
            }
        }
    }

    protected abstract void appendLine(String line);
}
