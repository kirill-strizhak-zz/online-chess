package ks3.oc.swing;

import ks3.oc.Figure;
import ks3.oc.MainWindow;
import ks3.oc.board.Board;
import ks3.oc.board.BoardState;
import ks3.oc.chat.ChatDisplay;
import ks3.oc.res.ResourceManager;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class SwingBoardDisplay extends Board {

    private final JPanel component;
    private final SwingDebugOverlay debugOverlay;
    private final Color myRed = new Color(220, 0, 0);

    public SwingBoardDisplay(ResourceManager resourceManager, MainWindow main, ChatDisplay chat, SwingDebugOverlay debugOverlay) {
        super(resourceManager, main, chat);
        this.debugOverlay = debugOverlay;
        this.component = createComponent();
    }

    protected JPanel createComponent() {
        JPanel component = new JPanel() {
            @Override
            public void paint(Graphics g) {
                drawBoard(g);
            }

            @Override
            public void update(Graphics g) {
                drawBoard(g);
            }
        };
        component.setSize(480, 480);
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                selectFigure(event.getX() / BoardState.CELL_SIZE, event.getY() / BoardState.CELL_SIZE);
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                if (isDragging()) {
                    releaseFigure(event.getX() / BoardState.CELL_SIZE, event.getY() / BoardState.CELL_SIZE);
                }
                refresh();
            }
        });
        component.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent event) {
                if (isDragging()) {
                    dragFigure(event.getX(), event.getY());
                    refresh();
                }
            }
        });
        return component;
    }

    protected void drawBoard(Graphics g) {
        g.drawImage(resourceManager.getBoard(), 0, 0, component);
        if (!isLoading()) {
            Figure figure;
            for (int col = 0; col <= 7; col++) {
                for (int row = 0; row <= 7; row++) {
                    if (!isCellEmpty(col, row)) {
                        figure = figureAt(col, row);
                        g.drawImage(getImageOfFigure(figure), figure.oX, figure.oY, component);
                    }
                }
            }
            if (needToDrawHighlight()) {
                drawHighlight(g);
            }
            if (isDragging()) {
                figure = getDraggedFigure();
                g.drawImage(getImageOfDraggedFigure(), figure.oX, figure.oY, component);
            }
            debugOverlay.draw(g, this, logic, main.getMyColor(), main.getOppColor());
        }
    }

    private void drawHighlight(Graphics g) {
        int[][] highlight = getHighlight();
        g.setColor(myRed);
        g.drawRect(highlight[0][0] - 1, highlight[0][1] - 1, BoardState.CELL_SIZE + 1, BoardState.CELL_SIZE + 1);
        g.drawRect(highlight[0][0], highlight[0][1], BoardState.CELL_SIZE - 1, BoardState.CELL_SIZE - 1);
        g.drawRect(highlight[1][0] - 1, highlight[1][1] - 1, BoardState.CELL_SIZE + 1, BoardState.CELL_SIZE + 1);
        g.drawRect(highlight[1][0], highlight[1][1], BoardState.CELL_SIZE - 1, BoardState.CELL_SIZE - 1);
    }

    @Override
    public boolean isDebug() {
        return debugOverlay.isEnabled();
    }

    @Override
    public void setDebug(boolean enabled) {
        debugOverlay.setEnabled(enabled);
    }

    @Override
    public void refresh() {
        component.repaint();
    }

    public Component getComponent() {
        return component;
    }
}
