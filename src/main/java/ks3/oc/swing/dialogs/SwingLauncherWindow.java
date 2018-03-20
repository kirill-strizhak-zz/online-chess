package ks3.oc.swing.dialogs;

import ks3.oc.Protocol;
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
import java.awt.event.ItemEvent;

public class SwingLauncherWindow extends SwingDialogWindow {

    private final Starter starter;
    private JTextField addr, port, name;
    private Checkbox cbServer, cbClient, cbWhite, cbBlack;
    private String savedAddress = "localhost";

    public SwingLauncherWindow(Starter starter) {
        super(new JFrame("Start game"));
        this.starter = starter;
        frame.setSize(frame.getInsets().left + frame.getInsets().right + 200,
                frame.getInsets().top + frame.getInsets().bottom + 230);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setIgnoreRepaint(false);
        frame.getContentPane().setLayout(new BorderLayout(10, 10));
        frame.setBackground(Color.black);
        frame.setResizable(false);

        frame.getContentPane().add("North", createInputPanel());
        frame.getContentPane().add("West", createTypePanel());
        frame.getContentPane().add("East", createColorPanel());
        frame.getContentPane().add("South", createStartButton());
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel();
        JPanel nameFields = new JPanel();
        JPanel addrFields = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        nameFields.setLayout(new BorderLayout());
        addrFields.setLayout(new BorderLayout());
        panel.setSize(300, 30);
        addr = new JTextField("localhost");
        addr.setEditable(false);
        addr.setText(" - - - - - - - - - - - - - - - - ");
        addr.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                savedAddress = addr.getText();
            }
        });
        port = new JTextField("12345");
        name = new JTextField("Player");
        JLabel n = new JLabel("Your name:");
        JLabel a = new JLabel("Address:");
        port.setColumns(5);
        panel.add("North", nameFields);
        nameFields.add("North", n);
        nameFields.add("Center", name);
        panel.add("South", addrFields);
        addrFields.add("North", a);
        addrFields.add("East", port);
        addrFields.add("Center", addr);
        return panel;
    }

    private JPanel createTypePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add("North", new JLabel("You are:"));

        CheckboxGroup typeGroup = new CheckboxGroup();
        cbServer = new Checkbox("Server", typeGroup, false);
        cbServer.setState(true);
        cbServer.addItemListener((event) -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                addr.setEditable(false);
                addr.setText(" - - - - - - - - - - - - - - - - ");
                cbWhite.setEnabled(true);
                cbBlack.setEnabled(true);
            }
        });
        panel.add("Center", cbServer);

        cbClient = new Checkbox("Client", typeGroup, false);
        cbClient.addItemListener((event) -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                addr.setEditable(true);
                addr.setText(savedAddress);
                cbWhite.setEnabled(false);
                cbBlack.setEnabled(false);
            }
        });
        panel.add("South", cbClient);

        return panel;
    }

    private JPanel createColorPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add("North", new JLabel("Your color:"));

        CheckboxGroup colorGroup = new CheckboxGroup();
        cbWhite = new Checkbox("White", colorGroup, false);
        cbWhite.setState(true);
        panel.add("Center", cbWhite);

        cbBlack = new Checkbox("Black", colorGroup, false);
        panel.add("South", cbBlack);
        return panel;
    }

    private JButton createStartButton() {
        JButton start = new JButton("Start");
        start.addActionListener(event -> {
            if ((!addr.getText().equals("")) && (!port.getText().equals("")) && (!name.getText().equals(""))) {
                frame.setVisible(false);
                int type = cbServer.getState() ? Protocol.SERVER : Protocol.CLIENT;
                int color = cbWhite.getState() ? Protocol.WHITE : Protocol.BLACK;
                starter.begin(type, color, addr.getText(), Integer.parseInt(port.getText()), name.getText());
            }
        });
        return start;
    }
}
