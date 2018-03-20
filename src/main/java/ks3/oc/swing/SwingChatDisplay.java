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

    private final JTextArea textArea;
    private final JTextField textInput;
    private final JPanel component;

    public SwingChatDisplay(String playerName) {
        super(playerName);
        textArea = createTextArea();
        textInput = createTextInput();
        component = createComponent(textArea, textInput);
    }

    private JTextArea createTextArea() {
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setColumns(25);
        textArea.setRows(7);
        return textArea;
    }

    private JPanel createComponent(JTextArea textArea, JTextField textInput) {
        JPanel component = new JPanel(true);
        component.setSize(320, 400);
        component.setLayout(new BorderLayout());
        component.add("Center", wrapInScrollPane(textArea));
        component.add("South", textInput);
        return component;
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

    @Override
    public Component getComponent() {
        return component;
    }

    protected JTextField getTextInput() {
        return textInput;
    }
}
