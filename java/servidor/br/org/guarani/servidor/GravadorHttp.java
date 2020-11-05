/*
		sjohn@via-rs.net ago/2002
*/
package br.org.guarani.servidor;

import java.io.*;
import java.net.*;
import br.org.guarani.util.*;

//class GravadorHttp extends PrintWriter implements Gravador {
class GravadorHttp extends Gravador {
	int tt=0;

	//******************************
	public void deb(String s) {
		logs.grava("===> "+s+(erro?" --> ER":""));
	}
	//******************************
	public void close() {
		//deb("close");		
		try {
			flush();
			outb.close();
		} catch (Exception e) {
		}
	}

	//******************************
	public void println() {
		//deb("println null");
		print("\r\n");
	}
	//******************************
	public void println(String s) {
		//deb("println s");
		print(s+"\r\n");
	}

	//******************************
	public GravadorHttp(OutputStream o) {
		super(o);
	}
	//******************************
	public void flush() {
		//deb("flush");
		if (erro) return;
		gravaBuf();
	}
	//******************************
	public void print(String s) {
		//deb("print s="+s);
		try {
			byte b[] = s.getBytes();
			write(b,0,b.length);
		} catch (Exception e) {
			erro(".print c/conv p/"+Http.charset,e);
			return;
		}
	}
	//******************************
	public boolean checkError() {
		//deb("checkError");
		flush();
		return erro;
	}
	//******************************
	public int write(byte b[],int in,int tm) {
		//deb("write "+in+"a"+tm+" t="+b.length+" pBuf="+pBuf+" tBuf="+tBuf+" tt="+tt);
		if (pBuf+tm>=tBuf) {
			gravaBuf();
		}
		int t = in+tm;
		for (int i=in;i<t;i++) {
			if (pBuf>=tBuf) {
				gravaBuf();
			}
			buf[pBuf++] = b[i];
		}
		return tm;
	}
	//******************************
	protected void gravaBuf() {
		//deb("gravaBuf pBuf="+pBuf+" tBuf="+tBuf+" tt="+tt);
		if (pBuf==0) return;
		tt += pBuf;
		try {
			int t = pBuf;
			pBuf = 0;
			outb.write(buf,0,t);
			outb.flush();
		} catch (Exception e) {
			erro("gravadorHttp.gravaBuf",e);
		}	
	}
}
