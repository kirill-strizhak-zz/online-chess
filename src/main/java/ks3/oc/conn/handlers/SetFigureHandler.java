package ks3.oc.conn.handlers;

import ks3.oc.board.Board;

import java.io.BufferedReader;
import java.io.IOException;

public class SetFigureHandler implements MessageHandler {

    private final Board board;

    public SetFigureHandler(Board board) {
        this.board = board;
    }

    @Override
    public void handle(BufferedReader reader) throws IOException {
        int x = reader.read();
        int y = reader.read();
        int color = reader.read();
        int type = reader.read();
        int isEmpty = reader.read();
        int firstStep = reader.read();
        board.localSetFigure(x, y, color, type, isEmpty == 1, firstStep == 1);
    }
}
