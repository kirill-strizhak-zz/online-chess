package ks3.oc.swing.dialogs;

import ks3.oc.Starter;
import ks3.oc.dialogs.LauncherWindow;

public class SwingLauncherWindow implements LauncherWindow {

    @Override
    public void open(Starter starter) {
        new Messenjah(starter);
    }
}
