package ks3.oc;

import ks3.oc.res.ResourceManager;
import ks3.oc.swing.SwingMainWindow;
import ks3.oc.swing.dialogs.SwingLauncherWindow;

public class Starter {

    private final ResourceManager resourceManager;

    public Starter() {
        resourceManager = new ResourceManager("/img/figures/", "/img/boards/", "default", "default");
        new SwingLauncherWindow(this).open();
    }

    public void begin(int type, int color, String addr, int port, String name) {
        new SwingMainWindow(resourceManager, type, color, addr, port, name);
    }

    public static void main(String args[]) {
        new Starter();
    }
}
