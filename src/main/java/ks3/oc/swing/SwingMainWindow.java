package ks3.oc.swing;

import ks3.oc.ChatPanel;
import ks3.oc.Figure;
import ks3.oc.Logger;
import ks3.oc.MainWindow;
import ks3.oc.board.start.ClassicStartingBoardInitializer;
import ks3.oc.swing.dialogs.Messenjah;
import ks3.oc.Protocol;
import ks3.oc.Sender;
import ks3.oc.board.Board;
import ks3.oc.logic.Logic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class SwingMainWindow extends JFrame implements Protocol, MainWindow {

    private static final String ERR_BASE = "oc.MainFrame::";

    private Logger log;
    private SwingMainWindow self = null;
    public String opponentName;
    public String myName;
    private ChatPanel chatPanel = null;
    private Board board = null;
    private Sender sender = null;
    private int oppColor;
    private int myColor = -1;
    private boolean myTurn = false;
    private Messenjah aboutWND;
    private Logic logic = null;
    private JMenuItem shortXchng, longXchng;

    public SwingMainWindow(Logger log, int type, int c, String addr, int port, String name) {
        super("Online Chess");
        self = this;
        this.log = log;
        setSize(768, 531);
        setIgnoreRepaint(false);
        getContentPane().setLayout(new BorderLayout());
        setBackground(Color.black);
        setResizable(false);

        myName = name;
        sender = new Sender(this, log, type, addr, port);
        if (type == SERVER) {
            setMyColor(c);
            if (getMyColor() == BLACK) {
                setOppColor(WHITE);
            } else {
                setOppColor(BLACK);
            }
            try {
                while (!sender.isFree()) {
                    log.log(ERR_BASE + "(): waiting to send color");
                    Thread.sleep(1000);
                }
                sender.send(COLOR);
                if (getMyColor() == WHITE) {
                    setMyTurn(true);
                    sender.send(BLACK);
                } else {
                    sender.send(WHITE);
                }
                sender.free();
            } catch (Exception e) {
                log.log(ERR_BASE + "(): exception while waiting to send color: " + e.getMessage());
            }
        }

        while (getMyColor() == -1) {
            log.log(ERR_BASE + "(): waiting for color");
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                log.log(ERR_BASE + "(): exception while waiting to receive color: " + e.getMessage());
            }
        }

        board = new Board(log, this, sender, chatPanel);
        getContentPane().add("Center", board);

        chatPanel = new ChatPanel(sender, this);
        getContentPane().add("East", chatPanel);

        try {
            while (!sender.isFree()) {
                log.log(ERR_BASE + "(): waiting to send name");
                Thread.sleep(1000);
            }
            sender.send(NAME);
            sender.send(myName);
            sender.free();
        } catch (Exception e) {
            log.log(ERR_BASE + "(): exception while waiting to send name: " + e.getMessage());
        }

        JMenuBar menuBar = new JMenuBar();
        JMenu help = new JMenu("Help");
        JMenu game = new JMenu("Game");
        JMenu debug = new JMenu("Debug");
        shortXchng = new JMenuItem("Castle king side");
        longXchng = new JMenuItem("Castle queen side");
        JMenuItem preferences = new JMenuItem("Preferences");
        JMenuItem about = new JMenuItem("About");
        JMenuItem dump = new JMenuItem("Dump");
        JCheckBoxMenuItem overlay = new JCheckBoxMenuItem("Overlay", board.isDebug());
        game.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                while (logic == null) {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception ex) {
                        self.log.log(ERR_BASE + "gamePressed(): " + ex.getMessage());
                    }
                    logic = board.getLogic();
                }
                shortXchng.setEnabled(logic.kingSideCastlingAllowed());
                longXchng.setEnabled(logic.queenSideCastlingAllowed());
            }
        });
        shortXchng.addActionListener(event -> {
            if (shortXchng.isEnabled()) {
                board.shortXchng();
            }
        });
        longXchng.addActionListener(event -> {
            if (longXchng.isEnabled()) {
                board.longXchng();
            }
        });
        dump.addActionListener(event -> save());
        overlay.addActionListener(event -> {
            board.setDebug(!board.isDebug());
            overlay.setSelected(board.isDebug());
            board.repaint();
        });
        if (type == SERVER) {
            JMenu file = new JMenu("File");
            JMenuItem newGame = new JMenuItem("New game");
            JMenuItem saveGame = new JMenuItem("Save game");
            JMenuItem loadGame = new JMenuItem("Load game");
            game.add(newGame);
            game.addSeparator();
            file.add(saveGame);
            file.add(loadGame);
            menuBar.add(file);
            newGame.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    while (!sender.isFree()) {
                        self.log.log(ERR_BASE + "newGame(): waiting to send RESET");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            //ignore
                        }
                    }
                    try {
                        sender.send(OFFER_RESET);
                    } catch (IOException ex) {
                        log.log(ex.getMessage());
                    }
                    sender.free();
                }
            });
            saveGame.addActionListener(event -> save());
            loadGame.addActionListener(event -> {
                board.isLoading = true;
                load();
            });
        }
        about.addActionListener(event -> {
            if (aboutWND == null) {
                aboutWND = new Messenjah();
            } else {
                aboutWND.setVisible(true);
            }
        });
        preferences.addActionListener(event -> new Messenjah(board));
        game.add(shortXchng);
        game.add(longXchng);
        game.addSeparator();
        game.add(preferences);
        help.add(about);
        debug.add(dump);
        debug.add(overlay);
        menuBar.add(game);
        menuBar.add(help);
        menuBar.add(debug);
        setJMenuBar(menuBar);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);
                while (!sender.isFree()) {
                    self.log.log(ERR_BASE + "windowClosing(): waiting to send CLOSE");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        //ignore
                    }
                }
                try {
                    sender.send(CLOSE);
                } catch (IOException ex) {
                    log.log(ex.getMessage());
                }
                sender.suicide("MF: client closed app");
                System.exit(0);
            }
        });
        this.setVisible(true);
    }

    public void reset() {
        setMyTurn(false);
        board.initFigures(new ClassicStartingBoardInitializer());
        board.hlight[0][0] = -1;
        if (getMyColor() == WHITE) {
            setMyTurn(true);
        }
        board.repaint();
        chatPanel.addChatLine("* Server starts new game", "sys_&^_tem");
    }

    public void connectionKilled() {
        chatPanel.addChatLine("* " + opponentName + " quits", "sys_&^_tem");
    }

    @Override
    public ChatPanel getChat() {
        return chatPanel;
    }

    public Board getBoard() {
        return board;
    }

    private void save() {
        int i, j;
        boolean t = isMyTurn();
        String cat;
        setMyTurn(false);
        board.reaveTurn();
        try {
            Figure fig;
            PrintWriter pw = new PrintWriter(new FileOutputStream("save.txt"));
            for (i = 0; i <= 7; i++) {
                for (j = 0; j <= 7; j++) {
                    fig = board.figureAt(i, j);
                    cat = fig.empty + ":" + fig.firstStep + ":" + fig.type + ":" + fig.color + ":" + fig.oX + ":" + fig.oY;
                    pw.println(cat);
                }
            }
            pw.println(t + ":" + board.hlight[0][0] + ":" + board.hlight[0][1] + ":" + board.hlight[1][0] + ":" + board.hlight[1][1]);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            log.log(ERR_BASE + "save(): exception while saving: " + e.getMessage());
        }
        setMyTurn(t);
        if (!isMyTurn()) {
            board.giveTurn();
        }
    }

    private void load() {
        int i, j, type, color, oX, oY;
        boolean firstStep, empty;
        String str;
        setMyTurn(false);
        board.reaveTurn();
        StringTokenizer breaker;
        try {
            board.globalClear();
            BufferedReader br = new BufferedReader(new FileReader("save.txt"));

            for (i = 0; i <= 7; i++) {
                for (j = 0; j <= 7; j++) {
                    str = br.readLine();
                    breaker = new StringTokenizer(str, ":");
                    empty = Boolean.parseBoolean(breaker.nextToken());
                    firstStep = Boolean.parseBoolean(breaker.nextToken());
                    type = Integer.parseInt(breaker.nextToken());
                    color = Integer.parseInt(breaker.nextToken());
                    oX = Integer.parseInt(breaker.nextToken());
                    oY = Integer.parseInt(breaker.nextToken());
                    board.globalSetFigure(oX / 60, oY / 60, color, type, empty, firstStep);
                    if (i == 0 && j == 0) {
                        System.out.println("MF: " + empty + ":" + firstStep + ":" + type);
                    }
                }
            }

            str = br.readLine();
            breaker = new StringTokenizer(str, ":");
            setMyTurn(Boolean.parseBoolean(breaker.nextToken()));
            board.hlight[0][0] = Integer.parseInt(breaker.nextToken());
            board.hlight[0][1] = Integer.parseInt(breaker.nextToken());
            board.hlight[1][0] = Integer.parseInt(breaker.nextToken());
            board.hlight[1][1] = Integer.parseInt(breaker.nextToken());
            br.close();
            log.log(ERR_BASE + "load(): successful load");
        } catch (Exception e) {
            log.log(ERR_BASE + "load(): exception while loading");
        }
        if (!isMyTurn()) {
            board.giveTurn();
        }
        board.repaint();
        board.isLoading = false;
    }

    @Override
    public void say(String s) {
        log.log(s);
    }

    @Override
    public int getOppColor() {
        return oppColor;
    }

    public void setOppColor(int oppColor) {
        this.oppColor = oppColor;
    }

    @Override
    public int getMyColor() {
        return myColor;
    }

    public void setMyColor(int myColor) {
        this.myColor = myColor;
    }

    @Override
    public boolean isMyTurn() {
        return myTurn;
    }

    @Override
    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }
}
