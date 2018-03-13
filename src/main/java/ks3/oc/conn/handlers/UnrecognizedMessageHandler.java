package ks3.oc.conn.handlers;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;

public class UnrecognizedMessageHandler implements MessageHandler {

    private static final Logger LOGGER = Logger.getLogger(UnrecognizedMessageHandler.class);

    @Override
    public void handle(BufferedReader reader) throws IOException {
        LOGGER.error("Received unexpected message header");
    }
}
