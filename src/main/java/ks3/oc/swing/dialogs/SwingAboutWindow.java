package ks3.oc.swing.dialogs;

import ks3.oc.dialogs.AboutWindow;

public class SwingAboutWindow implements AboutWindow {

    private Messenjah aboutWindow;

    @Override
    public void open() {
        if (aboutWindow == null) {
            aboutWindow = new Messenjah();
        } else {
            aboutWindow.setVisible(true);
        }
    }
}
