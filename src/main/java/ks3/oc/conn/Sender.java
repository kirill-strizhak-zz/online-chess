package ks3.oc.conn;

import ks3.oc.board.BoardState;
import ks3.oc.chat.ChatDisplay;
import ks3.oc.main.MainWindow;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public abstract class Sender {

    private static final Logger LOGGER = Logger.getLogger(Sender.class);

    private final SocketFactory socketFactory;
    protected final MainWindow main;
    protected final BoardState board;
    protected final ChatDisplay chat;
    private final Semaphore lock;

    private Socket socket;
    private PrintWriter writer;

    public Sender(MainWindow main, BoardState board, ChatDisplay chat, String host, int port) {
        this(new SocketFactory(), main, board, chat, host, port);
    }

    protected Sender(SocketFactory socketFactory, MainWindow main, BoardState board, ChatDisplay chat, String host, int port) {
        this.socketFactory = socketFactory;
        this.main = main;
        this.board = board;
        this.chat = chat;
        lock = new Semaphore(1, true);
        start(host, port);
    }

    private void start(String host, int port) {
        try {
            socket = openConnection(host, port);
            writer = createWriter(socket);
            sendHandshake(writer);
            startReceiver(createReader(socket));
            LOGGER.info("Initialization completed");
        } catch (IOException ex) {
            LOGGER.error("Failed to establish connection", ex);
        }
    }

    private PrintWriter createWriter(Socket socket) throws IOException {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
        return new PrintWriter(outputStreamWriter);
    }

    private BufferedReader createReader(Socket socket) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
        return new BufferedReader(inputStreamReader);
    }

    private void send(int i) {
        try {
            lock.acquireUninterruptibly();
            writer.write(i);
            writer.flush();
        } finally {
            lock.release();
        }
    }

    private void send(String s) {
        try {
            lock.acquireUninterruptibly();
            writer.println(s);
            writer.flush();
        } finally {
            lock.release();
        }
    }

    public void deactivate(String reason) {
        try {
            LOGGER.info("Closing: " + reason);
            socket.close();
            main.connectionKilled();
        } catch (IOException ex) {
            LOGGER.error("Failed to do clean shutdown", ex);
        }
    }

    public void sendMove(int col, int row, int newCol, int newRow) {
        send(Headers.COORDINATES);
        send(Math.abs(7 - col));
        send(Math.abs(7 - row));
        send(Math.abs(7 - newCol));
        send(Math.abs(7 - newRow));
    }

    public void sendSet(int col, int row, int color, int type, boolean isEmpty, boolean firstStep) {
        send(Headers.SET);
        send(Math.abs(7 - col));
        send(Math.abs(7 - row));
        send(color);
        send(type);
        send(isEmpty ? 1 : 0);
        send(firstStep ? 1 : 0);
    }

    public void sendChat(String message) {
        send(Headers.CHAT);
        send(message);
    }

    public void sendMate() {
        send(Headers.MATE);
    }

    public void sendGiveTurn() {
        send(Headers.GIVE_TURN);
    }

    public void sendOfferReset() {
        send(Headers.OFFER_RESET);
    }

    public void sendAcceptReset() {
        send(Headers.ACCEPT_RESET);
    }

    public void sendDeclineReset() {
        send(Headers.DECLINE_RESET);
    }

    public void sendClose() {
        send(Headers.CLOSE);
        deactivate("Client closed app");
    }

    protected SocketFactory getSocketFactory() {
        return socketFactory;
    }

    protected abstract Socket openConnection(String host, int port) throws IOException;

    protected abstract void sendHandshake(PrintWriter writer);

    protected abstract void startReceiver(BufferedReader reader);
}
