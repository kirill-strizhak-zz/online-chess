package ks3.oc.swing.dialogs;

import ks3.oc.MainWindow;
import ks3.oc.Protocol;
import ks3.oc.conn.Sender;
import org.apache.log4j.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

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
        yes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    LOGGER.info("Waiting to accept reset");
                    while (!sender.isFree()) {
                        Thread.sleep(10);
                    }
                    sender.send(Protocol.ACCEPT_RESET);
                    sender.free();
                } catch (InterruptedException | IOException ex) {
                    LOGGER.error("Failed to accept reset", ex);
                }
                owner.reset();
                frame.setVisible(false);
            }
        });
        no.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    LOGGER.info("Waiting to decline reset");
                    while (!sender.isFree()) {
                        Thread.sleep(10);
                    }
                    sender.send(Protocol.DECLINE_RESET);
                    sender.free();
                } catch (InterruptedException | IOException ex) {
                    LOGGER.error("Failed to decline reset", ex);
                }
                frame.setVisible(false);
            }
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
