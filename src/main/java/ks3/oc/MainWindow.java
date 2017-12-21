package ks3.oc;

public interface MainWindow {

    ChatPanel getChat();

    int getOppColor();

    int getMyColor();

    boolean isMyTurn();

    void setMyTurn(boolean myTurn);

    void refresh();
}
