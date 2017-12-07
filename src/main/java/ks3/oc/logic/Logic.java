package ks3.oc.logic;

import ks3.oc.Figure;
import ks3.oc.MainWindow;
import ks3.oc.Messenjah;
import ks3.oc.Protocol;
import ks3.oc.board.BoardState;

public class Logic implements Protocol {

    private int[][] allowed = new int[100][2];
    private int arrPos = 0;
    public boolean calculating = false;
    private BoardState board;
    private MainWindow owner;
    private int[] attacker = new int[2];

    public Logic(BoardState brd, MainWindow own) {
        board = brd;
        owner = own;
        allowed[0][0] = -1;
    }

    public void calculateAllowedMoves(Figure figure, int col, int row) {
        calculating = true;
        switch (figure.type) {
            case PAWN:
                allowed[0][0] = -1;
                allowedMovesOfPawn(figure, col, row);
                arrPos = 0;
                break;
            case ROOK:
                allowed[0][0] = -1;
                allowedMovesOfRook(col, row);
                arrPos = 0;
                break;
            case KNIGHT:
                allowed[0][0] = -1;
                allowedMovesOfKnight(col, row);
                arrPos = 0;
                break;
            case BISHOP:
                allowed[0][0] = -1;
                allowedMovesOfBishop(col, row);
                arrPos = 0;
                break;
            case QUEEN:
                allowed[0][0] = -1;
                allowedMovesOfQueen(col, row);
                arrPos = 0;
                break;
            case KING:
                allowed[0][0] = -1;
                allowedMovesOfKing(col, row);
                arrPos = 0;
                break;
        }
        calculating = false;
    }

    private void allowedMovesOfPawn(Figure figure, int col, int row) {
        boolean simpleMoveAdded = checkPawnFirstStepRule(figure, col, row);
        if (!simpleMoveAdded) {
            checkPawnStepRule(col, row);
        }
        checkPawnCanAttackLeft(col, row);
        checkPawnCanAttackRight(col, row);
    }

    private boolean checkPawnFirstStepRule(Figure figure, int col, int row) {
        boolean conditionMet = figure.firstStep && isEmpty(col, row - 1) && isEmpty(col, row - 2);
        if (conditionMet) {
            addAllowedMove(col, row - 1);
            addAllowedMove(col, row - 2);
        }
        return conditionMet;
    }

    private void checkPawnStepRule(int col, int row) {
        if (row != 0 && isEmpty(col, row - 1)) {
            addAllowedMove(col, row - 1);
        }
    }

    private void checkPawnCanAttackLeft(int col, int row) {
        if (col != 0 && !isEmpty(col - 1, row - 1) && isNotFriendly(col - 1, row - 1)) {
            addAllowedMove(col - 1, row - 1);
        }
    }

    private void checkPawnCanAttackRight(int col, int row) {
        if (col != 7 && !isEmpty(col + 1, row - 1) && isNotFriendly(col + 1, row - 1)) {
            addAllowedMove(col + 1, row - 1);
        }
    }

    private void allowedMovesOfRook(int col, int row) {
        checkRookMovesToTheLeft(col, row);
        checkRookMovesUpwards(col, row);
        checkRookMovesToTheRight(col, row);
        checkRookMovesDownwards(col, row);
    }

    private void checkRookMovesToTheLeft(int col, int row) {
        for (int colLeft = col - 1; colLeft >= 0; colLeft--) {
            if (isNotFriendly(colLeft, row)) {
                addAllowedMove(colLeft, row);
            }
            if (!isEmpty(colLeft, row)) {
                break;
            }
        }
    }

    private void checkRookMovesUpwards(int col, int row) {
        for (int rowUp = row - 1; rowUp >= 0; rowUp--) {
            if (isNotFriendly(col, rowUp)) {
                addAllowedMove(col, rowUp);
            }
            if (!isEmpty(col, rowUp)) {
                break;
            }
        }
    }

