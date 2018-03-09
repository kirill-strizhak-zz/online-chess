package ks3.oc.swing.dialogs;

import ks3.oc.dialogs.DialogWindow;

import javax.swing.JFrame;

abstract class SwingDialogWindow implements DialogWindow {

    protected final JFrame frame;

    protected SwingDialogWindow(JFrame frame) {
        this.frame = frame;
    }

    @Override
    public void open() {
        frame.setVisible(true);
    }
}
