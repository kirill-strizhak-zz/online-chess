package ks3.oc.logic;

import ks3.oc.Protocol;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class LogicPawnMoveTest extends LogicMoveTester {

    @Before
    public void setUp() {
        commonSetUp();
    }

    // ~ x ~
    // ~ x ~
    // ~ o ~
    @Test
    public void testAllowedMoves_WhenAllClearAndFirstMove() {
        Set<String> expected = new HashSet<>(2);
        expected.add("1:0");
        expected.add("1:1");

        logic.calculateAllowedMoves(fig[1][2], 1, 2);
        validate(expected);
    }

    // x ~ ~
    // x ~ ~
    // o ~ ~
    @Test
    public void testAllowedMoves_LeftmostPawn() {
        initFigure(0, 2);

        Set<String> expected = new HashSet<>(2);
        expected.add("0:0");
        expected.add("0:1");

        logic.calculateAllowedMoves(fig[0][2], 0, 2);
        validate(expected);
    }

    // ~ ~ x
    // ~ ~ x
    // ~ ~ o
    @Test
    public void testAllowedMoves_RightmostPawn() {
        initFigure(7, 2);

        Set<String> expected = new HashSet<>(2);
        expected.add("7:0");
        expected.add("7:1");

        logic.calculateAllowedMoves(fig[7][2], 7, 2);
        validate(expected);
    }

    // ~ ~ ~
    // ~ x ~
    // ~ o ~
    @Test
    public void testAllowedMoves_WhenAllClearAndNotFirstMove() {
        fig[1][2].firstStep = false;

        Set<String> expected = new HashSet<>(1);
        expected.add("1:1");

        logic.calculateAllowedMoves(fig[1][2], 1, 2);
        validate(expected);
    }

    // ~ ! ~
    // ~ x ~
    // ~ o ~
    @Test
    public void testAllowedMoves_WhenFarBlocked() {
        fig[1][0].empty = false;

        Set<String> expected = new HashSet<>(1);
        expected.add("1:1");

        logic.calculateAllowedMoves(fig[1][2], 1, 2);
        validate(expected);
    }

    // ~ ~ ~
    // ~ ! ~
    // ~ o ~
    @Test
    public void testAllowedMoves_WhenNearBlocked() {
        fig[1][1].empty = false;

        Set<String> expected = new HashSet<>(0);

        logic.calculateAllowedMoves(fig[1][2], 1, 2);
        validate(expected);
    }

    // ~ ~ ~
    // x ! ~
    // ~ o ~
    @Test
    public void testAllowedMoves_WhenNearBlocked_LeftEnemy() {
        fig[1][1].empty = false;
        fig[0][1].empty = false;
        fig[0][1].color = Protocol.BLACK;

        Set<String> expected = new HashSet<>(1);
        expected.add("0:1");

        logic.calculateAllowedMoves(fig[1][2], 1, 2);
        validate(expected);
    }

    // ~ ~ ~
    // ~ ! x
    // ~ o ~
    @Test
    public void testAllowedMoves_WhenNearBlocked_RightEnemy() {
        fig[1][1].empty = false;
        fig[2][1].empty = false;
        fig[2][1].color = Protocol.BLACK;

        Set<String> expected = new HashSet<>(1);
        expected.add("2:1");

        logic.calculateAllowedMoves(fig[1][2], 1, 2);
        validate(expected);
    }

    // ~ ~ ~
    // o ! ~
    // ~ o ~
    @Test
    public void testAllowedMoves_WhenNearBlocked_LeftFriendly() {
        fig[1][1].empty = false;
        fig[0][1].empty = false;
        fig[0][1].color = Protocol.WHITE;

        Set<String> expected = new HashSet<>(0);

        logic.calculateAllowedMoves(fig[1][2], 1, 2);
        validate(expected);
    }

    // ~ ~ ~
    // ~ ! o
    // ~ o ~
    @Test
    public void testAllowedMoves_WhenNearBlocked_RightFriendly() {
        fig[1][1].empty = false;
        fig[0][1].empty = false;
        fig[0][1].color = Protocol.WHITE;

        Set<String> expected = new HashSet<>(0);

        logic.calculateAllowedMoves(fig[1][2], 1, 2);
        validate(expected);
    }

    // ~ x ~
    // x x x
    // ~ o ~
    @Test
    public void testAllowedMoves_WhenFree_TwoEnemies() {
        fig[0][1].empty = false;
        fig[0][1].color = Protocol.BLACK;
        fig[2][1].empty = false;
        fig[2][1].color = Protocol.BLACK;

        Set<String> expected = new HashSet<>(4);
        expected.add("1:0");
        expected.add("0:1");
        expected.add("1:1");
        expected.add("2:1");

        logic.calculateAllowedMoves(fig[1][2], 1, 2);
        validate(expected);
    }

    @Override
    protected void initFigure() {
        initFigure(1, 2);
    }

    private void initFigure(int col, int row) {
        fig[col][row].empty = false;
        fig[col][row].firstStep = true;
        fig[col][row].type = Protocol.PAWN;
        fig[col][row].color = Protocol.WHITE;
    }
}
