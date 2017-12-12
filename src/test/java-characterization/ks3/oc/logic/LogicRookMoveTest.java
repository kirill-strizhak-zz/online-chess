package ks3.oc.logic;

import ks3.oc.Protocol;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LogicRookMoveTest extends LogicTester {

    private static final Set<String> IMMEDIATE_SURROUNDINGS =
            new HashSet<>(Arrays.asList("2:1", "1:2", "3:2", "2:3"));

    // ~ ~ # ~ ~
    // ~ ~ # ~ ~
    // # # o # #
    // ~ ~ # ~ ~
    // ~ ~ # ~ ~
    @Test
    public void testAllowedMoves_WhenAllClear() {
        Set<String> expected = new HashSet<>(14);
        expected.addAll(Arrays.asList(
                "0:2", "1:2", "3:2", "4:2", "5:2", "6:2", "7:2",
                "2:0", "2:1", "2:3", "2:4", "2:5", "2:6", "2:7"
        ));
        validate(expected);
    }

    // ~ ~ ~ ~ ~
    // ~ ~ ! ~ ~
    // ~ ! o ! ~
    // ~ ~ ! ~ ~
    // ~ ~ ~ ~ ~
    @Test
    public void testAllowedMoves_WhenAllNearFriendly() {
        initFriendly(2, 1);
        initFriendly(1, 2);
        initFriendly(3, 2);
        initFriendly(2, 3);
        validate(Collections.emptySet());
    }

    // ~ ~ ! ~ ~
    // ~ ~ # ~ ~
    // ! # o # !
    // ~ ~ # ~ ~
    // ~ ~ ! ~ ~
    @Test
    public void testAllowedMoves_WhenAllFarFriendly() {
        initFriendly(2, 0);
        initFriendly(0, 2);
        initFriendly(4, 2);
        initFriendly(2, 4);
        validate(IMMEDIATE_SURROUNDINGS);
    }

    // ~ ~ ~ ~ ~
    // ~ ~ x ~ ~
    // ~ x o x ~
    // ~ ~ x ~ ~
    // ~ ~ ~ ~ ~
    @Test
    public void testAllowedMoves_WhenAllNearEnemy() {
        initEnemy(2, 1);
        initEnemy(1, 2);
        initEnemy(3, 2);
        initEnemy(2, 3);
        validate(IMMEDIATE_SURROUNDINGS);
    }

    // ~ ~ x ~ ~
    // ~ ~ # ~ ~
    // x # o # x
    // ~ ~ # ~ ~
    // ~ ~ x ~ ~
    @Test
    public void testAllowedMoves_WhenAllFarEnemy() {
        initEnemy(2, 0);
        initEnemy(0, 2);
        initEnemy(4, 2);
        initEnemy(2, 4);
        Set<String> expected = new HashSet<>(8);
        expected.addAll(IMMEDIATE_SURROUNDINGS);
        expected.addAll(Arrays.asList("0:2", "4:2", "2:0", "2:4"));
        validate(expected);
    }

    // o # # # #
    // # ~ ~ ~ ~
    // # ~ ~ ~ ~
    // # ~ ~ ~ ~
    // # ~ ~ ~ ~
    @Test
    public void testAllowedMoves_WhenInTopLeft() {
        initFigure(0, 0);
        Set<String> expected = new HashSet<>(14);
        expected.addAll(Arrays.asList(
                "0:1", "0:2", "0:3", "0:4", "0:5", "0:6", "0:7",
                "1:0", "2:0", "3:0", "4:0", "5:0", "6:0", "7:0"
        ));
        validate(0, 0, expected);
    }

    // ~ ~ ~ ~ #
    // ~ ~ ~ ~ #
    // ~ ~ ~ ~ #
    // ~ ~ ~ ~ #
    // # # # # o
    @Test
    public void testAllowedMoves_WhenInBottomRight() {
        initFigure(7, 7);
        Set<String> expected = new HashSet<>(14);
        expected.addAll(Arrays.asList(
                "7:0", "7:1", "7:2", "7:3", "7:4", "7:5", "7:6",
                "0:7", "1:7", "2:7", "3:7", "4:7", "5:7", "6:7"
        ));
        validate(7, 7, expected);
    }

    // !
    // # ~ ~ ~ ~
    // # ~ ~ ~ ~
    // # ~ ~ ~ ~
    // # ~ ~ ~ ~
    // o # # # # x
    @Test
    public void testAllowedMoves_WhenFurthestEnemyOrFriendly() {
        initFigure(0, 7);
        initFriendly(0, 0);
        initEnemy(7, 7);
        Set<String> expected = new HashSet<>(13);
        expected.addAll(Arrays.asList(
                "0:1", "0:2", "0:3", "0:4", "0:5", "0:6",
                "1:7", "2:7", "3:7", "4:7", "5:7", "6:7", "7:7"
        ));
        validate(0, 7, expected);
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
        return Protocol.ROOK;
    }
}
