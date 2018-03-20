package ks3.oc.board;

import ks3.oc.Figure;
import ks3.oc.board.start.StartingBoardInitializer;
import ks3.oc.conn.Sender;
import ks3.oc.logic.Logic;

import java.awt.Component;

public interface BoardState {

    public static final int CELL_SIZE = 60;

    void initFigures(StartingBoardInitializer startingBoardInitializer);

    int[][] getHighlight();

    void makeMove(int currX, int currY, int newX, int newY);

    void makeMove(int newX, int newY);

    void globalSetFigure(int x, int y, int color, int type, boolean isEmpty, boolean firstStep);

    void localSetFigure(int x, int y, int color, int type, boolean isEmpty, boolean firstStep);

    void giveTurn();

    void castleKingSide();

    void castleQueenSide();

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

    boolean isDebug();

    void setDebug(boolean enabled);

    void refresh();

    void setLogic(Logic logic);

    void setSender(Sender sender);

    Component getComponent();
}
