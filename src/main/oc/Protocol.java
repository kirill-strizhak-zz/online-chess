package oc;

public interface Protocol {
	static final int IDENT = 0;
	static final int NAME = 1;
	static final int COORDINATES = 2;
	static final int CHAT = 3;
	static final int CLOSE = 4;
	static final int COLOR = 5;
	static final int OFFER_RESET = 6;
	static final int SET = 7;
	static final int GIVE_TURN = 8;
	static final int MATE = 9;
	static final int ACCEPT_RESET = 10;
	static final int DECLINE_RESET = 11;
	static final int REAVE_TURN = 12;
	static final int CLEAR = 13;
	
	static final int SERVER = 0;
	static final int CLIENT = 1;
	
	static final String DATE_FORMAT = "[HH:mm]";
	
	static final int WHITE = 2;
	static final int BLACK = 0;
	
	static final int PAWN = 0;
	static final int ROOK = 1;
	static final int KNIGHT = 2;
	static final int BISHOP = 3;
	static final int QUEEN = 4;
	static final int KING = 5;
	
	static final int NULL = 6;
}
