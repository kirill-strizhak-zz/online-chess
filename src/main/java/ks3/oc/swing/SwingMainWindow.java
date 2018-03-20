package ks3.oc.swing;

import ks3.oc.Protocol;
import ks3.oc.board.start.ClassicStartingBoardInitializer;
import ks3.oc.conn.ClientSender;
import ks3.oc.conn.ServerSender;
import ks3.oc.dialogs.DialogWindow;
import ks3.oc.dialogs.FigurePickerWindow;
import ks3.oc.logic.Logic;
import ks3.oc.main.Main;
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
import java.io.IOException;

public class SwingMainWindow extends Main {

    private static final Logger LOGGER = Logger.getLogger(SwingMainWindow.class);

    private final JFrame component;
    private final Logic logic;
    private final DialogWindow aboutWindow;
    private final DialogWindow preferencesWindow;
    private JMenuItem castleKingSide, castleQueenSide;

    public SwingMainWindow(ResourceManager resourceManager, int type, int color, String address, int port, String name) {
        component = new JFrame("Online Chess");
        component.setSize(768, 531);
        component.setIgnoreRepaint(false);
        component.getContentPane().setLayout(new BorderLayout());
        component.setBackground(Color.black);
        component.setResizable(false);

        setMyName(name);
        chat = new SwingChatDisplay(getMyName());
        SwingDebugOverlay debugOverlay = new SwingDebugOverlay();
        board = new SwingBoardDisplay(resourceManager, this, chat, debugOverlay);
        FigurePickerWindow figurePickerWindow = new SwingFigurePicker(board, resourceManager);
        logic = new Logic(board, this, figurePickerWindow);
        board.setLogic(logic);

        component.getContentPane().add("Center", board.getComponent());
        component.getContentPane().add("East", chat.getComponent());

        this.aboutWindow = new SwingAboutWindow();
        this.preferencesWindow = new SwingPreferencesWindow(resourceManager, board);

        if (type == Protocol.CLIENT) {
            sender = new ClientSender(this, board, chat, address, port);
        } else {
            sender = new ServerSender(this, board, chat, address, port);
            setMyColor(color);
            if (getMyColor() == Protocol.BLACK) {
                setOppColor(Protocol.WHITE);
            } else {
                setOppColor(Protocol.BLACK);
            }
            try {
                sender.send(Protocol.COLOR);
                if (getMyColor() == Protocol.WHITE) {
                    setMyTurn(true);
                    sender.send(Protocol.BLACK);
                } else {
                    sender.send(Protocol.WHITE);
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
            sender.send(Protocol.NAME);
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
        castleKingSide = new JMenuItem("Castle king side");
        castleQueenSide = new JMenuItem("Castle queen side");
        JMenuItem preferences = new JMenuItem("Preferences");
        JMenuItem about = new JMenuItem("About");
        JMenuItem dump = new JMenuItem("Dump");
        JCheckBoxMenuItem overlay = new JCheckBoxMenuItem("Overlay", board.isDebug());
        game.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                castleKingSide.setEnabled(logic.kingSideCastlingAllowed());
                castleQueenSide.setEnabled(logic.queenSideCastlingAllowed());
            }
        });
        castleKingSide.addActionListener(event -> {
            if (castleKingSide.isEnabled()) {
                board.castleKingSide();
            }
        });
        castleQueenSide.addActionListener(event -> {
            if (castleQueenSide.isEnabled()) {
                board.castleQueenSide();
            }
        });
        dump.addActionListener(event -> save());
        overlay.addActionListener(event -> {
            board.setDebug(!board.isDebug());
            overlay.setSelected(board.isDebug());
            board.refresh();
        });
        if (type == Protocol.SERVER) {
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
                        sender.send(Protocol.OFFER_RESET);
                    } catch (IOException ex) {
                        LOGGER.error("Failed to send reset offer", ex);
                    }
                }
            });
            saveGame.addActionListener(event -> save());
            loadGame.addActionListener(event -> load());
            saveGame.setEnabled(false);
            loadGame.setEnabled(false);
        }
        about.addActionListener(event -> aboutWindow.open());
        preferences.addActionListener(event -> preferencesWindow.open());
        game.add(castleKingSide);
        game.add(castleQueenSide);
        game.addSeparator();
        game.add(preferences);
        help.add(about);
        debug.add(dump);
        debug.add(overlay);
        menuBar.add(game);
        menuBar.add(help);
        menuBar.add(debug);
        component.setJMenuBar(menuBar);

        component.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                component.setVisible(false);
                try {
                    sender.send(Protocol.CLOSE);
                } catch (IOException ex) {
                    LOGGER.error("Failed to send close notification", ex);
                }
                sender.deactivate("MF: client closed app");
                System.exit(0);
            }
        });
        component.setVisible(true);
    }
}
