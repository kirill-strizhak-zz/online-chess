package ks3.oc.logic;

import ks3.oc.Protocol;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LogicCastlingWhenWhiteTest extends LogicTester {

    // ~ ~ ~ ~ ~ ~ ~ ~
    // o ~ ~ ~ o ~ ~ o
    @Test
    public void testCastling_WhenAllowed() {
        initRooks();
        assertTrue(logic.queenSideCastlingAllowed());
        assertTrue(logic.kingSideCastlingAllowed());
    }

    @Test
    public void testCastling_WhenNotMyTurn() {
        setMyTurn(false);
        initRooks();
        assertFalse(logic.queenSideCastlingAllowed());
        assertFalse(logic.kingSideCastlingAllowed());
    }

    @Test
    public void testCastling_WhenIsCheck() {
        setCheck(true);
        initRooks();
        assertFalse(logic.queenSideCastlingAllowed());
        assertFalse(logic.kingSideCastlingAllowed());
    }

    @Test
    public void testCastling_WhenKingMoved() {
        initRooks();
        fig[col()][row()].firstStep = false;
        assertFalse(logic.queenSideCastlingAllowed());
        assertFalse(logic.kingSideCastlingAllowed());
    }

    @Test
    public void testCastling_WhenRooksMoved() {
        initRooks();
        fig[0][7].firstStep = false;
        fig[7][7].firstStep = false;
        assertFalse(logic.queenSideCastlingAllowed());
        assertFalse(logic.kingSideCastlingAllowed());
    }

    @Test
    public void testCastling_WhenKingNotPresent() {
        clearFigure(col(), row());
        initRooks();
        assertFalse(logic.queenSideCastlingAllowed());
        assertFalse(logic.kingSideCastlingAllowed());
    }

    @Test
    public void testCastling_WhenRooksNotPresent() {
        assertFalse(logic.queenSideCastlingAllowed());
        assertFalse(logic.kingSideCastlingAllowed());
    }

    // ~ ~ ~ ~ ~ ~ ~ ~
    // o w ~ ~ o w ~ o
    @Test
    public void testCastling_WhenOtherFiguresInTheWay() {
        initRooks();
        initFriendly(1, 7);
        initFriendly(5, 7);
        assertFalse(logic.queenSideCastlingAllowed());
        assertFalse(logic.kingSideCastlingAllowed());
    }

    // ~ ~ x ~ ~ x ~ ~
    // o ~ * ~ o * ~ o
    @Test
    public void testCastling_WhenKingsPathUnderAttack() {
        initRooks();
        initEnemy(col() - 2, 6, Protocol.ROOK);
        initEnemy(col() + 1, 6, Protocol.ROOK);
        assertFalse(logic.queenSideCastlingAllowed());
        assertFalse(logic.kingSideCastlingAllowed());
    }

    // ~ x ~ ~ ~ ~ ~ x
    // o * ~ ~ o ~ ~ o
    @Test
    public void testCastling_WhenOnlyRooksPathUnderAttack() {
        initRooks();
        initEnemy(col() - 3, 6, Protocol.ROOK);
        initEnemy(col() + 3, 6, Protocol.ROOK);
        assertTrue(logic.queenSideCastlingAllowed());
        assertTrue(logic.kingSideCastlingAllowed());
    }

    private void initRooks() {
        initFriendly(0, 7, Protocol.ROOK);
        initFriendly(7, 7, Protocol.ROOK);
    }

    @Override
    protected int col() {
        return 4;
    }

    @Override
    protected int row() {
        return 7;
    }

    @Override
    protected int type() {
        return Protocol.KING;
    }
}
