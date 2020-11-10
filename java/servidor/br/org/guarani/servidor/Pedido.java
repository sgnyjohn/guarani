/*
* Signey John jan/2001.
transf chunked jul/2010
auth SSL X509Certificate - set/2016
	
HTTP/1.1 200 OK
Content-Type: text/plain
Transfer-Encoding: chunked

25
This is the data in the first chunk

1C
and this is the second one

3
con
8
sequence
0
	
*/

package br.org.guarani.servidor;

import javax.servlet.*;
import br.org.guarani.util.*;
import java.util.*;
import java.net.*;
import java.io.*;

import java.security.*;
import javax.net.ssl.*; //SSLServerSocket;
import java.security.cert.X509Certificate;

//**********************************************
//**********************************************
public class Pedido {
	public httpSessao sessao;
	public String ip;
 
	public boolean msie=false,win=false;
	public String browser;
	private Http prot;
	private long testeErro=0,ultFlush=0,milisFlush;
 
	protected boolean segura = false;
	public boolean proxy = false;
	public int debug=2;

	private Socket sk;
	private OutputStream os;
	public GravadorHttp out;

	//armazena o cab resposta cliente
	public String cab=null;
	protected int nBytes;

	//cabeçalho do pedido
	public Hashtable ped;
	//string get... acho
	public String queryString,raizWeb;
	//variaveis passadas por get ou post
	public Hashtable h = new Hashtable();
	public Hashtable cook = new Hashtable();

	public boolean erro = false;

	//servlet
	public Hashtable servletP;
 
	//para post com arquivo anexo
	public ServletInputStream io=null;
	int naImg = 0;
	public boolean chunked = false;
	public String outArq = null;
	boolean naoSessao = false;
	boolean keepAlive = false;

	public boolean naoSessao() {
		return naoSessao;
	}
	//***********************************
	//public int write(byte[] b,int pi,int tm) {
	//	return out.write(b,pi,tm);
	//}
	//*************************************************
	public boolean x509Val() {
		//logs.grava("x509","https="+(prot.https?"sim":"nao")+" val509 ("+prot.cnf.get("keyNeedClientAuth")+")");
		return prot.https && "true".equals(""+prot.cnf.get("keyNeedClientAuth"));
	}		
	//***********************************
	public String dirRoot() {
		return prot.dirRoot();
	}
	//***********************************
	public Object getHttpConf(String s) {
		return prot.getConf(s);
	}

	//***********************************
	public PrintWriter getWriter() {
		try {
			return new PrintWriter(out.outb);
		} catch (Exception e) {
		}
		return null;
	}

