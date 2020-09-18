/*
	* Signey John jan/2001.
	*/

package br.org.guarani.util;

import br.org.guarani.util.*;
import java.util.*;
import java.text.*;
import java.io.*;

public class logs {
	protected static String logTela;
	protected static String dCfg="./"+data.strSql().substring(0,10);
	protected static String ext = ".log";
	protected static Hashtable h = new Hashtable();
	protected static arqLog std,err;
	protected static logsFiltro lf;
	//*********************************
	public static void debug(String s) {
		grava("debug",s);
	}
	//*********************************
	//grava log
	public static boolean grava(String log,Throwable e,String par) {
		return grava(e,par,log);
	}
	//*********************************
	//grava log
	public static boolean grava(Throwable e,String par,String log) {
		return gravaStream(getOut(log),"<b>"+par+"<br>"+e.getMessage()+
			"</b><br>"+str.troca(str.erro(e),"\n","<br>"));
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

	/*********************************
	//grava log
	public static void grava(String tx,Exception e) {
		FileOutputStream l = getOut("err");
		gravaStream(l,tx);
		PrintWriter p = new PrintWriter(l);
		e.printStackTrace(p);
		//e.printStackTrace(l);
	}
	*/

	//*********************************
	// grava log
	public static boolean gravaStream(arqLog alog,String tx) {
		//System.out.println(alog.nome+" "+logTela+" = "+tela(alog.nome));
		tx = data.strSql()+"\t"+tx+"\r\n";
		if (tela(alog.nome,tx)) {
			String tx1 = data.strSql()+"\t"+alog.nome+"\t"+tx+"\r\n";
			/*System.out.print(
				str.trimm(
				str.troca(
					str.troca(
						str.troca(
							str.substrAt(tx," "),
						"\t"," * "),
					"<br>","\n"),
				"<hr>","\n")
				)+"\n\n"
			);
			*/
			System.out.print(tx1);
		}
		int t = tx.length();
		try {
			for (int i=0;i<t;i++) {
				alog.f.write((byte)tx.charAt(i));
			}
			alog.f.flush();
		} catch (IOException ioe) {
			try {
				alog.f.flush();
			} catch (Exception e) {
			}
			System.out.println("ERRO write LOG=="+ioe);
			return false;
		}

		return true;

	}
	//*********************************
	//grava log
	/*public static boolean gravaStream1(FileOutputStream olog,String tx) {
		//tx = data.strSql()+"\t"+tx+"\r\n";
		//if (log Tela) {
			//System.out.print(str.troca(str.substrAt(tx," "),"\t"," * "));
		//}
		int t = tx.length();
		try {
			for (int i=0;i<t;i++) {
				olog.write((byte)tx.charAt(i));
			}
		} catch (IOException ioe) {
			System.out.println("ERRO write LOG=="+ioe);
			return false;
		}

		return true;

	}
	*/


	//*********************************
	//retora output log
	public synchronized static arqLog getOut(String log) {

		//FileOutputStream o = (FileOutputStream)h.get(log);
		Object o[] = (Object[])h.get(log);
		if (o==null) {
			try {
				o = new Object[3];
				o[0] = dCfg+log+ext;
				o[1] = new File((String)o[0]);
				if (!((File)o[1]).exists()) ((File)o[1]).createNewFile();
				o[2] = new arqLog(log,new FileOutputStream((String)o[0],true));
				h.put(log,o);

			} catch (IOException ioe) {
				System.out.println("ERRO setando LOG="+log+"="+ioe);
				return null;
			}
		}
		return (arqLog)o[2];
	}

	private static boolean tela(String s,String tx) {
		if (lf != null) {
			return lf.tela(s,tx);
		}
		if (logTela==null) return false;
		if (logTela.equals(",.,")) return true;
		return logTela.indexOf(","+s+",")!=-1;
	}

	//*********************************
	public static void setFiltro(logsFiltro LF) {
		lf = LF;
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
