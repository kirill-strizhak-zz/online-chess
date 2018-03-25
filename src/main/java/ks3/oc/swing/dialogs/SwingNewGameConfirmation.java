package ks3.oc.swing.dialogs;

import ks3.oc.conn.Sender;
import ks3.oc.main.MainWindow;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;

public class SwingNewGameConfirmation extends SwingDialogWindow {

    private static final Logger LOGGER = Logger.getLogger(SwingNewGameConfirmation.class);

    public SwingNewGameConfirmation(Sender sender, MainWindow owner) {
        super(new JFrame("Start new game?"));
        frame.setSize(250, 100);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        frame.setLayout(new GridLayout(2, 1));
        JPanel top = new JPanel();
        JPanel bottom = new JPanel();
        top.setLayout(new BorderLayout());
        bottom.setLayout(new BorderLayout());
        JLabel str = new JLabel("Server offered to start new game. Accept?");
        JButton yes = new JButton("Yes");
        JButton no = new JButton("No");
        yes.addActionListener(event -> {
            LOGGER.info("Waiting to accept reset");
            sender.sendAcceptReset();
            owner.reset();
            frame.setVisible(false);
        });
        no.addActionListener(event -> {
            LOGGER.info("Waiting to decline reset");
            sender.sendDeclineReset();
            frame.setVisible(false);
        });
        top.add("Center", str);
        JPanel butt = new JPanel();
        butt.setLayout(new GridLayout(1, 2));
        butt.add(yes);
        butt.add(no);
        bottom.add("Center", butt);
        frame.getContentPane().add(top);
        frame.getContentPane().add(bottom);
    }
}
