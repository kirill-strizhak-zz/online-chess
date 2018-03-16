package ks3.oc.swing.dialogs;

import ks3.oc.board.BoardState;
import ks3.oc.res.ResourceManager;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.Map;

public class SwingPreferencesWindow extends SwingDialogWindow {

    public SwingPreferencesWindow(ResourceManager resourceManager, BoardState boardDisplay) {
        super(new JFrame("Preferences"));
        frame.setSize(400, 300);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        frame.setLayout(new GridLayout(3, 1));

        ImageIcon[] boardIcons = new ImageIcon[4];
        ImageIcon[] figureSetIcons = new ImageIcon[2];
        int idx = 0;
        int selectedBoardIdx = 0;
        for (Map.Entry<String, Image> boards: resourceManager.getBoardIcons().entrySet()) {
            if (boards.getKey().equals(resourceManager.getBoardName())) {
                selectedBoardIdx = idx;
            }
            boardIcons[idx++] = new ImageIcon(boards.getValue(), boards.getKey());
        }
        idx = 0;
        int selectedFigureSetIdx = 0;
        for (Map.Entry<String, Image> figureSets: resourceManager.getFigureSetIcons().entrySet()) {
            if (figureSets.getKey().equals(resourceManager.getFigureSetName())) {
                selectedFigureSetIdx = idx;
            }
            figureSetIcons[idx++] = new ImageIcon(figureSets.getValue(), figureSets.getKey());
        }
        JComboBox boardCB = new JComboBox<>(boardIcons);
        JComboBox figureCB = new JComboBox<>(figureSetIcons);
        boardCB.setSelectedIndex(selectedBoardIdx);
        figureCB.setSelectedIndex(selectedFigureSetIdx);

        JPanel brdPanel = new JPanel();
        JPanel figPanel = new JPanel();
        JPanel buttPanel = new JPanel();
        brdPanel.setLayout(new BorderLayout());
        figPanel.setLayout(new BorderLayout());
        buttPanel.setLayout(new GridLayout(1, 2));
        JLabel brdTxt = new JLabel("Select board background   ");
        JLabel figTxt = new JLabel("Select figure set         ");
        JButton ok = new JButton("Ok");
        JButton cancel = new JButton("Cancel");
        ok.setSize(80, 30);
        cancel.setSize(80, 30);
        brdPanel.add("West", brdTxt);
        brdPanel.add("Center", boardCB);
        figPanel.add("West", figTxt);
        figPanel.add("Center", figureCB);
        buttPanel.add(ok);
        buttPanel.add(cancel);
        frame.getContentPane().add(brdPanel);
        frame.getContentPane().add(figPanel);
        frame.getContentPane().add(buttPanel);
        ok.addActionListener(event -> {
            String selectedBoard = ((ImageIcon) boardCB.getSelectedItem()).getDescription();
            resourceManager.selectBoard(selectedBoard);
            String selectedFigureSet = ((ImageIcon) figureCB.getSelectedItem()).getDescription();
            resourceManager.selectFigureSet(selectedFigureSet);
            boardDisplay.refresh();
            frame.setVisible(false);
        });
        cancel.addActionListener(event -> frame.setVisible(false));
        frame.pack();
    }
}
