package ks3.oc.conn;

import ks3.oc.board.BoardState;
import ks3.oc.chat.ChatDisplay;
import ks3.oc.conn.handlers.*;
import ks3.oc.dialogs.DialogWindow;
import ks3.oc.main.MainWindow;
import ks3.oc.swing.dialogs.SwingNewGameConfirmation;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class Receiver implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Receiver.class);

    private final Map<Integer, MessageHandler> handlers;
    private final MessageHandler defaultHandler = new UnrecognizedMessageHandler();

    private final BufferedReader reader;
    private final Sender sender;

    private boolean active = true;
    private final DialogWindow newGameConfirmation;

    public Receiver(MainWindow main, BoardState board, ChatDisplay chat, BufferedReader reader, Sender sender) {
        this.reader = reader;
        this.sender = sender;
        newGameConfirmation = new SwingNewGameConfirmation(sender, main);
        handlers = registerHandlers(main, board, chat, reader);
    }

    protected Map<Integer, MessageHandler> registerHandlers(MainWindow main, BoardState board, ChatDisplay chat, BufferedReader reader) {
        Map<Integer, MessageHandler> handlers = new HashMap<>();
        handlers.put(Headers.HANDSHAKE, createHandshakeHandler(main, chat, reader));
        handlers.put(Headers.COORDINATES, new CoordinateHandler(board, reader));
        handlers.put(Headers.CHAT, new ChatHandler(main, chat, reader));
        handlers.put(Headers.CLOSE, new CloseHandler(this, sender));
        handlers.put(Headers.OFFER_RESET, new ResetOfferHandler(newGameConfirmation));
        handlers.put(Headers.ACCEPT_RESET, new ResetAcceptHandler(main));
        handlers.put(Headers.DECLINE_RESET, new ResetDeclineHandler(chat));
        handlers.put(Headers.SET, new SetFigureHandler(board, reader));
        handlers.put(Headers.GIVE_TURN, new GiveTurnHandler(main));
        handlers.put(Headers.MATE, new MateHandler(main, chat));
        return handlers;
    }

    public void run() {
        LOGGER.info("Receiver: activated");
        int header;
        while (active) {
            try {
                header = reader.read();
                handlers.getOrDefault(header, defaultHandler).handle();
            } catch (IOException ex) {
                LOGGER.error("Can't reach opponent", ex);
                deactivate();
                sender.deactivate("Receiver: IOException");
            }
        }
    }

    public void deactivate() {
        active = false;
    }

    protected abstract MessageHandler createHandshakeHandler(MainWindow main, ChatDisplay chat, BufferedReader reader);
}
