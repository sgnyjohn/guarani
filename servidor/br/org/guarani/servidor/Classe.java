package br.org.guarani.servidor;

import br.org.guarani.util.*;

import java.io.*;
import java.util.Hashtable;
import java.lang.reflect.Method;
import java.util.Enumeration;

//*************************
public class Classe {
	//path .class
	static String classes;
	//path .java
	static String classesj[],raizjava;
	//cmd compila
	static String compila;
	String superClasses;

	//logs
	static protected arqLog logC;
	static boolean procuraJava,procuraClasses;
 
	String nome;
	String subdir;
	File arquivo;
	long alterado;
	Class classe;
	File arquivoj;
	long alteradoj;
	boolean dep,dev;
	boolean jacompilada = false;
	boolean carregada = false;
 
	//*************************
	protected boolean compilar() {
		if (!procuraJava | !existe()) {
			return false;
		} else if (arquivoj==null || !arquivoj.exists()) {
			return false;
		} else if (arquivoj.lastModified()!=alteradoj) {
			return true;
		} else if (arquivo.lastModified()<arquivoj.lastModified()) {
			return true;
		}
		return false;
	}
 
	//*************************
	protected boolean recarregar() {
		if (!procuraClasses | !existe()) {
			return false;
		} else if (arquivo.lastModified()!=alterado) {
			return true;
		}
		return false;
	}
 
	//*************************
	protected boolean existe() {
		boolean r = false;
		if (arquivoj==null) {
			//logs.grava("classe="+arquivo.exists());
			r = arquivo.exists();
		} else {
			//logs.grava("classe="+arquivo.exists());
			//logs.grava("classej="+arquivoj.exists());
			r = arquivo.exists() || arquivoj.exists();
		}
		if (!r) {
			logs.grava("não existe "+arquivoj+" "+arquivo);
		}
		return r;
	}
 
	//*************************
	protected byte[] carrega() {
		int read = 0;
		int t = (int)arquivo.length();
		byte[] buf = new byte[t];
		try {
			//InputStreamReader r = new InputStreamReader(new FileInputStream(f));
			FileInputStream r = new FileInputStream(arquivo);
			read = r.read(buf,0,t);
			r.close();
		} catch (IOException e ) {
			logs.gravaStream(logC,"ERRO ARQ "+arquivo.getPath()+"<br>");
			logs.gravaStream(logC,e.toString());
			return new byte[0];
		}
  
		//gcc##
		carregada = true;
		alterado = arquivo.lastModified();

		return buf;
	}
 
	//*************************
	public static void setDirs(String compil,String cl,String clj) {
		compila = compil;
		procuraClasses = compil.length()>0; 
		procuraJava = clj.length()>0;
  
		classes = str.dir(cl);
  
		if (procuraJava) {
			String s[] = str.palavraA(clj,";");
			raizjava = str.dir(s[0]);
			classesj = new String[s.length-1];
			for (int i=1;i<s.length;i++) {
				classesj[i-1] = str.dir(s[i]);
			}
		}
  
		logC = logs.getOut("class");
	}
 
	//*************************
	public Classe(Pedido ped,String nom) {
		nome = nom;
  
		//ver .class
		arquivo = new File(classes+nome+".class");
		alterado = arquivo.lastModified();
  
		//localiza .java
		if (raizjava!=null) procJava();
  
		/*sj
		//compilar
				//logs.debug("ped="+ped+" a="+arquivo+" aj="+arquivoj);
		if (arquivoj != null) {
				//logs.debug("ped="+ped+" a="+arquivo+" aj="+arquivoj);
			if (!arquivo.exists() ||
							arquivo.lastModified()<arquivoj.lastModified()) {
				//logs.debug("ped="+ped+" a="+arquivo+" aj="+arquivoj);
				compila(ped);
			}
		}
		*/
  
	}
 
	//*************************
	private void procJava() {
		File fj;
		for (int i=0;i<classesj.length;i++) {
			//System.out.println(raizjava+classesj[i]+nome+".java");
			if ((fj = new File(raizjava+classesj[i]+nome+".java")).exists()) {
				//guarda subdir para escolha de bibliotecas na compilação
				arquivoj = fj;
				alteradoj = arquivoj.lastModified();
				subdir = classesj[i];
				return;
			}
		}
		return;
	}
	//*************************
	protected String arquivo() {
		return arquivoj.getAbsolutePath();
	}
	//*************************
	protected static boolean compila(Pedido ped,String cl) {
		//String nome = arquivoj.getAbsolutePath();
		ped.print("<html><font color=blue><b>Compilando: "
			+cl+"</b></font><br>");
  
		//logs.grava(((f.exists())?"final ":"desv ")+classe_root+nome);
		logs.grava("classe","Compilando-> "+cl+" "+ped);
  
		executa e = new executa();
		e.exec(ped,compila+" -d "+classes+" "+cl,null,new File(classes));
		int pe = e.exitValue();
  
		if (pe>0) {
			ped.println("<hr>SAIDA Compilação:<hr><pre>"+
			str.troca(str.troca(e.getOut(),"<","&lt;"),">","&gt;")+"</pre>");
			ped.println("<hr>ERRO Compilando:<hr><font color=red><pre>"+
			str.troca(str.troca(e.getErr(),"<","&lt;"),">","&gt;")+
			"</pre></font><hr>");
			return false;
		}
  
		//alteradoj = arquivoj.lastModified();
		//alterado = arquivo.lastModified();
		//classe = null;
		return true;
  
	}
 
	//*************************
	protected String mostra() {
		return
		"<td>"+((alteradoj==0)?"":data.strSql(alteradoj)+"<br>")+
		data.strSql(alterado)+
		"<td>"+((dep)?"-":"<font color=red>(C)")+"<td>"+
		((dev)?"-":"<font color=green>(F)")+
		"<td>"+superClasses;
	}
  
}