	//***********************************
	public X509Certificate getCliCert() {
		X509Certificate r = null;
		try {
			if (sk.getClass().getName().indexOf("SSL")!=-1) {
				//sun.security.ssl.SSLSocketImpl
				SSLSession sls = ((SSLSocket)sk).getSession();//sk.getHandshakeSession();
				//on("sk="+sk.getClass().getName()+" sslS="+sls,"p");
				//javax.security.cert.X509Certificate c[] = sls.getPeerCertificateChain();
				X509Certificate cr[] = (X509Certificate[])sls.getPeerCertificates();
				if (cr.length>0) {
					r = cr[0];
					if (cr.length>1) {
						logs.grava("https","usuário apresentou mais de um X509... assumindo o [0]");
					}
				}
			}
		} catch (Exception e) {
		}
		return r;
	}
	//***********************************
	public String debug() {
		String r = "<table border=1>";
		Hashtable h;
		h = this.ped;
		r += "<tr><td colspan=2><h3>Cab Pedido</h3>";
		for (Enumeration e=h.keys();e.hasMoreElements();) {
			String ch = ""+e.nextElement();
			r += "<tr><td>"+ch+"<td>"+str.html(""+h.get(ch));
		}
		//ped
		h = this.h;
		r += "<tr><td colspan=2><h3>Pedido</h3>";
		for (Enumeration e=h.keys();e.hasMoreElements();) {
			String ch = ""+e.nextElement();
			r += "<tr><td>"+ch+"<td>"+str.html(""+h.get(ch));
		}
		//ped
		h = this.cook;
		r += "<tr><td colspan=2><h3>Cookies</h3>";
		for (Enumeration e=h.keys();e.hasMoreElements();) {
			String ch = ""+e.nextElement();
			r += "<tr><td>"+ch+"<td>"+str.html(""+h.get(ch));
		}
		return r+"</table>";
	}
	//***********************************
	public void img(byte buf[]) {
		//logsL.grava(nome+",img="+tam);
		String lf = "\r\n";
		if (ped.get("if-modified-since")!=null && naImg<23) {
			naImg++;
			cab = "HTTP/1.0 304 Not Modified"+lf;
			o("");
			return;
		}
		naImg=0;
		if (cab==null) {
			String er = "Você enviou informações ao cliente antes"
				+" do envio do cabecalho...";
			erro(er);
			return;
		}
		cab = str.troca(cab,"Cache-Control: private"+lf
				+ "Cache-Control: no-store"+lf,"");
		setMime("image/gif");
		cab += "Last-Modified: "+data.strHttp(data.ms())+lf;
		cab += "Content-Length: "+buf.length+lf;
		o("");
		out.write(buf,0,buf.length);
	}
	//**************************************/
	public String getCookie(String nome,String padrao) {
		if (str.vazio(getCookie(nome))) {
			return padrao;
		}
		return getCookie(nome);
	}
	//**************************************/
	public String getCookie(String nome) {
		return (String)cook.get(nome);
	}
	//**************************************/
	// coloca em cookie dados formulário
	public boolean setCookie(String nome,String valor) {
		return setCookie(nome,valor,null);
	}
	//**************************************/
	public boolean setCookie(String nome,String valor,String vcto) {
		/*
			Set-Cookie: oreo=a5-07UNJk:70; expires=Mon, 09 Jun 2003 17:22:30 GMT; path=/
			Set-Cookie: geoloc=br; expires=Tue, 18 Mar 2003 17:22:27 GMT; path=/M
			Set-Cookie: locin=lang=None&loc=br&VT=14;
					expires=Thursday, 10 March 2005 23&#58;59&#58;59 GMT;
					path=/; domain=.real.comM
		*/
		if (cab==null) {
			return false;
		}
		if (valor==null) {
			valor = getString(nome);
			if (valor==null) {
				return false;
			}
		}
  
		//calcula vencimento exemplo a10,2m,4d,6h,59M
		//  (dez anos, etc...)
		if (vcto!=null && vcto.indexOf(" ")==-1) {
			String v[] = str.palavraA(vcto,",");
			long l = data.ms(), dia = 3600000*24;
			for (short i=0;i<v.length;i++) {
				String s = v[i].substring(0,1);
				int inc = str.inteiro(v[i].substring(1),0);
				if (s.equals("a")) { //ano
					l += dia*365.25*inc;
				} else if (s.equals("m")) { //
					l += dia*30.44*inc;
				} else if (s.equals("d")) { //
					l += dia*inc;
				} else if (s.equals("h")) { //
					l += 3600000*inc;
				} else if (s.equals("M")) { //
					l += 60000*inc;
				}
			}
			vcto = data.strHttp(l);
		}
  
		cab += "Set-Cookie: "+nome+"="+valor+
			(vcto!=null?"; expires="+vcto:"")+"; path=/"+prot.lf;
		return true;
	}
 
	//**************************************/
	public boolean recuperaAmbiente() {
		Object oret[] = (Object[])sessao.get("retorna"); 
		if (oret==null) {
			return false;
		}
		sessao.remove("retorna");
		ped.remove("?");
		ped.put("?",(String)oret[0]); //get ou post
		h = (Hashtable)oret[1]; //parametros
		return true;
	}

	//**************************************/
	public boolean guardaAmbiente() {
		sessao.put("retorna",
			new Object[]
				{getCab("?"),
					h,
					"",
					""+data.ms(),
					queryString
				}
			);
		return true;
	}

	//**************************************/
	public boolean redireciona(String url,String cb) {
		if (cab==null) {
			return false;
		}
		/*cab = prot.httpVer+" 302 Moved Temporarily"+Http.lf
			+"Location: "+url+Http.lf
			+prot.respp()
		;
		on(prot.movTemp );
		*/
		prot.movTemp(url,cb);

		return true;
	}
 
