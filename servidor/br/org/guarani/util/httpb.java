package br.org.guarani.util;

import java.net.*;
import java.io.*;
import java.util.*;

/****************************************/
/****************************************/
public class httpb {

	protected Socket sk;
	protected OutputStreamWriter ou;
	protected BufferedReader in;
	protected String host, ender, ref, cab;


	/****************************************/
	public httpb(String h) {
		int i;

		host = h;
		ender = "";
		ref = "";
		cab = "";

		//http://
		if (h.substring(0,7).compareTo("http://")==0) {
			host = h.substring(7,h.length());
			i = host.indexOf("/");
			if (i!=0) {
				ender = host.substring(i,host.length());
				host = host.substring(0,i);
			}
		}

	}

	/****************************************/
	public String lePag() {
		return lePag(ender);
	}

	/****************************************/
	public void setRef() {
		ref = "http://"+host+ender;
	}

	/****************************************/
	public void setRef(String a) {
		ref = a;
	}

	/****************************************/
	public void setCab(String a) {
		cab = a;
	}

	/****************************************/
	public String cabec(String e) {

		String a;

		a  = "GET "+e+" HTTP/1.0\r\n";
		a += "Accept: */*\r\n";
		if (ref!="") {
			a += "Referer: http://"+ref+"\r\n";
		}
		ender = e;
		a += "Accept-Language: pt-br\r\n";
		a += "Accept-Encoding: gzip, deflate\r\n";
		a += "User-Agent: Mozilla/4.0 (compatible; MSIE 5.0; Windows 98; DigExt)\r\n";
		a += "Host: localhost\r\n";
		a += "Connection: Keep-Alive\r\n"+cab+"\r\n";

		return a;
	}


	/****************************************/
	public String lePag(String e) {

		String r = "", a,l;

		a = cabec(e);

		try {
			sk = new Socket(host,80);
			//System.out.println("Abriu sk");

			ou = new OutputStreamWriter(sk.getOutputStream());
			ou.write(a,0,a.length());
			ou.flush();
			//System.out.println("Gravou pedido!="+e);

			in = new BufferedReader(new InputStreamReader(sk.getInputStream()));
			while ((l = in.readLine()) != null) {
				//System.out.println(l);
				r += l+"\r\n";
			}
			//System.out.println("Leu sk");

		} catch (java.io.IOException eio) {
			System.out.println("http class: erro conexção ou lendo cab");

		}


		return r;

	}


	/****************************************/
	public static Hashtable retTag(String g,String a) {
		int i,st=0,f;
		String op="",vr="";
		char s;
		Hashtable r = new Hashtable();
		boolean fim = false;

		//g = g.toLowerCase();
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
				//System.out.println(op);
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

	/*public String[] retAtalho(String g) {
		int i,f;
		i = Math.max(g.indexOf("<a "),0);
		i = g.indexOf(" src=");
		if (g.substring(i+5,i+6).compareTo("\"")==0) {
		}

	}

	*/

	/****************************************/
	public static String tabHtmTxt(String g) {
		int i,t;
		int l=-1,c=-1,nc=0;
		String r="";
		char s;
		boolean esp;
		//java.util.Vector v;
		//g = str.troca(str.troca(g,"\n",""),"\r","");

		esp = false;
		t = g.length();
		for (i=0;i<t;i++) {
			//s = g.substring(i,i+1);
			s = g.charAt(i);
			if (esp & s=='>') {
				esp = false;
			} else if (s=='<') {
				esp = true;
				if (g.substring(i,i+3).compareTo("<tr")==0) {
					l++;
					if (l>0) r += "\r\n";
					c = -1;
				} else if (g.substring(i,i+3).compareTo("<td")==0) {
					//c++;
					//v.set(l,v.get(l).add(""));
					nc++;
					c++;
					if (c>0) r += "\t";
				}
			} else if (!esp & s!='\n' & s!='\r') {
				r += s;
			}
		}

		//System.out.println();
		//System.out.println("nl="+l+" ncl="+nc);
		//System.out.println();
		return r;

	}


	public void proxybin(Socket s,OutputStream out) {
		int tBf = 1024*4;
		byte[] buf = new byte[tBf];

		try {

			InputStream io = s.getInputStream();

			int read = 0;
			//!out.checkError() &
			while ((read = io.read(buf,0,tBf)) != -1) {
				out.write(buf, 0, read);
				out.flush();
			}

			io.close();

		} catch (java.io.IOException eio) {
			System.out.println("http class: erro proxybin");
		}

	}

	public void proxy(String e,OutputStream out) {

		String a;

		try {
			sk = new Socket(host,80);
			//System.out.println("Abriu sk");
			OutputStreamWriter oo = new OutputStreamWriter(sk.getOutputStream());
			a = cabec(e);
			oo.write(a,0,a.length());
			oo.flush();
			//oo.close();

			proxybin(sk,out);

			//out.close();
			sk.close();

		} catch (java.io.IOException eio) {
			System.out.println("http class: erro conexção ou lendo cab");

		}

	}

}
