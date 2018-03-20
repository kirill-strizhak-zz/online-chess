package ks3.oc.chat;

import ks3.oc.Protocol;
import ks3.oc.conn.Sender;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class ChatTest {

    private static final String PLAYER_NAME = "name";

    @Mock
    private Sender sender;

    private Chat chat;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        chat = mock(Chat.class, withSettings()
                .useConstructor(PLAYER_NAME)
                .defaultAnswer(CALLS_REAL_METHODS));
        chat.setSender(sender);
    }

    @Test
    public void testSendChat() throws Exception {
        doNothing().when(chat).addChatLine(anyString(), anyString());
        String message = "Hello!";
        chat.sendChat(message);
        InOrder inOrder = inOrder(sender, chat);
        inOrder.verify(sender, times(1)).send(Protocol.CHAT);
        inOrder.verify(sender, times(1)).send(message);
        inOrder.verify(chat, times(1)).addChatLine(message, PLAYER_NAME);
    }

    @Test
    public void testSendChatWhenConnectionException() throws Exception {
        doThrow(new IOException()).when(sender).send(anyString());
        doNothing().when(chat).addChatLine(anyString(), anyString());
        chat.sendChat("");
        verify(chat, times(1)).addChatLine(anyString(), anyString());
        verify(chat, times(1)).addChatLine("* Cannot send chat: connection lost", Protocol.SYSTEM);
    }

    @Test
    public void testAddChatLineWhenSimpleMessage() {
        String message = "Hello!";
        chat.addChatLine(message, PLAYER_NAME);
        verify(chat, times(1)).appendLine(matches("\\[\\d\\d:\\d\\d\\] <" + PLAYER_NAME + "> " + message));
    }

    @Test
    public void testAddChatLineWhenEmoteMessage() {
        String message = "/me Hello!";
        chat.addChatLine(message, PLAYER_NAME);
        verify(chat, times(1)).appendLine(matches("\\[\\d\\d:\\d\\d\\] \\* " + PLAYER_NAME + " " + message.substring(4)));
    }

    @Test
    public void testAddChatLineWhenSystemMessage() {
        String message = "* Hello!";
        chat.addChatLine(message, Protocol.SYSTEM);
        verify(chat, times(1)).appendLine(matches("\\[\\d\\d:\\d\\d\\] \\* " + message));
    }
}