	//**************************************/
	public Hashtable getParametros() {
		return h;
	}
	//**************************************/
	public void setParametros(Hashtable hs) {
		h = hs;
	}
	//**************************************/
	public httpSessao getSessao() {
		return sessao;
	}
	//**************************************/
	public String toString() {
		String s = "";
		if (h.get("temSenha")==null) {
			s += h;
		} else {
			s += " *temSenha* ";
		}
		return "(<b>pedido</b>: param="
			//(s.length()>40?s.substring(0,40)+", ...}":s)
			+s
			+" sk="+sk+" "+sessao+")";
	}
	//**************************************/
	public boolean addCab(String s) {
		if (cab==null) {
			return false;
		} else {
			cab += s+Http.lf;
			return true;
		}
	}
	//**************************************/
	public boolean setMime(String mime) {
		if (cab==null) {
			return false;
		} else {
			if (str.equals(mime,".")) {
				mime = Guarani.tipos.getTipo(mime);
				if (mime==null) {
					mime = Guarani.tipos.getTipo(".exe");
				}
			}
			cab = str.troca(cab,Guarani.tipos.getTipo(".html"),mime);
			return true;
		}
	}
	//********************************
	public void movTemp(String url, String cb) {
		prot.movTemp(url, cb);
	}	
	//**************************************/
	public boolean mandaArq(String arq,String nome,boolean attach) {
		return prot.mandaArq(arq,nome,attach);
	}
	//**************************************/
	public boolean setTxt() {
		return setMime(Guarani.tipos.getTipo(".txt"));
	}

