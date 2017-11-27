package oc;

public class Check implements Protocol {

    private int[][] allowed = new int[100][2];
    private int arrPos = 0;
    public boolean calculating = false;
    private Board board;
    private MainFrame owner;
    private int[] attacker = new int[2];

    public Check(Board brd, MainFrame own) {
        board = brd;
        owner = own;
        allowed[0][0] = -1;
    }

    public void move(Figure figure, int x, int y) {
        calculating = true;
        switch (figure.type) {
            case PAWN:
                allowed[0][0] = -1;
                pawn(figure, x, y);
                arrPos = 0;
                break;
            case ROOK:
                allowed[0][0] = -1;
                rook(x, y);
                arrPos = 0;
                break;
            case KNIGHT:
                allowed[0][0] = -1;
                knight(x, y);
                arrPos = 0;
                break;
            case BISHOP:
                allowed[0][0] = -1;
                bishop(x, y);
                arrPos = 0;
                break;
            case QUEEN:
                allowed[0][0] = -1;
                queen(x, y);
                arrPos = 0;
                break;
            case KING:
                allowed[0][0] = -1;
                king(x, y);
                arrPos = 0;
                break;
        }
        calculating = false;
    }

    private void pawn(Figure figure, int x, int y) {
        if ((figure.firstStep) && (board.fig[x][y - 2].empty)) {
            add(x, y - 2);
        }
        if ((x != 0) && (!board.fig[x - 1][y - 1].empty)) {
            add(x - 1, y - 1);
        }
        if ((x != 7) && (!board.fig[x + 1][y - 1].empty)) {
            add(x + 1, y - 1);
        }
        if ((y != 0) && (board.fig[x][y - 1].empty)) {
            add(x, y - 1);
        }
    }

    private void rook(int x, int y) {
        int i;
        for (i = x; i >= 0; i--) {
            if (board.fig[i][y].color != owner.myColor) {
                add(i, y);
            }
            if ((board.fig[i][y].color != NULL) && (i != x)) {
                break;
            }
        }
        for (i = y; i >= 0; i--) {
            if (board.fig[x][i].color != owner.myColor) {
                add(x, i);
            }
            if ((board.fig[x][i].color != NULL) && (i != y)) {
                break;
            }
        }
        for (i = x; i <= 7; i++) {
            if (board.fig[i][y].color != owner.myColor) {
                add(i, y);
            }
            if ((board.fig[i][y].color != NULL) && (i != x)) {
                break;
            }
        }
        for (i = y; i <= 7; i++) {
            if (board.fig[x][i].color != owner.myColor) {
                add(x, i);
            }
            if ((board.fig[x][i].color != NULL) && (i != y)) {
                break;
            }
        }
    }

    private void knight(int x, int y) {
        if (((x - 1) >= 0) && ((y - 2) >= 0)) {
            add(x - 1, y - 2);
        }
        if (((x + 1) <= 7) && ((y - 2) >= 0)) {
            add(x + 1, y - 2);
        }
        if (((x + 2) <= 7) && ((y - 1) >= 0)) {
            add(x + 2, y - 1);
        }
        if (((x + 2) <= 7) && ((y + 1) <= 7)) {
            add(x + 2, y + 1);
        }
        if (((x + 1) <= 7) && ((y + 2) <= 7)) {
            add(x + 1, y + 2);
        }
        if (((x - 1) >= 0) && ((y + 2) <= 7)) {
            add(x - 1, y + 2);
        }
        if (((x - 2) >= 0) && ((y + 1) <= 7)) {
            add(x - 2, y + 1);
        }
        if (((x - 2) >= 0) && ((y - 1) >= 0)) {
            add(x - 2, y - 1);
        }
    }

