package ks3.oc.logic;

import ks3.oc.Protocol;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Logic_MoveKingTest extends LogicTester {

    private static final Set<String> IMMEDIATE_SURROUNDINGS =
            new HashSet<>(Arrays.asList("1:1", "2:1", "3:1", "1:2", "3:2", "1:3", "2:3", "3:3"));

    // ~ ~ ~ ~ ~
    // ~ # # # ~
    // ~ # o # ~
    // ~ # # # ~
    // ~ ~ ~ ~ ~
    @Test
    public void testAllowedMoves_WhenAllClear() {
        validate(IMMEDIATE_SURROUNDINGS);
    }

    // ~ ~ ~ ~ ~
    // ~ ! ! ! ~
    // ~ ! o ! ~
    // ~ ! ! ! ~
    // ~ ~ ~ ~ ~
    @Test
    public void testAllowedMoves_WhenAllNearFriendly() {
        initFriendly(1, 1);
        initFriendly(2, 1);
        initFriendly(3, 1);
        initFriendly(1, 2);
        initFriendly(3, 2);
        initFriendly(1, 3);
        initFriendly(2, 3);
        initFriendly(3, 3);
        validate(Collections.emptySet());
    }

    // ~ ~ ~ ~ ~
    // ~ x ~ x ~
    // ~ ~ o ~ ~
    // ~ x ~ x ~
    // ~ ~ ~ ~ ~
    @Test
    public void testAllowedMoves_WhenAllDiagonalSafeEnemy() {
        initEnemy(1, 1, Protocol.PAWN);
        initEnemy(3, 1, Protocol.PAWN);
        initEnemy(1, 3, Protocol.PAWN);
        initEnemy(3, 3, Protocol.PAWN);
        validate(IMMEDIATE_SURROUNDINGS);
    }

    // ~ ~ ~ ~ ~
    // ~ * x * ~
    // ~ x o x ~
    // ~ * x * ~
    // ~ ~ ~ ~ ~
    @Test
    public void testAllowedMoves_WhenAllLinearEnemy() {
        initEnemy(2, 1, Protocol.KNIGHT);
        initEnemy(1, 2, Protocol.KNIGHT);
        initEnemy(3, 2, Protocol.KNIGHT);
        initEnemy(2, 3, Protocol.KNIGHT);
        Set<String> expected = new HashSet<>(4);
        expected.addAll(Arrays.asList("2:1", "1:2", "3:2", "2:3"));
        validate(expected);
    }

    // ~ x ~ x ~
    // x * * * x
    // ~ * o * ~
    // x * * * x
    // ~ x ~ x ~
    @Test
    public void testAllowedMoves_WhenAllUnsafe() {
        initEnemy(1, 0);
        initEnemy(3, 0);
        initEnemy(0, 1);
        initEnemy(4, 1);
        initEnemy(0, 3);
        initEnemy(4, 3);
        initEnemy(1, 4);
        initEnemy(3, 4);
        validate(Collections.emptySet());
    }

    // o # ~ ~ ~
    // # # ~ ~ ~
    // ~ ~ ~ ~ ~
    // ~ ~ ~ ~ ~
    // ~ ~ ~ ~ ~
    @Test
    public void testAllowedMoves_WhenInTopLeft() {
        initFigure(0, 0);
        Set<String> expected = new HashSet<>(3);
        expected.addAll(Arrays.asList("1:0", "0:1", "1:1"));
        validate(0, 0, expected);
    }

    // ~ ~ ~ ~ ~
    // ~ ~ ~ ~ ~
    // ~ ~ ~ ~ ~
    // ~ ~ ~ # #
    // ~ ~ ~ # o
    @Test
    public void testAllowedMoves_WhenInBottomRight() {
        initFigure(7, 7);
        Set<String> expected = new HashSet<>(3);
        expected.addAll(Arrays.asList("6:6", "7:6", "6:7"));
        validate(7, 7, expected);
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
