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

    public void calculateAllowedMoves(int col, int row) {
        calculating = true;
        switch (board.figureAt(col, row).type) {
            case PAWN:
                allowed[0][0] = -1;
                allowedMovesOfPawn(col, row);
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

    private void allowedMovesOfPawn(int col, int row) {
        boolean simpleMoveAdded = checkPawnFirstStepRule(col, row);
        if (!simpleMoveAdded) {
            checkPawnStepRule(col, row);
        }
        checkPawnAttack(col - 1, row - 1, BoundaryValidators.LEFT);
        checkPawnAttack(col + 1, row - 1, BoundaryValidators.RIGHT);
    }

    private boolean checkPawnFirstStepRule(int col, int row) {
        boolean conditionMet = board.figureAt(col, row).firstStep && isEmpty(col, row - 1) && isEmpty(col, row - 2);
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
        if (boundaryValidator.test(col, row) && isNotFriendly(col, row) && kingSafeAt(col, row, owner.getOppColor())) {
            addAllowedMove(col, row);
        }
    }

    public boolean kingSafeAt(int col, int row, int oppColor) {
        return kingIsSafeFromPoint(col - 1, row - 1, oppColor, PAWN, BoundaryValidators.TOP_LEFT)
                && kingIsSafeFromPoint(col + 1, row - 1, oppColor, PAWN, BoundaryValidators.TOP_RIGHT)

                && kingIsSafeFromPoint(col - 1, row - 2, oppColor, KNIGHT, BoundaryValidators.TOP_LEFT)
                && kingIsSafeFromPoint(col - 2, row - 1, oppColor, KNIGHT, BoundaryValidators.TOP_LEFT)
                && kingIsSafeFromPoint(col + 1, row - 2, oppColor, KNIGHT, BoundaryValidators.TOP_RIGHT)
                && kingIsSafeFromPoint(col + 2, row - 1, oppColor, KNIGHT, BoundaryValidators.TOP_RIGHT)
                && kingIsSafeFromPoint(col + 2, row + 1, oppColor, KNIGHT, BoundaryValidators.BOTTOM_RIGHT)
                && kingIsSafeFromPoint(col + 1, row + 2, oppColor, KNIGHT, BoundaryValidators.BOTTOM_RIGHT)
                && kingIsSafeFromPoint(col - 1, row + 2, oppColor, KNIGHT, BoundaryValidators.BOTTOM_LEFT)
                && kingIsSafeFromPoint(col - 2, row + 1, oppColor, KNIGHT, BoundaryValidators.BOTTOM_LEFT)

                && kingIsSafeFromPoint(col - 1, row - 1, oppColor, KING, BoundaryValidators.TOP_LEFT)
                && kingIsSafeFromPoint(col + 1, row - 1, oppColor, KING, BoundaryValidators.TOP_RIGHT)
                && kingIsSafeFromPoint(col - 1, row + 1, oppColor, KING, BoundaryValidators.BOTTOM_LEFT)
                && kingIsSafeFromPoint(col + 1, row + 1, oppColor, KING, BoundaryValidators.BOTTOM_RIGHT)
                && kingIsSafeFromPoint(col, row - 1, oppColor, KING, BoundaryValidators.TOP)
                && kingIsSafeFromPoint(col, row + 1, oppColor, KING, BoundaryValidators.BOTTOM)
                && kingIsSafeFromPoint(col + 1, row, oppColor, KING, BoundaryValidators.RIGHT)
                && kingIsSafeFromPoint(col - 1, row, oppColor, KING, BoundaryValidators.LEFT)

                && kingIsSafeFromDirection(col, row, -1, 0, oppColor, ROOK, BoundaryValidators.LEFT)
                && kingIsSafeFromDirection(col, row, 1, 0, oppColor, ROOK, BoundaryValidators.RIGHT)
                && kingIsSafeFromDirection(col, row, 0, -1, oppColor, ROOK, BoundaryValidators.TOP)
                && kingIsSafeFromDirection(col, row, 0, 1, oppColor, ROOK, BoundaryValidators.BOTTOM)

                && kingIsSafeFromDirection(col, row, -1, -1, oppColor, BISHOP, BoundaryValidators.TOP_LEFT)
                && kingIsSafeFromDirection(col, row, 1, -1, oppColor, BISHOP, BoundaryValidators.TOP_RIGHT)
                && kingIsSafeFromDirection(col, row, -1, 1, oppColor, BISHOP, BoundaryValidators.BOTTOM_LEFT)
                && kingIsSafeFromDirection(col, row, 1, 1, oppColor, BISHOP, BoundaryValidators.BOTTOM_RIGHT);
    }

    private boolean kingIsSafeFromPoint(int col, int row, int oppColor, int possibleAttacker, BoundaryValidator boundaryValidator) {
        if (boundaryValidator.test(col, row) && board.figureAt(col, row).color == oppColor
                && board.figureAt(col, row).type == possibleAttacker) {
            saveAttackerPosition(col, row);
            return false;
        } else {
            return true;
        }
    }

    private boolean kingIsSafeFromDirection(int col, int row, int colMod, int rowMod, int oppColor, int possibleAttacker, BoundaryValidator boundaryValidator) {
        for (col += colMod, row += rowMod; boundaryValidator.test(col, row); col += colMod, row += rowMod) {
            if (board.figureAt(col, row).color == oppColor
                    && ((board.figureAt(col, row).type == possibleAttacker) || (board.figureAt(col, row).type == QUEEN))) {
                saveAttackerPosition(col, row);
                return false;
            }
            if (board.figureAt(col, row).color == owner.getMyColor() && board.figureAt(col, row).type == KING) {
                continue;
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

    public void drop(int col, int row) {
        int colorId = owner.getMyColor() / 2;
        Figure targetFigure = new Figure();
        Figure sourceFigure = new Figure();
        if (isAllowed(col, row)) {
            copy(board.figureAt(col, row), targetFigure);
            copy(board.draggedFigure(), sourceFigure);
            moveAndClear(board.draggedFigure(), col, row);
            if (sourceFigure.type == KING) {
                board.moveKing(colorId, col, row);
                board.setCheck(false);
                board.makeMove(col, row);

            } else if (kingSafeAt(board.getKingCol(colorId), board.getKingRow(colorId), owner.getOppColor())) {
                if (sourceFigure.type == PAWN && row == 0) {
                    figurePicker.open(board, owner.getMyColor(), col, row);
                }
                board.setCheck(false);
                board.makeMove(col, row);

            } else {
                copy(sourceFigure, board.draggedFigure());
                copy(targetFigure, board.figureAt(col, row));
                board.updateDraggedPosition();
            }
        } else {
            board.updateDraggedPosition();
        }
    }

    private void copy(Figure from, Figure to) {
        to.empty = from.empty;
        to.firstStep = from.firstStep;
        to.type = from.type;
        to.color = from.color;
        to.oX = from.oX;
        to.oY = from.oY;
    }

    private void moveAndClear(Figure figure, int col, int row) {
        board.figureAt(col, row).oX = col * 60;
        board.figureAt(col, row).oY = row * 60;
        board.figureAt(col, row).empty = false;
        board.figureAt(col, row).firstStep = false;
        board.figureAt(col, row).type = figure.type;
        board.figureAt(col, row).color = figure.color;
        
        figure.empty = true;
        figure.type = NULL;
        figure.color = NULL;
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
                && kingSafeAt(kingCol + colMod, 7, owner.getOppColor())
                && kingSafeAt(kingCol + colMod * 2, 7, owner.getOppColor());
    }

    public boolean mate(int col, int row) {
        if (board.isCheck()) {
            calculateAllowedMoves(col, row);
            if (allowed[0][0] == -1) {
                return !canCover(col, row);
            }
        }
        return false;
    }

    private boolean canCover(int kingCol, int kingRow) {
        Figure currentFigure;
        Figure sourceFigure = new Figure();
        Figure targetFigure = new Figure();
        for (int col = 0; col <= 7; col++) {
            for (int row = 0; row <= 7; row++) {
                currentFigure = board.figureAt(col, row);
                if (currentFigure.color == owner.getMyColor() && currentFigure.type != KING) {
                    calculateAllowedMoves(col, row);
                    int idx = -1;
                    int targetCol, targetRow;
                    while (allowed[++idx][0] != -1) {
                        targetCol = allowed[idx][0];
                        targetRow = allowed[idx][1];
                        copy(currentFigure, sourceFigure);
                        copy(board.figureAt(targetCol, targetRow), targetFigure);
                        moveAndClear(currentFigure, targetCol, targetRow);
                        if (kingSafeAt(kingCol, kingRow, owner.getOppColor())) {
                            copy(sourceFigure, currentFigure);
                            copy(targetFigure, board.figureAt(targetCol, targetRow));
                            return true;
                        }
                        copy(sourceFigure, currentFigure);
                        copy(targetFigure, board.figureAt(targetCol, targetRow));
                    }
                }
            }
        }
        return false;
    }

    public int[][] getAllowed() {
        return allowed;
    }
    
    public int[] getAttacker() {
        return attacker;
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