    private void checkRookMovesToTheRight(int col, int row) {
        for (int colRight = col + 1; colRight <= 7; colRight++) {
            if (isNotFriendly(colRight, row)) {
                addAllowedMove(colRight, row);
            }
            if (!isEmpty(colRight, row)) {
                break;
            }
        }
    }

    private void checkRookMovesDownwards(int col, int row) {
        for (int rowDown = row + 1; rowDown <= 7; rowDown++) {
            if (isNotFriendly(col, rowDown)) {
                addAllowedMove(col, rowDown);
            }
            if (!isEmpty(col, rowDown)) {
                break;
            }
        }
    }

    private void allowedMovesOfKnight(int col, int row) {
        if (topLeftBound(col - 1, row - 2) && isNotFriendly(col - 1, row - 2)) {
            addAllowedMove(col - 1, row - 2);
        }
        if (topRightBound(col + 1, row - 2) && isNotFriendly(col + 1, row - 2)) {
            addAllowedMove(col + 1, row - 2);
        }
        if (topRightBound(col + 2, row - 1) && isNotFriendly(col + 2, row - 1)) {
            addAllowedMove(col + 2, row - 1);
        }
        if (bottomRightBound(col + 2, row + 1) && isNotFriendly(col + 2, row + 1)) {
            addAllowedMove(col + 2, row + 1);
        }
        if (bottomRightBound(col + 1, row + 2) && isNotFriendly(col + 1, row + 2)) {
            addAllowedMove(col + 1, row + 2);
        }
        if (bottomLeftBound(col - 1, row + 2) && isNotFriendly(col - 1, row + 2)) {
            addAllowedMove(col - 1, row + 2);
        }
        if (bottomLeftBound(col - 2, row + 1) && isNotFriendly(col - 2, row + 1)) {
            addAllowedMove(col - 2, row + 1);
        }
        if (topLeftBound(col - 2, row - 1) && isNotFriendly(col - 2, row - 1)) {
            addAllowedMove(col - 2, row - 1);
        }
    }

    private void allowedMovesOfBishop(int col, int row) {
        checkDiagonalMovesByDirection(col, row, -1, -1, BoundaryValidators.TOP_LEFT);
        checkDiagonalMovesByDirection(col, row, -1, 1, BoundaryValidators.BOTTOM_LEFT);
        checkDiagonalMovesByDirection(col, row, 1, 1, BoundaryValidators.BOTTOM_RIGHT);
        checkDiagonalMovesByDirection(col, row, 1, -1, BoundaryValidators.TOP_RIGHT);
    }

    private void checkDiagonalMovesByDirection(int col, int row, int colMod, int rowMod, BoundaryValidator boundaryValidator) {
        for (col += colMod, row += rowMod; boundaryValidator.test(col, row); col += colMod, row += rowMod) {
            if (isNotFriendly(col, row)) {
                addAllowedMove(col, row);
            }
            if (!isEmpty(col, row)) {
                break;
            }
        }
    }

    private void allowedMovesOfQueen(int col, int row) {
        allowedMovesOfRook(col, row);
        allowedMovesOfBishop(col, row);
    }

    private void allowedMovesOfKing(int col, int row) {
        if (((col - 1) >= 0) && ((row - 1) >= 0) && (board.figureAt(col - 1, row - 1).color != owner.getMyColor()) && (kingSafeAt(col - 1, row - 1, owner.getOppColor()))) {
            addAllowedMove(col - 1, row - 1);
        }
        if (((col + 1) <= 7) && ((row - 1) >= 0) && (board.figureAt(col + 1, row - 1).color != owner.getMyColor()) && (kingSafeAt(col + 1, row - 1, owner.getOppColor()))) {
            addAllowedMove(col + 1, row - 1);
        }
        if (((col - 1) >= 0) && ((row + 1) <= 7) && (board.figureAt(col - 1, row + 1).color != owner.getMyColor()) && (kingSafeAt(col - 1, row + 1, owner.getOppColor()))) {
            addAllowedMove(col - 1, row + 1);
        }
        if (((col + 1) <= 7) && ((row + 1) <= 7) && (board.figureAt(col + 1, row + 1).color != owner.getMyColor()) && (kingSafeAt(col + 1, row + 1, owner.getOppColor()))) {
            addAllowedMove(col + 1, row + 1);
        }
        if (((row - 1) >= 0) && (board.figureAt(col, row - 1).color != owner.getMyColor()) && (kingSafeAt(col, row - 1, owner.getOppColor()))) {
            addAllowedMove(col, row - 1);
        }
        if (((row + 1) <= 7) && (board.figureAt(col, row + 1).color != owner.getMyColor()) && (kingSafeAt(col, row + 1, owner.getOppColor()))) {
            addAllowedMove(col, row + 1);
        }
        if (((col + 1) <= 7) && (board.figureAt(col + 1, row).color != owner.getMyColor()) && (kingSafeAt(col + 1, row, owner.getOppColor()))) {
            addAllowedMove(col + 1, row);
        }
        if (((col - 1) >= 0) && (board.figureAt(col - 1, row).color != owner.getMyColor()) && (kingSafeAt(col - 1, row, owner.getOppColor()))) {
            addAllowedMove(col - 1, row);
        }
    }

