package ks3.oc.conn;

import ks3.oc.board.BoardState;
import ks3.oc.chat.ChatDisplay;
import ks3.oc.swing.SwingMainWindow;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.*;
import java.net.Socket;

import static org.mockito.Mockito.when;

public class SenderTest {

    @Mock
    private SwingMainWindow main;
    @Mock
    private BoardState board;
    @Mock
    private ChatDisplay chat;
    @Mock
    private Socket socket;
    @Mock
    private InputStream inputStream;
    @Mock
    private OutputStream outputStream;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(socket.getInputStream()).thenReturn(inputStream);
        when(socket.getOutputStream()).thenReturn(outputStream);
    }

    @Test
    public void testStartServerWhenSuccessfullyConnected() throws Exception {
        when(inputStream.read()).thenReturn(Headers.HANDSHAKE);
        Sender sender = new Sender(main, board, chat, "host", 1234) {
            @Override
            protected Socket openConnection(String host, int port) throws IOException {
                return socket;
            }

            @Override
            protected void sendHandshake(PrintWriter writer) {

            }

            @Override
            protected void startReceiver(BufferedReader reader) {

            }
        };
        Assert.assertNotNull(sender);
    }
}
