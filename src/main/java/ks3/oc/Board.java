package ks3.oc;

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
    private MainFrame owner;
    public int dragX,  dragY,  x,  y;
    private Figure drawOnTop = null;
    private Thread trtr;
    private Sender sender;
    private ChatPanel chat;
    private Check check;
    private Color myRed = new Color(220, 0, 0);
    public int bDis,  fDis;
    public boolean isLoading = false;

    public Board(MainFrame own, Sender send, ChatPanel ch) {
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
        testIni();
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
                if ((fig[x][y].color == owner.myColor) && (owner.myTurn)) {
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
            if ((owner.myTurn) && (hlight[0][0] != -1)) {
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
        int i, j, k, l, m, n;
        while (owner.myColor == -1) {
            try {
                trtr.sleep(100);
            } catch (Exception e) {
            }
        }
        if (owner.myColor == BLACK) {
            k = 7;
            l = 0;
            m = 6;
            n = 1;
        } else {
            k = 0;
            l = 7;
            m = 1;
            n = 6;
        }
        for (i = 0; i <= 7; i++) {
            for (j = 0; j <= 7; j++) {
                fig[i][j] = new Figure();
            }
        }
        for (i = 0; i <= 7; i++) {
            fig[i][k] = new Figure();
            fig[i][k].empty = false;
            fig[i][k].color = BLACK;
            fig[i][k].oX = i * 60;
            fig[i][k].oY = k * 60;
            switch (i) {
                case 0:
                    fig[i][k].type = ROOK;
                    break;
                case 1:
                    fig[i][k].type = KNIGHT;
                    break;
                case 2:
                    fig[i][k].type = BISHOP;
                    break;
                case 3:
                    if (owner.myColor == BLACK) {
                        fig[i][k].type = KING;
                        king[0][0] = i;
                        king[0][1] = k;
                    } else {
                        fig[i][k].type = QUEEN;
                    }
                    break;
                case 4:
                    if (owner.myColor == BLACK) {
                        fig[i][k].type = QUEEN;
                    } else {
                        fig[i][k].type = KING;
                        king[0][0] = i;
                        king[0][1] = k;
                    }
                    break;
                case 5:
                    fig[i][k].type = BISHOP;
                    break;
                case 6:
                    fig[i][k].type = KNIGHT;
                    break;
                case 7:
                    fig[i][k].type = ROOK;
                    break;
            }
        }
        for (i = 0; i <= 7; i++) {
            fig[i][l] = new Figure();
            fig[i][l].empty = false;
            fig[i][l].color = WHITE;
            fig[i][l].oX = i * 60;
            fig[i][l].oY = l * 60;
            switch (i) {
                case 0:
                    fig[i][l].type = ROOK;
                    break;
                case 1:
                    fig[i][l].type = KNIGHT;
                    break;
                case 2:
                    fig[i][l].type = BISHOP;
                    break;
                case 3:
                    if (owner.myColor == BLACK) {
                        fig[i][l].type = KING;
                        king[1][0] = i;
                        king[1][1] = l;
                    } else {
                        fig[i][l].type = QUEEN;
                    }
                    break;
                case 4:
                    if (owner.myColor == BLACK) {
                        fig[i][l].type = QUEEN;
                    } else {
                        fig[i][l].type = KING;
                        king[1][0] = i;
                        king[1][1] = l;
                    }
                    break;
                case 5:
                    fig[i][l].type = BISHOP;
                    break;
                case 6:
                    fig[i][l].type = KNIGHT;
                    break;
                case 7:
                    fig[i][l].type = ROOK;
                    break;
            }
        }
        for (i = 0; i <= 7; i++) {
            fig[i][m] = new Figure();
            fig[i][m].empty = false;
            fig[i][m].type = PAWN;
            fig[i][m].color = BLACK;
            fig[i][m].oX = i * 60;
            fig[i][m].oY = m * 60;
        }
        for (i = 0; i <= 7; i++) {
            fig[i][n] = new Figure();
            fig[i][n].empty = false;
            fig[i][n].type = PAWN;
            fig[i][n].color = WHITE;
            fig[i][n].oX = i * 60;
            fig[i][n].oY = n * 60;
        }
    }

    public void testIni() {
        /*debugging tool*/
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
        owner.myTurn = true;
        int z = owner.myColor / 2;
        isCheck = !check.free2go(king[z][0], king[z][1], owner.oppColor);
        if (!isLoading) {
            Figure[][] mf = fig;
            if (check.mate(king[z][0], king[z][1], fig)) {
                owner.myTurn = false;
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
        owner.myTurn = false;
        try {
            int z = owner.myColor / 2;
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
            isCheck = !check.free2go(king[z][0], king[z][1], owner.oppColor);
            if (!isLoading) {
                Figure[][] mf = fig;
                if (check.mate(king[z][0], king[z][1], mf)) {
                    owner.myTurn = false;
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
        owner.myTurn = false;
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
        owner.myTurn = false;
        while (!sender.isFree()) {
            owner.say("B: waiting to send coordinates");
            try {
                trtr.sleep(1000);
            } catch (Exception e) {
            }
        }
        if (owner.myColor == WHITE) {
            globalSetFigure(7, 7, NULL, NULL, true, false);
            globalSetFigure(4, 7, NULL, NULL, true, false);
            globalSetFigure(5, 7, owner.myColor, ROOK, false, false);
            globalSetFigure(6, 7, owner.myColor, KING, false, false);
        } else {
            globalSetFigure(0, 7, NULL, NULL, true, false);
            globalSetFigure(3, 7, NULL, NULL, true, false);
            globalSetFigure(2, 7, owner.myColor, ROOK, false, false);
            globalSetFigure(1, 7, owner.myColor, KING, false, false);
        }
        giveTurn();
        sender.free();
        repaint();
    }

    public void longXchng() {
        owner.myTurn = false;
        while (!sender.isFree()) {
            owner.say("B: waiting to send coordinates");
            try {
                trtr.sleep(1000);
            } catch (Exception e) {
            }
        }
        if (owner.myColor == WHITE) {
            globalSetFigure(0, 7, NULL, NULL, true, false);
            globalSetFigure(4, 7, NULL, NULL, true, false);
            globalSetFigure(3, 7, owner.myColor, ROOK, false, false);
            globalSetFigure(2, 7, owner.myColor, KING, false, false);
        } else {
            globalSetFigure(7, 7, NULL, NULL, true, false);
            globalSetFigure(3, 7, NULL, NULL, true, false);
            globalSetFigure(4, 7, owner.myColor, ROOK, false, false);
            globalSetFigure(5, 7, owner.myColor, KING, false, false);
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
