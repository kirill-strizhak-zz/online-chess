package ks3.oc.conn;

import ks3.oc.ChatPanel;
import ks3.oc.Protocol;
import ks3.oc.board.Board;
import ks3.oc.dialogs.DialogWindow;
import ks3.oc.swing.SwingMainWindow;
import ks3.oc.swing.dialogs.SwingNewGameConfirmation;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;

public class Receiver implements Runnable, Protocol {

    private static final Logger LOGGER = Logger.getLogger(Receiver.class);

    private boolean active = true;
    private BufferedReader br;
    private SwingMainWindow owner;
    private Sender sender;
    private ChatPanel chat = null;
    private Board board = null;
    private DialogWindow newGameConfirmation;

    public Receiver(SwingMainWindow own, BufferedReader b, Sender send) {
        br = b;
        owner = own;
        sender = send;
        newGameConfirmation = new SwingNewGameConfirmation(send, own);
    }

    public void run() {
        LOGGER.info("Receiver: activated");
        while (active) {
            try {
                int header = br.read();
                switch (header) {
                    case NAME:
                        LOGGER.info("Got NameID");
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
                        LOGGER.info("Name received");
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
                        int myColor = br.read();
                        owner.setMyColor(myColor);
                        if (myColor == WHITE) {
                            owner.setMyTurn(true);
                            owner.setOppColor(BLACK);
                        } else {
                            owner.setOppColor(WHITE);
                        }
                        break;
                    case OFFER_RESET:
                        newGameConfirmation.open();
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
            } catch (IOException ex) {
                LOGGER.error("Can't reach opponent", ex);
                active = false;
                sender.suicide("Receiver: IOException");
            }
        }
    }
}
