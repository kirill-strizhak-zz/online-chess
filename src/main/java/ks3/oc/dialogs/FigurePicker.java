package ks3.oc.dialogs;

import ks3.oc.board.BoardState;

public interface FigurePicker {
    public void open(BoardState boardState, int myColor, int col, int row);
}
