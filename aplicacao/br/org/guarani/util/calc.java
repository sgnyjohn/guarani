package br.org.guarani.util;

//import java.util.*;

//***************************************
//***************************************
public class calc {
	int pos=0;
	double v[] = new double[8];
	String vs[] = new String[8];
	boolean debug = false;
	//***************************************
	public double res() {
		deb("res="+v[pos-1]);
		return v[pos-1];
	}
	//***************************************
	public void empil(String s) {
		deb("emp: "+s);
		if (str.vazio(s)) {
			empilS("0");
			return;
		}
		char c = s.charAt(0);
		if (c=='/') {
			pos--;
			set(pos-1,v[pos-1]/v[pos]);
		} else if (c=='%') {
			pos--;
			set(pos-1,v[pos-1]/v[pos]*100D);
		} else if (c=='*') {
			pos--;
			set(pos-1,v[pos-1]*v[pos]);
		} else if (c=='+') {
			pos--;
			set(pos-1,v[pos-1]+v[pos]);
		} else if (c=='-') {
			pos--;
			set(pos-1,v[pos-1]-v[pos]);
		} else if (c=='=') {
			pos--;
			//logs.grava(vs[pos-1]+"="+(vs[pos]));
			set(pos-1,(vs[pos-1].equals(vs[pos])?1:0));
		} else if (c=='!') {
			pos--;
			set(pos-1,(!vs[pos-1].equals(vs[pos])?1:0));
		} else if (c=='|') {
			pos--;
			//logs.grava(v[pos-1]+" | "+(v[pos]));
			set(pos-1,(v[pos-1]!=0 || v[pos]!=0?1:0));
		} else if (c=='&') {
			pos--;
			set(pos-1,(v[pos-1]!=0 && v[pos]!=0?1:0));
		} else {
			empilS(s);
		}
	}
	//***************************************
	public void set(int p,double v) {
		this.v[p] = v;
		this.vs[p] = ""+v;
		//logs.grava("set="+vs[p]);
	}
	//***************************************
	public void empilS(String s) {
		if (s.equals(":debug")) {
			debug = true;
			return;
		}
		this.vs[pos] = s;
		//logs.grava(vs[pos]);
		this.v[pos++] = str.duplo(s,-1);
	}
	//***************************************
	public void deb(String s) {
		if (debug) {
			logs.grava("calc debug: "+s);
		}
	}
	//***************************************
	public calc() {
	}
}
