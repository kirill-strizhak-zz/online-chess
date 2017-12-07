package ks3.oc.logic;

import ks3.oc.Figure;
import ks3.oc.MainWindow;
import ks3.oc.Protocol;
import ks3.oc.board.BoardState;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

public abstract class LogicMoveTester {

    @Mock
    private BoardState board;
    @Mock
    private MainWindow mainWindow;

    protected Logic logic;

    protected Figure[][] fig = {
            { new Figure(), new Figure(), new Figure(), new Figure(), new Figure(), new Figure(), new Figure(), new Figure() },
            { new Figure(), new Figure(), new Figure(), new Figure(), new Figure(), new Figure(), new Figure(), new Figure() },
            { new Figure(), new Figure(), new Figure(), new Figure(), new Figure(), new Figure(), new Figure(), new Figure() },
            { new Figure(), new Figure(), new Figure(), new Figure(), new Figure(), new Figure(), new Figure(), new Figure() },
            { new Figure(), new Figure(), new Figure(), new Figure(), new Figure(), new Figure(), new Figure(), new Figure() },
            { new Figure(), new Figure(), new Figure(), new Figure(), new Figure(), new Figure(), new Figure(), new Figure() },
            { new Figure(), new Figure(), new Figure(), new Figure(), new Figure(), new Figure(), new Figure(), new Figure() },
            { new Figure(), new Figure(), new Figure(), new Figure(), new Figure(), new Figure(), new Figure(), new Figure() }
    };

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        initFigure(col(), row());
        when(board.figureAt(anyInt(), anyInt())).thenAnswer((in) -> {
            int col = (Integer) in.getArguments()[0];
            int row = (Integer) in.getArguments()[1];
            return fig[col][row];
        });
        when(mainWindow.getMyColor()).thenReturn(Protocol.WHITE);
        logic = new Logic(board, mainWindow);
    }

    protected void initFigure(int col, int row) {
        fig[col][row].empty = false;
        fig[col][row].firstStep = true;
        fig[col][row].type = type();
        fig[col][row].color = Protocol.WHITE;
    }

    protected void initEnemy(int col, int row) {
        initSimple(col, row, Protocol.BLACK);
    }

    protected void initFriendly(int col, int row) {
        initSimple(col, row, Protocol.WHITE);
    }

    private void initSimple(int col, int row, int color) {
        fig[col][row].empty = false;
        fig[col][row].color = color;
    }

    protected void clearFigure(int col, int row) {
        fig[col][row].empty = false;
        fig[col][row].color = Protocol.NULL;
    }

    protected void validate(Set<String> expected) {
        validate(col(), row(), expected);
    }

    protected void validate(int col, int row, Set<String> expected) {
        logic.calculateAllowedMoves(fig[col][row], col, row);
        Set<String> result = convertResult();
        assertTrue("Result does not contain all expected values: " + result, result.containsAll(expected));
        assertTrue("Result has unexpected values: " + result, expected.containsAll(result));
    }

    private Set<String> convertResult() {
        Set<String> result = new HashSet<>();
        for (int i = 0; logic.getAllowed()[i][0] != -1; i++) {
            result.add(logic.getAllowed()[i][0] + ":" + logic.getAllowed()[i][1]);
        }
        return result;
    }

    protected abstract int col();

    protected abstract int row();

    protected abstract int type();

}
