/*
	* Signey John jan/2001.
	*/

package br.org.guarani.loader;

import java.util.*;
import java.text.*;
import java.io.*;

public class logsL {
	protected static String logTela;
	protected static String dCfg="./"+dataL.strSql().substring(0,10);
	protected static String ext = ".log";
	protected static Hashtable h = new Hashtable();
	protected static arqLogL std,err;
	//**************************************************************
	public static void deb(int nv,String a) {
		if (nv<=load.debNv) {
			grava("load",a);
		}
	}
	//*********************************
	public static void debug(String s) {
		grava("debug",s);
	}
	//*********************************
	//grava log
	public static boolean grava(Throwable e,String par,String log) {
		return gravaStream(getOut(log),"<b>"+par+"<br>"+e.getMessage()+
			"</b><br>"+strL.troca(strL.erro(e),"\n","<br>"));
	}
	//*********************************
	//grava log
	public static boolean grava(Throwable e,String par) {
		return grava(e,par,"erro");
	}
	//*********************************
	//grava log
	public static boolean grava(Throwable e) {
		return grava(e,"","erro");
	}
	//*********************************
	//grava log
	public static boolean grava(String log,Throwable e) {
		return grava(e,"",log);
	}
	//*********************************
	//grava log
	public static boolean grava() {
		return gravaStream(std,"");
	}

	//*********************************
	//grava log
	public static boolean grava(String tx) {
		return gravaStream(std,tx);
	}

	//*********************************
	//grava log
	public static boolean gravaErr(String tx) {
		return gravaStream(err,tx);
	}

	//*********************************
	//grava log
	public static void grava(String log,String tx) {
		gravaStream(getOut(log),tx);
	}

	//*********************************
	//grava log
	public static boolean gravaStream(arqLogL alog,String tx) {
		tx = dataL.strSql()+"\t"+tx+"\r\n";
		//System.out.println(alog.nome+" "+logTela+" = "+tela(alog.nome));
		if (tela(alog.nome)) {
			System.out.print(
				strL.trimm(
				strL.troca(
					strL.troca(
						strL.troca(
							strL.substrAt(tx," "),
						"\t"," * "),
					"<br>","\n"),
				"<hr>","\n")
				)+"\n\n"
			);
		}
		int t = tx.length();
		try {
			for (int i=0;i<t;i++) {
				alog.f.write((byte)tx.charAt(i));
				alog.f.flush();
			}
		} catch (IOException ioe) {
			System.out.println("ERRO write LOG=="+ioe);
			return false;
		}

		return true;

	}
	//*********************************
	//retora output log
	public synchronized static arqLogL getOut(String log) {

		//FileOutputStream o = (FileOutputStream)h.get(log);
		Object o[] = (Object[])h.get(log);
		if (o==null) {
			try {
				o = new Object[3];
				o[0] = dCfg+log+ext;
				o[1] = new File((String)o[0]);
				if (!((File)o[1]).exists()) ((File)o[1]).createNewFile();
				o[2] = new arqLogL(log,new FileOutputStream((String)o[0],true));
				h.put(log,o);

			} catch (IOException ioe) {
				System.out.println("ERRO setando LOG='"+o[1]+"'="+ioe);
				return null;
			}
		}
		return (arqLogL)o[2];
	}

	private static boolean tela(String s) {
		if (logTela==null) return false;
		if (logTela.equals(",.,")) return true;
		return logTela.indexOf(","+s+",")!=-1;
	}
 
	//*********************************
	//inicializa log
	public static void inicia(String dir,String tela) {
		if (tela!=null) {
			logTela = ","+tela+",";
		}
		dCfg = dir;

		std = getOut("std");
		err = getOut("erro");

		if (logTela==null) {
			System.setOut(new PrintStream(getOut("sys_std").f));
			System.setErr(new PrintStream(getOut("sys_err").f));
		}

	}

	//*********************************
	//inicializa log
	public static Hashtable logs() {
		return h;
	}
}
