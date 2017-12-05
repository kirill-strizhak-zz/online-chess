package ks3.oc.board;

import ks3.oc.Figure;
import ks3.oc.MainWindow;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

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
        testInitFigures(board, BoardSetup.PLAYING_WHITE);
    }

    @Test
    public void testInitFigures_IfBlack() {
        when(mainWindow.getMyColor()).thenReturn(Board.BLACK);
        Board board = new Board(mainWindow, null, null);
        testInitFigures(board, BoardSetup.PLAYING_BLACK);
    }

    private void testInitFigures(Board board, BoardSetup setup) {
        testMiddleIsEmpty(board.figures());
        testPawnMatch(board.figures(), blackFigureSet[Board.PAWN], setup.blackPawnRow);
        testPawnMatch(board.figures(), whiteFigureSet[Board.PAWN], setup.whitePawnRow);
        testFigureMatch(board.figures(), blackFigureSet, setup.blackFigureRow, setup.queenCol, setup.kingCol);
        testFigureMatch(board.figures(), whiteFigureSet, setup.whiteFigureRow, setup.queenCol, setup.kingCol);
        testFigureCoordinates(board.figures(), 0, 2);
        testFigureCoordinates(board.figures(), 6, 8);
        testKing(board.king, setup.whiteFigureRow, setup.blackFigureRow, setup.kingCol);
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
                assertEquals("X coordinate should be correct", col * 60, figures[col][row].oX);
                assertEquals("Y coordinate should be correct", row * 60, figures[col][row].oY);
            }
        }
    }

    private void testKing(int[][] kings, int whiteRow, int blackRow, int kingCol) {
        String colMsg = "King coordinate should be in correct column";
        assertEquals(colMsg, kingCol, kings[0][0]);
        assertEquals(colMsg, kingCol, kings[1][0]);
        assertEquals("Black king coordinate should be in correct row", blackRow, kings[0][1]);
        assertEquals("White king coordinate should be in correct row", whiteRow, kings[1][1]);
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
