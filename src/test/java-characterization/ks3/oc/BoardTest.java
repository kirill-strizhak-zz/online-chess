package ks3.oc;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

public class BoardTest {

    @Mock
    private MainWindow mainWindow;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testInitFigures_IfBlack() {
        when(mainWindow.getMyColor()).thenReturn(Board.BLACK);
        Board board = new Board(mainWindow, null, null);
        Assert.assertEquals(board.fig[0][0].empty, false);
    }
}
