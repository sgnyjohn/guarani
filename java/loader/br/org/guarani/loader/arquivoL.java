package br.org.guarani.loader;

import java.io.*;
import java.util.*;
import java.net.*;
import java.lang.reflect.*;

//**************************************************************
//**************************************************************
class arquivoL {
	//**************************************************************
	public static boolean dev() {
		return true;
	}
	//**************************************************************
	public arquivoL(String s) {
	}
	//**************************************************************
	protected boolean gravaTxt(File arquivo,String tx) {
		//cria os dirs?
		File fd = new File(strL.leftRat(""+arquivo,"/"));
		if (!fd.exists()) {
			fd.mkdirs();
		}

		try {
			OutputStreamWriter o = new OutputStreamWriter(
				new FileOutputStream(arquivo, false)
			,load.charsetO);
			o.write(tx,0,tx.length());
			o.close();
			return true;
		} catch (IOException e ) {
			on("ERRO ARQ "+arquivo+"<br>");
			on(e.toString());
			return false;
		}

		/*
		int t = tx.length();
		byte[] buf = new byte[t];
		for (int i=0;i<t;i++) {
			buf[i] = (byte)tx.charAt(i);
		}
		try {
			//InputStreamReader r = new InputStreamReader(new FileInputStream(f));
			, StandardCharsets.UTF_8));

			FileOutputStream r = new FileOutputStream(arquivo,load.charset);
			r.write(buf,0,t);
			r.close();
		} catch (IOException e ) {
			on("ERRO ARQ "+arquivo+"<br>");
			on(e.toString());
			return false;
		}
		return true;
		*/
	}
	//**************************************************************
	protected String leTxt(File arquivo) {
		return new String(carrega(arquivo));
	}
	//**************************************************************
	protected byte[] carrega(File arquivo) {
		int read = 0;
		int t = (int)arquivo.length();
		byte[] buf = new byte[t];
		try {
			//InputStreamReader r = new InputStreamReader(new FileInputStream(f));
			FileInputStream r = new FileInputStream(arquivo);
			read = r.read(buf,0,t);
			r.close();
		} catch (IOException e ) {
			on("ERRO ARQ "+arquivo+"<br>");
			on(e.toString());
			return new byte[0];
		}
		return buf;
	}
	//**************************************************************
	protected void on(String s) {
		load.on(s);
	}
}
