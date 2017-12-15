package ks3.oc.logic;

import org.junit.Test;

import ks3.oc.Protocol;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;

public class Logic_MateTest extends LogicTester {

    // ~ ~ ~ ~ ~
    // ~ ~ ~ ~ ~
    // ~ ~ o ~ ~
    // ~ ~ ~ ~ ~
    // ~ ~ ~ ~ ~
    @Test
    public void testMate_WhenNotInCheck() {
        assertFalse(logic.mate(col(), row()));
    }

    // ~ x x ~ ~
    // ~ * * ~ ~
    // ~ * o ~ ~
    // ~ * * ~ ~
    // ~ * * ~ ~
    @Test
    public void testMate_WhenInCheckButCanEscape() {
        setCheck(true);
        initEnemy(1, 0, Protocol.ROOK);
        initEnemy(2, 0, Protocol.ROOK);
        assertFalse(logic.mate(col(), row()));
    }

    // ~ x ~ x ~
    // ~ * x * ~
    // ~ * o * ~
    // ~ * * * ~
    // ~ * * * ~
    @Test
    public void testMate_WhenKingCanTakeAttacker() {
        setCheck(true);
        initEnemy(1, 0, Protocol.ROOK);
        initEnemy(3, 0, Protocol.ROOK);
        initEnemy(2, 1, Protocol.ROOK);
        assertFalse(logic.mate(col(), row()));
    }

    // ~ x x x ~
    // ~ * * * ~
    // ~ * o * ~
    // ~ * * * ~
    // ~ * * * ~
    @Test
    public void testMate_WhenCantDoAnything() {
        setCheck(true);
        initEnemy(1, 0, Protocol.ROOK);
        initEnemy(2, 0, Protocol.ROOK);
        initEnemy(3, 0, Protocol.ROOK);
        assertTrue(logic.mate(col(), row()));
    }

    // ~ x x x ~
    // ~ * * * o
    // ~ * o * ~
    // ~ * * * ~
    // ~ * * * ~
    @Test
    public void testMate_WhenCanCover() {
        setCheck(true);
        initEnemy(1, 0, Protocol.ROOK);
        initEnemy(2, 0, Protocol.ROOK);
        initEnemy(3, 0, Protocol.ROOK);
        initFriendly(4, 1, Protocol.QUEEN);
        assertFalse(logic.mate(col(), row()));
    }

    // ~ x x x ~
    // ~ * * * ~
    // ~ * o * o
    // ~ * * * ~
    // ~ * * * ~
    @Test
    public void testMate_WhenAnotherCanTakeAttacker() {
        setCheck(true);
        initEnemy(1, 0, Protocol.ROOK);
        initEnemy(2, 0, Protocol.ROOK);
        initEnemy(3, 0, Protocol.ROOK);
        initFriendly(4, 2, Protocol.QUEEN);
        assertFalse(logic.mate(col(), row()));
    }

    // ~ x x x ~
    // ~ * * * ~
    // ~ * o * ~
    // ~ * * * ~
    // ~ * * * o
    @Test
    public void testMate_WhenHasFriendlyNear_ButCantDoAnything() {
        setCheck(true);
        initEnemy(1, 0, Protocol.ROOK);
        initEnemy(2, 0, Protocol.ROOK);
        initEnemy(3, 0, Protocol.ROOK);
        initFriendly(4, 4, Protocol.QUEEN);
        assertTrue(logic.mate(col(), row()));
    }

    // ~ x x x ~
    // ~ * x * ~
    // ~ * o * ~
    // ~ * * * ~
    // ~ * * * ~
    @Test
    public void testMate_WhenCantTakeDefendedAttacker() {
        setCheck(true);
        initEnemy(1, 0, Protocol.ROOK);
        initEnemy(2, 0, Protocol.ROOK);
        initEnemy(3, 0, Protocol.ROOK);
        initEnemy(2, 1, Protocol.ROOK);
        assertTrue(logic.mate(col(), row()));
    }

    // ~ x * x ~
    // ~ * * * ~
    // ~ * o * ~
    // ~ * x * ~
    // ~ * * * ~
    // ~ * x * ~
    @Test
    public void testMate_WhenTakingAttackerWouldExposeToAnother() {
        setCheck(true);
        initEnemy(1, 0, Protocol.ROOK);
        initEnemy(3, 0, Protocol.ROOK);
        initEnemy(2, 3, Protocol.ROOK);
        initEnemy(2, 5, Protocol.ROOK);
        assertTrue(logic.mate(col(), row()));
    }

    @Override
    protected int col() {
        return 2;
    }

    @Override
    protected int row() {
        return 2;
    }

    @Override
    protected int type() {
        return Protocol.KING;
    }

}
