package ks3.oc.swing.dialogs;

import ks3.oc.board.BoardState;
import ks3.oc.dialogs.FigurePicker;

public class SwingFigurePicker implements FigurePicker {

    @Override
    public void open(BoardState boardState, int myColor, int col, int row) {
        new Messenjah(boardState, myColor, col, row);
    }
}
