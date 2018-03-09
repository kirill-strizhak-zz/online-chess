package ks3.oc.swing.dialogs;

import ks3.oc.Starter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SwingLauncherWindow extends SwingDialogWindow {

    private JTextField addr, port, name;
    private Checkbox cbServer, cbClient, cbWhite, cbBlack;
    private String save = "localhost";

    public SwingLauncherWindow(Starter starter) {
        super(new JFrame("Start game"));
        frame.setSize(frame.getInsets().left + frame.getInsets().right + 200,
                frame.getInsets().top + frame.getInsets().bottom + 230);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setIgnoreRepaint(false);
        frame.getContentPane().setLayout(new BorderLayout(10, 10));
        frame.setBackground(Color.black);
        frame.setResizable(false);

        JPanel txtFields = new JPanel();
        JPanel nameFields = new JPanel();
        JPanel addrFields = new JPanel();
        txtFields.setLayout(new BorderLayout(10, 10));
        nameFields.setLayout(new BorderLayout());
        addrFields.setLayout(new BorderLayout());
        txtFields.setSize(300, 30);
        addr = new JTextField("localhost");
        addr.setEditable(false);
        addr.setText(" - - - - - - - - - - - - - - - - ");
        port = new JTextField("12345");
        name = new JTextField("Player");
        JLabel n = new JLabel("Your name:");
        JLabel a = new JLabel("Address:");
        port.setColumns(5);
        frame.getContentPane().add("North", txtFields);
        txtFields.add("North", nameFields);
        nameFields.add("North", n);
        nameFields.add("Center", name);
        txtFields.add("South", addrFields);
        addrFields.add("North", a);
        addrFields.add("East", port);
        addrFields.add("Center", addr);

        JPanel cbTPanel = new JPanel();
        JPanel cbCPanel = new JPanel();
        cbTPanel.setLayout(new BorderLayout());
        cbCPanel.setLayout(new BorderLayout());
        JLabel t = new JLabel("You are:");
        JLabel c = new JLabel("Your color:");
        CheckboxGroup cbTypeGroup = new CheckboxGroup();
        CheckboxGroup cbColorGroup = new CheckboxGroup();
        cbServer = new Checkbox("Server", cbTypeGroup, false);
        cbServer.setState(true);
        cbClient = new Checkbox("Client", cbTypeGroup, false);
        cbWhite = new Checkbox("White", cbColorGroup, false);
        cbWhite.setState(true);
        cbBlack = new Checkbox("Black", cbColorGroup, false);
        frame.getContentPane().add("West", cbTPanel);
        frame.getContentPane().add("East", cbCPanel);
        cbTPanel.add("North", t);
        cbTPanel.add("Center", cbServer);
        cbTPanel.add("South", cbClient);
        cbCPanel.add("North", c);
        cbCPanel.add("Center", cbWhite);
        cbCPanel.add("South", cbBlack);

        addr.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                save = addr.getText();
            }
        });
        cbServer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if (cbServer.getState()) {
                    addr.setEditable(false);
                    addr.setText(" - - - - - - - - - - - - - - - - ");
                    cbWhite.setEnabled(true);
                    cbBlack.setEnabled(true);
                }
            }
        });
        cbClient.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (cbClient.getState()) {
                    addr.setEditable(true);
                    addr.setText(save);
                    cbWhite.setEnabled(false);
                    cbBlack.setEnabled(false);
                }
            }
        });

        JButton start = new JButton("Start");
        start.addActionListener(event -> {
            if ((!addr.getText().equals("")) && (!port.getText().equals("")) && (!name.getText().equals(""))) {
                frame.setVisible(false);
                int type, color1;
                if (cbServer.getState()) type = 0;
                else type = 1;
                if (cbWhite.getState()) color1 = 2;
                else color1 = 0;
                starter.begin(type, color1, addr.getText(), Integer.parseInt(port.getText()), name.getText());
            }
        });
        frame.getContentPane().add("South", start);
    }
}
