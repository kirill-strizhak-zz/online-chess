package ks3.oc;

import ks3.oc.board.Board;

public class Logic implements Protocol {

    private int[][] allowed = new int[100][2];
    private int arrPos = 0;
    public boolean calculating = false;
    private Board board;
    private MainWindow owner;
    private int[] attacker = new int[2];

    public Logic(Board brd, MainWindow own) {
        board = brd;
        owner = own;
        allowed[0][0] = -1;
    }

    public void calculateAllowedMoves(Figure figure, int x, int y) {
        calculating = true;
        switch (figure.type) {
            case PAWN:
                allowed[0][0] = -1;
                allowedMovesOfPawn(figure, x, y);
                arrPos = 0;
                break;
            case ROOK:
                allowed[0][0] = -1;
                allowedMovesOfRook(x, y);
                arrPos = 0;
                break;
            case KNIGHT:
                allowed[0][0] = -1;
                allowedMovesOfKnight(x, y);
                arrPos = 0;
                break;
            case BISHOP:
                allowed[0][0] = -1;
                allowedMovesOfBishop(x, y);
                arrPos = 0;
                break;
            case QUEEN:
                allowed[0][0] = -1;
                allowedMovesOfQueen(x, y);
                arrPos = 0;
                break;
            case KING:
                allowed[0][0] = -1;
                allowedMovesOfKing(x, y);
                arrPos = 0;
                break;
        }
        calculating = false;
    }

    private void allowedMovesOfPawn(Figure figure, int x, int y) {
        if ((figure.firstStep) && (board.fig[x][y - 2].empty)) {
            addAllowedMove(x, y - 2);
        }
        if ((x != 0) && (!board.fig[x - 1][y - 1].empty)) {
            addAllowedMove(x - 1, y - 1);
        }
        if ((x != 7) && (!board.fig[x + 1][y - 1].empty)) {
            addAllowedMove(x + 1, y - 1);
        }
        if ((y != 0) && (board.fig[x][y - 1].empty)) {
            addAllowedMove(x, y - 1);
        }
    }

    private void allowedMovesOfRook(int x, int y) {
        int i;
        for (i = x; i >= 0; i--) {
            if (board.fig[i][y].color != owner.getMyColor()) {
                addAllowedMove(i, y);
            }
            if ((board.fig[i][y].color != NULL) && (i != x)) {
                break;
            }
        }
        for (i = y; i >= 0; i--) {
            if (board.fig[x][i].color != owner.getMyColor()) {
                addAllowedMove(x, i);
            }
            if ((board.fig[x][i].color != NULL) && (i != y)) {
                break;
            }
        }
        for (i = x; i <= 7; i++) {
            if (board.fig[i][y].color != owner.getMyColor()) {
                addAllowedMove(i, y);
            }
            if ((board.fig[i][y].color != NULL) && (i != x)) {
                break;
            }
        }
        for (i = y; i <= 7; i++) {
            if (board.fig[x][i].color != owner.getMyColor()) {
                addAllowedMove(x, i);
            }
            if ((board.fig[x][i].color != NULL) && (i != y)) {
                break;
            }
        }
    }

    private void allowedMovesOfKnight(int x, int y) {
        if (((x - 1) >= 0) && ((y - 2) >= 0)) {
            addAllowedMove(x - 1, y - 2);
        }
        if (((x + 1) <= 7) && ((y - 2) >= 0)) {
            addAllowedMove(x + 1, y - 2);
        }
        if (((x + 2) <= 7) && ((y - 1) >= 0)) {
            addAllowedMove(x + 2, y - 1);
        }
        if (((x + 2) <= 7) && ((y + 1) <= 7)) {
            addAllowedMove(x + 2, y + 1);
        }
        if (((x + 1) <= 7) && ((y + 2) <= 7)) {
            addAllowedMove(x + 1, y + 2);
        }
        if (((x - 1) >= 0) && ((y + 2) <= 7)) {
            addAllowedMove(x - 1, y + 2);
        }
        if (((x - 2) >= 0) && ((y + 1) <= 7)) {
            addAllowedMove(x - 2, y + 1);
        }
        if (((x - 2) >= 0) && ((y - 1) >= 0)) {
            addAllowedMove(x - 2, y - 1);
        }
    }

