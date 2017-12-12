package ks3.oc.logic;

import ks3.oc.Protocol;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LogicQueenMoveTest extends LogicTester {

    private static final Set<String> IMMEDIATE_SURROUNDINGS =
            new HashSet<>(Arrays.asList("1:1", "2:1", "3:1", "1:2", "3:2", "1:3", "2:3", "3:3"));

    // # ~ # ~ #
    // ~ # # # ~
    // # # o # #
    // ~ # # # ~
    // # ~ # ~ #
    @Test
    public void testAllowedMoves_WhenAllClear() {
        Set<String> expected = new HashSet<>(25);
        expected.addAll(Arrays.asList(
                "0:2", "1:2", "3:2", "4:2", "5:2", "6:2", "7:2",
                "2:0", "2:1", "2:3", "2:4", "2:5", "2:6", "2:7",
                "0:0", "1:1", "3:3", "4:4", "5:5", "6:6", "7:7",
                "0:4", "1:3", "3:1", "4:0"
        ));
        validate(expected);
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
    // ~ x x x ~
    // ~ x o x ~
    // ~ x x x ~
    // ~ ~ ~ ~ ~
    @Test
    public void testAllowedMoves_WhenAllNearEnemy() {
        initEnemy(1, 1);
        initEnemy(2, 1);
        initEnemy(3, 1);
        initEnemy(1, 2);
        initEnemy(3, 2);
        initEnemy(1, 3);
        initEnemy(2, 3);
        initEnemy(3, 3);
        validate(IMMEDIATE_SURROUNDINGS);
    }

    // o # # # #
    // # # ~ ~ ~
    // # ~ # ~ ~
    // # ~ ~ # ~
    // # ~ ~ ~ #
    @Test
    public void testAllowedMoves_WhenInTopLeft() {
        initFigure(0, 0);
        clearFigure(2, 2);
        Set<String> expected = new HashSet<>(21);
        expected.addAll(Arrays.asList(
                "0:1", "0:2", "0:3", "0:4", "0:5", "0:6", "0:7",
                "1:0", "2:0", "3:0", "4:0", "5:0", "6:0", "7:0",
                "1:1", "2:2", "3:3", "4:4", "5:5", "6:6", "7:7"
        ));
        validate(0, 0, expected);
    }

    // # ~ ~ ~ #
    // ~ # ~ ~ #
    // ~ ~ # ~ #
    // ~ ~ ~ # #
    // # # # # o
    @Test
    public void testAllowedMoves_WhenInBottomRight() {
        initFigure(7, 7);
        clearFigure(2, 2);
        Set<String> expected = new HashSet<>(21);
        expected.addAll(Arrays.asList(
                "7:0", "7:1", "7:2", "7:3", "7:4", "7:5", "7:6",
                "0:7", "1:7", "2:7", "3:7", "4:7", "5:7", "6:7",
                "0:0", "1:1", "2:2", "3:3", "4:4", "5:5", "6:6"
        ));
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
        return Protocol.QUEEN;
    }
}
