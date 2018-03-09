package ks3.oc;

import ks3.oc.swing.SwingMainWindow;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Sender implements Protocol {

    private static final Logger LOGGER = Logger.getLogger(Sender.class);

    private Socket sock;
    private PrintWriter pw;
    private SwingMainWindow owner;
    private boolean free = true;

    public Sender(SwingMainWindow own, int type, String host, int port) {
        owner = own;
        try {
            if (type == 0) {
                boolean connected = false;
                while (!connected) {
                    ServerSocket server = new ServerSocket(port);
                    LOGGER.info("Waiting for connection...");
                    sock = server.accept();
                    byte b = (byte) sock.getInputStream().read();
                    if (b != IDENT) {
                        LOGGER.info("Unknown app, connection closed");
                        sock.close();
                        continue;
                    }
                    connected = true;
                    sock.getOutputStream().write(IDENT);
                }
            } else {
                LOGGER.info("Connecting to server");
                sock = new Socket(host, port);
                sock.getOutputStream().write(IDENT);
                byte b = (byte) sock.getInputStream().read();
                if (b != IDENT) {
                    throw new IOException("Invalid identification token received");
                }
            }
            LOGGER.info("Connection established, creating i/o streams");
            InputStream in = sock.getInputStream();
            OutputStream out = sock.getOutputStream();
            InputStreamReader inr = new InputStreamReader(in);
            OutputStreamWriter outw = new OutputStreamWriter(out);
            BufferedReader br = new BufferedReader(inr);
            pw = new PrintWriter(outw);
            Receiver receiver = new Receiver(owner, br, this);
            new Thread(receiver).start();
            LOGGER.info("Initialization completed");
        } catch (IOException ex) {
            LOGGER.error("Failed to establish connection", ex);
        }
    }

    public void send(int i) throws IOException {
        pw.write(i);
        pw.flush();
    }

    public void send(String s) throws IOException {
        pw.println(s);
        pw.flush();
    }

    public void suicide(String reason) {
        try {
            LOGGER.info("Closing: " + reason);
            sock.close();
            owner.connectionKilled();
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
}
