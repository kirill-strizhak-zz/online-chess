package ks3.oc.swing;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

public class SwingBoardDisplayTest {

    @Mock
    private SwingDebugOverlay overlay;
    @Mock
    private Graphics graphics;
    @Mock
    private Image figureImage;

    private SwingBoardDisplay board;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        board = spy(SwingBoardDisplay.class);
    }

    @Test
    public void testMousePressListener() {
        doNothing().when(board).selectFigure(anyInt(), anyInt());
        MouseEvent event = new MouseEvent(board.getComponent(), MouseEvent.MOUSE_PRESSED, 0, 0, 70, 130, 1, false, MouseEvent.BUTTON1);
        for (MouseListener listener: board.getComponent().getMouseListeners()) {
            listener.mousePressed(event);
        }
        verify(board, times(1)).selectFigure(1, 2);
    }

    @Test
    public void testMouseReleaseListener_WhenDragging() {
        when(board.isDragging()).thenReturn(true);
        doNothing().when(board).releaseFigure(anyInt(), anyInt());
        MouseEvent event = new MouseEvent(board.getComponent(), MouseEvent.MOUSE_RELEASED, 0, 0, 130, 10, 1, false, MouseEvent.BUTTON1);
        for (MouseListener listener: board.getComponent().getMouseListeners()) {
            listener.mouseReleased(event);
        }
        verify(board, times(1)).isDragging();
        verify(board, times(1)).releaseFigure(2, 0);
        verify(board, times(1)).refresh();
    }

    @Test
    public void testMouseReleaseListener_WhenNotDragging() {
        when(board.isDragging()).thenReturn(false);
        MouseEvent event = new MouseEvent(board.getComponent(), MouseEvent.MOUSE_RELEASED, 0, 0, 130, 10, 1, false, MouseEvent.BUTTON1);
        for (MouseListener listener: board.getComponent().getMouseListeners()) {
            listener.mouseReleased(event);
        }
        verify(board, times(1)).isDragging();
        verify(board, never()).releaseFigure(anyInt(), anyInt());
        verify(board, times(1)).refresh();
    }

    @Test
    public void testMouseMotionListener_WhenDragging() {
        when(board.isDragging()).thenReturn(true);
        doNothing().when(board).dragFigure(anyInt(), anyInt());
        MouseEvent event = new MouseEvent(board.getComponent(), MouseEvent.MOUSE_DRAGGED, 0, 0, 130, 10, 1, false, MouseEvent.BUTTON1);
        for (MouseMotionListener listener: board.getComponent().getMouseMotionListeners()) {
            listener.mouseDragged(event);
        }
        verify(board, times(1)).isDragging();
        verify(board, times(1)).dragFigure(130, 10);
        verify(board, times(1)).refresh();
    }

    @Test
    public void testMouseMotionListener_WhenNotDragging() {
        when(board.isDragging()).thenReturn(false);
        MouseEvent event = new MouseEvent(board.getComponent(), MouseEvent.MOUSE_DRAGGED, 0, 0, 130, 10, 1, false, MouseEvent.BUTTON1);
        for (MouseMotionListener listener: board.getComponent().getMouseMotionListeners()) {
            listener.mouseDragged(event);
        }
        verify(board, times(1)).isDragging();
        verify(board, never()).dragFigure(anyInt(), anyInt());
        verify(board, never()).refresh();
    }

//    @Test
//    public void testDrawBoard_WhenOnlyFigures() {
//        when(board.isLoading()).thenReturn(false);
//        doReturn(false).when(board).needToDrawHighlight();
//        when(board.isDragging()).thenReturn(false);
//        Figure pawn = createPawn();
//        when(board.figureAt(anyInt(), anyInt())).thenReturn(pawn);
//        doReturn(false).when(board).isCellEmpty(anyInt(), anyInt());
//        doReturn(true).when(board).isCellEmpty(1, 2);
//        doReturn(figureImage).when(board).getImageOfFigure(pawn);
//        board.drawBoard(graphics);
//
//        verify(board, times(1)).isLoading();
//        verify(board, times(1)).needToDrawHighlight();
//        verify(board, times(1)).isDragging();
//        verify(board, times(49)).isCellEmpty(anyInt(), anyInt());
//        verify(board, times(1)).figureAt(1, 2);
//        verify(graphics, times(1)).drawImage(figureImage, pawn.oX, pawn.oY, board.getComponent());
//    }
//
//    private Figure createPawn() {
//        Figure figure = new Figure();
//        figure.empty = false;
//        figure.type = Protocol.PAWN;
//        figure.oX = 111;
//        figure.oY = 222;
//        return figure;
//    }
}
