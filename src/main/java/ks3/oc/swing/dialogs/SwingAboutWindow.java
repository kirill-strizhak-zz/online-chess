package ks3.oc.swing.dialogs;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;

public class SwingAboutWindow extends SwingDialogWindow {

    public SwingAboutWindow() {
        super(new JFrame("About Online Chess"));
        frame.setSize(300, 150);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        JPanel textPanel = new JPanel();
        textPanel.setSize(200, 100);
        JTextArea textArea = new JTextArea();
        textArea.setText("         ");
        textArea.setEditable(false);
        textPanel.add(textArea);
        frame.getContentPane().add("Center", textPanel);
        JButton okButton = new JButton("OK");
        okButton.setSize(80, 30);
        frame.getContentPane().add("South", okButton);
        okButton.addActionListener(event -> frame.setVisible(false));
    }
}
