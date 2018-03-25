package ks3.oc.board;

import ks3.oc.Figure;
import ks3.oc.Protocol;
import ks3.oc.board.start.StartingBoardInitializer;
import ks3.oc.chat.ChatDisplay;
import ks3.oc.conn.Sender;
import ks3.oc.logic.Logic;
import ks3.oc.main.MainWindow;
import ks3.oc.res.ResourceManager;

import java.awt.Image;

public abstract class Board implements BoardState {

    protected final ResourceManager resourceManager;
    protected final MainWindow main;

    private final ChatDisplay chat;

    private boolean dragging = false;
    private boolean check = false;
    private Figure[][] figures = new Figure[8][8];
    private int[][] king = new int[2][2]; // i = (0 - black; 1 - white;)
    private int[][] highlight = new int[2][2];
    private int hlPos = 0;
    private int draggedCol, draggedRow, selectedCol, selectedRow;

    private Sender sender;
    protected Logic logic;

    protected Board(ResourceManager resourceManager, MainWindow main, ChatDisplay chat) {
        this.resourceManager = resourceManager;
        this.main = main;
        this.chat = chat;
        highlight[0][0] = -1;
    }

    @Override
    public void initFigures(StartingBoardInitializer startingBoardInitializer) {
        if (main.getMyColor() == Protocol.BLACK) {
            startingBoardInitializer.initFigureData(BoardSetup.PLAYING_BLACK, figures, king);
        } else {
            startingBoardInitializer.initFigureData(BoardSetup.PLAYING_WHITE, figures, king);
        }
    }

    public void selectFigure(int col, int row) {
        selectedCol = clipToCellBounds(col);
        selectedRow = clipToCellBounds(row);
        if ((draggedFigure().color == main.getMyColor()) && (main.isMyTurn())) {
            draggedCol = selectedCol;
            draggedRow = selectedRow;
            logic.calculateAllowedMoves(selectedCol, selectedRow);
            dragging = true;
        }
    }

    public void releaseFigure(int col, int row) {
        dragging = false;
        int boundCol = clipToCellBounds(col);
        int boundRow = clipToCellBounds(row);
        if ((figures[boundCol][boundRow].color == draggedFigure().color) && (!figures[boundCol][boundRow].empty)) {
            draggedFigure().oX = draggedCol * CELL_SIZE;
            draggedFigure().oY = draggedRow * CELL_SIZE;
        } else {
            logic.drop(boundCol, boundRow);
        }
    }

    private int clipToCellBounds(int cell) {
        if (cell > 7) return 7;
        else if (cell < 0) return 0;
        else return cell;
    }

    public void dragFigure(int dragX, int dragY) {
        draggedFigure().oX = clipToCoordinateBounds(dragX - (CELL_SIZE / 2));
        draggedFigure().oY = clipToCoordinateBounds(dragY - (CELL_SIZE / 2));
    }

    private int clipToCoordinateBounds(int coordinate) {
        if (coordinate > 420) return 420;
        else if (coordinate < 0) return 0;
        else return coordinate;
    }

    @Override
    public int[][] getHighlight() {
        return highlight;
    }

    public boolean isCellEmpty(int col, int row) {
        return figures[col][row].empty;
    }

    public boolean needToDrawHighlight() {
        return main.isMyTurn() && highlight[0][0] != -1;
    }

    public Image getImageOfFigure(Figure figure) {
        return resourceManager.getFigureSet().getImage(figure.color, figure.type);
    }

    public Image getImageOfDraggedFigure() {
        return resourceManager.getFigureSet().getImage(draggedFigure().color + 1, draggedFigure().type);
    }

    public Figure getDraggedFigure() {
        return draggedFigure();
    }

    @Override
    public void makeMove(int currX, int currY, int newX, int newY) {
        moveAndClear(figureAt(currX, currY), newX, newY);
        if (figures[newX][newY].type == Protocol.KING) {
            int colorId = main.getOppColor() / 2;
            king[colorId][0] = newX;
            king[colorId][1] = newY;
        }
        main.setMyTurn(true);
        int z = main.getMyColor() / 2;
        check = !logic.kingSafeAt(king[z][0], king[z][1], main.getOppColor());
        if (logic.mate(king[z][0], king[z][1])) {
            main.setMyTurn(false);
            sender.send(Protocol.MATE);
            chat.addChatLine("* You lose! Check and mate.", Protocol.SYSTEM);
        }
        highlight[0][0] = newX * CELL_SIZE;
        highlight[0][1] = newY * CELL_SIZE;
        highlight[1][0] = currX * CELL_SIZE;
        highlight[1][1] = currY * CELL_SIZE;
        main.refresh();
    }