    private void allowedMovesOfBishop(int x, int y) {
        int i, j;
        j = y;
        for (i = x; i >= 0; i--) {
            if (board.fig[i][j].color != owner.getMyColor()) {
                addAllowedMove(i, j);
            }
            if ((board.fig[i][j].color != NULL) && ((i != x) || (j != y))) {
                break;
            }
            j--;
            if (j < 0) {
                break;
            }
        }
        j = y;
        for (i = x; i >= 0; i--) {
            if (board.fig[i][j].color != owner.getMyColor()) {
                addAllowedMove(i, j);
            }
            if ((board.fig[i][j].color != NULL) && ((i != x) || (j != y))) {
                break;
            }
            j++;
            if (j > 7) {
                break;
            }
        }
        j = y;
        for (i = x; i <= 7; i++) {
            if (board.fig[i][j].color != owner.getMyColor()) {
                addAllowedMove(i, j);
            }
            if ((board.fig[i][j].color != NULL) && ((i != x) || (j != y))) {
                break;
            }
            j++;
            if (j > 7) {
                break;
            }
        }
        j = y;
        for (i = x; i <= 7; i++) {
            if (board.fig[i][j].color != owner.getMyColor()) {
                addAllowedMove(i, j);
            }
            if ((board.fig[i][j].color != NULL) && ((i != x) || (j != y))) {
                break;
            }
            j--;
            if (j < 0) {
                break;
            }
        }
    }

    private void allowedMovesOfQueen(int x, int y) {
        allowedMovesOfRook(x, y);
        allowedMovesOfBishop(x, y);
    }

    private void allowedMovesOfKing(int x, int y) {
        if (((x - 1) >= 0) && ((y - 1) >= 0) && (board.fig[x - 1][y - 1].color != owner.getMyColor()) && (kingSafeAt(x - 1, y - 1, owner.getOppColor()))) {
            addAllowedMove(x - 1, y - 1);
        }
        if (((x + 1) <= 7) && ((y - 1) >= 0) && (board.fig[x + 1][y - 1].color != owner.getMyColor()) && (kingSafeAt(x + 1, y - 1, owner.getOppColor()))) {
            addAllowedMove(x + 1, y - 1);
        }
        if (((x - 1) >= 0) && ((y + 1) <= 7) && (board.fig[x - 1][y + 1].color != owner.getMyColor()) && (kingSafeAt(x - 1, y + 1, owner.getOppColor()))) {
            addAllowedMove(x - 1, y + 1);
        }
        if (((x + 1) <= 7) && ((y + 1) <= 7) && (board.fig[x + 1][y + 1].color != owner.getMyColor()) && (kingSafeAt(x + 1, y + 1, owner.getOppColor()))) {
            addAllowedMove(x + 1, y + 1);
        }
        if (((y - 1) >= 0) && (board.fig[x][y - 1].color != owner.getMyColor()) && (kingSafeAt(x, y - 1, owner.getOppColor()))) {
            addAllowedMove(x, y - 1);
        }
        if (((y + 1) <= 7) && (board.fig[x][y + 1].color != owner.getMyColor()) && (kingSafeAt(x, y + 1, owner.getOppColor()))) {
            addAllowedMove(x, y + 1);
        }
        if (((x + 1) <= 7) && (board.fig[x + 1][y].color != owner.getMyColor()) && (kingSafeAt(x + 1, y, owner.getOppColor()))) {
            addAllowedMove(x + 1, y);
        }
        if (((x - 1) >= 0) && (board.fig[x - 1][y].color != owner.getMyColor()) && (kingSafeAt(x - 1, y, owner.getOppColor()))) {
            addAllowedMove(x - 1, y);
        }
    }

