package ks3.oc.board;

import ks3.oc.ChatPanel;
import ks3.oc.Check;
import ks3.oc.Figure;
import ks3.oc.MainWindow;
import ks3.oc.Protocol;
import ks3.oc.Sender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class Board extends JPanel implements Protocol, Runnable {

    private boolean isDragging = false;
    public boolean isCheck = false;
    public Figure[][] fig = new Figure[8][8]; // figures on board
    public int[][] king = new int[2][2]; // i = (0 - black; 1 - white;)
    public int[] bckKing = new int[2];
    public int[][] hlight = new int[2][2];
    public int hlPos = 0;
    public Image board;
    public Image figureSet[][] = new Image[4][6]; // black; black selected; white; white selected
    private MainWindow owner;
    public int dragX, dragY, x, y;
    private Figure drawOnTop = null;
    private Thread trtr;
    private Sender sender;
    private ChatPanel chat;
    private Check check;
    private Color myRed = new Color(220, 0, 0);
    public int bDis, fDis;
    public boolean isLoading = false;

    public Board(MainWindow own, Sender send, ChatPanel ch) {
        super();
        owner = own;
        sender = send;
        chat = ch;
        check = new Check(this, owner);
        hlight[0][0] = -1;
        this.setSize(480, 480);
        bDis = 1;
        fDis = 0;
        loadImg(bDis, fDis);
        initFigures();
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                x = e.getX() / 60;
                y = e.getY() / 60;
                if (x > 7) x = 7;
                else if (x < 0) x = 0;
                if (y > 7) y = 7;
                else if (y < 0) y = 0;
                if ((fig[x][y].color == owner.getMyColor()) && (owner.isMyTurn())) {
                    dragX = x;
                    dragY = y;
                    check.move(fig[x][y], x, y);
                    drawOnTop = fig[x][y];
                    isDragging = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (isDragging) {
                    isDragging = false;
                    int a = e.getX() / 60;
                    int b = e.getY() / 60;
                    if (a > 7) a = 7;
                    else if (a < 0) a = 0;
                    if (b > 7) b = 7;
                    else if (b < 0) b = 0;
                    if ((fig[a][b].color == fig[x][y].color) && (!fig[a][b].empty)) {
                        fig[x][y].oX = dragX * 60;
                        fig[x][y].oY = dragY * 60;
                    } else {
                        while (check.calculating) {
                            try {
                                trtr.sleep(10);
                            } catch (Exception ex) {
                            }
                        }
                        check.drop(a, b);
                    }
                }
                repaint();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragging) {
                    fig[x][y].oX = e.getX() - 30;
                    if (fig[x][y].oX > 420) {
                        fig[x][y].oX = 420;
                    } else {
                        if (fig[x][y].oX < 0) {
                            fig[x][y].oX = 0;
                        }
                    }
                    fig[x][y].oY = e.getY() - 30;
                    if (fig[x][y].oY > 420) {
                        fig[x][y].oY = 420;
                    } else {
                        if (fig[x][y].oY < 0) {
                            fig[x][y].oY = 0;
                        }
                    }
                    repaint();
                }
            }
        });
        trtr = new Thread(this);
        trtr.start();
        owner.say("B: ini completed");
    }

    public void loadImg(int bd, int d) {
        MediaTracker mt = new MediaTracker(this);
        board = getToolkit().getImage(getClass().getResource("/img/" + bd + ".jpg"));
        mt.addImage(board, 0);
        try {
            mt.waitForAll();
        } catch (InterruptedException e) {
        }
        int i, j;
        for (i = 0; i <= 3; i++) {
            for (j = 0; j <= 5; j++) {
                figureSet[i][j] = getToolkit().getImage(getClass().getResource("/img/" + (i + d) + j + ".gif"));
                mt.addImage(figureSet[i][j], 0);
                try {
                    mt.waitForAll();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public void run() {
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(board, 0, 0, this);
        if (!isLoading) {
            int i, j;
            for (i = 0; i <= 7; i++) {
                for (j = 0; j <= 7; j++) {
                    if ((!fig[i][j].empty) && (fig[i][j].color != NULL)) {
                        g.drawImage(
                                figureSet[fig[i][j].color][fig[i][j].type],
                                fig[i][j].oX, fig[i][j].oY, this);
                    }
                }
            }
            if ((owner.isMyTurn()) && (hlight[0][0] != -1)) {
                g.setColor(myRed);
                g.drawRect(hlight[0][0] - 1, hlight[0][1] - 1, 61, 61);
                g.drawRect(hlight[0][0], hlight[0][1], 59, 59);
                g.drawRect(hlight[1][0] - 1, hlight[1][1] - 1, 61, 61);
                g.drawRect(hlight[1][0], hlight[1][1], 59, 59);
                g.setColor(myRed);
            }
            if (isDragging && !(drawOnTop == null)) {
                g.drawImage(
                        figureSet[fig[x][y].color + 1][fig[x][y].type],
                        fig[x][y].oX, fig[x][y].oY, this);
            }
        }
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    public void initFigures() {
        while (owner.getMyColor() == -1) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }

        BoardSetup setup;
        if (owner.getMyColor() == BLACK) {
            setup = BoardSetup.PLAYING_BLACK;
        } else {
            setup = BoardSetup.PLAYING_WHITE;
        }

        initMiddleRows();
        initPawnRow(setup.blackPawnRow, BLACK);
        initPawnRow(setup.whitePawnRow, WHITE);
        initFigureRow(setup.blackFigureRow, setup.queenCol, setup.kingCol, BLACK);
        initFigureRow(setup.whiteFigureRow, setup.queenCol, setup.kingCol, WHITE);
        initKingCoordinates(setup.kingCol, setup.blackFigureRow, setup.whiteFigureRow);
    }

    private void initMiddleRows() {
        for (int row = 2; row < 6; row++) {
            for (int col = 0; col < 8; col++) {
                fig[col][row] = new Figure();
            }
        }
    }

    private void initPawnRow(int row, int color) {
        for (int col = 0; col <= 7; col++) {
            fig[col][row] = createFigure(PAWN, color, col, row);
        }
    }

    private void initFigureRow(int row, int queenCol, int kingCol, int color) {
        fig[0][row] = createFigure(ROOK, color, 0, row);
        fig[7][row] = createFigure(ROOK, color, 7, row);
        fig[1][row] = createFigure(KNIGHT, color, 1, row);
        fig[6][row] = createFigure(KNIGHT, color, 6, row);
        fig[2][row] = createFigure(BISHOP, color, 2, row);
        fig[5][row] = createFigure(BISHOP, color, 5, row);
        fig[queenCol][row] = createFigure(QUEEN, color, queenCol, row);
        fig[kingCol][row] = createFigure(KING, color, kingCol, row);
    }

    private void initKingCoordinates(int kingCol, int blackFigureRow, int whiteFigureRow) {
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

    public void makeMove(int currX, int currY, int newX, int newY) {
        fig[newX][newY].oX = newX * 60;
        fig[newX][newY].oY = newY * 60;
        fig[newX][newY].empty = false;
        fig[newX][newY].type = fig[currX][currY].type;
        fig[newX][newY].color = fig[currX][currY].color;
        fig[currX][currY].empty = true;
        fig[currX][currY].firstStep = false;
        fig[currX][currY].color = NULL;
        fig[currX][currY].type = NULL;
        owner.setMyTurn(true);
        int z = owner.getMyColor() / 2;
        isCheck = !check.free2go(king[z][0], king[z][1], owner.getOppColor());
        if (!isLoading) {
            Figure[][] mf = fig;
            if (check.mate(king[z][0], king[z][1], fig)) {
                owner.setMyTurn(false);
                try {
                    while (!sender.isFree()) {
                        owner.say("B: waiting to send coordinates");
                        trtr.sleep(1000);
                    }
                    sender.send(MATE);
                    sender.free();
                } catch (Exception e) {
                }
                while (chat == null) {
                    try {
                        trtr.sleep(1000);
                    } catch (Exception e) {
                    }
                    chat = owner.getChat();
                }
                chat.addChatLine("* You lose! Check and mate.", "sys_&^_tem");
            }
        }
        hlight[0][0] = newX * 60;
        hlight[0][1] = newY * 60;
        hlight[1][0] = currX * 60;
        hlight[1][1] = currY * 60;
        repaint();
    }

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
                trtr.sleep(1000);
            }
            sender.send(COORDINATES);
            sender.send(invertedX);
            sender.send(invertedY);
            sender.send(newInvertedX);
            sender.send(newInvertedY);
            sender.free();
            isCheck = !check.free2go(king[z][0], king[z][1], owner.getOppColor());
            if (!isLoading) {
                Figure[][] mf = fig;
                if (check.mate(king[z][0], king[z][1], mf)) {
                    owner.setMyTurn(false);
                    while (!sender.isFree()) {
                        owner.say("B: waiting to send coordinates");
                        try {
                            trtr.sleep(1000);
                        } catch (Exception e) {
                        }
                    }
                    sender.send(MATE);
                    sender.free();
                    while (chat == null) {
                        try {
                            trtr.sleep(1000);
                        } catch (Exception e) {
                        }
                        chat = owner.getChat();
                    }
                    chat.addChatLine("* You lose! Check and mate.", "sys_&^_tem");
                }
            }
        } catch (Exception e) {
        }
        repaint();
    }

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
        } catch (Exception e) {
        }
    }

    public void localSetFigure(int x, int y, int color, int type, boolean isEmpty, boolean firstStep) {
        fig[x][y].empty = isEmpty;
        fig[x][y].firstStep = firstStep;
        fig[x][y].oX = x * 60;
        fig[x][y].oY = y * 60;
        fig[x][y].color = color;
        fig[x][y].type = type;
        hlight[hlPos][0] = x * 60;
        hlight[hlPos][1] = y * 60;
        if (hlPos == 1) {
            hlPos = 0;
        } else {
            ++hlPos;
        }
        repaint();
    }

    public void giveTurn() {
        try {
            sender.send(GIVE_TURN);
        } catch (Exception e) {
        }
    }

    public void reaveTurn() {
        try {
            sender.send(REAVE_TURN);
        } catch (Exception e) {
        }
    }

    public Check getCheck() {
        return check;
    }

    public void shortXchng() {
        owner.setMyTurn(false);
        while (!sender.isFree()) {
            owner.say("B: waiting to send coordinates");
            try {
                trtr.sleep(1000);
            } catch (Exception e) {
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
                trtr.sleep(1000);
            } catch (Exception e) {
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
                trtr.sleep(1000);
            } catch (Exception e) {
            }
        }
        try {
            sender.send(CLEAR);
        } catch (Exception e) {
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
}
