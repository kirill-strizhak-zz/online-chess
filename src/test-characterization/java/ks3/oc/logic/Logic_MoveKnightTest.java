package ks3.oc.logic;

import ks3.oc.Protocol;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Logic_MoveKnightTest extends LogicTester {

    private static final Set<String> ALL_LEGAL_MOVES =
            new HashSet<>(Arrays.asList("1:0", "3:0", "0:1", "4:1", "0:3", "4:3", "1:4", "3:4"));

    // ~ # ~ # ~
    // # ~ ~ ~ #
    // ~ ~ o ~ ~
    // # ~ ~ ~ #
    // ~ # ~ # ~
    @Test
    public void testAllowedMoves_WhenAllClear() {
        validate(ALL_LEGAL_MOVES);
    }

    // ~ x ~ x ~
    // x ~ ~ ~ x
    // ~ ~ o ~ ~
    // x ~ ~ ~ x
    // ~ x ~ x ~
    @Test
    public void testAllowedMoves_WhenAllOccupiedByEnemies() {
        initEnemy(1, 0);
        initEnemy(3, 0);
        initEnemy(0, 1);
        initEnemy(4, 1);
        initEnemy(0, 3);
        initEnemy(4, 3);
        initEnemy(1, 4);
        initEnemy(3, 4);
        validate(ALL_LEGAL_MOVES);
    }

    // ~ ! ~ ! ~
    // ! ~ ~ ~ !
    // ~ ~ o ~ ~
    // ! ~ ~ ~ !
    // ~ ! ~ ! ~
    @Test
    public void testAllowedMoves_WhenAllBlockedByFriendlies() {
        initFriendly(1, 0);
        initFriendly(3, 0);
        initFriendly(0, 1);
        initFriendly(4, 1);
        initFriendly(0, 3);
        initFriendly(4, 3);
        initFriendly(1, 4);
        initFriendly(3, 4);
        validate(Collections.emptySet());
    }

    // o ~ ~ ~ ~
    // ~ ~ # ~ ~
    // ~ # ~ ~ ~
    // ~ ~ ~ ~ ~
    // ~ ~ ~ ~ ~
    @Test
    public void testAllowedMoves_WhenInTopLeft() {
        initFigure(0, 0);
        Set<String> expected = new HashSet<>(2);
        expected.add("2:1");
        expected.add("1:2");
        validate(0, 0, expected);
    }

    // ~ ~ ~ ~ ~
    // ~ ~ ~ ~ ~
    // ~ ~ ~ # ~
    // ~ ~ # ~ ~
    // ~ ~ ~ ~ o
    @Test
    public void testAllowedMoves_WhenInBottomRight() {
        initFigure(7, 7);
        Set<String> expected = new HashSet<>(2);
        expected.add("6:5");
        expected.add("5:6");
        validate(7, 7, expected);
    }

    // ~ # ! # ~
    // # ! ! ! #
    // ! ! o x x
    // # x x x #
    // ~ # x # ~
    @Test
    public void testAllowedMoves_WhenJumpingOver() {
        initFriendly(2, 0);
        initFriendly(1, 1);
        initFriendly(2, 1);
        initFriendly(3, 1);
        initFriendly(0, 2);
        initFriendly(1, 2);
        initEnemy(3, 2);
        initEnemy(4, 2);
        initEnemy(1, 3);
        initEnemy(2, 3);
        initEnemy(3, 3);
        initEnemy(2, 4);
        validate(ALL_LEGAL_MOVES);
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
        return Protocol.KNIGHT;
    }
}
