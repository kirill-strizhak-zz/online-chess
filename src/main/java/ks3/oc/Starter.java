package ks3.oc;

import ks3.oc.swing.SwingMainWindow;
import ks3.oc.swing.dialogs.SwingLauncherWindow;

public class Starter {

    private final Logger log;

    public Starter() {
        log = new Logger(true);
        new SwingLauncherWindow().open(this);
    }

    public void begin(int type, int color, String addr, int port, String name) {
        new SwingMainWindow(log, type, color, addr, port, name);
    }

    public static void main(String args[]) {
        new Starter();
    }
}
