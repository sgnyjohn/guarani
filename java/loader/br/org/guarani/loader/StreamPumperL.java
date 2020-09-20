package br.org.guarani.loader;

import java.io.*;
import java.util.Hashtable;
import java.lang.reflect.Method;
import java.util.Enumeration;


/*************************************/
/*************************************/
// Inner class for continually pumping the input stream during
// Process's runtime.
//gcc##
//class StreamPumper extends Thread {
public class StreamPumperL extends Thread {
	private BufferedInputStream stream;
	private boolean endOfStream = false;
	private boolean stopSignal  = false;
	private int SLEEP_TIME = 5;
	private OutputStream out;

	public StreamPumperL(BufferedInputStream is, OutputStream out) {
		this.stream = is;
		this.out = out;
	}

	public void pumpStream() throws IOException {
		byte[] buf = new byte[4096];
		if (!endOfStream) {
			int bytesRead=stream.read(buf, 0, 4096);

			if (bytesRead > 0) {
				out.write(buf, 0, bytesRead);
			} else if (bytesRead==-1) {
				endOfStream=true;
			}
		}
	}

	public void run() {
		try {
		//while (!endOfStream || !stopSignal) {
			while (!endOfStream) {
				pumpStream();
				sleep(SLEEP_TIME);
			}
		} catch (InterruptedException ie) {
		} catch (IOException ioe) {
		}
	}

}
