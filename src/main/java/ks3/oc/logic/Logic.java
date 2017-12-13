package ks3.oc.logic;

import ks3.oc.Figure;
import ks3.oc.MainWindow;
import ks3.oc.Protocol;
import ks3.oc.board.BoardState;
import ks3.oc.dialogs.FigurePicker;

public class Logic implements Protocol {

    private int[][] allowed = new int[100][2];
    private int arrPos = 0;
    public boolean calculating = false;
    private BoardState board;
    private MainWindow owner;
    private FigurePicker figurePicker;
    private int[] attacker = new int[2];

    public Logic(BoardState board, MainWindow owner, FigurePicker figurePicker) {
        this.board = board;
        this.owner = owner;
        this.figurePicker = figurePicker;
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
        checkPawnAttack(col - 1, row - 1, BoundaryValidators.LEFT);
        checkPawnAttack(col + 1, row - 1, BoundaryValidators.RIGHT);
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

    private void checkPawnAttack(int col, int row, BoundaryValidator boundaryValidator) {
        if (boundaryValidator.test(col, row) && !isEmpty(col, row) && isNotFriendly(col, row)) {
            addAllowedMove(col, row);
        }
    }

    private void allowedMovesOfRook(int col, int row) {
        checkAndAddAllowedMovesInDirection(col, row, -1, 0, BoundaryValidators.LEFT);
        checkAndAddAllowedMovesInDirection(col, row, 0, -1, BoundaryValidators.TOP);
        checkAndAddAllowedMovesInDirection(col, row, 1, 0, BoundaryValidators.RIGHT);
        checkAndAddAllowedMovesInDirection(col, row, 0, 1, BoundaryValidators.BOTTOM);
    }

    private void allowedMovesOfKnight(int col, int row) {
        checkAndAddAllowedKnightMove(col - 1, row - 2, BoundaryValidators.TOP_LEFT);
        checkAndAddAllowedKnightMove(col - 2, row - 1, BoundaryValidators.TOP_LEFT);
        checkAndAddAllowedKnightMove(col + 1, row - 2, BoundaryValidators.TOP_RIGHT);
        checkAndAddAllowedKnightMove(col + 2, row - 1, BoundaryValidators.TOP_RIGHT);
        checkAndAddAllowedKnightMove(col + 2, row + 1, BoundaryValidators.BOTTOM_RIGHT);
        checkAndAddAllowedKnightMove(col + 1, row + 2, BoundaryValidators.BOTTOM_RIGHT);
        checkAndAddAllowedKnightMove(col - 1, row + 2, BoundaryValidators.BOTTOM_LEFT);
        checkAndAddAllowedKnightMove(col - 2, row + 1, BoundaryValidators.BOTTOM_LEFT);
    }

    private void checkAndAddAllowedKnightMove(int col, int row, BoundaryValidator boundaryValidator) {
        if (boundaryValidator.test(col, row) && isNotFriendly(col, row)) {
            addAllowedMove(col, row);
        }
    }

    private void allowedMovesOfBishop(int col, int row) {
        checkAndAddAllowedMovesInDirection(col, row, -1, -1, BoundaryValidators.TOP_LEFT);
        checkAndAddAllowedMovesInDirection(col, row, -1, 1, BoundaryValidators.BOTTOM_LEFT);
        checkAndAddAllowedMovesInDirection(col, row, 1, 1, BoundaryValidators.BOTTOM_RIGHT);
        checkAndAddAllowedMovesInDirection(col, row, 1, -1, BoundaryValidators.TOP_RIGHT);
    }

    private void allowedMovesOfQueen(int col, int row) {
        allowedMovesOfRook(col, row);
        allowedMovesOfBishop(col, row);
    }

    private void allowedMovesOfKing(int col, int row) {
        checkAndAddAllowedKingMove(col - 1, row - 1, BoundaryValidators.TOP_LEFT);
        checkAndAddAllowedKingMove(col + 1, row - 1, BoundaryValidators.TOP_RIGHT);
        checkAndAddAllowedKingMove(col - 1, row + 1, BoundaryValidators.BOTTOM_LEFT);
        checkAndAddAllowedKingMove(col + 1, row + 1, BoundaryValidators.BOTTOM_RIGHT);
        checkAndAddAllowedKingMove(col, row - 1, BoundaryValidators.TOP);
        checkAndAddAllowedKingMove(col, row + 1, BoundaryValidators.BOTTOM);
        checkAndAddAllowedKingMove(col + 1, row, BoundaryValidators.RIGHT);
        checkAndAddAllowedKingMove(col - 1, row, BoundaryValidators.LEFT);
    }

    private void checkAndAddAllowedKingMove(int col, int row, BoundaryValidator boundaryValidator) {
        if (boundaryValidator.test(col, row) && isNotFriendly(col, row) && kingSafeAt(col, row)) {
            addAllowedMove(col, row);
        }
    }

    public boolean kingSafeAt(int col, int row) {
        return kingIsSafeFromPoint(col - 1, row - 1, PAWN, BoundaryValidators.TOP_LEFT)
                && kingIsSafeFromPoint(col + 1, row - 1, PAWN, BoundaryValidators.TOP_RIGHT)

                && kingIsSafeFromPoint(col - 1, row - 2, KNIGHT, BoundaryValidators.TOP_LEFT)
                && kingIsSafeFromPoint(col - 2, row - 1, KNIGHT, BoundaryValidators.TOP_LEFT)
                && kingIsSafeFromPoint(col + 1, row - 2, KNIGHT, BoundaryValidators.TOP_RIGHT)
                && kingIsSafeFromPoint(col + 2, row - 1, KNIGHT, BoundaryValidators.TOP_RIGHT)
                && kingIsSafeFromPoint(col + 2, row + 1, KNIGHT, BoundaryValidators.BOTTOM_RIGHT)
                && kingIsSafeFromPoint(col + 1, row + 2, KNIGHT, BoundaryValidators.BOTTOM_RIGHT)
                && kingIsSafeFromPoint(col - 1, row + 2, KNIGHT, BoundaryValidators.BOTTOM_LEFT)
                && kingIsSafeFromPoint(col - 2, row + 1, KNIGHT, BoundaryValidators.BOTTOM_LEFT)

                && kingIsSafeFromPoint(col - 1, row - 1, KING, BoundaryValidators.TOP_LEFT)
                && kingIsSafeFromPoint(col + 1, row - 1, KING, BoundaryValidators.TOP_RIGHT)
                && kingIsSafeFromPoint(col - 1, row + 1, KING, BoundaryValidators.BOTTOM_LEFT)
                && kingIsSafeFromPoint(col + 1, row + 1, KING, BoundaryValidators.BOTTOM_RIGHT)
                && kingIsSafeFromPoint(col, row - 1, KING, BoundaryValidators.TOP)
                && kingIsSafeFromPoint(col, row + 1, KING, BoundaryValidators.BOTTOM)
                && kingIsSafeFromPoint(col + 1, row, KING, BoundaryValidators.RIGHT)
                && kingIsSafeFromPoint(col - 1, row, KING, BoundaryValidators.LEFT)

                && kingIsSafeFromDirection(col, row, -1, 0, ROOK, BoundaryValidators.LEFT)
                && kingIsSafeFromDirection(col, row, 1, 0, ROOK, BoundaryValidators.RIGHT)
                && kingIsSafeFromDirection(col, row, 0, -1, ROOK, BoundaryValidators.TOP)
                && kingIsSafeFromDirection(col, row, 0, 1, ROOK, BoundaryValidators.BOTTOM)

                && kingIsSafeFromDirection(col, row, -1, -1, BISHOP, BoundaryValidators.TOP_LEFT)
                && kingIsSafeFromDirection(col, row, 1, -1, BISHOP, BoundaryValidators.TOP_RIGHT)
                && kingIsSafeFromDirection(col, row, -1, 1, BISHOP, BoundaryValidators.BOTTOM_LEFT)
                && kingIsSafeFromDirection(col, row, 1, 1, BISHOP, BoundaryValidators.BOTTOM_RIGHT);
    }

    private boolean kingIsSafeFromPoint(int col, int row, int possibleAttacker, BoundaryValidator boundaryValidator) {
        if (boundaryValidator.test(col, row) && isNotFriendly(col, row) && board.figureAt(col, row).type == possibleAttacker) {
            saveAttackerPosition(col, row);
            return false;
        } else {
            return true;
        }
    }

    private boolean kingIsSafeFromDirection(int col, int row, int colMod, int rowMod, int possibleAttacker, BoundaryValidator boundaryValidator) {
        for (col += colMod, row += rowMod; boundaryValidator.test(col, row); col += colMod, row += rowMod) {
            if (isNotFriendly(col, row) && ((board.figureAt(col, row).type == possibleAttacker) || (board.figureAt(col, row).type == QUEEN))) {
                saveAttackerPosition(col, row);
                return false;
            }
            if (!isEmpty(col, row)) {
                break;
            }
        }
        return true;
    }

    private void saveAttackerPosition(int col, int row) {
        attacker[0] = col;
        attacker[1] = row;
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

    private void copy(Figure to, Figure from) {
        to.empty = from.empty;
        to.firstStep = from.firstStep;
        to.type = from.type;
        to.color = from.color;
        to.oX = from.oX;
        to.oY = from.oY;
    }

    public void drop(int col, int row) {
        int colorId = owner.getMyColor() / 2;
        Figure targetBackup = new Figure();
        Figure sourceBackup = new Figure();
        if (isAllowed(col, row)) {
            if ((board.draggedFigure().type == PAWN) && (row == 0)) {
                clearSourcePosition();
                figurePicker.open(board, owner.getMyColor(), col, row);
            } else {
                copy(targetBackup, board.figureAt(col, row));
                copy(sourceBackup, board.draggedFigure());
                moveToTargetPosition(col, row);
                clearSourcePosition();
            }
            if (kingSafeAt(board.getKingCol(colorId), board.getKingRow(colorId))) {
                board.setCheck(false);
                board.makeMove(col, row);
            } else {
                copy(board.draggedFigure(), sourceBackup);
                copy(board.figureAt(col, row), targetBackup);
                board.updateDraggedPosition();
            }
        } else {
            board.updateDraggedPosition();
        }
    }

    private void moveToTargetPosition(int col, int row) {
        board.figureAt(col, row).oX = col * 60;
        board.figureAt(col, row).oY = row * 60;
        board.figureAt(col, row).empty = false;
        board.figureAt(col, row).firstStep = false;
        board.figureAt(col, row).type = board.draggedFigure().type;
        board.figureAt(col, row).color = board.draggedFigure().color;
    }

    private void clearSourcePosition() {
        board.draggedFigure().empty = true;
        board.draggedFigure().type = NULL;
        board.draggedFigure().color = NULL;
    }

    public boolean kingSideCastlingAllowed() {
        if (owner.getMyColor() == WHITE) {
            return castlingAllowed(7, 4, -1);
        } else {
            return castlingAllowed(0, 3, 1);
        }
    }

    public boolean queenSideCastlingAllowed() {
        if (owner.getMyColor() == WHITE) {
            return castlingAllowed(0, 4, 1);
        } else {
            return castlingAllowed(7, 3, -1);
        }
    }

    private boolean castlingAllowed(int rookCol, int kingCol, int colMod) {
        if (!owner.isMyTurn() || board.isCheck()) {
            return false;
        }
        for (int col = rookCol + colMod; col != kingCol; col += colMod) {
            if (!isEmpty(col, 7)) {
                return false;
            }
        }
        return !isEmpty(rookCol, 7) && board.figureAt(rookCol, 7).firstStep
                && !isEmpty(kingCol, 7) && board.figureAt(kingCol, 7).firstStep
                && kingSafeAt(kingCol + colMod, 7) && kingSafeAt(kingCol + colMod * 2, 7);
    }

    public boolean mate(int col, int row, Figure[][] fig) {
        if (board.isCheck()) {
            calculateAllowedMoves(fig[col][row], col, row);
            if (allowed[0][0] == -1) {
                if (kingSafeAt(attacker[0], attacker[1])) {
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
                    if (kingSafeAt(allowed[k][0], allowed[k][1])) {
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
                        if (kingSafeAt(col, row)) {
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

    private void checkAndAddAllowedMovesInDirection(int col, int row, int colMod, int rowMod, BoundaryValidator boundaryValidator) {
        for (col += colMod, row += rowMod; boundaryValidator.test(col, row); col += colMod, row += rowMod) {
            if (isNotFriendly(col, row)) {
                addAllowedMove(col, row);
            }
            if (!isEmpty(col, row)) {
                break;
            }
        }
    }
}
