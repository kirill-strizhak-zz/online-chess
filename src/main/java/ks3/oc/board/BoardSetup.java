package ks3.oc.board;

public class BoardSetup {

    private int blackFigureRow;
    private int whiteFigureRow;
    private int blackPawnRow;
    private int whitePawnRow;
    private int queenCol;
    private int kingCol;

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