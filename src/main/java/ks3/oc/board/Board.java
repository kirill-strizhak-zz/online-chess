package ks3.oc.board;

import ks3.oc.ChatPanel;
import ks3.oc.Figure;
import ks3.oc.Logger;
import ks3.oc.MainWindow;
import ks3.oc.Protocol;
import ks3.oc.Sender;
import ks3.oc.board.start.ClassicStartingBoardInitializer;
import ks3.oc.board.start.StartingBoardInitializer;
import ks3.oc.logic.Logic;
import ks3.oc.swing.SwingDebugOverlay;
import ks3.oc.swing.dialogs.SwingFigurePicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;

public class Board extends JPanel implements Protocol, BoardState {

    private static final int CELL_SIZE = 60;

    private final SwingDebugOverlay debugOverlay;
    private final Logger log;

    private boolean isDragging = false;
    private boolean check = false;
    private Figure[][] fig = new Figure[8][8]; // figures on board
    public int[][] king = new int[2][2]; // i = (0 - black; 1 - white;)
    public int[] bckKing = new int[2];
    public int[][] hlight = new int[2][2];
    public int hlPos = 0;
    public Image boardBackground;
    public Image figureSet[][] = new Image[4][6]; // black; black selected; white; white selected
    private MainWindow owner;
    public int dragX, dragY, x, y;
    private Sender sender;
    private ChatPanel chat;
    private Logic logic;
    private Color myRed = new Color(220, 0, 0);
    public int boardId, figureId;
    public boolean isLoading = false;

