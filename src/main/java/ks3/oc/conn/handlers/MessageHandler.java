package ks3.oc.conn.handlers;

import java.io.BufferedReader;
import java.io.IOException;

public interface MessageHandler {

    public void handle(BufferedReader reader) throws IOException;
}
