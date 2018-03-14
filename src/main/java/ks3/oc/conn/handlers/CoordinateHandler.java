package ks3.oc.conn.handlers;

import ks3.oc.board.BoardState;

import java.io.BufferedReader;
import java.io.IOException;

public class CoordinateHandler implements MessageHandler {

    private final BoardState board;
    private final BufferedReader reader;

    public CoordinateHandler(BoardState board, BufferedReader reader) {
        this.board = board;
        this.reader = reader;
    }

    @Override
    public void handle() throws IOException {
        int currX = reader.read();
        int currY = reader.read();
        int newX = reader.read();
        int newY = reader.read();
        board.makeMove(currX, currY, newX, newY);
    }
}
