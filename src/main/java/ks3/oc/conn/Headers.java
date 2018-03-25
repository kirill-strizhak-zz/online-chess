package ks3.oc.conn;

interface Headers {

    int HANDSHAKE = 42;

    int COORDINATES = 1;
    int CHAT = 2;
    int CLOSE = 3;
    int OFFER_RESET = 4;
    int SET = 5;
    int GIVE_TURN = 6;
    int MATE = 7;
    int ACCEPT_RESET = 8;
    int DECLINE_RESET = 9;
}
