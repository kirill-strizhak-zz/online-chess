package ks3.oc.logic;

import ks3.oc.Figure;
import ks3.oc.Protocol;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class Logic_DropTest extends LogicTester {

    // ~ ~ ~ ~ ~
    // ~ ~ ~ ~ ~
    // ~ ~ o ~ ~
    @Test
    public void testCoordinatesUpdated_WhenDroppedAtOldPosition() {
        setDroppedAtNewPosition(false);
        setDraggedFigure(col(), row());
        logic.drop(col(), row());
        assertEquals(col() * 60, getDraggedFigure().oX);
        assertEquals(row() * 60, getDraggedFigure().oY);
    }

    // ~ ~ ~ ~ ~
    // # ~ ~ ~ ~
    // ~ ~ o ~ ~
    @Test
    public void testDraggedPositionUpdated_WhenDroppedInUnallowedPosition() {
        setDraggedFigure(col(), row());
        logic.drop(0, 1);
        verify(board, times(1)).updateDraggedPosition();
    }

    // ~ ~ ~ ~ ~
    // ~ ~ # ~ ~
    // ~ ~ o ~ ~
    @Test
    public void testDroppedAtAllowedPosition() {
        setDraggedFigure(col(), row());
        logic.calculateAllowedMoves(fig[col()][row()], col(), row());
        int targetRow = row() - 1;
        logic.drop(col(), targetRow);
        verifyEmpty(col(), row());
        verifyMoved(col(), targetRow);
        verify(board, times(1)).setCheck(false);
        verify(board, times(1)).makeMove(col(), targetRow);
    }

    // ~ ~ ~ ~ ~
    // ~ ~ # ~ ~
    // ~ ~ o ~ ~
    @Test
    public void testKingDroppedAtAllowedPosition() {
        initFriendly(col(), row(), Protocol.KING);
        setDraggedFigure(col(), row());
        logic.calculateAllowedMoves(fig[col()][row()], col(), row());
        int targetRow = row() - 1;
        logic.drop(col(), targetRow);
        verifyEmpty(col(), row());
        verifyMoved(col(), targetRow);
        verify(board, times(1)).moveKing(getMyColor() / 2, col(), targetRow);
        verify(board, times(1)).setCheck(false);
        verify(board, times(1)).makeMove(col(), targetRow);
    }

    // ~ ~ ~ ~ ~
    // ~ ~ # ~ ~
    // ~ ! o * x
    @Test
    public void testDropped_WhenExposingKing() {
        int kingCol = col() - 1;
        initFriendly(kingCol, row(), Protocol.KING);
        setKingPosition(kingCol, row());
        initEnemy(col() + 2, row(), Protocol.ROOK);
        setDraggedFigure(col(), row());
        logic.calculateAllowedMoves(fig[col()][row()], col(), row());
        int targetRow = row() - 1;
        logic.drop(col(), targetRow);
        verifyEmpty(col(), targetRow);
        verifyOriginal(col(), row());
        verify(board, times(1)).updateDraggedPosition();
    }

    private void verifyEmpty(int col, int row) {
        Figure figure = fig[col][row];
        assertTrue(figure.empty);
        assertEquals(Protocol.NULL, figure.type);
        assertEquals(Protocol.NULL, figure.color);
    }

    private void verifyMoved(int col, int row) {
        board.figureAt(col, row).oX = col * 60;
        board.figureAt(col, row).oY = row * 60;
        board.figureAt(col, row).empty = false;
        board.figureAt(col, row).firstStep = false;
        board.figureAt(col, row).type = board.draggedFigure().type;
        board.figureAt(col, row).color = board.draggedFigure().color;
    }

    private void verifyOriginal(int col, int row) {
        board.figureAt(col, row).oX = col * 60;
        board.figureAt(col, row).oY = row * 60;
        board.figureAt(col, row).empty = false;
        board.figureAt(col, row).firstStep = true;
        board.figureAt(col, row).type = board.draggedFigure().type;
        board.figureAt(col, row).color = board.draggedFigure().color;
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
        return Protocol.PAWN;
    }
}
