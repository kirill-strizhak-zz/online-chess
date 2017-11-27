package oc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class Sender implements Protocol {
	
        private Logger log = null;
	private int type;
	private ServerSocket serv = null;
	private Socket sock;
	private PrintWriter pw;
	private MainFrame owner;
	private Receiver receiver = null;
	private Thread rec = null;
	private boolean free = true;
	
	public Sender(MainFrame own, Logger log, int t, String host, int port) {
                this.log = log;
		owner = own;
		type = t;
		try {
			if (type == 0) {
				boolean connected = false;
				while (!connected) {
					serv = new ServerSocket(port);
					owner.say("Sender: waiting for connection...");
					sock = serv.accept();
					byte b = (byte) sock.getInputStream().read();
					if (b != IDENT) {
						owner.say("Sender: unknown app, connection closed");
						sock.close();
						continue;
					}
					connected = true;
					sock.getOutputStream().write(IDENT);
				}
			} else {
				owner.say("Sender: connecting to server");
				sock = new Socket(host,port);
				sock.getOutputStream().write(IDENT);
				byte b = (byte) sock.getInputStream().read();
				if (b != IDENT) {
					IOException e = new IOException();
					throw e;
				}
			}
			owner.say("Sender: connection established, creating i/o streams");
			InputStream in = sock.getInputStream();
			OutputStream out = sock.getOutputStream();
			InputStreamReader inr = new InputStreamReader(in);
			OutputStreamWriter outw = new OutputStreamWriter(out);
			BufferedReader br = new BufferedReader(inr);
			pw = new PrintWriter(outw);
			receiver = new Receiver(owner, log, br, this);
			rec = new Thread(receiver);
			while (receiver == null) { }
			rec.start();
			owner.say("Sender: ini completed");
		} catch (Exception e) {
			if (type == 0) {
				owner.say("Error:Sender: port "+port+" is taken");
			} else {
				owner.say("Error:Sender: noone there - "+host+":"+port);
			}
		}
	}
	
	public void send(int i) throws IOException {
		pw.write(i);
		pw.flush();
	}
	public void send(String s) throws IOException {
		pw.println(s);
		pw.flush();
	}
	
	protected void suicide(String s) {
		try { owner.say("Sender: suicidal tendency caused by: "+s);
		sock.close();
		owner.connectionKilled();
		} catch (IOException e) {}
	}
	
	public Receiver getReceiver() {
		return receiver;
	}
	
	public boolean isFree() {
		if (!free) { return false; }
		else {
			free = false;
			return true;
		}
	}
	
	public void free() {
		free = true;
	}
	
	public Thread getThread() {
		return rec;
	}
}