    public boolean kingSafeAt(int x, int y, int oppColor) {
        int i, j;

        // vertical-horizontal
        for (i = (x - 1); i >= 0; i--) {
            if ((board.fig[i][y].color == oppColor) && ((board.fig[i][y].type == ROOK) || (board.fig[i][y].type == QUEEN))) {
                attacker[0] = i;
                attacker[1] = y;
                return false;
            }
            if ((board.fig[i][y].color != NULL) && (i != x)) {
                break;
            }
        }
        for (i = (y - 1); i >= 0; i--) {
            if ((board.fig[x][i].color == oppColor) && ((board.fig[x][i].type == ROOK) || (board.fig[x][i].type == QUEEN))) {
                attacker[0] = x;
                attacker[1] = i;
                return false;
            }
            if ((board.fig[x][i].color != NULL) && (i != y)) {
                break;
            }
        }
        for (i = (x + 1); i <= 7; i++) {
            if ((board.fig[i][y].color == oppColor) && ((board.fig[i][y].type == ROOK) || (board.fig[i][y].type == QUEEN))) {
                attacker[0] = i;
                attacker[1] = y;
                return false;
            }
            if ((board.fig[i][y].color != NULL) && (i != x)) {
                break;
            }
        }
        for (i = (y + 1); i <= 7; i++) {
            if ((board.fig[x][i].color == oppColor) && ((board.fig[x][i].type == ROOK) || (board.fig[x][i].type == QUEEN))) {
                attacker[0] = x;
                attacker[1] = i;
                return false;
            }
            if ((board.fig[x][i].color != NULL) && (i != y)) {
                break;
            }
        }

        // diagonals
        j = y - 1;
        for (i = (x - 1); i >= 0; i--) {
            if (j < 0) {
                break;
            }
            if ((board.fig[i][j].color == oppColor) && ((board.fig[i][j].type == BISHOP) || (board.fig[i][j].type == QUEEN))) {
                attacker[0] = i;
                attacker[1] = j;
                return false;
            }
            if ((board.fig[i][j].color != NULL) && ((i != x) || (j != y))) {
                break;
            }
            j--;
        }
        j = y + 1;
        for (i = (x - 1); i >= 0; i--) {
            if (j > 7) {
                break;
            }
            if ((board.fig[i][j].color == oppColor) && ((board.fig[i][j].type == BISHOP) || (board.fig[i][j].type == QUEEN))) {
                attacker[0] = i;
                attacker[1] = j;
                return false;
            }
            if ((board.fig[i][j].color != NULL) && ((i != x) || (j != y))) {
                break;
            }
            j++;
        }
        j = y + 1;
        for (i = (x + 1); i <= 7; i++) {
            if (j > 7) {
                break;
            }
            if ((board.fig[i][j].color == oppColor) && ((board.fig[i][j].type == BISHOP) || (board.fig[i][j].type == QUEEN))) {
                attacker[0] = i;
                attacker[1] = j;
                return false;
            }
            if ((board.fig[i][j].color != NULL) && ((i != x) || (j != y))) {
                break;
            }
            j++;
        }
        j = y - 1;
        for (i = (x + 1); i <= 7; i++) {
            if (j < 0) {
                break;
            }
            if ((board.fig[i][j].color == oppColor) && ((board.fig[i][j].type == BISHOP) || (board.fig[i][j].type == QUEEN))) {
                attacker[0] = i;
                attacker[1] = j;
                return false;
            }
            if ((board.fig[i][j].color != NULL) && ((i != x) || (j != y))) {
                break;
            }
            j--;
        }

        // pawns in front of
        if ((y != 0) && (x != 0) && (board.fig[x - 1][y - 1].color == oppColor) && (board.fig[x - 1][y - 1].type == PAWN)) {
            attacker[0] = x - 1;
            attacker[1] = y - 1;
            return false;
        }
        if ((y != 0) && (x != 7) && (board.fig[x + 1][y - 1].color == oppColor) && (board.fig[x + 1][y - 1].type == PAWN)) {
            attacker[0] = x + 1;
            attacker[1] = y - 1;
            return false;
        }

        // knights
        if (((x - 1) >= 0) && ((y - 2) >= 0) && (board.fig[x - 1][y - 2].color == oppColor) && (board.fig[x - 1][y - 2].type == KNIGHT)) {
            attacker[0] = x - 1;
            attacker[1] = y - 2;
            return false;
        }
        if (((x + 1) <= 7) && ((y - 2) >= 0) && (board.fig[x + 1][y - 2].color == oppColor) && (board.fig[x + 1][y - 2].type == KNIGHT)) {
            attacker[0] = x + 1;
            attacker[1] = y - 2;
            return false;
        }
        if (((x + 2) <= 7) && ((y - 1) >= 0) && (board.fig[x + 2][y - 1].color == oppColor) && (board.fig[x + 2][y - 1].type == KNIGHT)) {
            attacker[0] = x + 2;
            attacker[1] = y - 1;
            return false;
        }
        if (((x + 2) <= 7) && ((y + 1) <= 7) && (board.fig[x + 2][y + 1].color == oppColor) && (board.fig[x + 2][y + 1].type == KNIGHT)) {
            attacker[0] = x + 2;
            attacker[1] = y + 1;
            return false;
        }
        if (((x + 1) <= 7) && ((y + 2) <= 7) && (board.fig[x + 1][y + 2].color == oppColor) && (board.fig[x + 1][y + 2].type == KNIGHT)) {
            attacker[0] = x + 1;
            attacker[1] = y + 2;
            return false;
        }
        if (((x - 1) >= 0) && ((y + 2) <= 7) && (board.fig[x - 1][y + 2].color == oppColor) && (board.fig[x - 1][y + 2].type == KNIGHT)) {
            attacker[0] = x - 1;
            attacker[1] = y + 2;
            return false;
        }
        if (((x - 2) >= 0) && ((y + 1) <= 7) && (board.fig[x - 2][y + 1].color == oppColor) && (board.fig[x - 2][y + 1].type == KNIGHT)) {
            attacker[0] = x - 2;
            attacker[1] = y + 1;
            return false;
        }
        if (((x - 2) >= 0) && ((y - 1) >= 0) && (board.fig[x - 2][y - 1].color == oppColor) && (board.fig[x - 2][y - 1].type == KNIGHT)) {
            attacker[0] = x - 2;
            attacker[1] = y - 1;
            return false;
        }

        // king
        if (((x - 1) >= 0) && ((y - 1) >= 0) && (board.fig[x - 1][y - 1].color == oppColor) && (board.fig[x - 1][y - 1].type == KING)) {
            return false;
        }
        if (((x + 1) <= 7) && ((y - 1) >= 0) && (board.fig[x + 1][y - 1].color == oppColor) && (board.fig[x + 1][y - 1].type == KING)) {
            return false;
        }
        if (((x - 1) >= 0) && ((y + 1) <= 7) && (board.fig[x - 1][y + 1].color == oppColor) && (board.fig[x - 1][y + 1].type == KING)) {
            return false;
        }
        if (((x + 1) <= 7) && ((y + 1) <= 7) && (board.fig[x + 1][y + 1].color == oppColor) && (board.fig[x + 1][y + 1].type == KING)) {
            return false;
        }
        if ((y - 1) >= 0 && (board.fig[x][y - 1].color == oppColor) && (board.fig[x][y - 1].type == KING)) {
            return false;
        }
        if ((y + 1) <= 7 && (board.fig[x][y + 1].color == oppColor) && (board.fig[x][y + 1].type == KING)) {
            return false;
        }
        if ((x + 1) <= 7 && (board.fig[x + 1][y].color == oppColor) && (board.fig[x + 1][y].type == KING)) {
            return false;
        }
        if ((x - 1) >= 0 && (board.fig[x - 1][y].color == oppColor) && (board.fig[x - 1][y].type == KING)) {
            return false;
        }

        // finally...
        return true;
    }

