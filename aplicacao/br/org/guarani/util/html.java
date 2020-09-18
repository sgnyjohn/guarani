package br.org.guarani.util;

import java.net.*;
import java.io.*;
import java.util.*;
import br.org.guarani.util.*;


//****************************************
//****************************************
public class html {
	public final static String lf="\r\n";
	protected Socket sk;
	protected OutputStreamWriter ou;
	protected BufferedReader in;
	//pedido
	protected String host,ender,ref,cab="";
	protected int porta;
	protected int timeOut=120000;
	//resposta
	public String tCab,tCorpo;
	public Hashtable hCab;
	public boolean erro=false;
	public String sErro;

	//****************************************
	public html(String host) {
		this(host,80);
	}
	//****************************************
	public String toString() {
		return "html{host="+host+",porta="+porta
			+",ender="+ender+"}";
	}
	//****************************************
	public html(String host,int porta) {
		if (host.indexOf("://")!=-1) {
			ender = str.substrAt(host,"://");
			ender = "/"+str.substrAt(ender,"/");
			host = str.substrAtAt(host,"://","/");
			if (host.indexOf(":")!=-1) {
				porta = str.inteiro(str.substrAt(host,":"),80);
				host = str.leftAt(host,":");
			}
		}
		this.host = host;
		this.porta = porta;
	}
	//****************************************
	public boolean conecta() {
		try {
			sk = new Socket(host,80);
			ou = new OutputStreamWriter(sk.getOutputStream());
			sk.setSoTimeout(timeOut);
		} catch (java.io.IOException eio) {
			erro("erro conectando: "+eio);
			return false;
		}
		return true;
	}
	//****************************************
	public String lePag() {
		return lePag(ender);
	}
	//****************************************
	public String lePag(String e) {
		String l, a;
		int nlll=0;
		if (!conecta()) {
			return null;
		}
		a = cabec(e);
		try {
			ou.write(a,0,a.length());
			ou.flush();
			if (!leCab()) {
				return null;
			}
			tCorpo = "";
			while ((l = in.readLine()) != null) {
				tCorpo += l+lf;
				nlll++;
			}
		} catch (java.io.IOException eio) {
			erro("erro lendo Corpo="+eio);
			tCorpo += "<font color=red><b>ERRO LENDO Corpo</b></font>";
		}
		return tCorpo;
	}
	//****************************************
	private void erro(String s) {
		sErro = s;
		erro = true;
	}
	//****************************************
	public int setTimeOut(int a) {
		int r = timeOut;
		timeOut = a;
		return r;
	}
	//****************************************
	public void addCab(String s) {
		cab += str.trimm(s)+lf;
	}
	//****************************************
	public String getCab(String s) {
		if (s==null) return tCab;
		if (hCab==null) initCab();
		return ""+hCab.get(s.toLowerCase());
	}
	//****************************************
	private void initCab() {
		hCab = new Hashtable();
		String l,v[]=str.palavraA(tCab,lf);
		for (short i=0;i<v.length;i++) {
			l = v[i];
			if (i==0) {
				hCab.put("?",l);
			} else {
				int p = l.indexOf(": ");
				if (p<0) {
					hCab.put("?"+i,l);
				} else {
					hCab.put(
						str.leftAt(l,": ").toLowerCase()
						,str.substrAt(l,": ")
					);
				}
			}
		}
	}
	//****************************************
	public boolean leCab() {
		int i=0,p;
		String l;
		tCab = "";
		try {
			in = new BufferedReader(
				new InputStreamReader(sk.getInputStream())
			);
			while ((l = in.readLine()).compareTo("")!=0) {
				tCab += l+lf;
			}
		} catch (java.io.IOException eio) {
			erro("erro leCab: "+eio);
			return false;
		}
		return true;
	}
	//****************************************
	public void setRef() {
		ref = "http://"+host+ender;
	}
	//****************************************
	public void setRef(String a) {
		ref = a;
	}
	//****************************************
	public String cabec(String e) {
		String a;
		a  = "GET "+e+" HTTP/1.0"+lf;
		a += "Accept: */*"+lf;
		if (ref!=null) {
			a += "Referer: http://"+ref+lf;
		}
		ender = e;
		a += "Accept-Language: pt-br"+lf;
		a += "Accept-Encoding: gzip, deflate"+lf;
		a += "User-Agent: Mozilla/4.0 "
			+"(compatible; MSIE 5.0; Windows 98; DigExt)"+lf;
		a += "Connection: Keep-Alive"+lf+cab+lf;
		return a;
	}
	/****************************************/
	// STATICAS
	/****************************************/
	//****************************************
	//
	public static Hashtable camposForm(String tx) {
		Hashtable h = new Hashtable();
		h.put("input","");
		h.put("select","/select");
		h.put("textarea","/textarea");
		return tags(tx,h);
	}
	//****************************************
	//tg string como esta: ,input,select,ratio,
	public static Hashtable tags(String tx,Hashtable ht) {
		int pos=0,p,p1,nr=0,e=9999999;
		String tag,b;
		String txl = tx.toLowerCase();
		Hashtable r = new Hashtable();
		while ((pos=tx.indexOf("<",pos))!=-1) {
			p = tx.indexOf(" ",pos);
			p = p==-1?e:p;
			p1 = tx.indexOf(">",pos);
			p1 = p1==-1?e:p1;
			p = p<p1?p:p1;
			if (p!=e) {
				tag = tx.substring(pos+1,p).toLowerCase();
				b = (String)ht.get(tag);
				if (b!=null) {
					if (b.length()!=0) {
						p1 = txl.indexOf("<"+b,pos);
						if (p1==-1) {
							p = tx.indexOf(">",pos);
						} else {
							p = tx.indexOf(">",p1);
						}
					} else {
						p = tx.indexOf(">",pos);
					}
					r.put(""+nr,tx.substring(pos,p+1));
					nr++;
				}
			}
			pos++;
		}
		return r;
	}
	//****************************************
	// retorna todas as ocorrencias ini até fim inclusive na string
	// exemplo tags(tx,"<form","</form")
	public static Hashtable tags(String tx,String ini,String fim) {
		int pos=0,p,nr=0;
		String txl = tx.toLowerCase(),a,b;
		Hashtable r = new Hashtable();
		while ((pos=txl.indexOf(ini,pos))!=-1) {
			p = txl.indexOf(fim,pos);
			if (p==-1) break;
			if (str.equals(fim,"<")) {
				p = txl.indexOf(">",p);
				if (p==-1) break;
				r.put(""+nr,tx.substring(pos,p+1));
			} else {
				r.put(""+nr,tx.substring(pos,p+fim.length()));
			}
			nr++;
			pos=p+1;
		}
		return r;
	}
	public static Hashtable retTag(String g,String a) {
		int i,st=0,f;
		String op="",vr="";
		char s;
		Hashtable r = new Hashtable();
		boolean fim = false;
		i = g.toLowerCase().indexOf("<"+a);
		if (i<0) return r;
		f = i;
		r.put("tag",a);
		i = i+a.length()+1;
		for (i=i;i<g.length();i++) {
			s = g.charAt(i);
			if (st==0) {
				if (s == '>') {
					fim = true;
				} else if (op=="" & s==' ') {
				} else if (s=='=') {
					st = 1;
				} else {
					op += s;
				}
			} else if (st==1) {
				if (vr=="" & s==' ') {
				} else if (s==' ' | s=='>') {
					fim = true;
				} else if (s=='"') {
					st = 2;
				} else {
					vr += s;
				}
			} else if (st==2) {
				if (s=='"') {
					fim = true;
				} else {
					vr += s;
				}
			}
			if (fim) {
				st = 0;
				fim = false;
				if (op.trim() != "") r.put(op.trim().toLowerCase(),vr);
				vr = "";
				op = "";
				f = i+1;
				if (s=='>') i = g.length();
			}
		}
		i = g.toLowerCase().indexOf("</"+a,f);
		if (i!=-1) r.put("conteudo",g.substring(f,i));
		return r;
	}
	/****************************************/
	public static Hashtable retAtalhos(String g) {
		int i=0,p,p1,nv=0;
		String g1;
		g1 = g.toLowerCase();
		Hashtable r = new  Hashtable();
		while ((p=g1.indexOf("<a ",i))>-1) {
			i = p+3;
			p1 = g1.indexOf("</",i);
			if (p1>-1) {
				i = p1+2;
				r.put(String.valueOf(nv++),retTag(g.substring(p,i),"a"));
			}
		}
		return r;
	}
	/****************************************/
	public static String tabTxtHtm(String g) {
		String r = "<table border><tr><td>";
		r += str.troca(str.troca(g,lf,"<tr>"+lf+"<td>"),"\t","<td>");
		r += "</table>";
		return r;
	}
	/****************************************/
	public static String tabHtmTxt(String g) {
		int i,t;
		int l=-1,c=-1,nc=0;
		String r="";
		char s;
		boolean esp;
		esp = false;
		t = g.length();
		for (i=0;i<t;i++) {
			s = g.charAt(i);
			if (esp & s=='>') {
				esp = false;
			} else if (s=='<') {
				esp = true;
				if (g.substring(i,i+3).compareToIgnoreCase("<tr")==0) {
					l++;
					if (l>0) r += lf;
					c = -1;
				} else if (g.substring(i,i+3).compareToIgnoreCase("<td")==0) {
					nc++;
					c++;
					if (c>0) r += "\t";
				}
			} else if (!esp & s!='\n' & s!='\r') {
				r += s;
			}
		}
		return r;
	}
}
