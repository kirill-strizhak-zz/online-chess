package ks3.oc;

public interface MainWindow {

    ChatPanel getChat();

    void say(String s);

    int getOppColor();

    int getMyColor();

    boolean isMyTurn();

    void setMyTurn(boolean myTurn);
}
