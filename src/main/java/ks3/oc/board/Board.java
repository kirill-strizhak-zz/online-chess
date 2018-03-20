package ks3.oc.board;

import ks3.oc.Figure;
import ks3.oc.main.MainWindow;
import ks3.oc.Protocol;
import ks3.oc.board.start.StartingBoardInitializer;
import ks3.oc.chat.ChatDisplay;
import ks3.oc.conn.Sender;
import ks3.oc.logic.Logic;
import ks3.oc.res.ResourceManager;
import org.apache.log4j.Logger;

import java.awt.Image;
import java.io.IOException;

public abstract class Board implements BoardState {

    private static final Logger LOGGER = Logger.getLogger(Board.class);

    protected final ResourceManager resourceManager;
    protected final MainWindow main;
    protected final ChatDisplay chat;

    private boolean dragging = false;
    public boolean loading = false;
    private boolean check = false;
    private Figure[][] figures = new Figure[8][8]; // figures on board
    public int[][] king = new int[2][2]; // i = (0 - black; 1 - white;)
    public int[] bckKing = new int[2];
    public int[][] highlight = new int[2][2];
    public int hlPos = 0;
    public int dragX, dragY, x, y;
    private Sender sender;
    protected Logic logic;

    public Board(ResourceManager resourceManager, MainWindow main, ChatDisplay chat) {
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

    @Override
    public void selectFigure(int col, int row) {
        x = col;
        y = row;
        if (col > 7) col = 7;
        else if (col < 0) col = 0;
        if (row > 7) row = 7;
        else if (row < 0) row = 0;
        if ((figures[col][row].color == main.getMyColor()) && (main.isMyTurn())) {
            dragX = col;
            dragY = row;
            logic.calculateAllowedMoves(col, row);
            dragging = true;
        }
    }

    @Override
    public void releaseFigure(int col, int row) {
        dragging = false;
        if (col > 7) col = 7;
        else if (col < 0) col = 0;
        if (row > 7) row = 7;
        else if (row < 0) row = 0;
        if ((figures[col][row].color == figures[x][y].color) && (!figures[col][row].empty)) {
            figures[x][y].oX = dragX * CELL_SIZE;
            figures[x][y].oY = dragY * CELL_SIZE;
        } else {
            logic.drop(col, row);
        }
    }

    @Override
    public void dragFigure(int dragX, int dragY) {
        figures[x][y].oX = dragX - (CELL_SIZE / 2);
        if (figures[x][y].oX > 420) {
            figures[x][y].oX = 420;
        } else {
            if (figures[x][y].oX < 0) {
                figures[x][y].oX = 0;
            }
        }
        figures[x][y].oY = dragY - (CELL_SIZE / 2);
        if (figures[x][y].oY > 420) {
            figures[x][y].oY = 420;
        } else {
            if (figures[x][y].oY < 0) {
                figures[x][y].oY = 0;
            }
        }
    }

    @Override
    public int[][] getHighlight() {
        return highlight;
    }

    @Override
    public boolean isCellEmpty(int col, int row) {
        return figures[col][row].empty;
    }

    @Override
    public boolean needToDrawHighlight() {
        return main.isMyTurn() && highlight[0][0] != -1;
    }

    @Override
    public Image getImageOfFigure(Figure figure) {
        return resourceManager.getFigureSet().getImage(figure.color, figure.type);
    }

    @Override
    public Image getImageOfDraggedFigure() {
        return resourceManager.getFigureSet().getImage(figures[x][y].color + 1, figures[x][y].type);
    }

    @Override
    public Figure getDraggedFigure() {
        return figures[x][y];
    }

    @Override
    public void makeMove(int currX, int currY, int newX, int newY) {
        figures[newX][newY].oX = newX * CELL_SIZE;
        figures[newX][newY].oY = newY * CELL_SIZE;
        figures[newX][newY].empty = false;
        figures[newX][newY].type = figures[currX][currY].type;
        figures[newX][newY].color = figures[currX][currY].color;
        figures[currX][currY].empty = true;
        figures[currX][currY].firstStep = false;
        figures[currX][currY].color = Protocol.NULL;
        figures[currX][currY].type = Protocol.NULL;
        if (figures[newX][newY].type == Protocol.KING) {
            int colorId = main.getOppColor() / 2;
            king[colorId][0] = newX;
            king[colorId][1] = newY;
        }
        main.setMyTurn(true);
        int z = main.getMyColor() / 2;
        check = !logic.kingSafeAt(king[z][0], king[z][1], main.getOppColor());
        if (!isLoading()) {
            if (logic.mate(king[z][0], king[z][1])) {
                main.setMyTurn(false);
                try {
                    LOGGER.info("Sending mate notification");
                    sender.send(Protocol.MATE);
                } catch (IOException ex) {
                    LOGGER.error("Failed to send mate notification", ex);
                }
                chat.addChatLine("* You lose! Check and mate.", Protocol.SYSTEM);
            }
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
        try {
            int z = main.getMyColor() / 2;
            int invertedX, invertedY, newInvertedX, newInvertedY;
            invertedX = Math.abs(7 - dragX);
            invertedY = Math.abs(7 - dragY);
            newInvertedX = Math.abs(7 - newX);
            newInvertedY = Math.abs(7 - newY);
            LOGGER.info("Sending coordinates");
            sender.send(Protocol.COORDINATES);
            sender.send(invertedX);
            sender.send(invertedY);
            sender.send(newInvertedX);
            sender.send(newInvertedY);
            check = !logic.kingSafeAt(king[z][0], king[z][1], main.getOppColor());
            if (!isLoading()) {
                if (logic.mate(king[z][0], king[z][1])) {
                    main.setMyTurn(false);
                    LOGGER.info("Sending mate notification");
                    sender.send(Protocol.MATE);
                    chat.addChatLine("* You lose! Check and mate.", Protocol.SYSTEM);
                }
            }
        } catch (IOException ex) {
            LOGGER.error("Failed to send move", ex);
        }
        main.refresh();
    }

    @Override
    public void globalSetFigure(int x, int y, int color, int type, boolean isEmpty, boolean firstStep) {
        main.setMyTurn(false);
        localSetFigure(x, y, color, type, isEmpty, firstStep);
        int iIsEmpty, iFirstStep;
        if (isEmpty) {
            iIsEmpty = 1;
        } else {
            iIsEmpty = 0;
        }
        if (firstStep) {
            iFirstStep = 1;
        } else {
            iFirstStep = 0;
        }
        try {
            int invertedX, invertedY;
            invertedX = Math.abs(7 - x);
            invertedY = Math.abs(7 - y);
            sender.send(Protocol.SET);
            sender.send(invertedX);
            sender.send(invertedY);
            sender.send(color);
            sender.send(type);
            sender.send(iIsEmpty);
            sender.send(iFirstStep);
        } catch (IOException ex) {
            LOGGER.error("Failed to set figure", ex);
        }
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
        try {
            sender.send(Protocol.GIVE_TURN);
        } catch (IOException ex) {
            LOGGER.error("Failed to give turn", ex);
        }
    }

    public void reaveTurn() {
        try {
            sender.send(Protocol.REAVE_TURN);
        } catch (IOException ex) {
            LOGGER.error("Failed to reave turn", ex);
        }
    }

    public Logic getLogic() {
        return logic;
    }

    @Override
    public void castleKingSide() {
        main.setMyTurn(false);
        LOGGER.info("Waiting to send coordinates");
        if (main.getMyColor() == Protocol.WHITE) {
            globalSetFigure(7, 7, Protocol.NULL, Protocol.NULL, true, false);
            globalSetFigure(4, 7, Protocol.NULL, Protocol.NULL, true, false);
            globalSetFigure(5, 7, main.getMyColor(), Protocol.ROOK, false, false);
            globalSetFigure(6, 7, main.getMyColor(), Protocol.KING, false, false);
        } else {
            globalSetFigure(0, 7, Protocol.NULL, Protocol.NULL, true, false);
            globalSetFigure(3, 7, Protocol.NULL, Protocol.NULL, true, false);
            globalSetFigure(2, 7, main.getMyColor(), Protocol.ROOK, false, false);
            globalSetFigure(1, 7, main.getMyColor(), Protocol.KING, false, false);
        }
        giveTurn();
        main.refresh();
    }

    @Override
    public void castleQueenSide() {
        main.setMyTurn(false);
        LOGGER.info("Waiting to send coordinates");
        if (main.getMyColor() == Protocol.WHITE) {
            globalSetFigure(0, 7, Protocol.NULL, Protocol.NULL, true, false);
            globalSetFigure(4, 7, Protocol.NULL, Protocol.NULL, true, false);
            globalSetFigure(3, 7, main.getMyColor(), Protocol.ROOK, false, false);
            globalSetFigure(2, 7, main.getMyColor(), Protocol.KING, false, false);
        } else {
            globalSetFigure(7, 7, Protocol.NULL, Protocol.NULL, true, false);
            globalSetFigure(3, 7, Protocol.NULL, Protocol.NULL, true, false);
            globalSetFigure(4, 7, main.getMyColor(), Protocol.ROOK, false, false);
            globalSetFigure(5, 7, main.getMyColor(), Protocol.KING, false, false);
        }
        giveTurn();
        main.refresh();
    }

    public void globalClear() {
        localClear();
        LOGGER.info("Sending clear notification");
        try {
            sender.send(Protocol.CLEAR);
        } catch (IOException ex) {
            LOGGER.error("Failed to send clear notification", ex);
        }
    }

    public void localClear() {
        for (int i = 0; i <= 7; i++) {
            for (int j = 0; j <= 7; j++) {
                figures[i][j].empty = true;
                figures[i][j].firstStep = true;
                figures[i][j].type = 6;
                figures[i][j].color = 6;
                figures[i][j].oX = -1;
                figures[i][j].oY = -1;
            }
        }
    }

    @Override
    public boolean isDragging() {
        return dragging;
    }

    @Override
    public boolean isLoading() {
        return loading;
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
        return figures[x][y];
    }

    @Override
    public void updateDraggedPosition() {
        figures[x][y].oX = dragX * CELL_SIZE;
        figures[x][y].oY = dragY * CELL_SIZE;
    }

    @Override
    public void moveKing(int color, int col, int row) {
        bckKing[0] = king[color][0];
        bckKing[1] = king[color][1];
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
