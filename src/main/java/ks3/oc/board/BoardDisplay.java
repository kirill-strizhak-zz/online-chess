package ks3.oc.board;

public interface BoardDisplay {
    boolean isDebug();

    void setDebug(boolean enabled);

    void refresh();
}
