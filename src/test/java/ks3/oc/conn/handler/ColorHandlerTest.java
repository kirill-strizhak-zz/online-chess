package ks3.oc.conn.handler;

import ks3.oc.MainWindow;
import ks3.oc.Protocol;
import ks3.oc.conn.handlers.ColorHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;

import static org.mockito.Mockito.*;

public class ColorHandlerTest {

    @Mock
    private MainWindow main;
    @Mock
    private BufferedReader reader;

    private ColorHandler handler;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        handler = new ColorHandler(main, reader);
    }

    @Test
    public void testHandleWhenWhite() throws Exception {
        when(reader.read()).thenReturn(Protocol.WHITE);
        handler.handle();
        verify(main, times(1)).setMyColor(Protocol.WHITE);
        verify(main, times(1)).setOppColor(Protocol.BLACK);
        verify(main, times(1)).setMyTurn(true);
    }

    @Test
    public void testHandleWhenBlack() throws Exception {
        when(reader.read()).thenReturn(Protocol.BLACK);
        handler.handle();
        verify(main, times(1)).setMyColor(Protocol.BLACK);
        verify(main, times(1)).setOppColor(Protocol.WHITE);
        verify(main, never()).setMyTurn(anyBoolean());
    }
}
