package ks3.oc.board;

class BoardSetup {

    int blackFigureRow;
    int whiteFigureRow;
    int blackPawnRow;
    int whitePawnRow;
    int queenCol;
    int kingCol;

    public static final BoardSetup PLAYING_BLACK = new BoardSetup(7, 0, 6, 1, 4, 3);
    public static final BoardSetup PLAYING_WHITE = new BoardSetup(0, 7, 1, 6, 3, 4);

    private BoardSetup(int blackFigureRow, int whiteFigureRow, int blackPawnRow, int whitePawnRow, int queenCol, int kingCol) {
        this.blackFigureRow = blackFigureRow;
        this.whiteFigureRow = whiteFigureRow;
        this.blackPawnRow = blackPawnRow;
        this.whitePawnRow = whitePawnRow;
        this.queenCol = queenCol;
        this.kingCol = kingCol;
    }
}