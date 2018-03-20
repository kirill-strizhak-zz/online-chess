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
        component = createComponent(resourceManager, type);
        component.setVisible(true);
    }

    private JFrame createComponent(ResourceManager resourceManager, int type) {
        JFrame component = new JFrame("Online Chess");
        component.setSize(768, 531);
        component.setIgnoreRepaint(false);
        component.getContentPane().setLayout(new BorderLayout());
        component.setBackground(Color.black);
        component.setResizable(false);
        component.getContentPane().add("Center", board.getComponent());
        component.getContentPane().add("East", chat.getComponent());
        component.setJMenuBar(createMenuBar(resourceManager, type));
        component.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                component.setVisible(false);
                sender.send(Protocol.CLOSE);
                sender.deactivate("Client closed app");
                System.exit(0);
            }
        });
        return component;
    }

    private JMenuBar createMenuBar(ResourceManager resourceManager, int type) {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createGameMenu(resourceManager, type));
        menuBar.add(createHelpMenu());
        menuBar.add(createDebugMenu());
        return menuBar;
    }

    private JMenu createGameMenu(ResourceManager resourceManager, int type) {
        JMenu menu = new JMenu("Game");
        if (type == Protocol.SERVER) {
            menu.add(createActionMenuItem("New game", () -> sender.send(Protocol.OFFER_RESET)));
        }
        addCastlingMenuItems(menu);
        menu.addSeparator();
        menu.add(createDialogMenuItem("Preferences", new SwingPreferencesWindow(resourceManager, board)));
        return menu;
    }

    private void addCastlingMenuItems(JMenu menu) {
        JMenuItem castleKingSide = createActionMenuItem("Castle king side", board::castleKingSide);
        JMenuItem castleQueenSide = createActionMenuItem("Castle queen side", board::castleQueenSide);
        menu.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                castleKingSide.setEnabled(logic.kingSideCastlingAllowed());
                castleQueenSide.setEnabled(logic.queenSideCastlingAllowed());
            }
        });
        menu.add(castleKingSide);
        menu.add(castleQueenSide);
    }

    private JMenu createHelpMenu() {
        JMenu menu = new JMenu("Help");
        menu.add(createDialogMenuItem("About", new SwingAboutWindow()));
        return menu;
    }

    private JMenu createDebugMenu() {
        JMenu menu = new JMenu("Debug");
        JCheckBoxMenuItem overlay = new JCheckBoxMenuItem("Overlay", board.isDebug());
        overlay.addActionListener(event -> {
            board.setDebug(!board.isDebug());
            overlay.setSelected(board.isDebug());
            board.refresh();
        });
        menu.add(overlay);
        return menu;
    }

    private JMenuItem createActionMenuItem(String text, Runnable action) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.addActionListener(event -> action.run());
        return menuItem;
    }

    private JMenuItem createDialogMenuItem(String text, DialogWindow dialogWindow) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.addActionListener(event -> dialogWindow.open());
        return menuItem;
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
