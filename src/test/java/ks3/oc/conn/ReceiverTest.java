package ks3.oc.conn;

import ks3.oc.chat.ChatDisplay;
import ks3.oc.main.MainWindow;
import ks3.oc.board.BoardState;
import ks3.oc.conn.handlers.MessageHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class ReceiverTest {

    @Mock
    private BufferedReader reader;
    @Mock
    private Sender sender;

    private Receiver receiver;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        receiver = new Receiver(null, null, null, reader, sender) {
            @Override
            protected Map<Integer, MessageHandler> registerHandlers(MainWindow main, BoardState board,
                                                                    ChatDisplay chat, BufferedReader reader) {
                Map<Integer, MessageHandler> handlers = new HashMap<>();
                handlers.put(1, reader::readLine);
                handlers.put(2, this::deactivate);
                handlers.put(666, () -> { throw new IOException(); });
                return handlers;
            }

            @Override
            protected MessageHandler createHandshakeHandler(MainWindow main, ChatDisplay chat, BufferedReader reader) {
                return null;
            }
        };
    }

    @Test
    public void testReceiveHandledMessage() throws Exception {
        when(reader.read()).thenReturn(1, 2);
        receiver.run();
        verify(reader, times(2)).read();
        verify(reader, times(1)).readLine();
    }

    @Test
    public void testReceiveIgnoringUnhandledMessages() throws Exception {
        when(reader.read()).thenReturn(314, 42, 2);
        receiver.run();
        verify(reader, times(3)).read();
    }

    @Test
    public void testReceiveWhenHandlerThrowsException() throws Exception {
        when(reader.read()).thenReturn(666);
        receiver.run();
        verify(reader, times(1)).read();
        verify(sender, times(1)).deactivate("Receiver: IOException");
    }

    @Test
    public void testReceiveWhenReadThrowsException() throws Exception {
        when(reader.read()).thenThrow(new IOException("Bang!"));
        receiver.run();
        verify(sender, times(1)).deactivate("Receiver: IOException");
    }
}
