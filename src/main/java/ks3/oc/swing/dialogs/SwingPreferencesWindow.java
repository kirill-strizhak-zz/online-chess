package ks3.oc.swing.dialogs;

import ks3.oc.board.BoardState;
import ks3.oc.dialogs.PreferencesWindow;

public class SwingPreferencesWindow implements PreferencesWindow {

    @Override
    public void open(BoardState boardState) {
        new Messenjah(boardState);
    }
}