    public boolean kingSafeAt(int col, int row, int oppColor) {
        int i, j;

        // vertical-horizontal
        for (i = (col - 1); i >= 0; i--) {
            if ((board.figureAt(i, row).color == oppColor) && ((board.figureAt(i, row).type == ROOK) || (board.figureAt(i, row).type == QUEEN))) {
                attacker[0] = i;
                attacker[1] = row;
                return false;
            }
            if ((board.figureAt(i, row).color != NULL) && (i != col)) {
                break;
            }
        }
        for (i = (row - 1); i >= 0; i--) {
            if ((board.figureAt(col, i).color == oppColor) && ((board.figureAt(col, i).type == ROOK) || (board.figureAt(col, i).type == QUEEN))) {
                attacker[0] = col;
                attacker[1] = i;
                return false;
            }
            if ((board.figureAt(col, i).color != NULL) && (i != row)) {
                break;
            }
        }
        for (i = (col + 1); i <= 7; i++) {
            if ((board.figureAt(i, row).color == oppColor) && ((board.figureAt(i, row).type == ROOK) || (board.figureAt(i, row).type == QUEEN))) {
                attacker[0] = i;
                attacker[1] = row;
                return false;
            }
            if ((board.figureAt(i, row).color != NULL) && (i != col)) {
                break;
            }
        }
        for (i = (row + 1); i <= 7; i++) {
            if ((board.figureAt(col, i).color == oppColor) && ((board.figureAt(col, i).type == ROOK) || (board.figureAt(col, i).type == QUEEN))) {
                attacker[0] = col;
                attacker[1] = i;
                return false;
            }
            if ((board.figureAt(col, i).color != NULL) && (i != row)) {
                break;
            }
        }

        // diagonals
        j = row - 1;
        for (i = (col - 1); i >= 0; i--) {
            if (j < 0) {
                break;
            }
            if ((board.figureAt(i, j).color == oppColor) && ((board.figureAt(i, j).type == BISHOP) || (board.figureAt(i, j).type == QUEEN))) {
                attacker[0] = i;
                attacker[1] = j;
                return false;
            }
            if ((board.figureAt(i, j).color != NULL) && ((i != col) || (j != row))) {
                break;
            }
            j--;
        }
        j = row + 1;
        for (i = (col - 1); i >= 0; i--) {
            if (j > 7) {
                break;
            }
            if ((board.figureAt(i, j).color == oppColor) && ((board.figureAt(i, j).type == BISHOP) || (board.figureAt(i, j).type == QUEEN))) {
                attacker[0] = i;
                attacker[1] = j;
                return false;
            }
            if ((board.figureAt(i, j).color != NULL) && ((i != col) || (j != row))) {
                break;
            }
            j++;
        }
        j = row + 1;
        for (i = (col + 1); i <= 7; i++) {
            if (j > 7) {
                break;
            }
            if ((board.figureAt(i, j).color == oppColor) && ((board.figureAt(i, j).type == BISHOP) || (board.figureAt(i, j).type == QUEEN))) {
                attacker[0] = i;
                attacker[1] = j;
                return false;
            }
            if ((board.figureAt(i, j).color != NULL) && ((i != col) || (j != row))) {
                break;
            }
            j++;
        }
        j = row - 1;
        for (i = (col + 1); i <= 7; i++) {
            if (j < 0) {
                break;
            }
            if ((board.figureAt(i, j).color == oppColor) && ((board.figureAt(i, j).type == BISHOP) || (board.figureAt(i, j).type == QUEEN))) {
                attacker[0] = i;
                attacker[1] = j;
                return false;
            }
            if ((board.figureAt(i, j).color != NULL) && ((i != col) || (j != row))) {
                break;
            }
            j--;
        }

        // pawns in front of
        if ((row != 0) && (col != 0) && (board.figureAt(col - 1, row - 1).color == oppColor) && (board.figureAt(col - 1, row - 1).type == PAWN)) {
            attacker[0] = col - 1;
            attacker[1] = row - 1;
            return false;
        }
        if ((row != 0) && (col != 7) && (board.figureAt(col + 1, row - 1).color == oppColor) && (board.figureAt(col + 1, row - 1).type == PAWN)) {
            attacker[0] = col + 1;
            attacker[1] = row - 1;
            return false;
        }

        // knights
        if (((col - 1) >= 0) && ((row - 2) >= 0) && (board.figureAt(col - 1, row - 2).color == oppColor) && (board.figureAt(col - 1, row - 2).type == KNIGHT)) {
            attacker[0] = col - 1;
            attacker[1] = row - 2;
            return false;
        }
        if (((col + 1) <= 7) && ((row - 2) >= 0) && (board.figureAt(col + 1, row - 2).color == oppColor) && (board.figureAt(col + 1, row - 2).type == KNIGHT)) {
            attacker[0] = col + 1;
            attacker[1] = row - 2;
            return false;
        }
        if (((col + 2) <= 7) && ((row - 1) >= 0) && (board.figureAt(col + 2, row - 1).color == oppColor) && (board.figureAt(col + 2, row - 1).type == KNIGHT)) {
            attacker[0] = col + 2;
            attacker[1] = row - 1;
            return false;
        }
        if (((col + 2) <= 7) && ((row + 1) <= 7) && (board.figureAt(col + 2, row + 1).color == oppColor) && (board.figureAt(col + 2, row + 1).type == KNIGHT)) {
            attacker[0] = col + 2;
            attacker[1] = row + 1;
            return false;
        }
        if (((col + 1) <= 7) && ((row + 2) <= 7) && (board.figureAt(col + 1, row + 2).color == oppColor) && (board.figureAt(col + 1, row + 2).type == KNIGHT)) {
            attacker[0] = col + 1;
            attacker[1] = row + 2;
            return false;
        }
        if (((col - 1) >= 0) && ((row + 2) <= 7) && (board.figureAt(col - 1, row + 2).color == oppColor) && (board.figureAt(col - 1, row + 2).type == KNIGHT)) {
            attacker[0] = col - 1;
            attacker[1] = row + 2;
            return false;
        }
        if (((col - 2) >= 0) && ((row + 1) <= 7) && (board.figureAt(col - 2, row + 1).color == oppColor) && (board.figureAt(col - 2, row + 1).type == KNIGHT)) {
            attacker[0] = col - 2;
            attacker[1] = row + 1;
            return false;
        }
        if (((col - 2) >= 0) && ((row - 1) >= 0) && (board.figureAt(col - 2, row - 1).color == oppColor) && (board.figureAt(col - 2, row - 1).type == KNIGHT)) {
            attacker[0] = col - 2;
            attacker[1] = row - 1;
            return false;
        }

        // king
        if (((col - 1) >= 0) && ((row - 1) >= 0) && (board.figureAt(col - 1, row - 1).color == oppColor) && (board.figureAt(col - 1, row - 1).type == KING)) {
            return false;
        }
        if (((col + 1) <= 7) && ((row - 1) >= 0) && (board.figureAt(col + 1, row - 1).color == oppColor) && (board.figureAt(col + 1, row - 1).type == KING)) {
            return false;
        }
        if (((col - 1) >= 0) && ((row + 1) <= 7) && (board.figureAt(col - 1, row + 1).color == oppColor) && (board.figureAt(col - 1, row + 1).type == KING)) {
            return false;
        }
        if (((col + 1) <= 7) && ((row + 1) <= 7) && (board.figureAt(col + 1, row + 1).color == oppColor) && (board.figureAt(col + 1, row + 1).type == KING)) {
            return false;
        }
        if ((row - 1) >= 0 && (board.figureAt(col, row - 1).color == oppColor) && (board.figureAt(col, row - 1).type == KING)) {
            return false;
        }
        if ((row + 1) <= 7 && (board.figureAt(col, row + 1).color == oppColor) && (board.figureAt(col, row + 1).type == KING)) {
            return false;
        }
        if ((col + 1) <= 7 && (board.figureAt(col + 1, row).color == oppColor) && (board.figureAt(col + 1, row).type == KING)) {
            return false;
        }
        if ((col - 1) >= 0 && (board.figureAt(col - 1, row).color == oppColor) && (board.figureAt(col - 1, row).type == KING)) {
            return false;
        }

        // finally...
        return true;
    }

