package ks3.oc.swing;

import ks3.oc.Figure;
import ks3.oc.MainWindow;
import ks3.oc.Protocol;
import ks3.oc.board.start.ClassicStartingBoardInitializer;
import ks3.oc.conn.ClientSender;
import ks3.oc.conn.Sender;
import ks3.oc.conn.ServerSender;
import ks3.oc.dialogs.DialogWindow;
import ks3.oc.dialogs.FigurePickerWindow;
import ks3.oc.logic.Logic;
import ks3.oc.res.ResourceManager;
import ks3.oc.swing.dialogs.SwingAboutWindow;
import ks3.oc.swing.dialogs.SwingFigurePicker;
import ks3.oc.swing.dialogs.SwingPreferencesWindow;
import org.apache.log4j.Logger;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import java.awt.BorderLayout;
import java.awt.Color;
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

    private static final Logger LOGGER = Logger.getLogger(SwingMainWindow.class);

    private final SwingBoardDisplay board;
    private final Logic logic;
    private final SwingChatDisplay chat;
    private final DialogWindow aboutWindow;
    private final DialogWindow preferencesWindow;

    private String opponentName;
    private String myName;
    private Sender sender;
    private int oppColor;
    private int myColor = -1;
    private boolean myTurn = false;
    private JMenuItem shortXchng, longXchng;

    public SwingMainWindow(ResourceManager resourceManager, int type, int c, String addr, int port, String name) {
        super("Online Chess");
        setSize(768, 531);
        setIgnoreRepaint(false);
        getContentPane().setLayout(new BorderLayout());
        setBackground(Color.black);
        setResizable(false);

        setMyName(name);
        chat = new SwingChatDisplay(getMyName());
        SwingDebugOverlay debugOverlay = new SwingDebugOverlay();
        board = new SwingBoardDisplay(resourceManager, this, chat, debugOverlay);
        FigurePickerWindow figurePickerWindow = new SwingFigurePicker(board, resourceManager);
        logic = new Logic(board, this, figurePickerWindow);
        board.setLogic(logic);

        getContentPane().add("Center", board.getComponent());
        getContentPane().add("East", chat.getComponent());

        this.aboutWindow = new SwingAboutWindow();
        this.preferencesWindow = new SwingPreferencesWindow(resourceManager, board);

        if (type == CLIENT) {
            sender = new ClientSender(this, board, chat, addr, port);
        } else {
            sender = new ServerSender(this, board, chat, addr, port);
            setMyColor(c);
            if (getMyColor() == BLACK) {
                setOppColor(WHITE);
            } else {
                setOppColor(BLACK);
            }
            try {
                sender.send(COLOR);
                if (getMyColor() == WHITE) {
                    setMyTurn(true);
                    sender.send(BLACK);
                } else {
                    sender.send(WHITE);
                }
            } catch (IOException ex) {
                LOGGER.error("Failed to send color", ex);
            }
        }

        while (getMyColor() == -1) {
            LOGGER.info("Waiting for color");
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                LOGGER.error("Failed to receive color", ex);
            }
        }

        try {
            sender.send(NAME);
            sender.send(getMyName());
        } catch (IOException ex) {
            LOGGER.error("Failed to send name", ex);
        }

        board.setSender(sender);
        board.initFigures(new ClassicStartingBoardInitializer());
        chat.setSender(sender);

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
            board.refresh();
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
                    try {
                        sender.send(OFFER_RESET);
                    } catch (IOException ex) {
                        LOGGER.error("Failed to send reset offer", ex);
                    }
                }
            });
            saveGame.addActionListener(event -> save());
            loadGame.addActionListener(event -> {
                board.loading = true;
                load();
            });
        }
        about.addActionListener(event -> aboutWindow.open());
        preferences.addActionListener(event -> preferencesWindow.open());
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
                try {
                    sender.send(CLOSE);
                } catch (IOException ex) {
                    LOGGER.error("Failed to send close notification", ex);
                }
                sender.deactivate("MF: client closed app");
                System.exit(0);
            }
        });
        this.setVisible(true);
    }

    @Override
    public void reset() {
        setMyTurn(false);
        board.initFigures(new ClassicStartingBoardInitializer());
        board.highlight[0][0] = -1;
        if (getMyColor() == WHITE) {
            setMyTurn(true);
        }
        board.refresh();
        chat.addChatLine("* Server starts new game", Protocol.SYSTEM);
    }

    @Override
    public void connectionKilled() {
        chat.addChatLine("* " + getOpponentName() + " quits", Protocol.SYSTEM);
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
            pw.println(t + ":" + board.highlight[0][0] + ":" + board.highlight[0][1] + ":" + board.highlight[1][0] + ":" + board.highlight[1][1]);
            pw.flush();
            pw.close();
        } catch (IOException ex) {
            LOGGER.error("Failed to save", ex);
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
            board.highlight[0][0] = Integer.parseInt(breaker.nextToken());
            board.highlight[0][1] = Integer.parseInt(breaker.nextToken());
            board.highlight[1][0] = Integer.parseInt(breaker.nextToken());
            board.highlight[1][1] = Integer.parseInt(breaker.nextToken());
            br.close();
        } catch (IOException ex) {
            LOGGER.error("Failed to load", ex);
        }
        if (!isMyTurn()) {
            board.giveTurn();
        }
        board.refresh();
        board.loading = false;
    }

    @Override
    public int getOppColor() {
        return oppColor;
    }

    @Override
    public void setOppColor(int oppColor) {
        this.oppColor = oppColor;
    }

    @Override
    public int getMyColor() {
        return myColor;
    }

    @Override
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

    @Override
    public void refresh() {
        board.refresh();
    }

    @Override
    public String getOpponentName() {
        return opponentName;
    }

    @Override
    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }

    public String getMyName() {
        return myName;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }
}
