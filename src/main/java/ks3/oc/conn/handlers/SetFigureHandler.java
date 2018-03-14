package ks3.oc.conn.handlers;

import ks3.oc.board.BoardState;

import java.io.BufferedReader;
import java.io.IOException;

public class SetFigureHandler implements MessageHandler {

    private final BoardState board;
    private final BufferedReader reader;

    public SetFigureHandler(BoardState board, BufferedReader reader) {
        this.board = board;
        this.reader = reader;
    }

    @Override
    public void handle() throws IOException {
        int x = reader.read();
        int y = reader.read();
        int color = reader.read();
        int type = reader.read();
        int isEmpty = reader.read();
        int firstStep = reader.read();
        board.localSetFigure(x, y, color, type, isEmpty == 1, firstStep == 1);
    }
}
