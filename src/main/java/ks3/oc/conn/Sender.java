package ks3.oc.conn;

import ks3.oc.MainWindow;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public abstract class Sender {

    private static final Logger LOGGER = Logger.getLogger(Sender.class);

    private final SocketFactory socketFactory;
    private final MainWindow main;

    private Socket sock;
    private PrintWriter pw;
    private boolean free = true;

    public Sender(MainWindow main, String host, int port) {
        this(new SocketFactory(), main, host, port);
    }

    protected Sender(SocketFactory socketFactory, MainWindow main, String host, int port) {
        this.socketFactory = socketFactory;
        this.main = main;
        start(host, port);
    }

    private void start(String host, int port) {
        try {
            sock = openConnection(host, port);
            LOGGER.info("Connection established, creating i/o streams");
            InputStreamReader inr = new InputStreamReader(sock.getInputStream());
            OutputStreamWriter outw = new OutputStreamWriter(sock.getOutputStream());
            BufferedReader br = new BufferedReader(inr);
            pw = new PrintWriter(outw);
            startReceiver(br);
            LOGGER.info("Initialization completed");
        } catch (IOException ex) {
            LOGGER.error("Failed to establish connection", ex);
        }
    }

    protected abstract Socket openConnection(String host, int port) throws IOException;

    protected void startReceiver(BufferedReader reader) {
        Receiver receiver = new Receiver(main, reader, this);
        new Thread(receiver).start();
    }

    public void send(int i) throws IOException {
        pw.write(i);
        pw.flush();
    }

    public void send(String s) throws IOException {
        pw.println(s);
        pw.flush();
    }

    public void deactivate(String reason) {
        try {
            LOGGER.info("Closing: " + reason);
            sock.close();
            main.connectionKilled();
        } catch (IOException ex) {
            LOGGER.error("Failed to do clean shutdown", ex);
        }
    }

    public boolean isFree() {
        if (!free) {
            return false;
        } else {
            free = false;
            return true;
        }
    }

    public void free() {
        free = true;
    }

    protected SocketFactory getSocketFactory() {
        return socketFactory;
    }
}
