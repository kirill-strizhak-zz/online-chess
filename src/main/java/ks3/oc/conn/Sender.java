package ks3.oc.conn;

import ks3.oc.chat.ChatDisplay;
import ks3.oc.MainWindow;
import ks3.oc.board.BoardState;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public abstract class Sender {

    private static final Logger LOGGER = Logger.getLogger(Sender.class);

    private final SocketFactory socketFactory;
    private final MainWindow main;
    private final BoardState board;
    private final ChatDisplay chat;
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
            LOGGER.info("Connection established, creating i/o streams");
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
            BufferedReader reader = new BufferedReader(inputStreamReader);
            writer = new PrintWriter(outputStreamWriter);
            startReceiver(reader);
            LOGGER.info("Initialization completed");
        } catch (IOException ex) {
            LOGGER.error("Failed to establish connection", ex);
        }
    }

    protected abstract Socket openConnection(String host, int port) throws IOException;

    protected void startReceiver(BufferedReader reader) {
        Receiver receiver = new Receiver(main, board, chat, reader, this);
        new Thread(receiver).start();
    }

    public void send(int i) throws IOException {
        try {
            lock.acquireUninterruptibly();
            writer.write(i);
            writer.flush();
        } finally {
            lock.release();
        }
    }

    public void send(String s) throws IOException {
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

    protected SocketFactory getSocketFactory() {
        return socketFactory;
    }
}
