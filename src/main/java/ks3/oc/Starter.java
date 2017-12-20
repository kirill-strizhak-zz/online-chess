package ks3.oc;

import ks3.oc.swing.dialogs.Messenjah;
import ks3.oc.swing.SwingMainWindow;

public class Starter {

    Logger log;

    public Starter() {
        log = new Logger(true);
        new Messenjah(this);
    }

    public void begin(int type, int color, String addr, int port, String name) {
        new SwingMainWindow(log, type, color, addr, port, name);
    }

    public static void main(String args[]) {
        new Starter();
    }
}