    private void bishop(int x, int y) {
        int i, j;
        j = y;
        for (i = x; i >= 0; i--) {
            if (board.fig[i][j].color != owner.myColor) {
                add(i, j);
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
            if (board.fig[i][j].color != owner.myColor) {
                add(i, j);
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
            if (board.fig[i][j].color != owner.myColor) {
                add(i, j);
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
            if (board.fig[i][j].color != owner.myColor) {
                add(i, j);
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

    private void queen(int x, int y) {
        rook(x, y);
        bishop(x, y);
    }

    private void king(int x, int y) {
        if (((x - 1) >= 0) && ((y - 1) >= 0) && (board.fig[x - 1][y - 1].color != owner.myColor) && (free2go(x - 1, y - 1, owner.oppColor))) {
            add(x - 1, y - 1);
        }
        if (((x + 1) <= 7) && ((y - 1) >= 0) && (board.fig[x + 1][y - 1].color != owner.myColor) && (free2go(x + 1, y - 1, owner.oppColor))) {
            add(x + 1, y - 1);
        }
        if (((x - 1) >= 0) && ((y + 1) <= 7) && (board.fig[x - 1][y + 1].color != owner.myColor) && (free2go(x - 1, y + 1, owner.oppColor))) {
            add(x - 1, y + 1);
        }
        if (((x + 1) <= 7) && ((y + 1) <= 7) && (board.fig[x + 1][y + 1].color != owner.myColor) && (free2go(x + 1, y + 1, owner.oppColor))) {
            add(x + 1, y + 1);
        }
        if (((y - 1) >= 0) && (board.fig[x][y - 1].color != owner.myColor) && (free2go(x, y - 1, owner.oppColor))) {
            add(x, y - 1);
        }
        if (((y + 1) <= 7) && (board.fig[x][y + 1].color != owner.myColor) && (free2go(x, y + 1, owner.oppColor))) {
            add(x, y + 1);
        }
        if (((x + 1) <= 7) && (board.fig[x + 1][y].color != owner.myColor) && (free2go(x + 1, y, owner.oppColor))) {
            add(x + 1, y);
        }
        if (((x - 1) >= 0) && (board.fig[x - 1][y].color != owner.myColor) && (free2go(x - 1, y, owner.oppColor))) {
            add(x - 1, y);
        }
    }

    public boolean free2go(int x, int y, int oppColor) {
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

    public void add(int x, int y) {
        allowed[arrPos][0] = x;
        allowed[arrPos][1] = y;
        ++arrPos;
        allowed[arrPos][0] = -1;
    }

    public boolean allowed(int x, int y) {
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
        int col = owner.myColor / 2;
        boolean flag = false;
        Figure f1 = new Figure();
        Figure f2 = new Figure();
        if (board.x != a || board.y != b) {
            if (allowed(a, b)) {
                if ((board.fig[board.x][board.y].type == PAWN) && (b == 0)) {
                    board.fig[board.x][board.y].empty = true;
                    board.fig[board.x][board.y].type = NULL;
                    board.fig[board.x][board.y].color = NULL;
                    Messenjah m = new Messenjah(board, owner.myColor, a, b);
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
                if (!free2go(board.king[col][0], board.king[col][1], owner.oppColor)) {
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

    public boolean shortXchng() {
        if (owner.myColor == WHITE) {
            if ((board.fig[5][7].empty) && (board.fig[6][7].empty) && (board.fig[7][7].firstStep) && (board.fig[4][7].firstStep) && (!board.isCheck) && (owner.myTurn)) {
                return true;
            } else {
                return false;
            }
        } else {
            if ((board.fig[1][7].empty) && (board.fig[2][7].empty) && (board.fig[0][7].firstStep) && (board.fig[3][7].firstStep) && (!board.isCheck) && (owner.myTurn)) {
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean longXchng() {
        if (owner.myColor == WHITE) {
            if ((board.fig[1][7].empty) && (board.fig[2][7].empty) && (board.fig[3][7].empty) && (board.fig[0][7].firstStep) && (board.fig[4][7].firstStep) && (!board.isCheck) && (owner.myTurn)) {
                return true;
            } else {
                return false;
            }
        } else {
            if ((board.fig[4][7].empty) && (board.fig[5][7].empty) && (board.fig[6][7].empty) && (board.fig[7][7].firstStep) && (board.fig[3][7].firstStep) && (!board.isCheck) && (owner.myTurn)) {
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean mate(int i, int j, Figure[][] fig) {
        if (board.isCheck) {
            move(fig[i][j], i, j);
            if (allowed[0][0] == -1) {
                if (free2go(attacker[0], attacker[1], owner.myColor)) {
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
                    fig[allowed[k][0]][allowed[k][1]].color = owner.myColor;
                    if (free2go(allowed[k][0], allowed[k][1], owner.oppColor)) {
                        fig[allowed[k][0]][allowed[k][1]].color = bck;
                        fig[i][j].color = owner.myColor;
                        break;
                    }
                    fig[allowed[k][0]][allowed[k][1]].color = bck;
                    fig[i][j].color = owner.myColor;
                }
            }
        }
        return false;
    }

    private boolean canCover(int i, int j, Figure[][] fig) {
        int k, l, bck;
        for (k = 0; k <= 7; k++) {
            for (l = 0; l <= 7; l++) {
                if ((fig[k][l].color == owner.myColor) && (fig[k][l].type != KING)) {
                    move(fig[k][l], k, l);
                    int m = 0;
                    while (allowed[m][0] != -1) {
                        fig[k][l].color = NULL;
                        bck = fig[allowed[m][0]][allowed[m][1]].color;
                        fig[allowed[m][0]][allowed[m][1]].color = owner.myColor;
                        if (free2go(i, j, owner.oppColor)) {
                            fig[allowed[m][0]][allowed[m][1]].color = bck;
                            fig[k][l].color = owner.myColor;
                            return true;
                        }
                        fig[allowed[m][0]][allowed[m][1]].color = bck;
                        fig[k][l].color = owner.myColor;
                        ++m;
                    }
                }
            }
        }
        return false;
    }
}
