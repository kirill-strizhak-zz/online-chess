package ks3.oc.board;

public enum BoardSetup {

    PLAYING_BLACK(7, 0, 6, 1, 4, 3),
    PLAYING_WHITE(0, 7, 1, 6, 3, 4);

    private final int blackFigureRow;
    private final int whiteFigureRow;
    private final int blackPawnRow;
    private final int whitePawnRow;
    private final int queenCol;
    private final int kingCol;

    BoardSetup(int blackFigureRow, int whiteFigureRow, int blackPawnRow, int whitePawnRow, int queenCol, int kingCol) {
        this.blackFigureRow = blackFigureRow;
        this.whiteFigureRow = whiteFigureRow;
        this.blackPawnRow = blackPawnRow;
        this.whitePawnRow = whitePawnRow;
        this.queenCol = queenCol;
        this.kingCol = kingCol;
    }

    public int getBlackFigureRow() {
        return blackFigureRow;
    }

    public int getWhiteFigureRow() {
        return whiteFigureRow;
    }

    public int getBlackPawnRow() {
        return blackPawnRow;
    }

    public int getWhitePawnRow() {
        return whitePawnRow;
    }

    public int getQueenCol() {
        return queenCol;
    }

    public int getKingCol() {
        return kingCol;
    }
}