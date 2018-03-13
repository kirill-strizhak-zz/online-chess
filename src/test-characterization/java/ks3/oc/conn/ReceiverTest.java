package ks3.oc.conn;

import ks3.oc.ChatPanel;
import ks3.oc.Protocol;
import ks3.oc.board.Board;
import ks3.oc.swing.SwingMainWindow;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;

import static org.mockito.Mockito.*;

public class ReceiverTest {

    @Mock
    private SwingMainWindow main;
    @Mock
    private ChatPanel chat;
    @Mock
    private Board board;
    @Mock
    private BufferedReader reader;
    @Mock
    private Sender sender;

    private Receiver receiver;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(main.getChat()).thenReturn(chat);
        when(main.getBoard()).thenReturn(board);
        receiver = new Receiver(main, reader, sender);
    }

    @Test(timeout = 1000L)
    public void testReadClose() throws Exception {
        when(reader.read()).thenReturn(Protocol.CLOSE);
        receiver.run();
        verify(sender, times(1)).deactivate("Receiver: client disconnected");
    }

    @Test(timeout = 1000L)
    public void testReadName() throws Exception {
        when(reader.read()).thenReturn(Protocol.NAME, Protocol.CLOSE);
        when(reader.readLine()).thenReturn("name");
        receiver.run();
        verify(chat, times(1)).addChatLine("* name connected", "sys_&^_tem");
    }

    @Test(timeout = 1000L)
    public void testReadCoordinates() throws Exception {
        when(reader.read()).thenReturn(Protocol.COORDINATES, 1, 2, 3, 4, Protocol.CLOSE);
        receiver.run();
        verify(board, times(1)).makeMove(1, 2, 3, 4);
    }

    @Test(timeout = 1000L)
    public void testReadChat() throws Exception {
        when(reader.read()).thenReturn(Protocol.CHAT, Protocol.CLOSE);
        when(reader.readLine()).thenReturn("chat message");
        receiver.run();
        verify(chat, times(1)).addChatLine("chat message", null);
    }

    @Test(timeout = 1000L)
    public void testReadColorWhenWhite() throws Exception {
        when(reader.read()).thenReturn(Protocol.COLOR, Protocol.WHITE, Protocol.CLOSE);
        receiver.run();
        verify(main, times(1)).setMyColor(Protocol.WHITE);
        verify(main, times(1)).setOppColor(Protocol.BLACK);
        verify(main, times(1)).setMyTurn(true);
    }

    @Test(timeout = 1000L)
    public void testReadColorWhenBlack() throws Exception {
        when(reader.read()).thenReturn(Protocol.COLOR, Protocol.BLACK, Protocol.CLOSE);
        receiver.run();
        verify(main, times(1)).setMyColor(Protocol.BLACK);
        verify(main, times(1)).setOppColor(Protocol.WHITE);
        verify(main, never()).setMyTurn(true);
    }
}