    private void addAllowedMove(int col, int row) {
        allowed[arrPos][0] = col;
        allowed[arrPos][1] = row;
        ++arrPos;
        allowed[arrPos][0] = -1;
    }

    private boolean isAllowed(int col, int row) {
        int i;
        for (i = 0; i < 100; i++) {
            if (allowed[i][0] == -1) {
                return false;
            }
            if ((allowed[i][0] == col) && (allowed[i][1] == row)) {
                return true;
            }
        }
        return false;
    }

    private void copy(Figure a, Figure b) {
        a.empty = b.empty;
        a.firstStep = b.firstStep;
        a.type = b.type;
        a.color = b.color;
        a.oX = b.oX;
        a.oY = b.oY;
    }

    public void drop(int col, int row) {
        int color = owner.getMyColor() / 2;
        boolean kingWasMoved = false;
        Figure f1 = new Figure();
        Figure f2 = new Figure();
        if (board.isFigureDroppedAtNewPosition(col, row)) {
            if (isAllowed(col, row)) {
                if ((board.draggedFigure().type == PAWN) && (row == 0)) {
                    board.draggedFigure().empty = true;
                    board.draggedFigure().type = NULL;
                    board.draggedFigure().color = NULL;
                    new Messenjah(board, owner.getMyColor(), col, row);
                } else {
                    copy(f1, board.figureAt(col, row));
                    copy(f2, board.draggedFigure());
                    board.figureAt(col, row).oX = col * 60;
                    board.figureAt(col, row).oY = row * 60;
                    board.figureAt(col, row).empty = false;
                    board.figureAt(col, row).firstStep = false;
                    board.figureAt(col, row).type = board.draggedFigure().type;
                    board.figureAt(col, row).color = board.draggedFigure().color;
                    board.draggedFigure().empty = true;
                    board.draggedFigure().type = NULL;
                    board.draggedFigure().color = NULL;
                    if (board.figureAt(col, row).type == KING) {
                        kingWasMoved = true;
                        board.moveKing(color, col, row);
                    }
                }
                if (!kingSafeAt(board.getKingCol(color), board.getKingRow(color), owner.getOppColor())) {
                    if (kingWasMoved) {
                        board.restoreKing(color);
                    }
                    copy(board.draggedFigure(), f2);
                    copy(board.figureAt(col, row), f1);
                    board.updateDraggedPosition();
                } else {
                    board.setCheck(false);
                    board.makeMove(col, row);
                }
            } else {
                board.updateDraggedPosition();
            }
        } else {
            board.draggedFigure().oX = col * 60;
            board.draggedFigure().oY = row * 60;
        }
    }