	//**************************************/
	//inicializa
	public String[] executa(String s) {
		executa e = new executa();
		e.exec(this,s);
		return new String[] {e.getOut(),e.getErr()};
	}
	//**************************************/
	public Hashtable getCab() {
		return ped;
	}
	//**************************************/
	public String getCab(String s) {
		return (String)ped.get(s);
	}
	//**************************************/
	//inicializa
	public Pedido(Http pr,Hashtable pd) {
		prot = pr;
		ped = pd;
		
		milisFlush = str.inteiro(""+prot.cnf.get("milisFlush"),500);
  
		//armazena cookie
		cook = new Hashtable();
		String c = (String)ped.get("cookie");
		if (c!=null) {
			String co,con="",v[] = str.palavraA(c,";");
			for (int i=0;i<v.length;i++) {
				co = str.trimm(str.leftAt(v[i],"="));
				con = str.trimm(str.substrAt(v[i],"="));
				if (cook.get(co)!=null) {
					con = cook.get(co)+"~~"+con;
					cook.remove(co);
				}
				cook.put(co,con);
			}
		}

		// user-agent=Mozilla/4.0 (compatible; MSIE 4.01
		browser = (String)ped.get("user-agent");
		//limita velocidade output msie
		//Mozilla/5.0 (Windows; U; Win98; pt-BR; rv:1.6) Gecko/20040206 Firefox/0.8
		if (browser!=null) {
			msie = browser.indexOf("MSIE")!=-1;
			win = browser.indexOf("Win")!=-1;
		}
  
		//via proxy
		if (ped.get("via")!=null) {
			proxy = true;
		}

	}
	//**************************************/
	public void setCab(String s) {
		cab = s;
	}
	//**************************************/
	public void setOut(GravadorHttp p,Socket s) {
		sk = s;
		out = p;
		nBytes = 0;
	}
	//**************************************/
	public void setSessao(httpSessao s) {
		ip = s.ip;
		sessao = s;
		//us = new us_tmp("?",sessao.ip);
	}
	//**************************************/
	//public PrintWriter getOut() {
	//	return out;
	//}
	public OutputStream getOutputStream() {
		return out.getOutputStream();
	}
	//**************************************/
	public void resetGet() {
		//h = new StrVetor();
		h = new Hashtable();
	}
	//**************************************/
	public void setGet(String a) {
		queryString = a;
		setPost(a);
	}
	//**************************************/
	public void setPost(String a) {
		a = str.trimm(a);
		String b[] = str.palavraA(a,"&");
		for (int i=0;i<b.length;i++) {
			String p = str.UnEscape(str.leftAt(b[i],"="));
			int x = 0;
			while (h.get(p)!=null) {
				p = (x==0?p:str.leftRat(p,"~"))+"~"+(x++);
				//logs.grava(p);
			}
			h.put(
				p,
				str.UnEscape(str.substrAt(b[i],"="))
			);
		}
	}
	/*/**************************************
	public StrVetor getParam() {
		return h;
	}
	*/
	//**************************************/
	public String getString(String a) {
		return (String)(h.get(a));
	}
	//**************************************/
	public String getString(String a,String b) {
		return str.seNull(getString(a),b);
	}
	//**************************************/
	public void flush() {
		try {
			out.flush();
		} catch (Exception e) {
			logs.grava("task","Erro flush!!=="+sk);
		}
	}
	//**************************************/
	public boolean testaErro() {
		long t = System.currentTimeMillis();
		if (!erro && t-testeErro>1000) {
			testeErro = t;
			//logs.grava("task","testando ERRO!!");
			//flush();
			erro = out.checkError();
			if (erro) {;
				logs.grava("out","CLIENTE sok="+sk);
			}
		}
		return erro;
	}
	//**************************************/
	public boolean println(String a) {
		return print(a+Http.lf);
	}
	//**************************************/
	public boolean o(String a) {
		return print(a);
	}
	//**************************************/
	public boolean on(String a,String tag) {
		a = "<"+tag+">"+a+"</"+str.leftAt(tag," ")+">";
		return println(a);
	}
	//**************************************/
	public boolean on(String a) {
		return println(a);
	}
	//**************************************/
	public long getBytes() {
		return nBytes;
	}
	//**************************************/
	public void erro(String s) {
		erro(s,new Exception());
	}
	//**************************************/
	public void erro(String s,Exception e) {
		String stv[],a,st,st1;
		st = this+"<hr>";
		st1 = this+"\n";
		stv = str.erroA(new Exception());
		st += "<p class=erro>ERRO: "+s+"</p>";
		st1 += "ERRO: "+s+"\n";
		if (stv.length>2) {
			st += "<p class=erroDet>DETECTADO EM: "+stv[2]+"</p>";
			st1 += "DETECTADO EM: "+stv[2]+"\n";
		}
		st += "<hr>";
		stv = str.erroA(e);
		if (!stv[0].equals(""+e)) {
			st += "<p class=erroTr>"+e+"</p>";
			st1 += e+"\n";
		}
		int ni=0;
		for (int i=0;i<stv.length;i++) {
			a = str.leftAt(str.trimm(stv[i])," ");
			st1 += stv[i]+"\n";
			if (!a.equals("at")) {
				st += "<p class=erroTr>"+stv[i]+"</p>";
			} else {
				if (ni==0) {
				st += "<p class=erroTr1>"+stv[i]+"</p>";
				} else {
					st += "<p class=erroTr2>"+stv[i]+"<p>";
				}
				ni++;
			}
		}
		logs.grava("erro",st1);
		String sf = "\"></select></center></table></b></div></pre>";
		if (debug==2) {
			on(sf+"<hr>"+st+"<hr>");
		} else if (debug==1) {
			on(sf+"<hr><p class=erro>ERRO: "+s+"</p>"
				+"<p class=erroTr>"+e+"</p>"
			);
		}
		//erro = true;
	}
	//**************************************/
	public int write(byte b[],int i,int t) {
		int to = out.write(b,i,t);
		nBytes += to;
		return to;
	}
	//**************************************/
	public void close() {
		if (chunked) {
			out.print("0"+Http.lf+Http.lf);
		}
		out.close();
	}
	//**************************************/
	public boolean print(String a) {
		long t = data.ms();
		if (erro) {
			return false;
		}
		//falta enviar Cabeçalho?
		if (cab!=null) {
			//sOutAnt = a;
			chunked = (outArq==null) && cab.indexOf("Content-Type: text/html")!=-1;
			chunked = false; //sj2017
			//logs.grava(" (outArq==null)="+outArq+" c="+chunked);
			out.print(str.trimm(cab," \r\n")
				+(chunked?Http.lf+"Transfer-Encoding: chunked":"")
				+Http.lf
				+Http.lf
			);
			nBytes += cab.length();
			cab = null;
			flush();
			ultFlush = t;
		}
		testaErro();
		if (!erro) {
			int tm = a.length();
			if (tm==0) {
				return !erro;
			} else if (chunked) {
				//a = str.trimm(a);
				//tm = a.length();
				//logs.grava("t="+tm+" "+str.strBase(tm,16));
				out.print(str.strBase(tm,16).toUpperCase()+Http.lf+a+Http.lf);
			} else {
				out.print(a);
			}
			nBytes += tm;
			if (t-ultFlush>milisFlush) {
				flush();
				ultFlush = t;
			}
		}
		return !erro;
	}
}
