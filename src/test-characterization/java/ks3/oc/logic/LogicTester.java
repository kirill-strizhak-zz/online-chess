package ks3.oc.logic;

import ks3.oc.Figure;
import ks3.oc.MainWindow;
import ks3.oc.Protocol;
import ks3.oc.board.BoardState;
import ks3.oc.dialogs.FigurePicker;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

abstract class LogicTester {

    @Mock
    protected BoardState board;
    @Mock
    private MainWindow mainWindow;
    @Mock
    protected FigurePicker figurePicker;

    protected Logic logic;

    private boolean check = false;
    private boolean myTurn = true;
    private Figure draggedFigure;
    private int kingCol;
    private int kingRow;

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
        when(board.isCheck()).thenAnswer((in) -> isCheck());
        when(board.draggedFigure()).thenAnswer((in) -> getDraggedFigure());
        when(board.getKingCol(anyInt())).thenAnswer((in) -> getKingCol());
        when(board.getKingRow(anyInt())).thenAnswer((in) -> getKingRow());
        when(board.figureAt(anyInt(), anyInt())).thenAnswer((in) -> {
            int col = (Integer) in.getArguments()[0];
            int row = (Integer) in.getArguments()[1];
            return fig[col][row];
        });
        when(mainWindow.getMyColor()).thenAnswer((in) -> getMyColor());
        when(mainWindow.getOppColor()).thenAnswer((in) -> getOppColor());
        when(mainWindow.isMyTurn()).thenAnswer((in) -> isMyTurn());
        logic = new Logic(board, mainWindow, figurePicker);
    }

    public void initFigure(int col, int row) {
        fig[col][row].empty = false;
        fig[col][row].firstStep = true;
        fig[col][row].type = type();
        fig[col][row].color = getMyColor();
    }

    public void initEnemy(int col, int row) {
        initEnemy(col, row, type());
    }

    public void initEnemy(int col, int row, int type) {
        initSimple(col, row, getOppColor(), type);
    }

    public void initFriendly(int col, int row) {
        initFriendly(col, row, type());
    }

    public void initFriendly(int col, int row, int type) {
        initSimple(col, row, getMyColor(), type);
    }

    public void initSimple(int col, int row, int color, int type) {
        fig[col][row].empty = false;
        fig[col][row].type = type;
        fig[col][row].color = color;
    }

    public void clearFigure(int col, int row) {
        fig[col][row].empty = true;
        fig[col][row].type = Protocol.NULL;
        fig[col][row].color = Protocol.NULL;
    }

    public void validate(Set<String> expected) {
        validate(col(), row(), expected);
    }

    public void validate(int col, int row, Set<String> expected) {
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

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public boolean isMyTurn() {
        return myTurn;
    }

    public void setMyTurn(boolean myTurn) {
        this.myTurn = myTurn;
    }

    public Figure getDraggedFigure() {
        return draggedFigure;
    }

    public void setDraggedFigure(int col, int row) {
        this.draggedFigure = fig[col][row];
    }

    public int getKingCol() {
        return kingCol;
    }

    public int getKingRow() {
        return kingRow;
    }

    public void setKingPosition(int col, int row) {
        kingCol = col;
        kingRow = row;
    }

    public int getMyColor() {
        return Protocol.WHITE;
    }

    public int getOppColor() {
        return Protocol.BLACK;
    }

    protected abstract int col();

    protected abstract int row();

    protected abstract int type();

}
