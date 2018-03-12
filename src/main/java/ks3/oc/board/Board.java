package ks3.oc.board;

import ks3.oc.ChatPanel;
import ks3.oc.Figure;
import ks3.oc.MainWindow;
import ks3.oc.Protocol;
import ks3.oc.conn.Sender;
import ks3.oc.board.start.ClassicStartingBoardInitializer;
import ks3.oc.board.start.StartingBoardInitializer;
import ks3.oc.logic.Logic;
import ks3.oc.res.ResourceManager;
import org.apache.log4j.Logger;

import java.awt.Image;
import java.io.IOException;

public class Board implements Protocol, BoardState {

    private static final Logger LOGGER = Logger.getLogger(Board.class);

    private final ResourceManager resourceManager;

    private boolean dragging = false;
    public boolean loading = false;
    private boolean check = false;
    private Figure[][] fig = new Figure[8][8]; // figures on board
    public int[][] king = new int[2][2]; // i = (0 - black; 1 - white;)
    public int[] bckKing = new int[2];
    public int[][] hlight = new int[2][2];
    public int hlPos = 0;
    private MainWindow owner;
    public int dragX, dragY, x, y;
    private Sender sender;
    private ChatPanel chat;
    private Logic logic;

    public Board(ResourceManager resourceManager, MainWindow own, Sender send, ChatPanel ch) {
        super();
        this.resourceManager = resourceManager;
        owner = own;
        sender = send;
        chat = ch;
        hlight[0][0] = -1;
        initFigures(new ClassicStartingBoardInitializer());
        LOGGER.info("Initialization completed");
    }

