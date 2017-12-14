package ks3.oc.board;

import ks3.oc.Figure;

import java.awt.*;

public interface BoardState {

    void makeMove(int newX, int newY);

    void globalSetFigure(int x, int y, int color, int type, boolean isEmpty, boolean firstStep);

    void giveTurn();

    boolean isCheck();

    void setCheck(boolean check);

    Figure figureAt(int col, int row);

    Figure draggedFigure();

    void updateDraggedPosition();

    void moveKing(int color, int col, int row);

    void restoreKing(int color);

    int getKingCol(int color);

    int getKingRow(int color);

    void reloadImages(int boardId, int figureId);

    int getBoardId();

    int getFigureId();

    void setHlPos(int hlPos);

    Image getFigureImage(int color, int type);
}
