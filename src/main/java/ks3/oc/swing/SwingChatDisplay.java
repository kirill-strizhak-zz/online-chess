package ks3.oc.swing;

import ks3.oc.chat.Chat;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SwingChatDisplay extends Chat {

    private final JPanel component;
    private final JTextArea textArea;

    public SwingChatDisplay(String playerName) {
        super(playerName);
        component = new JPanel(true);
        component.setSize(320, 400);
        component.setLayout(new BorderLayout());
        textArea = createTextArea();
        component.add("Center", wrapInScrollPane(textArea));
        component.add("South", createTextInput());
    }

    private JTextArea createTextArea() {
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setColumns(25);
        textArea.setRows(7);
        return textArea;
    }

    private JScrollPane wrapInScrollPane(Component component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setWheelScrollingEnabled(true);
        return scrollPane;
    }

    private JTextField createTextInput() {
        JTextField textInput = new JTextField();
        textInput.setEditable(true);
        textInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (!textInput.getText().equals("")) {
                        sendChat(textInput.getText());
                        textInput.setText("");
                    }
                }
            }
        });
        return textInput;
    }

    @Override
    protected void appendLine(String line) {
        textArea.append(line + "\n");
        textArea.setCaretPosition(textArea.getText().length());
    }

    public Component getComponent() {
        return component;
    }
}
