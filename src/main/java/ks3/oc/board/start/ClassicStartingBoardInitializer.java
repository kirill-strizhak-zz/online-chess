package ks3.oc.board.start;

import ks3.oc.Figure;
import ks3.oc.Protocol;
import ks3.oc.board.BoardSetup;

public class ClassicStartingBoardInitializer implements StartingBoardInitializer {

    @Override
    public void initFigureData(BoardSetup setup, Figure[][] fig, int[][] king) {
        initMiddleRows(fig);
        initPawnRow(fig, setup.getBlackPawnRow(), Protocol.BLACK);
        initPawnRow(fig, setup.getWhitePawnRow(), Protocol.WHITE);
        initFigureRow(fig, setup.getBlackFigureRow(), setup.getQueenCol(), setup.getKingCol(), Protocol.BLACK);
        initFigureRow(fig, setup.getWhiteFigureRow(), setup.getQueenCol(), setup.getKingCol(), Protocol.WHITE);
        initKingCoordinates(king, setup.getKingCol(), setup.getBlackFigureRow(), setup.getWhiteFigureRow());
    }

    private void initMiddleRows(Figure[][] fig) {
        for (int row = 2; row < 6; row++) {
            for (int col = 0; col < 8; col++) {
                fig[col][row] = new Figure();
            }
        }
    }

    private void initPawnRow(Figure[][] fig, int row, int color) {
        for (int col = 0; col <= 7; col++) {
            fig[col][row] = createFigure(Protocol.PAWN, color, col, row);
        }
    }

    private void initFigureRow(Figure[][] fig, int row, int queenCol, int kingCol, int color) {
        fig[0][row] = createFigure(Protocol.ROOK, color, 0, row);
        fig[7][row] = createFigure(Protocol.ROOK, color, 7, row);
        fig[1][row] = createFigure(Protocol.KNIGHT, color, 1, row);
        fig[6][row] = createFigure(Protocol.KNIGHT, color, 6, row);
        fig[2][row] = createFigure(Protocol.BISHOP, color, 2, row);
        fig[5][row] = createFigure(Protocol.BISHOP, color, 5, row);
        fig[queenCol][row] = createFigure(Protocol.QUEEN, color, queenCol, row);
        fig[kingCol][row] = createFigure(Protocol.KING, color, kingCol, row);
    }

    private void initKingCoordinates(int[][] king, int kingCol, int blackFigureRow, int whiteFigureRow) {
        king[0][0] = kingCol;
        king[1][0] = kingCol;
        king[0][1] = blackFigureRow;
        king[1][1] = whiteFigureRow;
    }

    private Figure createFigure(int type, int color, int col, int row) {
        Figure figure = new Figure();
        figure.empty = false;
        figure.type = type;
        figure.color = color;
        figure.oX = col * 60;
        figure.oY = row * 60;
        return figure;
    }
}
