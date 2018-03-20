package ks3.oc.conn;

import ks3.oc.Protocol;
import ks3.oc.board.BoardState;
import ks3.oc.chat.ChatDisplay;
import ks3.oc.conn.handlers.ChatHandler;
import ks3.oc.conn.handlers.CloseHandler;
import ks3.oc.conn.handlers.ColorHandler;
import ks3.oc.conn.handlers.CoordinateHandler;
import ks3.oc.conn.handlers.GiveTurnHandler;
import ks3.oc.conn.handlers.MateHandler;
import ks3.oc.conn.handlers.MessageHandler;
import ks3.oc.conn.handlers.NameHandler;
import ks3.oc.conn.handlers.ResetAcceptHandler;
import ks3.oc.conn.handlers.ResetDeclineHandler;
import ks3.oc.conn.handlers.ResetOfferHandler;
import ks3.oc.conn.handlers.SetFigureHandler;
import ks3.oc.conn.handlers.UnrecognizedMessageHandler;
import ks3.oc.dialogs.DialogWindow;
import ks3.oc.main.MainWindow;
import ks3.oc.swing.dialogs.SwingNewGameConfirmation;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Receiver implements Runnable {

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
        handlers.put(Protocol.NAME, new NameHandler(main, chat, reader));
        handlers.put(Protocol.COORDINATES, new CoordinateHandler(board, reader));
        handlers.put(Protocol.CHAT, new ChatHandler(main, chat, reader));
        handlers.put(Protocol.CLOSE, new CloseHandler(this, sender));
        handlers.put(Protocol.COLOR, new ColorHandler(main, reader));
        handlers.put(Protocol.OFFER_RESET, new ResetOfferHandler(newGameConfirmation));
        handlers.put(Protocol.ACCEPT_RESET, new ResetAcceptHandler(main));
        handlers.put(Protocol.DECLINE_RESET, new ResetDeclineHandler(chat));
        handlers.put(Protocol.SET, new SetFigureHandler(board, reader));
        handlers.put(Protocol.GIVE_TURN, new GiveTurnHandler(main));
        handlers.put(Protocol.MATE, new MateHandler(main, chat));
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
}
