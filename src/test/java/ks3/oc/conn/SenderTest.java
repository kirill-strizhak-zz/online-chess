package ks3.oc.conn;

import ks3.oc.Protocol;
import ks3.oc.swing.SwingMainWindow;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static org.mockito.Mockito.*;

public class SenderTest {

    @Mock
    private SwingMainWindow main;
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
        when(inputStream.read()).thenReturn(Protocol.IDENT);
        Sender sender = new Sender(main, "host", 1234) {
            @Override
            protected Socket openConnection(String host, int port) throws IOException {
                return socket;
            }
        };
        Assert.assertNotNull(sender);
    }
}
