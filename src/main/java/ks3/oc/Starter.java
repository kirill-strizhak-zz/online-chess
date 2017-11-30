package ks3.oc;

import ks3.oc.swing.SwingMainWindow;

public class Starter {
    
    Logger log = null;
    
    public Starter() {
        log = new Logger(true);
        Messenjah m = new Messenjah(this);
    }

    public void begin(int type, int color, String addr, int port, String name) {
        SwingMainWindow mf = new SwingMainWindow(log, type, color, addr, port, name);
    }

    public static void main(String args[]) {
        Starter self = new Starter();
    }
}
