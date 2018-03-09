package ks3.oc.swing.dialogs;

import ks3.oc.Protocol;
import ks3.oc.board.BoardState;
import ks3.oc.dialogs.FigurePickerWindow;
import ks3.oc.res.FigureSet;
import ks3.oc.res.ResourceManager;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class SwingFigurePicker implements FigurePickerWindow {

    private final JFrame frame;

    private int color, x, y, oldSel;
    private int sel = 0;

    public SwingFigurePicker(BoardState boardState, ResourceManager resourceManager) {
        frame = new JFrame("Choose a figure to set") {
            @Override
            public void paint(Graphics g) {
                g.setColor(Color.lightGray);
                g.drawRect(4, 23, 59, 59);
                g.drawRect(64, 23, 59, 59);
                g.drawRect(124, 23, 59, 59);
                g.drawRect(184, 23, 59, 59);
                FigureSet figureSet = resourceManager.getFigureSet();
                g.drawImage(figureSet.getImage(color, Protocol.ROOK), 4, 23, this);
                g.drawImage(figureSet.getImage(color, Protocol.KNIGHT), 64, 23, this);
                g.drawImage(figureSet.getImage(color, Protocol.BISHOP), 124, 23, this);
                g.drawImage(figureSet.getImage(color, Protocol.QUEEN), 184, 23, this);
                g.setColor(Color.black);
                g.drawRect(sel * 60 + 4, 23, 59, 59);
            }
        };
        frame.setSize(248, 87);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int a = e.getX() / 60;
                int type = 3;
                switch (a) {
                    case 0:
                        type = Protocol.ROOK;
                        break;
                    case 1:
                        type = Protocol.KNIGHT;
                        break;
                    case 2:
                        type = Protocol.BISHOP;
                        break;
                    case 3:
                        type = Protocol.QUEEN;
                        break;
                }
                boardState.globalSetFigure(x, y, color, type, false, false);
                boardState.setHlPos(1);
                boardState.giveTurn();
                frame.setVisible(false);
            }
        });
        frame.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int a = e.getX() / 60;
                if (a < 0) a = 0;
                if (a > 3) a = 3;
                sel = a;
                if (sel != oldSel) {
                    frame.repaint();
                    oldSel = sel;
                }
            }
        });
    }

    public void open(int myColor, int col, int row) {
        color = myColor;
        x = col;
        y = row;
        frame.setVisible(true);
    }
}
