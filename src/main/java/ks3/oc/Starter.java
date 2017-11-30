package ks3.oc;

public class Starter {
    
    Logger log = null;
    
    public Starter() {
        log = new Logger(true);
        Messenjah m = new Messenjah(this);
    }

    public void begin(int type, int color, String addr, int port, String name) {
        MainFrame mf = new MainFrame(log, type, color, addr, port, name);
    }

    public static void main(String args[]) {
        Starter self = new Starter();
    }
}
