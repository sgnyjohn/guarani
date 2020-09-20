package br.org.guarani.loader;

import java.util.*;
import java.io.*;

public class strL {
	protected static String tb_trim = " \r\n\t";
	//*****************************************//
	public static String substrAtAt(String g,String a,String b) {
		int i = g.indexOf(a);
		if (i<0) return "";
		int f = g.indexOf(b,i+a.length());
		if (f<0) return "";
		return g.substring(i+a.length(),f);
	}
	//*********************************************
	public static String grep(String tx,String s) {
		return grep(strL.palavraA(tx,"\n"),s);
	}
	//*********************************************
	public static String grep(String v[],String s) {
		String r = "";
		for (short i=0;i<v.length;i++) {
			if (v[i].indexOf(s)!=-1) {
				r += v[i]+"\n";
			}
		}
		return r;
	}
	//*****************************************//
	public static boolean equalsR(String maior,String menor) {
		int p = maior.length()-menor.length();
		if (p<0) {
			return false;
		}
		return maior.substring(p).equals(menor);
	}
	//*****************************************//
	public static String strZero(long b,int t) {
		return strL.right(strL.repl("0",t)+b,t);
	}
	//*****************************************//
	public static String repl(String a,int b) {
		int t = a.length();
		byte c[] = new byte[t*b];
		for (int i=0;i<t*b;i+=t) 
			for (int x=0;x<t;x++) c[i+x] = (byte)a.charAt(x);
		return new String(c,0,t*b);
	}
	//*****************************************//
	public static String substrRat(String g,String a) {
		int i = g.lastIndexOf(a);
		if (i<0) return g;
		return g.substring(i+a.length(),g.length());
	}
	//**************************************/
	public static String erro(Throwable e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
	//*****************************************//
	public static String troca(String g,String a,String b) {
		int i=0,p,ta,tb;

		ta = a.length();
		tb = b.length();

		while ( (p = g.indexOf(a,i)) > -1 )  {
			g = g.substring(0,p)+b+g.substring(p+ta,g.length());
			i = p - ta + tb + 1;
		}

		return g;

	}
	//*****************************************//
	public static String substrAt(String g,String a) {
		int i = g.indexOf(a);
		if (i<0) return g;
		return g.substring(i+a.length(),g.length());
	}
	//*****************************************//
	public static String leftAt(String g,String a) {
		int i = g.indexOf(a);
		if (i<0) return g;
		return g.substring(0,i);
	}
	//*****************************************//
	public static boolean vazio(String a) {
		if (a==null) return true;
		if (a.length()==0) return true;
		if (strL.trimm(a).length()==0) return true;
		return false;
	}
	//*****************************************//
	//conta número de ocorrencias de b em a
	public static int strConta(String a,String b) {
		int p=0,nv=0,pn,t=b.length();
		while ((pn=a.indexOf(b,p))!=-1) {
			nv++;
			p = pn + t;
		}
		return nv;
	}
	//*****************************************//
	public static String[] palavraA(String g,String a) {
		int nv = strConta(g,a)+1;
		String r[] = new String[nv];
		if (nv==0) {
			r[0] = g;
			return r;
		} else {
			int p=0,pn,t=a.length(),pi=0;
			while ((pn=g.indexOf(a,p))!=-1) {
				r[pi++] = g.substring(p,pn);
				p = pn + t;
			}
			r[pi++] = g.substring(p,g.length());
			return r;
		}
	}
	//*****************************************//
	public static boolean equals(String maior,String menor) {
		if (menor.length()>maior.length()) return false;
		return (maior.substring(0,menor.length()).equals(menor));
	}
	//*****************************************//
	public static String trimm(String a,String b) {
		int i,t;

		//retira do inicio
		t = a.length()-1;
		if (t<0) return a;
		i = 0;
		while (i<t & b.indexOf(a.substring(i,i+1))>-1) i++;
		if (i!=0) a = a.substring(i,t+1);

		//retira do fim
		t = a.length();
		if (t<1) return a;
		i = t-1;
		while (i>-1 && b.indexOf(a.substring(i,i+1))>-1) i--;
		if (i!=t-1) a = a.substring(0,i+1);

		return a;

	}
	//*****************************************//
	public static String trimm(String a) {
		return trimm(a,tb_trim);
	}
	//*****************************************//
	public static String right(String a,int b) {
		int t = a.length();
		return a.substring(t-b,a.length());
	}
	//*****************************************//
	public static int inteiro(String s,int padrao) {
		try {
			//logs.grava(s);
			if (s.indexOf(".")!=-1) {
				s = leftAt(s,".");
			}
			int i = Integer.parseInt(trimm(s));
			return i;
		} catch (Exception e) {
			return padrao;
		}
	}
	//**************************************
	public static String Escape(String a) {
		int pos=0,t = a.length(),b;
		char[] r = new char[t*3];
		char s;

		for (int i=0;i<t;i++) {
			s = a.charAt(i);
			if ( (s>='0' && s<='9') ||  (s>='a' && s<='z')  ||  (s>='A' && s<='Z') ) {
				r[pos++] = s;
			} else {
				String s1 = right("00"+base16((int)s),2);
				r[pos++] = '%';
				r[pos++] = s1.charAt(0);
				r[pos++] = s1.charAt(1);
			}
		}
		return new String(r,0,pos);
	}
	//*****************************************//
	public static String[][] palavraA(String g,String a,String b) {
		String t1[] = palavraA(g,a);
		int t = palavraA(t1[0],b).length;
		String r[][] = new String[t1.length][t];
		for (int i=0;i<t1.length;i++) {
			String r1[] = palavraA(t1[i],b);
			for (int ii=0;ii<Math.min(r1.length,t);ii++) {
				r[i][ii] = r1[ii];
			}
		}
		return r;
	}
	//*********************************************
	public static String base16(int x) {
		String r = "";
		int i,b=16;
		while (x>0) {
			i = x % b;
			r = "0123456789ABCDEF".charAt(i)+r;
			x = (x-i)/b;
		}
		return r;
	}
	//*****************************************//
	public static String leftRat(String g,String a) {
		int i = g.lastIndexOf(a);
		if (i<0) return g;
		return g.substring(0,i);
	}
}