    public boolean kingSideCastlingAllowed() {
        if (owner.getMyColor() == WHITE) {
            return (board.figureAt(5, 7).empty) && (board.figureAt(6, 7).empty)
                    && (board.figureAt(7, 7).firstStep) && (board.figureAt(4, 7).firstStep)
                    && (!board.isCheck()) && (owner.isMyTurn());
        } else {
            return (board.figureAt(1, 7).empty) && (board.figureAt(2, 7).empty)
                    && (board.figureAt(0, 7).firstStep) && (board.figureAt(3, 7).firstStep)
                    && (!board.isCheck()) && (owner.isMyTurn());
        }
    }

    public boolean queenSideCastlingAllowed() {
        if (owner.getMyColor() == WHITE) {
            return (board.figureAt(1, 7).empty) && (board.figureAt(2, 7).empty) && (board.figureAt(3, 7).empty)
                    && (board.figureAt(0, 7).firstStep) && (board.figureAt(4, 7).firstStep)
                    && (!board.isCheck()) && (owner.isMyTurn());
        } else {
            return (board.figureAt(4, 7).empty) && (board.figureAt(5, 7).empty) && (board.figureAt(6, 7).empty)
                    && (board.figureAt(7, 7).firstStep) && (board.figureAt(3, 7).firstStep)
                    && (!board.isCheck()) && (owner.isMyTurn());
        }
    }

