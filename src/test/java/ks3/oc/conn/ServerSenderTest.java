package ks3.oc.conn;

import ks3.oc.Protocol;
import ks3.oc.swing.SwingMainWindow;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class ServerSenderTest {

    @Mock
    private ServerSender sender;
    @Mock
    private SwingMainWindow main;
    @Mock
    private SocketFactory socketFactory;
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
        when(socketFactory.createServer(anyInt())).thenReturn(socket);
        when(sender.getSocketFactory()).thenReturn(socketFactory);
        when(sender.openConnection(anyString(), anyInt())).thenCallRealMethod();
    }

    @Test
    public void testOpenConnection() throws Exception {
        when(inputStream.read()).thenReturn(Protocol.IDENT);
        sender.openConnection("host", 1234);
        InOrder inOrder = inOrder(inputStream, outputStream);
        inOrder.verify(inputStream, times(1)).read();
        inOrder.verify(outputStream, times(1)).write(Protocol.IDENT);
        verify(socket, never()).close();
    }

    @Test(expected = IOException.class)
    public void testOpenConnectionThrowsWhenInvalidResponse() throws Exception {
        when(inputStream.read()).thenReturn(-1);
        sender.openConnection("host", 1234);
    }
}
