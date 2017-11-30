package ks3.oc;

import java.io.BufferedReader;
import java.io.IOException;

public class Receiver implements Runnable, Protocol {

    private Logger log = null;
    private boolean active = true;
    private BufferedReader br;
    private MainFrame owner;
    private Sender sender;
    private ChatPanel chat = null;
    private Board board = null;
    private Thread trtr;

    public Receiver(MainFrame own, Logger log, BufferedReader b, Sender send) {
        this.log = log;
        br = b;
        owner = own;
        sender = send;
        trtr = sender.getThread();
    }

    public void run() {
        owner.say("Receiver: activated");
        while (active) {
            try {
                int b = br.read();
                switch (b) {
                    case NAME:
                        owner.say("R: got NameID");
                        String name = br.readLine();
                        owner.opponentName = name;
                        while (chat == null) {
                            try {
                                trtr.sleep(100);
                            } catch (Exception e) {
                            }
                            chat = owner.getChat();
                        }
                        owner.say("R: name received");
                        chat.addChatLine("* " + name + " connected", "sys_&^_tem");
                        break;
                    case COORDINATES:
                        int currX = br.read();
                        int currY = br.read();
                        int newX = br.read();
                        int newY = br.read();
                        while (board == null) {
                            try {
                                trtr.sleep(100);
                            } catch (Exception e) {
                            }
                            board = owner.getBoard();
                        }
                        board.makeMove(currX, currY, newX, newY);
                        break;
                    case CHAT:
                        String msg = br.readLine();
                        while (chat == null) {
                            try {
                                trtr.sleep(100);
                            } catch (Exception e) {
                            }
                            chat = owner.getChat();
                        }
                        chat.addChatLine(msg, owner.opponentName);
                        break;
                    case CLOSE:
                        active = false;
                        sender.suicide("Receiver: client disconnected");
                        break;
                    case COLOR:
                        owner.myColor = br.read();
                        if (owner.myColor == BLACK) {
                            owner.oppColor = WHITE;
                        } else {
                            owner.oppColor = BLACK;
                        }
                        if (owner.myColor == WHITE) {
                            owner.myTurn = true;
                        }
                        break;
                    case OFFER_RESET:
                        Messenjah m = new Messenjah(log, sender, owner);
                        break;
                    case ACCEPT_RESET:
                        owner.reset();
                        break;
                    case DECLINE_RESET:
                        while (chat == null) {
                            try {
                                trtr.sleep(100);
                            } catch (Exception e) {
                            }
                            chat = owner.getChat();
                        }
                        chat.addChatLine("* Reset declined", "sys_&^_tem");
                        break;
                    case SET:
                        int x = br.read();
                        int y = br.read();
                        int color = br.read();
                        int type = br.read();
                        int a = br.read();
                        int f = br.read();
                        boolean isEmpty,
                         firstStep;
                        //owner.say("x: "+x+"  y: "+y+"  c: "+color+"  t: "+type+"  em: "+a+"  fStep: "+f);
                        if (a == 1) {
                            isEmpty = true;
                        } else {
                            isEmpty = false;
                        }
                        if (f == 1) {
                            firstStep = true;
                        } else {
                            firstStep = false;
                        }
                        while (board == null) {
                            try {
                                trtr.sleep(100);
                            } catch (Exception e) {
                            }
                            board = owner.getBoard();
                        }
                        board.localSetFigure(x, y, color, type, isEmpty, firstStep);
                        break;
                    case GIVE_TURN:
                        owner.myTurn = true;
                        break;
                    case REAVE_TURN:
                        owner.myTurn = false;
                        break;
                    case MATE:
                        owner.myTurn = false;
                        while (chat == null) {
                            try {
                                trtr.sleep(100);
                            } catch (Exception e) {
                            }
                            chat = owner.getChat();
                        }
                        chat.addChatLine("* You win! Check and mate", "sys_&^_tem");
                        break;
                    /*case CLEAR:
                    board.localClear();
                    break;*/
                }
            } catch (IOException e) {
                owner.say("Receiver: can't reach opponent");
                active = false;
                sender.suicide("Receiver: IOException");
            }
        }
    }
}
