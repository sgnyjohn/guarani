package br.org.guarani.util;

import java.net.*;
import javax.net.ssl.*;
import java.io.*;
import java.util.*;

import br.org.guarani.util.*;
import br.org.guarani.servidor.*;

//set 2016 - encoding
// file.encoding	bla. 

//***************************************
//***************************************
public class web {
	/*
	FASES:
	conecta
	pede
	leCab
	lePag
	*/
	protected Socket sk;
	protected OutputStreamWriter ou;
	//public InputStream inB;
	public BufferedReader in;
	public String prot, host, hostP, ender, enderIni, ref="", cab,sBf,sErro,
		tCab,cabPer=null,post=null,proxy=null;
	public boolean ignoraTam = false,setRef=true;
	int port = 80;
	int nAg;
	public String agent = null;
	public String locat; //movido, endereço final...
	static public String agentV[] = {
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; pt-BR; rv:1.8.1.4) Gecko/20070515 Firefox/2.0.0.4"
			,"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; HbTools 4.6.4; .NET CLR 2.0.50727)"
			,"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 2.0.50727)"
			,"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; InfoPath.1)"
			,"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)"
			,"Mozilla/5.0 (Windows; U; Windows NT 5.1; pt-BR; rv:1.7.2) Gecko/20040803"
			,"Mozilla/5.0 (Windows; U; Windows NT 5.1; pt-BR; rv:1.8.1.13) Gecko/20080311 Firefox/2.0.0.13"
			,"Mozilla/5.0 (Windows; U; Windows NT 5.1; pt-BR; rv:1.8.1.16) Gecko/20080702 Firefox/2.0.0.16"
			,"Mozilla/5.0 (Windows; U; Windows NT 5.1; pt-BR; rv:1.9.0.1) Gecko/2008070208 Firefox/3.0.1"
			,"Mozilla/5.0 (Windows; U; Windows NT 5.1; pt-BR; rv:1.9) Gecko/2008052906 Firefox/3.0"
			,"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.1.16) Gecko/20080715 Ubuntu/7.10 (gutsy) Firefox/2.0.0.16"
			,"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.1.8) Gecko/20071008 Iceape/1.1.5 (Ubuntu-1.1.5-1ubuntu0.7.10)"
			,"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.1) Gecko/2008071618 Iceweasel/3.0.1 (Debian-3.0.1-1)"
			,"Mozilla/5.0 (X11; U; Linux i686; pt-BR; rv:1.8.1.16) Gecko/20080702 Iceape/1.1.11 (Debian-1.1.11-1)"
			,"Mozilla/5.0 (X11; U; Linux i686; pt-BR; rv:1.8.1.16) Gecko/20080702 SeaMonkey/1.1.11"
			,"Mozilla/5.0 (X11; U; Linux i686; pt-BR; rv:1.8.1.16) Gecko/20080715 Ubuntu/7.10 (gutsy) Firefox/2.0.0.16"
			,"Mozilla/5.0 (X11; U; Linux i686; pt-BR; rv:1.8.1.17) Gecko/20080922 Ubuntu/7.10 (gutsy) Firefox/2.0.0.17"
		};
	//"Mozilla/4.0 (compatible; MSIE 5.0; Windows 98; DigExt)";
	public boolean bCo=false,bPe=false,bLe=false,bPa=false,debug=false;
	public Hashtable hCab;
	protected int tBf = 1024*4;
	protected byte[] buf = new byte[tBf];
	protected char[] bufc = new char[tBf];
	protected int timeOut=120000;
	public int sleep=0;
	public static String nl = "\r\n";
	public Hashtable hCook = new Hashtable();
	public int maxTam = 16*1024*1024; //tam max pagina 16 MiB megas
	public String charSetSite;
	//****************************************
	public String toString() {
		return prot+"://"+host+":"+port+"/"+ender+" erro="+sErro;
	}
	//****************************************
	public String getCharSet() {
		if (charSetSite==null) {
			charSetSite = str.substrAtAt(hCab.get("content-type")+";",";charset=",";");
		}
		return charSetSite;
		/*
			Writer out = new BufferedWriter(new OutputStreamWriter(
			new FileOutputStream("outfilename"), "UTF-8"));
			try {
			out.write(aString);
			} finally {
			out.close();
			}
		*/
		
	}
	//****************************************
	public boolean leGrava(String dest) {
		String a,l;
		int nlll=0;
		
		if (!leCab()) {
			erro("lendo cab...");
			return false;
		}
	
		//atualiza referer pra naveg..
		setRef();
		
		try {
			int tm = str.inteiro(""+hCab.get("content-length"),-1),jl=0,nl;
			//rro("tm="+tm+" Dest="+dest);
			boolean sk = false;
			if (hCab!=null) {
				String te = (String)hCab.get("transfer-encoding");
				if (te!=null) {
					sk = te.equals("chunked");
				}
			}
			//encod = do servidor? Content-Type: text/html;charset=utf-8
			/*String esrv = System.getProperties().getProperty("file.encoding");
			String esite = str.substrAtAt(hCab.get("content-type")+";",";charset=",";");
			logs.grava("esrv="+esrv+" esite="+esite+" cb="+hCab.get("content-type"));
			*/
			
			arquivo1 ad = new arquivo1(dest);
			while ((nl=in.read(bufc,0,tBf))>0) {
				//ogs.grava("nlidos="+nl);
				jl += nl;
				String r = (new String(bufc,0,nl));
				//byte b[] = r.getBytes("iso8859-1");
				ad.grava(bufc,0,nl);
				//ad.grava(b,0,b.length);
				if (tm!=-1 && jl>=tm) {
					//logs.grava("fim por content-length");
					break;
				}
				if (r.length()>40 && sk && str.trimm(str.right(r,40)).toLowerCase().indexOf("</html")!=-1) {
					break;
				}
			}
			//logs.grava("nlll="+nlll+" tm="+tm+" jl="+jl);
			ad.fecha();
		} catch (java.io.IOException eio) {
			erro("erro conexção ou lendo cab="+eio);
			return false;
		}
		return true;
	}
	//****************************************
	public void cook(String s) {
		//set-cookie="ASPSESSIONIDQSCRACDQ=ELCDNKMDKLLFJGOPFICFHKBN; path=/"
		//ogs.grava("set-cook="+s);
		String v[][] = str.palavraA(s,";","=");
		for (short i=0;i<v.length;i++) {
			v[i][0] = str.trimm(v[i][0]);
			if (",domain,path,expires,Path,".indexOf(","+v[i][0]+",")!=-1) {
			} else {
				//ogs.grava("web cook: "+v[i][0]+"="+(v[i][1]));
				hCook.put(v[i][0],v[i][1]==null?"true":str.trimm(v[i][1]));
			}
		}
	}
	//****************************************
	public void post(Hashtable h) {
		post = "";
		for (Enumeration e = h.keys();e.hasMoreElements();) {
			String s = (String)e.nextElement();
			post += "&"+s+"="+str1.Escape((String)h.get(s));
		}
		post = post.substring(1);
		//logs.grava("POST="+post);
	}
	//****************************************
	public static int htmlTabela(String or,String ds) {
		arquivo1 o = new arquivo1(or);
		arquivo1 d = new arquivo1(ds);
		
		int c,nl=0;
		String b="";
		boolean tg=false,st=false;
		while ((c=o.leChar())!=-1) {
			if (c=='<' && !tg && !st) {
				//ini tag
				if (!str.vazio(b)) {
					b = str.troca(str.trimm(b),"&nbsp;"," ");
					b = str.troca(str.troca(b,"\n","\\n"),"\r","");
					d.grava(b);
				}
				b = "";
				tg = true;
			} else if (c=='>' && tg && !st) {
				//fim tag
				String tn = str.leftAt(str.trimm(b)+" "," ").toLowerCase();
				if (tn.equals("td")) {
					d.grava("\t");
				} else if (tn.equals("tr")) {
					d.grava("\n");
				} else if (tn.equals("table")) {
					d.grava("\n\n(tabela)");
				}
				b = "";
				tg = false;
			} else if (c=='"' && tg && !st) {
				//inicio string
				st = true;
			} else if (c=='"' && tg && st) {
				//fim string
				st = false;
			} else {
				char c1[] = new char[]{(char)c};
				b += new String(c1);
			}
			nl++;
		}
		o.fecha();
		d.fecha();
		return nl;
	}
	//****************************************
	public web(String h) {
		proxy = Guarani.getCfg("proxy");
		//ogs.grava("web proxy="+proxy);
		ag();
		init(h,proxy);
	}
	//****************************************
	public web() {
		proxy = Guarani.getCfg("proxy");
		ag();
	}
	//****************************************
	public web(String h,String prox) {
		ag();
		init(h,prox);
	}
	//****************************************
	public void ag() {
		nAg = agentV.length;
		while (nAg>=agentV.length) {
			nAg = (int)(Math.random()*agentV.length);
			//logs.grava("sorteou agente: "+nAg+" de "+agentV.length);
		}
	}
	//****************************************
	public void init(String h,String prox) {
		proxy = prox;
		if (proxy != null && str.equals(proxy,"http://")) {
			proxy = str.substrAt(proxy,"//");
		}		
		init(h);
	}
	//****************************************
	public void init(String h) {
		int i;

		prot = "http";
		port = 80;
		ender = "";
		setRef = true;
		cab = "";

		
		bCo=false;bPe=false;bLe=false;bPa=false;
		
		host = h;
		
		//http://
		int p = h.indexOf("://");
		if (p<7) {
			prot = h.substring(0,p);
			if (prot.equals("http")) {
				port = 80;
			} else if (prot.equals("https")) {
				port = 443;
			}
			host = h.substring(p+3);
		}
		
		i = host.indexOf("/");
		if (i!=-1) {
			ender = host.substring(i);
			host = host.substring(0,i);
		}
			
		//com PROXY?
		enderIni = "";
		hostP = host;
		if (!str.vazio(proxy)) {
			enderIni = "http://"+host;
			host = proxy;
			port = 8080;
		}
			
		i = host.indexOf(":");
		if (i!=-1) {
			port = str.inteiro(str.substrAt(host,":"),-1);
			host = str.leftAt(host,":");
		}
		
	}
	//****************************************
	public boolean conecta() {
		if (bCo) return bCo;
		bCo = true;
		try {
			if (prot.equals("https")) {
				SSLSocketFactory factory =
					(SSLSocketFactory)SSLSocketFactory.getDefault();
				SSLSocket socket =
					(SSLSocket)factory.createSocket(host, port);
				sk = (Socket)socket;
			} else {
				sk = new Socket(host,port);
			}
			sk.setSoTimeout(timeOut);
			ou = new OutputStreamWriter(sk.getOutputStream());
		} catch (Exception eio) {
			erro("erro conectando: host="+host+" porta="+port+" = "+eio);
			bCo = false;
		}
		return bCo;
	}
	//****************************************
	public boolean pede() {
		if (bPe) return bPe;
		if (!conecta()) {
			return false;
		}
		bPe = true;
		try {
			String a = cabec(ender);
			ou.write(a,0,a.length());
			deb(a);
			
			//post?
			if (post!=null) {
				ou.flush();
				deb("post="+post);
				ou.write(post,0,post.length());
			}
			ou.flush();
		} catch (java.io.IOException eio) {
			erro("erro pede: "+eio);
			bPe = false;
		}
		return bPe;
	}
	//****************************************
	public boolean leCab() {
		if (bLe) return bLe;
		if (!pede()) {
			return false;
		}
		bLe = true;

		int i=0,p;
		String l;

		hCab = new Hashtable();
		tCab = "";
		try {
			//  in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));           
 			InputStream inB = sk.getInputStream();
			in = new BufferedReader(
				new InputStreamReader(inB,
					charSetSite==null
					?System.getProperties().getProperty("file.encoding")
					:charSetSite
				)
			);
			while ((l = in.readLine())!=null) {
				if (l.length()==0) {
					break;
				}
				tCab += l+"\r\n";
				if (i==0) {
					hCab.put("?",l);
				} else {
					p = l.indexOf(": ");
					if (p<0) {
						hCab.put("?"+i,l);
					} else {
						String c = str.leftAt(l,": ").toLowerCase();
						hCab.put(c,str.substrAt(l,": "));
						if (c.equals("set-cookie")) {
							cook(str.substrAt(l,": "));
						}
					}
				}
				i++;
			}
		} catch (java.io.IOException eio) {
			erro("erro leCab: "+eio);
			bLe = false;
		}
		
		//foi movido para outro endereco
		// HTTP/1.1 301 OK
		// ...
		// Location: http://sft.if.usp.br/debian/pool/main/j/joe/joe_3.7-2.3_amd64.deb
		while ( bLe && ( locat = (String)hCab.get("location"))!=null ) {
			init( locat, proxy );
			if (!leCab()) {
				return false;
			}
		}
		return bLe;
	}
	//****************************************
	public String lePag() {
		String r = "", a,l;
		int nlll=0;
  
		if (!leCab()) {
			return null;
		}
	
		//atualiza referer pra naveg..
		setRef();

		try {
			int tm = str.inteiro(""+hCab.get("content-length"),-1),jl=0,nl;
			boolean sk = false;
			if (hCab!=null) {
				String te = (String)hCab.get("transfer-encoding");
				if (te!=null) {
					sk = te.equals("chunked");
				}
			}
			while ((nl=in.read(bufc,0,tBf))>0) {
				//logs.grava("nlidos="+nl);
				jl += nl;
				if ( jl > maxTam ) {
					//aborta ...
					logs.grava("seguranca","web.lePag() tamanho lido > "+maxTam+" "+str1.erro());
					logs.grava("erro","web.lePag() tamanho lido > "+maxTam+" "+str1.erro());
					break;
				}
				r = r + (new String(bufc,0,nl));
				if (tm!=-1 && jl>=tm) {
					//logs.grava("fim por content-length");
					break;
				}
				if (r.length()>40 && sk && str.trimm(str.right(r,40)).toLowerCase().indexOf("</html")!=-1) {
					break;
				}
			}
			//logs.grava("nlll="+nlll+" tm="+tm+" jl="+jl);
		} catch (java.io.IOException eio) {
			erro("erro conexção ou lendo cab="+eio);
			//return null;
		}
		return r;
	}
	//****************************************
	public Hashtable getCab() {
		return hCab;
	}
	//****************************************
	public String leLinha() {
		try {
			sk.setSoTimeout(timeOut);
			return in.readLine();
		} catch (Exception e) {
			erro("Lendo Linha: "+e);
		}
		return null;
	}
	//****************************************
	public String erro() {
		return sErro;
	}
	//****************************************
	private void deb(String s) {
		if (debug) {
			logs.grava("web.class debug: "+s);
		}
	}
	//****************************************
	void erro(String s) {
		if (debug) logs.grava("web.class debug: "+s);
		if (sErro==null) {
			sErro = s;
		} else {
			sErro += "<br>"+s;
		}
	}
	//****************************************
	public int setTimeOut(int a) {
		int r = timeOut;
		timeOut = a;
		return r;
	}
	//****************************************
	public void setCab(String a) {
		cab = a;
	}
	//****************************************
	public boolean fecha() {
		try {
			ou.close();
		} catch (Exception eio) {
			erro("erro fechando in: "+eio);
		}
		try {
			in.close();
		} catch (Exception eio) {
			erro("erro fechando out: "+eio);
		}
		try {
			sk.close();
		} catch (Exception eio) {
			erro("erro fechando sk: "+eio);
			return false;
		}
		return true;
	}
	//****************************************
	public boolean leTxt() {
		String l;

		try {
			sBf = "";
			while ((l = in.readLine()) != null) {
				sBf += l + "\r\n";
			}
		} catch (java.io.IOException eio) {
			erro("erro leTxt: "+eio);
			return false;
		}
		return true;
	}
	//****************************************
	public boolean leOut(OutputStream out) {
		try {
			InputStream io = sk.getInputStream();
			int read = 0;
			while ((read = io.read(buf,0,tBf)) != -1) {
				out.write(buf, 0, read);
				out.flush();
			}
			io.close();
		} catch (java.io.IOException eio) {
			erro("erro leOut: "+eio+"=="+sk);
			return false;
		}
		return true;
	}
	//****************************************
	public String getTxt() {
		return sBf;
	}
	//****************************************
	//****************************************
	//****************************************
	public String getCabTipo() {
		return (String)hCab.get("content-type");
	}
	//****************************************
	public void setRef() {
		setRef("http://"+host+ender);
	}
	//****************************************
	public void setRef(String a) {
		if (setRef) {
			ref = a;
		}
	}
	//****************************************
	public String cabec(String e) {
		String a;
		if (cabPer!=null) {
			return cabPer;
		}
		
		a  = (post==null?"GET ":"POST ")+enderIni+e+" HTTP/1.1"+nl;
		a += "Host: "+hostP+nl;
		//logs.grava("agent: "+agent[nAg]);
		a += "User-Agent: "+(agent==null?agentV[nAg]:agent)+nl;
		a += "Accept: */*"+nl;
		/*a += "User-Agent: Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.8.1.3) Gecko/20070310 Iceweasel/2.0.0.3 (Debian-2.0.0.3-1)"+nl
			+"Accept: text/xml,application/xml,application/xhtml+xml,text/tml;q=0.9,text/plain;q=0.8,image/png,* /*;q=0.5"+nl
			+"Accept-Language: pt-br,en-us;q=0.7,en;q=0.3"+nl
			+"Accept-Encoding: gzip,deflate"+nl
			+"Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7"+nl
		;
		*/
		if (!str.vazio(ref)) {
			a += "Referer: "+ref+nl;
			//logs.grava("Referer: "+ref+nl);
		}
		ender = e;
		//a += "Accept-Language: pt-br\r\n";
		//a += "Accept-Encoding: gzip, deflate\r\n";
		//a += "User-Agent: "+agent+"\r\n";
		//a += "Host: localhost\r\n";
		a += "Connection: close"+nl;
		/*if (proxy==null) {
			a += "Connection: Keep-Alive"+nl;
		} else {
			a += "Keep-Alive: 300"+nl
				+"Proxy-Connection: keep-alive"+nl
			;
		}
		*/
		if (post!=null) {
			a += "Content-Type: application/x-www-form-urlencoded"+nl
				+"Content-Length: "+post.length()+nl
			;
		}
		//cookies
		if (hCook.size()!=0) {
			String t = "";
			for (Enumeration en = hCook.keys(); en.hasMoreElements(); ) {
				String c = (String)en.nextElement();
				t += "; "+c+"="+hCook.get(c);
			}
			cab += "Cookie: "+t.substring(2)+nl;
			//logs.grava("Cookie: "+t.substring(2)+nl);
		}
		cab = str.trimm(cab);
		a += str.vazio(cab)?nl:cab+nl+nl;
		
		return a;
	}
	//****************************************
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
	//****************************************
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
	//****************************************
	public static String tabTxtHtm(String g) {
		String r = "<table border><tr><td>";
		r += str.troca(str.troca(g,"\r\n","<tr>\r\n<td>"),"\t","<td>");
		r += "</table>";
		return r;
	}
	//****************************************
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
				if (g.substring(i,i+3).compareToIgnoreCase("<tr")==0) {
					l++;
					if (l>0) r += "\r\n";
					c = -1;
				} else if (g.substring(i,i+3).compareToIgnoreCase("<td")==0) {
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

		return r;

	}
	//****************************************
	public void proxybin(Socket s,OutputStream out) {
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
			erro("erro proxybin="+eio);
		}

	}
	//****************************************
	public void proxy(String e,OutputStream out) {
		String a;

		try {
			sk = new Socket(host,80);
			OutputStreamWriter oo = new OutputStreamWriter(sk.getOutputStream());
			a = cabec(e);
			oo.write(a,0,a.length());
			oo.flush();

			proxybin(sk,out);

			sk.close();

		} catch (java.io.IOException eio) {
			erro("erro conexção ou lendo cab="+eio);

		}

	}
}

