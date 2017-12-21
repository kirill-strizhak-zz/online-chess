package ks3.oc.swing;

import ks3.oc.board.BoardState;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

public class SwingBoardDisplayTest {

    @Mock
    private BoardState boardState;

    private SwingBoardDisplay display;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        display = new SwingBoardDisplay(null, null, boardState, null);
    }

    @Test
    public void testMousePressListener() {
        MouseEvent event = new MouseEvent(display, MouseEvent.MOUSE_PRESSED, 0, 0, 70, 130, 1, false, MouseEvent.BUTTON1);
        for (MouseListener listener: display.getMouseListeners()) {
            listener.mousePressed(event);
        }
        verify(boardState, times(1)).selectFigure(1, 2);
    }

    @Test
    public void testMouseReleaseListener_WhenDragging() {
        when(boardState.isDragging()).thenReturn(true);
        MouseEvent event = new MouseEvent(display, MouseEvent.MOUSE_RELEASED, 0, 0, 130, 10, 1, false, MouseEvent.BUTTON1);
        for (MouseListener listener: display.getMouseListeners()) {
            listener.mouseReleased(event);
        }
        verify(boardState, times(1)).isDragging();
        verify(boardState, times(1)).releaseFigure(2, 0);
    }

    @Test
    public void testMouseReleaseListener_WhenNotDragging() {
        when(boardState.isDragging()).thenReturn(false);
        MouseEvent event = new MouseEvent(display, MouseEvent.MOUSE_RELEASED, 0, 0, 130, 10, 1, false, MouseEvent.BUTTON1);
        for (MouseListener listener: display.getMouseListeners()) {
            listener.mouseReleased(event);
        }
        verify(boardState, times(1)).isDragging();
        verify(boardState, never()).releaseFigure(anyInt(), anyInt());
    }
}
