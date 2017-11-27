package oc;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatPanel extends JPanel implements Protocol {
	private JTextArea chat;
	private JTextField txt;
	private Sender sender;
	private MainFrame owner;
	private SimpleDateFormat time;
	
	public ChatPanel(Sender send, MainFrame own) {
		super(true);
		sender = send;
		owner = own;
		this.setSize(320,400);
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
		add("South",txt);
		time = new SimpleDateFormat(DATE_FORMAT);
		owner.say("CP: ini completed");
	}

	public void sendChat(String s) {
		while (!sender.isFree()) {
			try {
				owner.say("CP: waiting to send chat");
			} catch (Exception e) {}
		}
		try {
			sender.send(CHAT);
			sender.send(s);
		} catch (Exception e) {
			owner.say("CP: cannot send chat");
			addChatLine("* Cannot send chat: connection lost","sys_&^_tem");
		}
		sender.free();
		addChatLine(s,owner.myName);
	}
	
	public void addChatLine(String s, String senderName) {
		StringTokenizer getParam = new StringTokenizer(s," ",false);
		String param = getParam.nextToken();
		if (param.equals("*") && senderName.equals("sys_&^_tem")) {
			chat.append(time.format(new Date())+s+"\n");
		} else {
			if (param.equals("/me")) {
				chat.append(time.format(new Date())+" * "+senderName+" "+s.substring(4)+"\n");
			} else {
				chat.append(time.format(new Date())+" <"+senderName+"> "+s+"\n");
			}
		}
		chat.setCaretPosition(chat.getText().length());

	}
	
}
