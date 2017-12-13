package ks3.oc.logic;

import ks3.oc.Protocol;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Logic_MoveBishopTest extends LogicTester {

    private static final Set<String> IMMEDIATE_SURROUNDINGS =
            new HashSet<>(Arrays.asList("1:1", "3:1", "1:3", "3:3"));

    // # ~ ~ ~ #
    // ~ # ~ # ~
    // ~ ~ o ~ ~
    // ~ # ~ # ~
    // # ~ ~ ~ #
    @Test
    public void testAllowedMoves_WhenAllClear() {
        Set<String> expected = new HashSet<>(11);
        expected.addAll(Arrays.asList(
                "0:0", "1:1", "3:3", "4:4", "5:5", "6:6", "7:7",
                "0:4", "1:3", "3:1", "4:0"
        ));
        validate(expected);
    }

    // ~ ~ ~ ~ ~
    // ~ ! ~ ! ~
    // ~ ~ o ~ ~
    // ~ ! ~ ! ~
    // ~ ~ ~ ~ ~
    @Test
    public void testAllowedMoves_WhenAllNearFriendly() {
        initFriendly(1, 1);
        initFriendly(3, 1);
        initFriendly(1, 3);
        initFriendly(3, 3);
        validate(Collections.emptySet());
    }

    // ! ~ ~ ~ !
    // ~ # ~ # ~
    // ~ ~ o ~ ~
    // ~ # ~ # ~
    // ! ~ ~ ~ !
    @Test
    public void testAllowedMoves_WhenAllFarFriendly() {
        initFriendly(0, 0);
        initFriendly(4, 0);
        initFriendly(0, 4);
        initFriendly(4, 4);
        validate(IMMEDIATE_SURROUNDINGS);
    }

    // ~ ~ ~ ~ ~
    // ~ x ~ x ~
    // ~ ~ o ~ ~
    // ~ x ~ x ~
    // ~ ~ ~ ~ ~
    @Test
    public void testAllowedMoves_WhenAllNearEnemy() {
        initEnemy(1, 1);
        initEnemy(3, 1);
        initEnemy(1, 3);
        initEnemy(3, 3);
        validate(IMMEDIATE_SURROUNDINGS);
    }

    // x ~ ~ ~ x
    // ~ # ~ # ~
    // ~ ~ o ~ ~
    // ~ # ~ # ~
    // x ~ ~ ~ x
    @Test
    public void testAllowedMoves_WhenAllFarEnemy() {
        initEnemy(0, 0);
        initEnemy(4, 0);
        initEnemy(0, 4);
        initEnemy(4, 4);
        Set<String> expected = new HashSet<>(8);
        expected.addAll(IMMEDIATE_SURROUNDINGS);
        expected.addAll(Arrays.asList("0:0", "4:0", "0:4", "4:4"));
        validate(expected);
    }

    // o ~ ~ ~ ~
    // ~ # ~ ~ ~
    // ~ ~ # ~ ~
    // ~ ~ ~ # ~
    // ~ ~ ~ ~ #
    @Test
    public void testAllowedMoves_WhenInTopLeft() {
        initFigure(0, 0);
        clearFigure(2, 2);
        Set<String> expected = new HashSet<>(7);
        expected.addAll(Arrays.asList("1:1", "2:2", "3:3", "4:4", "5:5", "6:6", "7:7"));
        validate(0, 0, expected);
    }

    // # ~ ~ ~ ~
    // ~ # ~ ~ ~
    // ~ ~ # ~ ~
    // ~ ~ ~ # ~
    // ~ ~ ~ ~ o
    @Test
    public void testAllowedMoves_WhenInBottomRight() {
        initFigure(7, 7);
        clearFigure(2, 2);
        Set<String> expected = new HashSet<>(7);
        expected.addAll(Arrays.asList("0:0", "1:1", "2:2", "3:3", "4:4", "5:5", "6:6"));
        validate(7, 7, expected);
    }

    //           x
    // ~ ~ ~ ~ #
    // ~ ~ ~ # ~
    // ~ ~ # ~ ~
    // ~ # ~ ~ ~
    // o ~ ~ ~ ~
    @Test
    public void testAllowedMoves_WhenCanAttackFurthest() {
        initFigure(0, 7);
        initEnemy(7, 0);
        Set<String> expected = new HashSet<>(7);
        expected.addAll(Arrays.asList("1:6", "2:5", "3:4", "4:3", "5:2", "6:1", "7:0"));
        validate(0, 7, expected);
    }

    //   ~ ~ ~ ~ o
    //   ~ ~ ~ # ~
    //   ~ ~ # ~ ~
    //   ~ # ~ ~ ~
    //   # ~ ~ ~ ~
    // !
    @Test
    public void testAllowedMoves_WhenFurthestIsFriendly() {
        initFigure(7, 0);
        initFriendly(0, 7);
        Set<String> expected = new HashSet<>(6);
        expected.addAll(Arrays.asList("1:6", "2:5", "3:4", "4:3", "5:2", "6:1"));
        validate(7, 0, expected);
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
        return Protocol.BISHOP;
    }
}
