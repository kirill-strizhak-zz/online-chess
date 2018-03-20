package ks3.oc.main;

import ks3.oc.Protocol;
import ks3.oc.board.BoardState;
import ks3.oc.board.start.ClassicStartingBoardInitializer;
import ks3.oc.chat.ChatDisplay;
import ks3.oc.conn.ClientSender;
import ks3.oc.conn.Sender;
import ks3.oc.conn.ServerSender;
import ks3.oc.dialogs.FigurePickerWindow;
import ks3.oc.logic.Logic;
import ks3.oc.res.ResourceManager;
import org.apache.log4j.Logger;

public abstract class Main implements MainWindow {

    private static final Logger LOGGER = Logger.getLogger(MainWindow.class);

    protected final BoardState board;
    protected final ChatDisplay chat;
    protected final Logic logic;
    protected final Sender sender;

    private String opponentName;
    private String myName;
    private int oppColor;
    private int myColor = -1;
    private boolean myTurn = false;

    protected Main(ResourceManager resourceManager, int type, int color, String address, int port, String name) {
        this.myName = name;
        chat = createChat();
        board = createBoard(resourceManager);
        FigurePickerWindow figurePicker = createFigurePicker(resourceManager);
        logic = new Logic(board, this, figurePicker);
        board.setLogic(logic);

        if (type == Protocol.CLIENT) {
            sender = new ClientSender(this, board, chat, address, port);
        } else {
            sender = new ServerSender(this, board, chat, address, port);
            setMyColor(color);
            if (getMyColor() == Protocol.BLACK) {
                setOppColor(Protocol.WHITE);
            } else {
                setOppColor(Protocol.BLACK);
            }
            sender.send(Protocol.COLOR);
            if (getMyColor() == Protocol.WHITE) {
                setMyTurn(true);
                sender.send(Protocol.BLACK);
            } else {
                sender.send(Protocol.WHITE);
            }
        }

        while (getMyColor() == -1) {
            LOGGER.info("Waiting for color");
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                LOGGER.error("Failed to receive color", ex);
            }
        }

        sender.send(Protocol.NAME);
        sender.send(getMyName());

        chat.setSender(sender);
        board.setSender(sender);
        board.initFigures(new ClassicStartingBoardInitializer());
    }

    protected abstract BoardState createBoard(ResourceManager resourceManager);

    protected abstract ChatDisplay createChat();

    protected abstract FigurePickerWindow createFigurePicker(ResourceManager resourceManager);

    @Override
    public void reset() {
        setMyTurn(false);
        board.initFigures(new ClassicStartingBoardInitializer());
        board.getHighlight()[0][0] = -1;
        if (getMyColor() == Protocol.WHITE) {
            setMyTurn(true);
        }
        board.refresh();
        chat.addChatLine("* Server starts new game", Protocol.SYSTEM);
    }

    @Override
    public void connectionKilled() {
        chat.addChatLine("* " + getOpponentName() + " quits", Protocol.SYSTEM);
    }

    @Override
    public int getOppColor() {
        return oppColor;
    }

    @Override
    public void setOppColor(int oppColor) {
        this.oppColor = oppColor;
    }

    @Override
    public int getMyColor() {
        return myColor;
    }

    @Override
    public void setMyColor(int myColor) {
        this.myColor = myColor;
    }

    @Override
    public boolean isMyTurn() {
        return myTurn;
    }

    @Override
    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }

    @Override
    public void refresh() {
        board.refresh();
    }

    @Override
    public String getOpponentName() {
        return opponentName;
    }

    @Override
    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }

    protected String getMyName() {
        return myName;
    }
}
