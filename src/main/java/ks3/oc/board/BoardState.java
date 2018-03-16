package ks3.oc.board;

import ks3.oc.Figure;

import java.awt.Image;

public interface BoardState {

    public static final int CELL_SIZE = 60;

    void selectFigure(int col, int row);

    void releaseFigure(int col, int row);

    void dragFigure(int dragX, int dragY);

    int[][] getHighlight();

    boolean isCellEmpty(int col, int row);

    boolean needToDrawHighlight();

    Image getImageOfFigure(Figure figure);

    Image getImageOfDraggedFigure();

    Figure getDraggedFigure();

    void makeMove(int currX, int currY, int newX, int newY);

    void makeMove(int newX, int newY);

    void globalSetFigure(int x, int y, int color, int type, boolean isEmpty, boolean firstStep);

    void localSetFigure(int x, int y, int color, int type, boolean isEmpty, boolean firstStep);

    void giveTurn();

    boolean isDragging();

    boolean isLoading();

    boolean isCheck();

    void setCheck(boolean check);

    Figure figureAt(int col, int row);

    Figure draggedFigure();

    void updateDraggedPosition();

    void moveKing(int color, int col, int row);

    int getKingCol(int color);

    int getKingRow(int color);

    void setHlPos(int hlPos);

    boolean isDebug();

    void setDebug(boolean enabled);

    void refresh();
}
