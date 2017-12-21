package ks3.oc.swing.dialogs;

import ks3.oc.Sender;
import ks3.oc.dialogs.NewGameConfirmation;
import ks3.oc.res.ResourceManager;
import ks3.oc.swing.SwingMainWindow;

public class SwingNewGameConfirmation implements NewGameConfirmation {

    @Override
    public void open(ResourceManager resourceManager, Sender send, SwingMainWindow own) {
        new Messenjah(resourceManager, send, own);
    }
}