/*
POST /apl/ptsul.class? HTTP/1.1
Host: pt-coord071.pt-alergs.br
User-Agent: Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.8.1.3) Gecko/20070310 Iceweasel/2.0.0.3 (Debian-2.0.0.3-1)
Accept: text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,* / *;q=0.5
Accept-Language: pt-br,en-us;q=0.7,en;q=0.3
Accept-Encoding: gzip,deflate
Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7
Keep-Alive: 300
Connection: keep-alive
Referer: http://pt-coord071.pt-alergs.br/apl/ptsul.class?op=tabela&__tabela=Pessoa&__modo=A&__tabela1=0&_C_Pessoa_=1
Cookie: usu=signey; agendaRecepcao=sig; c_3ecafeac2b0f9117b21a07cffc2ceff0=SNNN; c_4473af42b905a9412b6b94c91c696352=SNN; c_7af6d12b4cb4b4427849945de356b68d=NSN; agendaPessoa=3; GSESSIONID=0070d28c08680039364a
Content-Type: application/x-www-form-urlencoded
Content-Length: 197

op=tabela&__tabela=Pessoa&__modo=A&__form=1177095897254&_C_Pessoa_=1&__tabela1=0&_C_PessoaTipo_=1&_Pessoa_=topo+dgdfgd+dfssdfsd&_C_Mun_C_MunUF_=24&_C_Mun_=9505&_Endereco_=sfddf&_Cep_=&__manda=Grava

HTTP/1.1 200 OK
Date: Fri, 20 Apr 2007 19:05:18 GMT
Server: Guarani 1.1
Cache-Control: private
Cache-Control: no-store
Content-Type: text/html; charset=iso-8859-1
Keep-Alive: timeout=15, max=100
Connection: Keep-Alive
Transfer-Encoding: chunked
*/
