package ks3.oc.swing;

import ks3.oc.Figure;
import ks3.oc.Protocol;
import ks3.oc.board.BoardState;
import ks3.oc.logic.Logic;

import java.awt.Color;
import java.awt.Graphics;

public class SwingDebugOverlay {

    private boolean enabled = false;

    public void draw(Graphics g, BoardState boardState, Logic logic, int myColor, int oppColor) {
        if (enabled) {
            drawCellStates(g, boardState);
            if (boardState.isDragging()) {
                drawAllowedPositions(g, logic.getAllowed());
            }
            drawKingSafety(g, boardState, logic, myColor, oppColor);
        }
    }

    private void drawCellStates(Graphics g, BoardState boardState) {
        for (int col = 0; col <= 7; col++) {
            for (int row = 0; row <= 7; row++) {
                drawCellState(g, boardState.figureAt(col, row), col * 60, row * 60);
            }
        }
    }

    private void drawCellState(Graphics g, Figure figure, int x, int y) {
        g.setColor(Color.WHITE);
        g.fillRect(x, y, 10, 60);
        drawColorCoded(figure.empty, g, "e", x, y + 10);
        drawColorCoded(figure.firstStep, g, "f", x, y + 20);
        drawColorCoded(figure.color == Protocol.NULL, g, "c", x, y + 30);
        drawColorCoded(figure.type == Protocol.NULL, g, "t", x, y + 40);
    }

    private void drawAllowedPositions(Graphics g, int[][] allowed) {
        for (int idx = 0; allowed[idx][0] != -1; idx++) {
            g.setColor(Color.BLUE);
            g.drawString("x", allowed[idx][0] * 60, allowed[idx][1] * 60 + 50);
        }
    }

    private void drawKingSafety(Graphics g, BoardState boardState, Logic logic, int myColor, int oppColor) {
        drawKingSafetyOfColor(g, boardState, logic, myColor / 2, oppColor);
        drawKingSafetyOfColor(g, boardState, logic, oppColor / 2, myColor);
    }

    private void drawKingSafetyOfColor(Graphics g, BoardState boardState, Logic logic, int colorId, int color) {
        int kingCol = boardState.getKingCol(colorId);
        int kingRow = boardState.getKingRow(colorId);
        int x = kingCol * 60 + 10;
        int y = kingRow * 60;
        g.setColor(Color.WHITE);
        g.fillRect(x, y, 10, 13);
        boolean isSafe = logic.kingSafeAt(kingCol, kingRow, color);
        String indicator = isSafe ? "s" : "!!!";
        drawColorCoded(!isSafe, g, indicator, x, y + 10);
        if (!isSafe) {
            int[] att = logic.getAttacker();
            g.setColor(Color.RED);
            g.drawRect(att[0] * 60 + 20, att[1] * 60 + 20, 20, 20);
        }
    }

    private void drawColorCoded(boolean condition, Graphics g, String string, int x, int y) {
        if (condition) {
            g.setColor(Color.RED);
            g.drawString(string, x, y);
        } else {
            g.setColor(Color.DARK_GRAY);
            g.drawString(string, x, y);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
