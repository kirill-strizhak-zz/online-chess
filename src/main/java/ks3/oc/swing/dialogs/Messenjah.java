package ks3.oc.swing.dialogs;

import ks3.oc.Logger;
import ks3.oc.Protocol;
import ks3.oc.Sender;
import ks3.oc.Starter;
import ks3.oc.board.Board;
import ks3.oc.board.BoardState;
import ks3.oc.swing.SwingMainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;

public class Messenjah extends JFrame implements Protocol {

    public static final String ERR_BASE = "oc.Messenjah::";

    private Logger log;
    private Messenjah self;
    private SwingMainWindow owner;
    private BoardState board;
    private Starter starter;
    private Sender sender;
    private int color, x, y, oldSel;
    private int sel = 0;
    private int mode;
    private JTextField addr, port, name;
    private Checkbox cbServer, cbClient, cbWhite, cbBlack;
    private String save = "localhost";
    private JComboBox boardCB, figureCB;

    // About window
    public Messenjah() {
        super("About Online Chess");
        setSize(300, 150);
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        mode = 0;
        JPanel t = new JPanel();
        t.setSize(200, 100);
        JTextArea txt = new JTextArea();
        txt.setText("         ");
        txt.setEditable(false);
        t.add(txt);
        getContentPane().add("Center", t);
        JButton b = new JButton("OK");
        b.setSize(80, 30);
        getContentPane().add("South", b);
        b.addActionListener(event -> setVisible(false));
        setVisible(true);
    }

    // Starter window
    public Messenjah(Starter strt) {
        super("Start game");
        setSize(this.getInsets().left + this.getInsets().right + 200,
                this.getInsets().top + this.getInsets().bottom + 230);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setIgnoreRepaint(false);
        getContentPane().setLayout(new BorderLayout(10, 10));
        setBackground(Color.black);
        setResizable(false);
        starter = strt;
        mode = 0;

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
        getContentPane().add("North", txtFields);
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
        getContentPane().add("West", cbTPanel);
        getContentPane().add("East", cbCPanel);
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
                setVisible(false);
                int type, color1;
                if (cbServer.getState()) type = 0;
                else type = 1;
                if (cbWhite.getState()) color1 = 2;
                else color1 = 0;
                starter.begin(type, color1, addr.getText(), Integer.parseInt(port.getText()), name.getText());
            }
        });
        getContentPane().add("South", start);
        setVisible(true);
    }

    // Figure chooser
    public Messenjah(BoardState boardState, int myColor, int col, int row) {
        super("Choose a figure to set");
        setSize(248, 87);
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        mode = 1;
        color = myColor;
        board = boardState;
        x = col;
        y = row;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int a = e.getX() / 60;
                int type = 3;
                switch (a) {
                    case 0:
                        type = ROOK;
                        break;
                    case 1:
                        type = KNIGHT;
                        break;
                    case 2:
                        type = BISHOP;
                        break;
                    case 3:
                        type = QUEEN;
                        break;
                }
                board.globalSetFigure(x, y, color, type, false, false);
                board.setHlPos(1);
                board.giveTurn();
                setVisible(false);
                dispose();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int a = e.getX() / 60;
                if (a < 0) a = 0;
                if (a > 3) a = 3;
                sel = a;
                if (sel != oldSel) {
                    repaint();
                    oldSel = sel;
                }
            }
        });
        setVisible(true);
    }

    // preferences panel
    public Messenjah(Board brd) {
        super("Preferences");
        setSize(400, 300);
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLayout(new GridLayout(3, 1));
        board = brd;
        mode = 0;
        int i;
        ImageIcon[] boardIcons = new ImageIcon[4];
        ImageIcon[] figureIcons = new ImageIcon[2];
        for (i = 0; i <= 3; i++) {
            boardIcons[i] = new ImageIcon("img/t" + i + ".gif");
        }
        for (i = 0; i <= 1; i++) {
            figureIcons[i] = new ImageIcon("img/f" + i + ".gif");
        }
        boardCB = new JComboBox<>(boardIcons);
        figureCB = new JComboBox<>(figureIcons);
        boardCB.setSelectedIndex(board.getBoardId());
        figureCB.setSelectedIndex(board.getFigureId() / 4);
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
        getContentPane().add(brdPanel);
        getContentPane().add(figPanel);
        getContentPane().add(buttPanel);
        ok.addActionListener(event -> {
            board.reloadImages(boardCB.getSelectedIndex(), figureCB.getSelectedIndex() * 4);
            dispose();
        });
        cancel.addActionListener(event -> dispose());
        pack();
        setVisible(true);
    }

    // new game
    public Messenjah(Logger log, Sender send, SwingMainWindow own) {
        super("Start new game?");
        self = this;
        this.log = log;
        setSize(250, 100);
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        sender = send;
        owner = own;
        mode = 0;
        setLayout(new GridLayout(2, 1));
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
                    while (!sender.isFree()) {
                        owner.say("M: waiting to send acc_reset");
                        Thread.sleep(100);
                    }
                    sender.send(ACCEPT_RESET);
                    sender.free();
                } catch (InterruptedException | IOException ex) {
                    self.log.log(ERR_BASE + "NewGame:yesListener():: exception: " + ex.getMessage());
                }
                owner.reset();
                dispose();
            }
        });
        no.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    while (!sender.isFree()) {
                        owner.say("M: waiting to send dec_reset");
                        Thread.sleep(100);
                    }
                    sender.send(DECLINE_RESET);
                    sender.free();
                } catch (InterruptedException | IOException ex) {
                    self.log.log(ERR_BASE + "NewGame:noListener():: exception: " + ex.getMessage());
                }
                dispose();
            }
        });
        top.add("Center", str);
        JPanel butt = new JPanel();
        butt.setLayout(new GridLayout(1, 2));
        butt.add(yes);
        butt.add(no);
        bottom.add("Center", butt);
        getContentPane().add(top);
        getContentPane().add(bottom);
        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        switch (mode) {
            case 0:
                super.paint(g);
                break;
            case 1:
                g.setColor(Color.lightGray);
                g.drawRect(4, 23, 59, 59);
                g.drawRect(64, 23, 59, 59);
                g.drawRect(124, 23, 59, 59);
                g.drawRect(184, 23, 59, 59);
                g.drawImage(board.getFigureImage(color, ROOK), 4, 23, this);
                g.drawImage(board.getFigureImage(color, KNIGHT), 64, 23, this);
                g.drawImage(board.getFigureImage(color, BISHOP), 124, 23, this);
                g.drawImage(board.getFigureImage(color, QUEEN), 184, 23, this);
                g.setColor(Color.black);
                g.drawRect(sel * 60 + 4, 23, 59, 59);
                break;
        }
    }
}
