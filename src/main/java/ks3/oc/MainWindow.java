package ks3.oc;

public interface MainWindow {

    void reset();

    void connectionKilled();

    ChatPanel getChat();

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
