package ks3.oc.swing.dialogs;

import ks3.oc.Logger;
import ks3.oc.Sender;
import ks3.oc.dialogs.NewGameConfirmation;
import ks3.oc.swing.SwingMainWindow;

public class SwingNewGameConfirmation implements NewGameConfirmation {

    @Override
    public void open(Logger log, Sender send, SwingMainWindow own) {
        new Messenjah(log, send, own);
    }
}
