package ks3.oc.conn.handlers;

import org.apache.log4j.Logger;

public class UnrecognizedMessageHandler implements MessageHandler {

    private static final Logger LOGGER = Logger.getLogger(UnrecognizedMessageHandler.class);

    @Override
    public void handle() {
        LOGGER.error("Received unexpected message header");
    }
}
