package ks3.oc;

import ks3.oc.board.Board;
import ks3.oc.swing.dialogs.Messenjah;
import ks3.oc.swing.SwingMainWindow;

import java.io.BufferedReader;
import java.io.IOException;

public class Receiver implements Runnable, Protocol {

    private Logger log = null;
    private boolean active = true;
    private BufferedReader br;
    private SwingMainWindow owner;
    private Sender sender;
    private ChatPanel chat = null;
    private Board board = null;

    public Receiver(SwingMainWindow own, Logger log, BufferedReader b, Sender send) {
        this.log = log;
        br = b;
        owner = own;
        sender = send;
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
                                Thread.sleep(100);
                            } catch (InterruptedException ex) {
                                //ignore
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
                                Thread.sleep(100);
                            } catch (InterruptedException ex) {
                                //ignore
                            }
                            board = owner.getBoard();
                        }
                        board.makeMove(currX, currY, newX, newY);
                        break;
                    case CHAT:
                        String msg = br.readLine();
                        while (chat == null) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException ex) {
                                //ignore
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
                        owner.setMyColor(br.read());
                        if (owner.getMyColor() == BLACK) {
                            owner.setOppColor(WHITE);
                        } else {
                            owner.setOppColor(BLACK);
                        }
                        if (owner.getMyColor() == WHITE) {
                            owner.setMyTurn(true);
                        }
                        break;
                    case OFFER_RESET:
                        new Messenjah(log, sender, owner);
                        break;
                    case ACCEPT_RESET:
                        owner.reset();
                        break;
                    case DECLINE_RESET:
                        while (chat == null) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException ex) {
                                //ignore
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
                        while (board == null) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException ex) {
                                //ignore
                            }
                            board = owner.getBoard();
                        }
                        board.localSetFigure(x, y, color, type, a == 1, f == 1);
                        break;
                    case GIVE_TURN:
                        owner.setMyTurn(true);
                        break;
                    case REAVE_TURN:
                        owner.setMyTurn(false);
                        break;
                    case MATE:
                        owner.setMyTurn(false);
                        while (chat == null) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException ex) {
                                //ignore
                            }
                            chat = owner.getChat();
                        }
                        chat.addChatLine("* You win! Check and mate", "sys_&^_tem");
                        break;
                }
            } catch (IOException e) {
                owner.say("Receiver: can't reach opponent");
                active = false;
                sender.suicide("Receiver: IOException");
            }
        }
    }
}
