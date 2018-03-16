package ks3.oc.board.start;

import ks3.oc.Figure;
import ks3.oc.Protocol;
import ks3.oc.board.BoardSetup;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ClassicStartingBoardInitializerTest {

    private Figure[][] figures;
    private int[][] kings;

    private final Figure[] whiteFigureSet = generateFigureSet(Protocol.WHITE);
    private final Figure[] blackFigureSet = generateFigureSet(Protocol.BLACK);

    @Before
    public void setUp() {
        figures = new Figure[8][8];
        kings = new int[2][2];
    }

    @Test
    public void testInitFigures_IfWhite() {
        testInitFigures(BoardSetup.PLAYING_WHITE);
    }

    @Test
    public void testInitFigures_IfBlack() {
        testInitFigures(BoardSetup.PLAYING_BLACK);
    }

    private void testInitFigures(BoardSetup setup) {
        new ClassicStartingBoardInitializer().initFigureData(setup, figures, kings);
        testMiddleIsEmpty();
        testPawnMatch(blackFigureSet[Protocol.PAWN], setup.getBlackPawnRow());
        testPawnMatch(whiteFigureSet[Protocol.PAWN], setup.getWhitePawnRow());
        testFigureMatch(blackFigureSet, setup.getBlackFigureRow(), setup.getQueenCol(), setup.getKingCol());
        testFigureMatch(whiteFigureSet, setup.getWhiteFigureRow(), setup.getQueenCol(), setup.getKingCol());
        testFigureCoordinates(0, 2);
        testFigureCoordinates(6, 8);
        testKing(setup.getWhiteFigureRow(), setup.getBlackFigureRow(), setup.getKingCol());
    }

    private void testMiddleIsEmpty() {
        for (int row = 2; row < 6; row++) {
            for (int col = 0; col < 8; col++) {
                assertTrue("Middle rows should be empty", figures[col][row].empty);
            }
        }
    }

    private void testPawnMatch(Figure pawn, int row) {
        String color = pawn.color == Protocol.BLACK ? "black" : "white";
        String errMsg = String.format("Row %d should have all %s pawns", row, color);
        for (int col = 0; col < 7; col++) {
            assertTrue(errMsg, figuresAreEqual(figures[col][row], pawn));
        }
    }

    private void testFigureMatch(Figure[] figureSet, int row, int queenRow, int kingRow) {
        String errMsg = "Figures should be in their expected places";
        assertTrue(errMsg + " : RL", figuresAreEqual(figures[0][row], figureSet[Protocol.ROOK]));
        assertTrue(errMsg + " : RR", figuresAreEqual(figures[7][row], figureSet[Protocol.ROOK]));
        assertTrue(errMsg + " : KL", figuresAreEqual(figures[1][row], figureSet[Protocol.KNIGHT]));
        assertTrue(errMsg + " : KR", figuresAreEqual(figures[6][row], figureSet[Protocol.KNIGHT]));
        assertTrue(errMsg + " : BL", figuresAreEqual(figures[2][row], figureSet[Protocol.BISHOP]));
        assertTrue(errMsg + " : BR", figuresAreEqual(figures[5][row], figureSet[Protocol.BISHOP]));
        assertTrue(errMsg + " : Q", figuresAreEqual(figures[queenRow][row], figureSet[Protocol.QUEEN]));
        assertTrue(errMsg + " : K", figuresAreEqual(figures[kingRow][row], figureSet[Protocol.KING]));
    }

    private void testFigureCoordinates(int fromRow, int toRow) {
        for (int row = fromRow; row < toRow; row++) {
            for (int col = 0; col < 8; col++) {
                assertEquals("X coordinate should be correct", col * 60, figures[col][row].oX);
                assertEquals("Y coordinate should be correct", row * 60, figures[col][row].oY);
            }
        }
    }

    private void testKing(int whiteRow, int blackRow, int kingCol) {
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
