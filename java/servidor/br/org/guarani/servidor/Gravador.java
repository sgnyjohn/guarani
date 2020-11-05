/*
		sjohn@via-rs.net ago/2002
*/
package br.org.guarani.servidor;

import java.io.*;
import java.net.*;
import br.org.guarani.util.*;

//interface Gravador {
public abstract class Gravador { //extends OutputStreamWriter { //PrintWriter {
	public OutputStream outb;
	//public Writer out,Kout;
	byte buf[];
	int pBuf=0;
	int tBuf=1024*4;
	boolean erro = false;
	String sErro;
	
	public Gravador(OutputStream o) {
		//super(o);
		buf = new byte[tBuf];
		outb = o;
		try {
			// Writer out   = new BufferedWriter(new OutputStreamWriter(System.out));
			// out = new BufferedWriter(new OutputStreamWriter(outb,Http.charset));
		} catch (Exception e) {
			erro(".criando Gravador: ",e);
		}		
	}
	//public Gravador(OutputStream o,boolean b) {
	//	//super(o,b);
	//	out = o;
	//}
	public OutputStream getOutputStream() {
		return outb;
	}

	//abstract void gravaBuf();
	//******************************
	public void erro(String m,Exception e) {
		erro = true;
		sErro = m;
		if ((""+e).indexOf("Pipe")==-1) {
			logs.grava("servidor","ERRO no Gravador ==> "+m+" "+e);
		}
	}
	/******************************
	public void println();
	/******************************
	public PrintWriter getPrintWriter();
	/******************************
	public void flush();
	/******************************
	public void print(String s);
	/******************************
	public boolean checkError();
	/******************************
	public void println(String s);
	*/
}
