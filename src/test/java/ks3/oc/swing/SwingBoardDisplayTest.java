package ks3.oc.swing;

import ks3.oc.Figure;
import ks3.oc.main.MainWindow;
import ks3.oc.Protocol;
import ks3.oc.board.Board;
import ks3.oc.res.ResourceManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

public class SwingBoardDisplayTest {

    @Mock
    private ResourceManager resourceManager;
    @Mock
    private MainWindow mainWindow;
    @Mock
    private SwingDebugOverlay debugOverlay;
    @Mock
    private Graphics graphics;
    @Mock
    private Image boardImage;
    @Mock
    private Image figureImage;

    private SwingBoardDisplay board;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(resourceManager.getBoard()).thenReturn(boardImage);
        board = mock(SwingBoardDisplay.class, withSettings()
                .useConstructor(resourceManager, mainWindow, null, debugOverlay)
                .defaultAnswer(CALLS_REAL_METHODS));
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

    @Test
    public void testDrawBoard_WhenOnlyOneFigure() {
        when(board.isLoading()).thenReturn(false);
        doReturn(false).when(board).needToDrawHighlight();
        when(board.isDragging()).thenReturn(false);
        Figure pawn = createPawn();
        when(board.figureAt(anyInt(), anyInt())).thenReturn(pawn);
        doReturn(true).when(board).isCellEmpty(anyInt(), anyInt());
        doReturn(false).when(board).isCellEmpty(1, 2);
        doReturn(figureImage).when(board).getImageOfFigure(pawn);
        board.drawBoard(graphics);

        verify(graphics, times(1)).drawImage(boardImage, 0, 0, board.getComponent());
        verify(board, times(1)).isLoading();
        verify(board, times(1)).needToDrawHighlight();
        verify(board, times(1)).isDragging();
        verify(board, times(64)).isCellEmpty(anyInt(), anyInt());
        verify(board, times(1)).figureAt(1, 2);
        verify(graphics, times(1)).drawImage(figureImage, pawn.oX, pawn.oY, board.getComponent());
    }

    @Test
    public void testDrawBoard_WhenHasHighlight() {
        when(board.isLoading()).thenReturn(false);
        doReturn(true).when(board).needToDrawHighlight();
        doReturn(true).when(board).isCellEmpty(anyInt(), anyInt());
        doReturn(new int[][] { {100, 200}, {300, 400} }).when(board).getHighlight();
        board.drawBoard(graphics);

        verify(graphics, times(1)).setColor(any(Color.class));
        int outerSize = Board.CELL_SIZE + 1;
        int innerSize = Board.CELL_SIZE - 1;
        verify(graphics, times(1)).drawRect(99, 199, outerSize, outerSize);
        verify(graphics, times(1)).drawRect(100, 200, innerSize, innerSize);
        verify(graphics, times(1)).drawRect(299, 399, outerSize, outerSize);
        verify(graphics, times(1)).drawRect(300, 400, innerSize, innerSize);
    }

    @Test
    public void testDrawBoard_WhenDragging() {
        when(board.isLoading()).thenReturn(false);
        doReturn(false).when(board).needToDrawHighlight();
        doReturn(true).when(board).isCellEmpty(anyInt(), anyInt());
        Figure pawn = createPawn();
        when(board.getDraggedFigure()).thenReturn(pawn);
        doReturn(figureImage).when(board).getImageOfDraggedFigure();
        when(board.isDragging()).thenReturn(true);
        board.drawBoard(graphics);

        verify(graphics, times(1)).drawImage(figureImage, pawn.oX, pawn.oY, board.getComponent());
    }

    private Figure createPawn() {
        Figure figure = new Figure();
        figure.empty = false;
        figure.type = Protocol.PAWN;
        figure.oX = 111;
        figure.oY = 222;
        return figure;
    }
}
