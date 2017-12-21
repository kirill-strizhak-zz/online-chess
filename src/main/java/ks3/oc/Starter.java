package ks3.oc;

import ks3.oc.res.ResourceManager;
import ks3.oc.swing.SwingMainWindow;
import ks3.oc.swing.dialogs.SwingLauncherWindow;

public class Starter {

    private final Logger log;
    private final ResourceManager resourceManager;

    public Starter() {
        log = new Logger(true);
        resourceManager = new ResourceManager("/img/figures/", "/img/boards/", "default", "default");
        new SwingLauncherWindow().open(this);
    }

    public void begin(int type, int color, String addr, int port, String name) {
        new SwingMainWindow(log, resourceManager, type, color, addr, port, name);
    }

    public static void main(String args[]) {
        new Starter();
    }
}