    public boolean mate(int col, int row, Figure[][] fig) {
        if (board.isCheck()) {
            calculateAllowedMoves(fig[col][row], col, row);
            if (allowed[0][0] == -1) {
                if (kingSafeAt(attacker[0], attacker[1], owner.getMyColor())) {
                    return !canCover(col, row, fig);
                }
            } else {
                int k, bck;
                for (k = 0; k < 8; k++) {
                    if (allowed[k][0] == -1) {
                        return !canCover(col, row, fig);
                    }
                    fig[col][row].color = NULL;
                    bck = fig[allowed[k][0]][allowed[k][1]].color;
                    fig[allowed[k][0]][allowed[k][1]].color = owner.getMyColor();
                    if (kingSafeAt(allowed[k][0], allowed[k][1], owner.getOppColor())) {
                        fig[allowed[k][0]][allowed[k][1]].color = bck;
                        fig[col][row].color = owner.getMyColor();
                        break;
                    }
                    fig[allowed[k][0]][allowed[k][1]].color = bck;
                    fig[col][row].color = owner.getMyColor();
                }
            }
        }
        return false;
    }

    private boolean canCover(int col, int row, Figure[][] fig) {
        int k, l, bck;
        for (k = 0; k <= 7; k++) {
            for (l = 0; l <= 7; l++) {
                if ((fig[k][l].color == owner.getMyColor()) && (fig[k][l].type != KING)) {
                    calculateAllowedMoves(fig[k][l], k, l);
                    int m = 0;
                    while (allowed[m][0] != -1) {
                        fig[k][l].color = NULL;
                        bck = fig[allowed[m][0]][allowed[m][1]].color;
                        fig[allowed[m][0]][allowed[m][1]].color = owner.getMyColor();
                        if (kingSafeAt(col, row, owner.getOppColor())) {
                            fig[allowed[m][0]][allowed[m][1]].color = bck;
                            fig[k][l].color = owner.getMyColor();
                            return true;
                        }
                        fig[allowed[m][0]][allowed[m][1]].color = bck;
                        fig[k][l].color = owner.getMyColor();
                        ++m;
                    }
                }
            }
        }
        return false;
    }

    protected int[][] getAllowed() {
        return allowed;
    }

    private boolean isEmpty(int col, int row) {
        return board.figureAt(col, row).empty;
    }

    private boolean isNotFriendly(int col, int row) {
        return board.figureAt(col, row).color != owner.getMyColor();
    }

    private boolean topLeftBound(int col, int row) {
        return col >= 0 && row >= 0;
    }

    private boolean bottomLeftBound(int col, int row) {
        return col >= 0 && row <= 7;
    }

    private boolean topRightBound(int col, int row) {
        return col <= 7 && row >= 0;
    }

    private boolean bottomRightBound(int col, int row) {
        return col <= 7 && row <= 7;
    }
}
