package oc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Logger {
    
    private static final String ERR_BASE = "oc.Logger:";
    
    private boolean dualMode = false;
    private BufferedWriter bw = null;
    
    public Logger() {
        File logFile = new File("oc-log.txt");
        try {
            bw = new BufferedWriter(new FileWriter(logFile));
        } catch (IOException ex) {
            say(ERR_BASE + "():: unable to create log file output stream: " + ex.getMessage());
        }
    }
    
    public Logger(boolean dualMode) {
        this.dualMode = dualMode;
        if (dualMode) {
            File logFile = new File("oc-log.txt");
            try {
                bw = new BufferedWriter(new FileWriter(logFile));
            } catch (IOException ex) {
                say(ERR_BASE + "():: unable to create log file output stream: " + ex.getMessage());
            }
        }
    }
    
    public void log(String msg) {
        say(new Date() + " :: " + msg);
        if (dualMode) try {
            bw.write(new Date() + " :: " + msg);
        } catch (IOException ex) {
            say(ERR_BASE + "log():: unable to log message: " + ex.getMessage());
        }
    }
    
    private void say(Object s) {
        System.out.println(s);
    }
}
