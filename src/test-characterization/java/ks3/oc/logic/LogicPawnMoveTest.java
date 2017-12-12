package ks3.oc.logic;

import ks3.oc.Protocol;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LogicPawnMoveTest extends LogicTester {

    // ~ # ~
    // ~ # ~
    // ~ o ~
    @Test
    public void testAllowedMoves_WhenAllClearAndFirstMove() {
        Set<String> expected = new HashSet<>(2);
        expected.add("1:0");
        expected.add("1:1");
        validate(expected);
    }

    // # ~ ~
    // # ~ ~
    // o ~ ~
    @Test
    public void testAllowedMoves_LeftmostPawn() {
        initFigure(0, 2);
        Set<String> expected = new HashSet<>(2);
        expected.add("0:0");
        expected.add("0:1");
        validate(0, 2, expected);
    }

    // ~ ~ #
    // ~ ~ #
    // ~ ~ o
    @Test
    public void testAllowedMoves_RightmostPawn() {
        initFigure(7, 2);
        Set<String> expected = new HashSet<>(2);
        expected.add("7:0");
        expected.add("7:1");
        validate(7, 2, expected);
    }

    // ~ ~ ~
    // ~ # ~
    // ~ o ~
    @Test
    public void testAllowedMoves_WhenAllClearAndNotFirstMove() {
        fig[1][2].firstStep = false;
        Set<String> expected = new HashSet<>(1);
        expected.add("1:1");
        validate(expected);
    }

    // ~ ! ~
    // ~ # ~
    // ~ o ~
    @Test
    public void testAllowedMoves_WhenFarBlocked() {
        initEnemy(1, 0);
        Set<String> expected = new HashSet<>(1);
        expected.add("1:1");
        validate(expected);
    }

    // ~ ~ ~
    // ~ ! ~
    // ~ o ~
    @Test
    public void testAllowedMoves_WhenNearBlocked() {
        initEnemy(1, 1);
        validate(Collections.emptySet());
    }

    // ~ ~ ~
    // x ! ~
    // ~ o ~
    @Test
    public void testAllowedMoves_WhenNearBlocked_LeftEnemy() {
        initEnemy(1, 1);
        initEnemy(0, 1);
        Set<String> expected = new HashSet<>(1);
        expected.add("0:1");
        validate(expected);
    }

    // ~ ~ ~
    // ~ ! x
    // ~ o ~
    @Test
    public void testAllowedMoves_WhenNearBlocked_RightEnemy() {
        initEnemy(1, 1);
        initEnemy(2, 1);
        Set<String> expected = new HashSet<>(1);
        expected.add("2:1");
        validate(expected);
    }

    // ~ ~ ~
    // ! ! ~
    // ~ o ~
    @Test
    public void testAllowedMoves_WhenNearBlocked_LeftFriendly() {
        initEnemy(1, 1);
        initFriendly(0, 1);
        validate(Collections.emptySet());
    }

    // ~ ~ ~
    // ~ ! !
    // ~ o ~
    @Test
    public void testAllowedMoves_WhenNearBlocked_RightFriendly() {
        initEnemy(1, 1);
        initFriendly(2, 1);
        validate(Collections.emptySet());
    }

    // ~ # ~
    // x # x
    // ~ o ~
    @Test
    public void testAllowedMoves_WhenFree_TwoEnemies() {
        initEnemy(0, 1);
        initEnemy(2, 1);
        Set<String> expected = new HashSet<>(4);
        expected.addAll(Arrays.asList("1:0", "0:1", "1:1", "2:1"));
        validate(expected);
    }

    @Override
    protected int col() {
        return 1;
    }

    @Override
    protected int row() {
        return 2;
    }

    @Override
    protected int type() {
        return Protocol.PAWN;
    }
}
