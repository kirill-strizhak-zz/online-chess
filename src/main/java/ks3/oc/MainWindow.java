package ks3.oc;

import ks3.oc.board.Board;

public interface MainWindow {

    void reset();

    void connectionKilled();

    ChatPanel getChat();

    Board getBoard();

    int getOppColor();

    void setOppColor(int oppColor);

    int getMyColor();

    void setMyColor(int myColor);

    boolean isMyTurn();

    void setMyTurn(boolean myTurn);

    void refresh();

    String getOpponentName();

    void setOpponentName(String opponentName);
}
