package ks3.oc.swing;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static org.mockito.Mockito.*;

public class SwingChatDisplayTest {

    private static final String MESSAGE = "Hello!";

    @Mock
    private KeyEvent keyEvent;

    private SwingChatDisplay chat;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        chat = mock(SwingChatDisplay.class, withSettings()
                .useConstructor("")
                .defaultAnswer(CALLS_REAL_METHODS));
        doNothing().when(chat).sendChat(anyString());
    }

    @Test
    public void testAnyKey() {
        when(keyEvent.getKeyCode()).thenReturn(KeyEvent.VK_0);
        chat.getTextInput().setText(MESSAGE);
        triggerEvent();
        verify(chat, never()).sendChat(anyString());
    }

    @Test
    public void testSubmit_WhenNoText() {
        when(keyEvent.getKeyCode()).thenReturn(KeyEvent.VK_ENTER);
        chat.getTextInput().setText("");
        triggerEvent();
        verify(chat, never()).sendChat(anyString());
    }

    @Test
    public void testSubmit_WhenHasText() {
        when(keyEvent.getKeyCode()).thenReturn(KeyEvent.VK_ENTER);
        chat.getTextInput().setText(MESSAGE);
        triggerEvent();
        verify(chat, times(1)).sendChat(MESSAGE);
    }

    private void triggerEvent() {
        for (KeyListener listener : chat.getTextInput().getKeyListeners()) {
            listener.keyPressed(keyEvent);
        }
    }
}
