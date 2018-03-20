package ks3.oc.swing;

import ks3.oc.Protocol;
import ks3.oc.board.BoardState;
import ks3.oc.chat.ChatDisplay;
import ks3.oc.dialogs.DialogWindow;
import ks3.oc.dialogs.FigurePickerWindow;
import ks3.oc.main.Main;
import ks3.oc.res.ResourceManager;
import ks3.oc.swing.dialogs.SwingAboutWindow;
import ks3.oc.swing.dialogs.SwingFigurePicker;
import ks3.oc.swing.dialogs.SwingPreferencesWindow;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SwingMainWindow extends Main {

    private final JFrame component;

    public SwingMainWindow(ResourceManager resourceManager, int type, int color, String address, int port, String name) {
        super(resourceManager, type, color, address, port, name);
        component = new JFrame("Online Chess");
        component.setSize(768, 531);
        component.setIgnoreRepaint(false);
        component.getContentPane().setLayout(new BorderLayout());
        component.setBackground(Color.black);
        component.setResizable(false);
        component.getContentPane().add("Center", board.getComponent());
        component.getContentPane().add("East", chat.getComponent());

        JMenuBar menuBar = new JMenuBar();
        JMenu help = new JMenu("Help");
        JMenu game = new JMenu("Game");
        JMenu debug = new JMenu("Debug");
        JMenuItem castleKingSide = new JMenuItem("Castle king side");
        JMenuItem castleQueenSide = new JMenuItem("Castle queen side");
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
            newGame.addActionListener(e -> sender.send(Protocol.OFFER_RESET));
            saveGame.addActionListener(event -> save());
            loadGame.addActionListener(event -> load());
            saveGame.setEnabled(false);
            loadGame.setEnabled(false);
        }

        DialogWindow aboutWindow = new SwingAboutWindow();
        about.addActionListener(event -> aboutWindow.open());

        DialogWindow preferencesWindow = new SwingPreferencesWindow(resourceManager, board);
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
                sender.send(Protocol.CLOSE);
                sender.deactivate("MF: client closed app");
                System.exit(0);
            }
        });
        component.setVisible(true);
    }

    @Override
    protected BoardState createBoard(ResourceManager resourceManager) {
        SwingDebugOverlay debugOverlay = new SwingDebugOverlay();
        return new SwingBoardDisplay(resourceManager, this, chat, debugOverlay);
    }

    @Override
    protected ChatDisplay createChat() {
        return new SwingChatDisplay(getMyName());
    }

    @Override
    protected FigurePickerWindow createFigurePicker(ResourceManager resourceManager) {
        return new SwingFigurePicker(board, resourceManager);
    }
}
