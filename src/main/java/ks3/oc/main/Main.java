package ks3.oc.main;

import ks3.oc.Protocol;
import ks3.oc.board.BoardState;
import ks3.oc.board.start.ClassicStartingBoardInitializer;
import ks3.oc.chat.ChatDisplay;
import ks3.oc.conn.Sender;

public abstract class Main implements MainWindow {

    protected BoardState board;
    protected ChatDisplay chat;
    protected Sender sender;

    private String opponentName;
    private String myName;
    private int oppColor;
    private int myColor = -1;
    private boolean myTurn = false;

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

    protected void save() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    protected void load() {
        throw new UnsupportedOperationException("Method not implemented");
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

    public String getMyName() {
        return myName;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }
}
