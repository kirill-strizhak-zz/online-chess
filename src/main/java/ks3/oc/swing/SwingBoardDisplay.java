package ks3.oc.swing;

import ks3.oc.Figure;
import ks3.oc.MainWindow;
import ks3.oc.board.BoardDisplay;
import ks3.oc.board.BoardState;
import ks3.oc.logic.Logic;
import ks3.oc.res.ResourceManager;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class SwingBoardDisplay extends JPanel implements BoardDisplay {

    private final ResourceManager resourceManager;
    private final MainWindow owner;
    private final BoardState boardState;
    private final Logic logic;

    private final SwingDebugOverlay debugOverlay;
    private final Color myRed = new Color(220, 0, 0);

    public SwingBoardDisplay(ResourceManager resourceManager, MainWindow owner, BoardState boardState, Logic logic) {
        this.resourceManager = resourceManager;
        this.owner = owner;
        this.boardState = boardState;
        this.logic = logic;
        this.debugOverlay = new SwingDebugOverlay();
        this.setSize(480, 480);
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent event) {
                boardState.selectFigure(event.getX() / BoardState.CELL_SIZE, event.getY() / BoardState.CELL_SIZE);
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                if (boardState.isDragging()) {
                    boardState.releaseFigure(event.getX() / BoardState.CELL_SIZE, event.getY() / BoardState.CELL_SIZE);
                }
                repaint();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent event) {
                if (boardState.isDragging()) {
                    boardState.dragFigure(event.getX(), event.getY());
                    repaint();
                }
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(resourceManager.getBoard(), 0, 0, this);
        if (!boardState.isLoading()) {
            Figure figure;
            for (int col = 0; col <= 7; col++) {
                for (int row = 0; row <= 7; row++) {
                    if (!boardState.isCellEmpty(col, row)) {
                        figure = boardState.figureAt(col, row);
                        g.drawImage(boardState.getImageOfFigure(figure), figure.oX, figure.oY, this);
                    }
                }
            }
            if (boardState.needToDrawHighlight()) {
                g.setColor(myRed);
                int[][] highlight = boardState.getHighlight();
                g.drawRect(highlight[0][0] - 1, highlight[0][1] - 1, BoardState.CELL_SIZE + 1, BoardState.CELL_SIZE + 1);
                g.drawRect(highlight[0][0], highlight[0][1], BoardState.CELL_SIZE - 1, BoardState.CELL_SIZE - 1);
                g.drawRect(highlight[1][0] - 1, highlight[1][1] - 1, BoardState.CELL_SIZE + 1, BoardState.CELL_SIZE + 1);
                g.drawRect(highlight[1][0], highlight[1][1], BoardState.CELL_SIZE - 1, BoardState.CELL_SIZE - 1);
                g.setColor(myRed);
            }
            if (boardState.isDragging()) {
                figure = boardState.getDraggedFigure();
                g.drawImage(boardState.getImageOfDraggedFigure(), figure.oX, figure.oY, this);
            }
            debugOverlay.draw(g, boardState, logic, owner.getMyColor(), owner.getOppColor());
        }
    }

    @Override
    public void update(Graphics g) {
        paint(g);
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
        repaint();
    }
}
