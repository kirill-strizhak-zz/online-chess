package ks3.oc.swing;

import ks3.oc.Protocol;
import ks3.oc.conn.Sender;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

public class SwingChatDisplay extends JPanel implements Protocol, ks3.oc.ChatDisplay {

    private static final Logger LOGGER = Logger.getLogger(SwingChatDisplay.class);

    private JTextArea chat;
    private JTextField txt;
    private Sender sender;
    private SwingMainWindow owner;
    private SimpleDateFormat time;

    public SwingChatDisplay(SwingMainWindow own) {
        super(true);
        owner = own;
        this.setSize(320, 400);
        this.setLayout(new BorderLayout());
        chat = new JTextArea();
        chat.setEditable(false);
        chat.setLineWrap(true);
        chat.setColumns(25);
        chat.setRows(7);
        JScrollPane sp = new JScrollPane(chat);
        sp.setWheelScrollingEnabled(true);
        add("Center", sp);

        txt = new JTextField();
        txt.setEditable(true);
        txt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (!txt.getText().equals("")) {
                        sendChat(txt.getText());
                        txt.setText("");
                    }
                }
            }
        });
        add("South", txt);
        time = new SimpleDateFormat(DATE_FORMAT);
        LOGGER.info("Initialization completed");
    }

    public void sendChat(String s) {
        LOGGER.info("Waiting to send chat");
        try {
            sender.send(CHAT);
            sender.send(s);
        } catch (IOException ex) {
            LOGGER.error("Failed to send chat", ex);
            addChatLine("* Cannot send chat: connection lost", "sys_&^_tem");
        }
        addChatLine(s, owner.getMyName());
    }

    @Override
    public void addChatLine(String s, String senderName) {
        StringTokenizer getParam = new StringTokenizer(s, " ", false);
        String param = getParam.nextToken();
        if (param.equals("*") && senderName.equals("sys_&^_tem")) {
            chat.append(time.format(new Date()) + s + "\n");
        } else {
            if (param.equals("/me")) {
                chat.append(time.format(new Date()) + " * " + senderName + " " + s.substring(4) + "\n");
            } else {
                chat.append(time.format(new Date()) + " <" + senderName + "> " + s + "\n");
            }
        }
        chat.setCaretPosition(chat.getText().length());

    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }
}