    @Override
    public void makeMove(int newX, int newY) {
        main.setMyTurn(false);
        int z = main.getMyColor() / 2;
        int invertedX, invertedY, newInvertedX, newInvertedY;
        invertedX = Math.abs(7 - draggedCol);
        invertedY = Math.abs(7 - draggedRow);
        newInvertedX = Math.abs(7 - newX);
        newInvertedY = Math.abs(7 - newY);
        sender.send(Protocol.COORDINATES);
        sender.send(invertedX);
        sender.send(invertedY);
        sender.send(newInvertedX);
        sender.send(newInvertedY);
        check = !logic.kingSafeAt(king[z][0], king[z][1], main.getOppColor());
        if (logic.mate(king[z][0], king[z][1])) {
            main.setMyTurn(false);
            sender.send(Protocol.MATE);
            chat.addChatLine("* You lose! Check and mate.", Protocol.SYSTEM);
        }
        main.refresh();
    }

    @Override
    public void globalSetFigure(int x, int y, int color, int type, boolean isEmpty, boolean firstStep) {
        main.setMyTurn(false);
        localSetFigure(x, y, color, type, isEmpty, firstStep);
        sender.send(Protocol.SET);
        sender.send(Math.abs(7 - x));
        sender.send(Math.abs(7 - y));
        sender.send(color);
        sender.send(type);
        sender.send(isEmpty ? 1 : 0);
        sender.send(firstStep ? 1 : 0);
    }

    @Override
    public void localSetFigure(int x, int y, int color, int type, boolean isEmpty, boolean firstStep) {
        figures[x][y].empty = isEmpty;
        figures[x][y].firstStep = firstStep;
        figures[x][y].oX = x * CELL_SIZE;
        figures[x][y].oY = y * CELL_SIZE;
        figures[x][y].color = color;
        figures[x][y].type = type;
        highlight[hlPos][0] = x * CELL_SIZE;
        highlight[hlPos][1] = y * CELL_SIZE;
        if (hlPos == 1) {
            hlPos = 0;
        } else {
            ++hlPos;
        }
        main.refresh();
    }

    @Override
    public void giveTurn() {
        sender.send(Protocol.GIVE_TURN);
    }

    @Override
    public void castleKingSide() {
        main.setMyTurn(false);
        if (main.getMyColor() == Protocol.WHITE) {
            castle(7, 4, 5, 6);
        } else {
            castle(0, 3, 2, 1);
        }
        giveTurn();
        main.refresh();
    }

    @Override
    public void castleQueenSide() {
        main.setMyTurn(false);
        if (main.getMyColor() == Protocol.WHITE) {
            castle(0, 4, 3, 2);
        } else {
            castle(7, 3, 4, 5);
        }
        giveTurn();
        main.refresh();
    }

    private void castle(int rookCol, int kingCol, int rookNewCol, int kingNewCol) {
        globalSetFigure(rookCol, 7, Protocol.NULL, Protocol.NULL, true, false);
        globalSetFigure(kingCol, 7, Protocol.NULL, Protocol.NULL, true, false);
        globalSetFigure(rookNewCol, 7, main.getMyColor(), Protocol.ROOK, false, false);
        globalSetFigure(kingNewCol, 7, main.getMyColor(), Protocol.KING, false, false);
    }

    @Override
    public boolean isDragging() {
        return dragging;
    }

    @Override
    public boolean isCheck() {
        return check;
    }

    @Override
    public void setCheck(boolean check) {
        this.check = check;
    }

    @Override
    public Figure figureAt(int col, int row) {
        return figures[col][row];
    }

    @Override
    public Figure draggedFigure() {
        return figures[selectedCol][selectedRow];
    }

    @Override
    public void updateDraggedPosition() {
        draggedFigure().oX = draggedCol * CELL_SIZE;
        draggedFigure().oY = draggedRow * CELL_SIZE;
    }

    @Override
    public void moveAndClear(Figure figure, int col, int row) {
        figureAt(col, row).oX = col * CELL_SIZE;
        figureAt(col, row).oY = row * CELL_SIZE;
        figureAt(col, row).empty = false;
        figureAt(col, row).firstStep = false;
        figureAt(col, row).type = figure.type;
        figureAt(col, row).color = figure.color;

        figure.empty = true;
        figure.firstStep = false;
        figure.type = Protocol.NULL;
        figure.color = Protocol.NULL;
    }

    @Override
    public void moveKing(int color, int col, int row) {
        king[color][0] = col;
        king[color][1] = row;
    }

    @Override
    public int getKingCol(int color) {
        return king[color][0];
    }

    @Override
    public int getKingRow(int color) {
        return king[color][1];
    }

    @Override
    public void setHlPos(int hlPos) {
        this.hlPos = hlPos;
    }

    @Override
    public void setLogic(Logic logic) {
        this.logic = logic;
    }

    @Override
    public void setSender(Sender sender) {
        this.sender = sender;
    }
}
