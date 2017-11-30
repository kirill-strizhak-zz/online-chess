package ks3.oc;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BoardInitialisationTest {

    @Mock
    private MainWindow mainWindow;
    private final Figure[] whiteFigureSet = generateFigureSet(Board.WHITE);
    private final Figure[] blackFigureSet = generateFigureSet(Board.BLACK);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testInitCompletes() {
        new Board(mainWindow, null, null);
        verify(mainWindow, times(1)).say("B: ini completed");
    }

    @Test
    public void testInitFigures_IfWhite() {
        when(mainWindow.getMyColor()).thenReturn(Board.WHITE);
        Board board = new Board(mainWindow, null, null);
        testMiddleIsEmpty(board.fig);
        testPawnMatch(board.fig, blackFigureSet[Board.PAWN], 1);
        testPawnMatch(board.fig, whiteFigureSet[Board.PAWN], 6);
        testFigureMatch(board.fig, blackFigureSet, 0, 3, 4);
        testFigureMatch(board.fig, whiteFigureSet, 7, 3, 4);
        testFigureCoordinates(board.fig, 0, 2);
        testFigureCoordinates(board.fig, 6, 8);
    }

    @Test
    public void testInitFigures_IfBlack() {
        when(mainWindow.getMyColor()).thenReturn(Board.BLACK);
        Board board = new Board(mainWindow, null, null);
        testMiddleIsEmpty(board.fig);
        testPawnMatch(board.fig, whiteFigureSet[Board.PAWN], 1);
        testPawnMatch(board.fig, blackFigureSet[Board.PAWN], 6);
        testFigureMatch(board.fig, whiteFigureSet, 0, 4, 3);
        testFigureMatch(board.fig, blackFigureSet, 7, 4, 3);
        testFigureCoordinates(board.fig, 0, 2);
        testFigureCoordinates(board.fig, 6, 8);
    }

    private void testMiddleIsEmpty(Figure[][] figures) {
        for (int row = 2; row < 6; row++) {
            for (int col = 0; col < 8; col++) {
                assertTrue("Middle rows should be empty", figures[col][row].empty);
            }
        }
    }

    private void testPawnMatch(Figure[][] figures, Figure pawn, int row) {
        String color = pawn.color == Board.BLACK ? "black" : "white";
        String errMsg = String.format("Row %d should have all %s pawns", row, color);
        for (int col = 0; col < 7; col++) {
            assertTrue(errMsg, figuresAreEqual(figures[col][row], pawn));
        }
    }

    private void testFigureMatch(Figure[][] figures, Figure[] figureSet, int row, int queenRow, int kingRow) {
        String errMsg = "Figures should be in their expected places";
        assertTrue(errMsg + " : RL", figuresAreEqual(figures[0][row], figureSet[Board.ROOK]));
        assertTrue(errMsg + " : RR", figuresAreEqual(figures[7][row], figureSet[Board.ROOK]));
        assertTrue(errMsg + " : KL", figuresAreEqual(figures[1][row], figureSet[Board.KNIGHT]));
        assertTrue(errMsg + " : KR", figuresAreEqual(figures[6][row], figureSet[Board.KNIGHT]));
        assertTrue(errMsg + " : BL", figuresAreEqual(figures[2][row], figureSet[Board.BISHOP]));
        assertTrue(errMsg + " : BR", figuresAreEqual(figures[5][row], figureSet[Board.BISHOP]));
        assertTrue(errMsg + " : Q", figuresAreEqual(figures[queenRow][row], figureSet[Board.QUEEN]));
        assertTrue(errMsg + " : K", figuresAreEqual(figures[kingRow][row], figureSet[Board.KING]));
    }

    private void testFigureCoordinates(Figure[][] figures, int fromRow, int toRow) {
        for (int row = fromRow; row < toRow; row++) {
            for (int col = 0; col < 8; col++) {
                Assert.assertEquals(col * 60, figures[col][row].oX);
                Assert.assertEquals(row * 60, figures[col][row].oY);
            }
        }
    }

    private boolean figuresAreEqual(Figure f1, Figure f2) {
        return f1.empty == f2.empty
                && f1.firstStep == f2.firstStep
                && f1.type == f2.type
                && f1.color == f2.color;
    }

    private Figure[] generateFigureSet(int color) {
        Figure[] figures = new Figure[6];
        for (int i = 0; i < figures.length; i++) {
            figures[i] = createFigure(color, i);
        }
        return figures;
    }

    private Figure createFigure(int color, int type) {
        Figure figure = new Figure();
        figure.empty = false;
        figure.color = color;
        figure.type = type;
        return figure;
    }
}
