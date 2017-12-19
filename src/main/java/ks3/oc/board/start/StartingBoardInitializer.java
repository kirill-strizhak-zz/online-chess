package ks3.oc.board.start;

import ks3.oc.Figure;
import ks3.oc.board.BoardSetup;

public interface StartingBoardInitializer {
    void initFigureData(BoardSetup setup, Figure[][] fig, int[][] king);
}
