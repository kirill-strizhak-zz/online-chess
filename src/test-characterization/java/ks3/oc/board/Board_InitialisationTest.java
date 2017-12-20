package ks3.oc.board;

import ks3.oc.MainWindow;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class Board_InitialisationTest {

    @Mock
    private MainWindow mainWindow;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testInitCompletes() {
        new Board(null, mainWindow, null, null);
        verify(mainWindow, times(1)).say("B: ini completed");
    }
}