    public void addAllowedMove(int x, int y) {
        allowed[arrPos][0] = x;
        allowed[arrPos][1] = y;
        ++arrPos;
        allowed[arrPos][0] = -1;
    }

    public boolean isAllowed(int x, int y) {
        int i;
        for (i = 0; i < 100; i++) {
            if (allowed[i][0] == -1) {
                return false;
            }
            if ((allowed[i][0] == x) && (allowed[i][1] == y)) {
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

    public void drop(int a, int b) {
        int col = owner.getMyColor() / 2;
        boolean flag = false;
        Figure f1 = new Figure();
        Figure f2 = new Figure();
        if (board.x != a || board.y != b) {
            if (isAllowed(a, b)) {
                if ((board.fig[board.x][board.y].type == PAWN) && (b == 0)) {
                    board.fig[board.x][board.y].empty = true;
                    board.fig[board.x][board.y].type = NULL;
                    board.fig[board.x][board.y].color = NULL;
                    Messenjah m = new Messenjah(board, owner.getMyColor(), a, b);
                } else {
                    copy(f1, board.fig[a][b]);
                    copy(f2, board.fig[board.x][board.y]);
                    board.fig[a][b].oX = a * 60;
                    board.fig[a][b].oY = b * 60;
                    board.fig[a][b].empty = false;
                    board.fig[a][b].firstStep = false;
                    board.fig[a][b].type = board.fig[board.x][board.y].type;
                    board.fig[a][b].color = board.fig[board.x][board.y].color;
                    board.fig[board.x][board.y].empty = true;
                    board.fig[board.x][board.y].type = NULL;
                    board.fig[board.x][board.y].color = NULL;
                    if (board.fig[a][b].type == KING) {
                        flag = true;
                        board.bckKing[0] = board.king[col][0];
                        board.bckKing[1] = board.king[col][1];
                        board.king[col][0] = a;
                        board.king[col][1] = b;
                    }
                }
                if (!kingSafeAt(board.king[col][0], board.king[col][1], owner.getOppColor())) {
                    if (flag) {
                        board.king[col][0] = board.bckKing[0];
                        board.king[col][1] = board.bckKing[1];
                    }
                    copy(board.fig[board.x][board.y], f2);
                    copy(board.fig[a][b], f1);
                    board.fig[board.x][board.y].oX = board.dragX * 60;
                    board.fig[board.x][board.y].oY = board.dragY * 60;
                } else {
                    board.isCheck = false;
                    board.makeMove(a, b);
                }
            } else {
                board.fig[board.x][board.y].oX = board.dragX * 60;
                board.fig[board.x][board.y].oY = board.dragY * 60;
            }
        } else {
            board.fig[board.x][board.y].oX = a * 60;
            board.fig[board.x][board.y].oY = b * 60;
        }
    }

    public boolean kingSideCastling() {
        if (owner.getMyColor() == WHITE) {
            if ((board.fig[5][7].empty) && (board.fig[6][7].empty) && (board.fig[7][7].firstStep) && (board.fig[4][7].firstStep) && (!board.isCheck) && (owner.isMyTurn())) {
                return true;
            } else {
                return false;
            }
        } else {
            if ((board.fig[1][7].empty) && (board.fig[2][7].empty) && (board.fig[0][7].firstStep) && (board.fig[3][7].firstStep) && (!board.isCheck) && (owner.isMyTurn())) {
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean queenSideCastling() {
        if (owner.getMyColor() == WHITE) {
            if ((board.fig[1][7].empty) && (board.fig[2][7].empty) && (board.fig[3][7].empty) && (board.fig[0][7].firstStep) && (board.fig[4][7].firstStep) && (!board.isCheck) && (owner.isMyTurn())) {
                return true;
            } else {
                return false;
            }
        } else {
            if ((board.fig[4][7].empty) && (board.fig[5][7].empty) && (board.fig[6][7].empty) && (board.fig[7][7].firstStep) && (board.fig[3][7].firstStep) && (!board.isCheck) && (owner.isMyTurn())) {
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean mate(int i, int j, Figure[][] fig) {
        if (board.isCheck) {
            calculateAllowedMoves(fig[i][j], i, j);
            if (allowed[0][0] == -1) {
                if (kingSafeAt(attacker[0], attacker[1], owner.getMyColor())) {
                    return !canCover(i, j, fig);
                }
            } else {
                int k, bck;
                for (k = 0; k < 8; k++) {
                    if (allowed[k][0] == -1) {
                        return !canCover(i, j, fig);
                    }
                    fig[i][j].color = NULL;
                    bck = fig[allowed[k][0]][allowed[k][1]].color;
                    fig[allowed[k][0]][allowed[k][1]].color = owner.getMyColor();
                    if (kingSafeAt(allowed[k][0], allowed[k][1], owner.getOppColor())) {
                        fig[allowed[k][0]][allowed[k][1]].color = bck;
                        fig[i][j].color = owner.getMyColor();
                        break;
                    }
                    fig[allowed[k][0]][allowed[k][1]].color = bck;
                    fig[i][j].color = owner.getMyColor();
                }
            }
        }
        return false;
    }

    private boolean canCover(int i, int j, Figure[][] fig) {
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
                        if (kingSafeAt(i, j, owner.getOppColor())) {
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
}