    public void initFigures(StartingBoardInitializer startingBoardInitializer) {
        while (owner.getMyColor() == -1) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                //ignore
            }
        }

        if (owner.getMyColor() == BLACK) {
            startingBoardInitializer.initFigureData(BoardSetup.PLAYING_BLACK, fig, king);
        } else {
            startingBoardInitializer.initFigureData(BoardSetup.PLAYING_WHITE, fig, king);
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
        if ((fig[col][row].color == owner.getMyColor()) && (owner.isMyTurn())) {
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
        if ((fig[col][row].color == fig[x][y].color) && (!fig[col][row].empty)) {
            fig[x][y].oX = dragX * CELL_SIZE;
            fig[x][y].oY = dragY * CELL_SIZE;
        } else {
            while (logic.calculating) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    //ignore
                }
            }
            logic.drop(col, row);
        }
    }

    @Override
    public void dragFigure(int dragX, int dragY) {
        fig[x][y].oX = dragX - (CELL_SIZE / 2);
        if (fig[x][y].oX > 420) {
            fig[x][y].oX = 420;
        } else {
            if (fig[x][y].oX < 0) {
                fig[x][y].oX = 0;
            }
        }
        fig[x][y].oY = dragY - (CELL_SIZE / 2);
        if (fig[x][y].oY > 420) {
            fig[x][y].oY = 420;
        } else {
            if (fig[x][y].oY < 0) {
                fig[x][y].oY = 0;
            }
        }
    }

    @Override
    public int[][] getHighlight() {
        return hlight;
    }

    @Override
    public boolean isCellEmpty(int col, int row) {
        return fig[col][row].empty;
    }

    @Override
    public boolean needToDrawHighlight() {
        return owner.isMyTurn() && hlight[0][0] != -1;
    }

    @Override
    public Image getImageOfFigure(Figure figure) {
        return resourceManager.getFigureSet().getImage(figure.color, figure.type);
    }

    @Override
    public Image getImageOfDraggedFigure() {
        return resourceManager.getFigureSet().getImage(fig[x][y].color + 1, fig[x][y].type);
    }

    @Override
    public Figure getDraggedFigure() {
        return fig[x][y];
    }

    public void makeMove(int currX, int currY, int newX, int newY) {
        fig[newX][newY].oX = newX * CELL_SIZE;
        fig[newX][newY].oY = newY * CELL_SIZE;
        fig[newX][newY].empty = false;
        fig[newX][newY].type = fig[currX][currY].type;
        fig[newX][newY].color = fig[currX][currY].color;
        fig[currX][currY].empty = true;
        fig[currX][currY].firstStep = false;
        fig[currX][currY].color = NULL;
        fig[currX][currY].type = NULL;
        if (fig[newX][newY].type == KING) {
            int colorId = owner.getOppColor() / 2;
            king[colorId][0] = newX;
            king[colorId][1] = newY;
        }
        owner.setMyTurn(true);
        int z = owner.getMyColor() / 2;
        check = !logic.kingSafeAt(king[z][0], king[z][1], owner.getOppColor());
        if (!isLoading()) {
            if (logic.mate(king[z][0], king[z][1])) {
                owner.setMyTurn(false);
                try {
                    LOGGER.info("Waiting to send coordinates");
                    while (!sender.isFree()) {
                        Thread.sleep(10);
                    }
                    sender.send(MATE);
                    sender.free();
                } catch (IOException | InterruptedException ex) {
                    LOGGER.error("Failed to send mate notification", ex);
                }
                while (chat == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        //ignore
                    }
                    chat = owner.getChat();
                }
                chat.addChatLine("* You lose! Check and mate.", "sys_&^_tem");
            }
        }
        hlight[0][0] = newX * CELL_SIZE;
        hlight[0][1] = newY * CELL_SIZE;
        hlight[1][0] = currX * CELL_SIZE;
        hlight[1][1] = currY * CELL_SIZE;
        owner.refresh();
    }

    @Override
    public void makeMove(int newX, int newY) {
        owner.setMyTurn(false);
        try {
            int z = owner.getMyColor() / 2;
            int invertedX, invertedY, newInvertedX, newInvertedY;
            invertedX = Math.abs(7 - dragX);
            invertedY = Math.abs(7 - dragY);
            newInvertedX = Math.abs(7 - newX);
            newInvertedY = Math.abs(7 - newY);
            LOGGER.info("Waiting to send coordinates");
            while (!sender.isFree()) {
                Thread.sleep(10);
            }
            sender.send(COORDINATES);
            sender.send(invertedX);
            sender.send(invertedY);
            sender.send(newInvertedX);
            sender.send(newInvertedY);
            sender.free();
            check = !logic.kingSafeAt(king[z][0], king[z][1], owner.getOppColor());
            if (!isLoading()) {
                if (logic.mate(king[z][0], king[z][1])) {
                    owner.setMyTurn(false);
                    LOGGER.info("Waiting to send coordinates");
                    while (!sender.isFree()) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                            //ignore
                        }
                    }
                    sender.send(MATE);
                    sender.free();
                    while (chat == null) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            //ignore
                        }
                        chat = owner.getChat();
                    }
                    chat.addChatLine("* You lose! Check and mate.", "sys_&^_tem");
                }
            }
        } catch (IOException | InterruptedException ex) {
            LOGGER.error("Failed to send move", ex);
        }
        owner.refresh();
    }

    @Override
    public void globalSetFigure(int x, int y, int color, int type, boolean isEmpty, boolean firstStep) {
        owner.setMyTurn(false);
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
            sender.send(SET);
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

    public void localSetFigure(int x, int y, int color, int type, boolean isEmpty, boolean firstStep) {
        fig[x][y].empty = isEmpty;
        fig[x][y].firstStep = firstStep;
        fig[x][y].oX = x * CELL_SIZE;
        fig[x][y].oY = y * CELL_SIZE;
        fig[x][y].color = color;
        fig[x][y].type = type;
        hlight[hlPos][0] = x * CELL_SIZE;
        hlight[hlPos][1] = y * CELL_SIZE;
        if (hlPos == 1) {
            hlPos = 0;
        } else {
            ++hlPos;
        }
        owner.refresh();
    }

    @Override
    public void giveTurn() {
        try {
            sender.send(GIVE_TURN);
        } catch (IOException ex) {
            LOGGER.error("Failed to give turn", ex);
        }
    }

    public void reaveTurn() {
        try {
            sender.send(REAVE_TURN);
        } catch (IOException ex) {
            LOGGER.error("Failed to reave turn", ex);
        }
    }

    public Logic getLogic() {
        return logic;
    }

    public void shortXchng() {
        owner.setMyTurn(false);
        LOGGER.info("Waiting to send coordinates");
        while (!sender.isFree()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                //ignore
            }
        }
        if (owner.getMyColor() == WHITE) {
            globalSetFigure(7, 7, NULL, NULL, true, false);
            globalSetFigure(4, 7, NULL, NULL, true, false);
            globalSetFigure(5, 7, owner.getMyColor(), ROOK, false, false);
            globalSetFigure(6, 7, owner.getMyColor(), KING, false, false);
        } else {
            globalSetFigure(0, 7, NULL, NULL, true, false);
            globalSetFigure(3, 7, NULL, NULL, true, false);
            globalSetFigure(2, 7, owner.getMyColor(), ROOK, false, false);
            globalSetFigure(1, 7, owner.getMyColor(), KING, false, false);
        }
        giveTurn();
        sender.free();
        owner.refresh();
    }

    public void longXchng() {
        owner.setMyTurn(false);
        LOGGER.info("Waiting to send coordinates");
        while (!sender.isFree()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                //ignore
            }
        }
        if (owner.getMyColor() == WHITE) {
            globalSetFigure(0, 7, NULL, NULL, true, false);
            globalSetFigure(4, 7, NULL, NULL, true, false);
            globalSetFigure(3, 7, owner.getMyColor(), ROOK, false, false);
            globalSetFigure(2, 7, owner.getMyColor(), KING, false, false);
        } else {
            globalSetFigure(7, 7, NULL, NULL, true, false);
            globalSetFigure(3, 7, NULL, NULL, true, false);
            globalSetFigure(4, 7, owner.getMyColor(), ROOK, false, false);
            globalSetFigure(5, 7, owner.getMyColor(), KING, false, false);
        }
        giveTurn();
        sender.free();
        owner.refresh();
    }

    public void globalClear() {
        localClear();
        LOGGER.info("Waiting to send clear notification");
        while (!sender.isFree()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                //ignore
            }
        }
        try {
            sender.send(CLEAR);
        } catch (IOException ex) {
            LOGGER.error("Failed to send clear notification", ex);
        }
        sender.free();
    }

    public void localClear() {
        for (int i = 0; i <= 7; i++) {
            for (int j = 0; j <= 7; j++) {
                fig[i][j].empty = true;
                fig[i][j].firstStep = true;
                fig[i][j].type = 6;
                fig[i][j].color = 6;
                fig[i][j].oX = -1;
                fig[i][j].oY = -1;
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
        return fig[col][row];
    }

    @Override
    public Figure draggedFigure() {
        return fig[x][y];
    }

    @Override
    public void updateDraggedPosition() {
        fig[x][y].oX = dragX * CELL_SIZE;
        fig[x][y].oY = dragY * CELL_SIZE;
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

    public void setLogic(Logic logic) {
        this.logic = logic;
    }
}
