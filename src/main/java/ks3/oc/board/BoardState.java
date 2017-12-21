package ks3.oc.board;

import ks3.oc.Figure;

public interface BoardState {

    void makeMove(int newX, int newY);

    void globalSetFigure(int x, int y, int color, int type, boolean isEmpty, boolean firstStep);

    void giveTurn();

    boolean isDragging();

    boolean isCheck();

    void setCheck(boolean check);

    Figure figureAt(int col, int row);

    Figure draggedFigure();

    void updateDraggedPosition();

    void moveKing(int color, int col, int row);

    int getKingCol(int color);

    int getKingRow(int color);

    void setHlPos(int hlPos);

    void refresh();
}
