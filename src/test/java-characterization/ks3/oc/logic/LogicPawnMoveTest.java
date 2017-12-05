package ks3.oc.logic;

import ks3.oc.Figure;
import ks3.oc.MainWindow;
import ks3.oc.Protocol;
import ks3.oc.board.BoardState;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

public class LogicPawnMoveTest {

    @Mock
    private BoardState board;
    @Mock
    private MainWindow mainWindow;

    private Logic logic;

    private Figure[][] fig = {
            { new Figure(), new Figure(), new Figure() },
            { new Figure(), new Figure(), new Figure() },
            { new Figure(), new Figure(), new Figure() }
    };

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        initPawn();
        when(board.figureAt(anyInt(), anyInt())).thenAnswer((in) -> {
            int col = (Integer) in.getArguments()[0];
            int row = (Integer) in.getArguments()[1];
            return fig[col][row];
        });
        when(mainWindow.getMyColor()).thenReturn(Protocol.WHITE);
        logic = new Logic(board, mainWindow);
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
    // x ! ~
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

    private void initPawn() {
        fig[1][2].empty = false;
        fig[1][2].firstStep = true;
        fig[1][2].type = Protocol.PAWN;
        fig[1][2].color = Protocol.WHITE;
    }

    private Set<String> convertResult() {
        Set<String> result = new HashSet<>();
        for (int i = 0; logic.getAllowed()[i][0] != -1; i++) {
            result.add(logic.getAllowed()[i][0] + ":" + logic.getAllowed()[i][1]);
        }
        return result;
    }

    private void validate(Set<String> expected) {
        Set<String> result = convertResult();
        assertTrue("Result does not contain all expected values: " + result, result.containsAll(expected));
        assertTrue("Result has unexpected values: " + result, expected.containsAll(result));
    }
}
