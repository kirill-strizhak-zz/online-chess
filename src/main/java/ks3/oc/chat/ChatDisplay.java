package ks3.oc.chat;

import ks3.oc.conn.Sender;

import java.awt.Component;

public interface ChatDisplay {

    void setSender(Sender sender);

    void addChatLine(String message, String senderName);

    Component getComponent();
}