    public Board(Logger log, MainWindow own, Sender send, ChatPanel ch) {
        super();
        debugOverlay = new SwingDebugOverlay();
        this.log = log;
        owner = own;
        sender = send;
        chat = ch;
        logic = new Logic(this, owner, new SwingFigurePicker());
        hlight[0][0] = -1;
        this.setSize(480, 480);
        boardId = 1;
        figureId = 0;
        loadImg();
        initFigures(new ClassicStartingBoardInitializer());
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent event) {
                selectFigure(event.getX() / CELL_SIZE, event.getY() / CELL_SIZE);
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                if (isDragging()) {
                    releaseFigure(event.getX() / CELL_SIZE, event.getY() / CELL_SIZE);
                }
                repaint();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent event) {
                if (isDragging()) {
                    dragFigure(event.getX(), event.getY());
                    repaint();
                }
            }
        });
        owner.say("B: ini completed");
    }

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
            isDragging = true;
        }
    }

    public void releaseFigure(int col, int row) {
        isDragging = false;
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

    public void loadImg() {
        MediaTracker mt = new MediaTracker(this);
        boardBackground = getToolkit().getImage(getClass().getResource("/img/" + boardId + ".jpg"));
        mt.addImage(boardBackground, 0);
        try {
            mt.waitForAll();
        } catch (InterruptedException ex) {
            //ignore
        }
        for (int i = 0; i <= 3; i++) {
            for (int j = 0; j <= 5; j++) {
                figureSet[i][j] = getToolkit().getImage(getClass().getResource("/img/" + (i + figureId) + j + ".gif"));
                mt.addImage(figureSet[i][j], 0);
                try {
                    mt.waitForAll();
                } catch (InterruptedException ex) {
                    //ignore
                }
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(boardBackground, 0, 0, this);
        if (!isLoading) {
            Figure figure;
            for (int col = 0; col <= 7; col++) {
                for (int row = 0; row <= 7; row++) {
                    if (!isCellEmpty(col, row)) {
                        figure = figureAt(col, row);
                        g.drawImage(getImageOfFigure(figure), figure.oX, figure.oY, this);
                    }
                }
            }
            if (needToDrawHighlight()) {
                g.setColor(myRed);
                int[][] highlight = getHighlight();
                g.drawRect(highlight[0][0] - 1, highlight[0][1] - 1, CELL_SIZE + 1, CELL_SIZE + 1);
                g.drawRect(highlight[0][0], highlight[0][1], CELL_SIZE - 1, CELL_SIZE - 1);
                g.drawRect(highlight[1][0] - 1, highlight[1][1] - 1, CELL_SIZE + 1, CELL_SIZE + 1);
                g.drawRect(highlight[1][0], highlight[1][1], CELL_SIZE - 1, CELL_SIZE - 1);
                g.setColor(myRed);
            }
            if (isDragging()) {
                figure = getDraggedFigure();
                g.drawImage(getImageOfDraggedFigure(), figure.oX, figure.oY, this);
            }
            debugOverlay.draw(g, this, logic, owner.getMyColor(), owner.getOppColor());
        }
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    public int[][] getHighlight() {
        return hlight;
    }

    public boolean isCellEmpty(int col, int row) {
        return fig[col][row].empty;
    }

    public boolean needToDrawHighlight() {
        return owner.isMyTurn() && hlight[0][0] != -1;
    }

    public Image getImageOfFigure(Figure figure) {
        return figureSet[figure.color][figure.type];
    }

    public Image getImageOfDraggedFigure() {
        Figure figure = fig[x][y];
        return figureSet[figure.color + 1][figure.type];
    }

    public Figure getDraggedFigure() {
        return fig[x][y];
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
        if (!isLoading) {
            if (logic.mate(king[z][0], king[z][1])) {
                owner.setMyTurn(false);
                try {
                    while (!sender.isFree()) {
                        owner.say("B: waiting to send coordinates");
                        Thread.sleep(1000);
                    }
                    sender.send(MATE);
                    sender.free();
                } catch (IOException | InterruptedException ex) {
                    log.log(ex.getMessage());
                }
                while (chat == null) {
                    try {
                        Thread.sleep(1000);
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
        repaint();
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
            while (!sender.isFree()) {
                owner.say("B: waiting to send coordinates");
                Thread.sleep(1000);
            }
            sender.send(COORDINATES);
            sender.send(invertedX);
            sender.send(invertedY);
            sender.send(newInvertedX);
            sender.send(newInvertedY);
            sender.free();
            check = !logic.kingSafeAt(king[z][0], king[z][1], owner.getOppColor());
            if (!isLoading) {
                if (logic.mate(king[z][0], king[z][1])) {
                    owner.setMyTurn(false);
                    while (!sender.isFree()) {
                        owner.say("B: waiting to send coordinates");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            //ignore
                        }
                    }
                    sender.send(MATE);
                    sender.free();
                    while (chat == null) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            //ignore
                        }
                        chat = owner.getChat();
                    }
                    chat.addChatLine("* You lose! Check and mate.", "sys_&^_tem");
                }
            }
        } catch (IOException | InterruptedException ex) {
            log.log(ex.getMessage());
        }
        repaint();
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
            log.log(ex.getMessage());
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
        repaint();
    }

    @Override
    public void giveTurn() {
        try {
            sender.send(GIVE_TURN);
        } catch (IOException ex) {
            log.log(ex.getMessage());
        }
    }

    public void reaveTurn() {
        try {
            sender.send(REAVE_TURN);
        } catch (IOException ex) {
            log.log(ex.getMessage());
        }
    }

    public Logic getLogic() {
        return logic;
    }

    public void shortXchng() {
        owner.setMyTurn(false);
        while (!sender.isFree()) {
            owner.say("B: waiting to send coordinates");
            try {
                Thread.sleep(1000);
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
        repaint();
    }

    public void longXchng() {
        owner.setMyTurn(false);
        while (!sender.isFree()) {
            owner.say("B: waiting to send coordinates");
            try {
                Thread.sleep(1000);
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
        repaint();
    }

    public void globalClear() {
        localClear();
        while (!sender.isFree()) {
            owner.say("B: waiting to send CLEAR");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                //ignore
            }
        }
        try {
            sender.send(CLEAR);
        } catch (IOException ex) {
            log.log(ex.getMessage());
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

    public boolean isDebug() {
        return debugOverlay.isEnabled();
    }

    public void setDebug(boolean enabled) {
        debugOverlay.setEnabled(enabled);
    }

    @Override
    public boolean isDragging() {
        return isDragging;
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
    public void reloadImages(int boardId, int figureId) {
        this.boardId = boardId;
        this.figureId = figureId;
        loadImg();
        repaint();
    }

    @Override
    public int getBoardId() {
        return boardId;
    }

    @Override
    public int getFigureId() {
        return figureId;
    }

    @Override
    public void setHlPos(int hlPos) {
        this.hlPos = hlPos;
    }

    @Override
    public Image getFigureImage(int color, int type) {
        return figureSet[color][type];
    }
}
