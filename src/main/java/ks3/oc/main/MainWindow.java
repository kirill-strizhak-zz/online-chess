package ks3.oc.main;

public interface MainWindow {

    void opponentConnected();

    void reset();

    void connectionKilled();

    int getOppColor();

    void setOppColor(int oppColor);

    int getMyColor();

    void setMyColor(int myColor);

    boolean isMyTurn();

    void setMyTurn(boolean myTurn);

    void refresh();

    String getOpponentName();

    void setOpponentName(String opponentName);

    String getMyName();
}
