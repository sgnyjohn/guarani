/*
 * Signey John mar/2007.
 */

package br.org.guarani.servidor;
 

import java.util.*;
import java.io.*;
import java.net.*;
import java.text.*;

import br.org.guarani.servidor.*;
import br.org.guarani.util.*;

//**************************************************
//**************************************************
public class aptProxy implements Prg {
	//static Hashtable bx = new Hashtable();
	static estat eIp = new estat("pedidos por ip");
	static estat eIpB = new estat("bytes por ip");
	int debNiv=1;
	String baseLoc = "/var/aptProxy";
	String ed,edLoc;
	Pedido pd;
	static String prx;
	//**************************************************
	public void subst(String s[],String s1) {
		for (int i=0;i<s.length;i++) {
			subst(s[i],s1);
		}
	}
	//**************************************************
	public void subst(String s,String s1) {
		if (ed.indexOf("://"+s+"/")!=-1) {
			ed = str.troca(ed,"://"+s+"/","://"+s1+"/");
		}
	}
	//**************************************************
	public boolean run(Pedido pd) {
		//if (true) return false;
		this.pd = pd;
		
		//logs.grava("h="+pd.ped);
		if (pd.getHttpConf("classeP")!=null && prx == null) {
			prx = (String)pd.getHttpConf("proxy");
			if (prx == null) {
				prx = Guarani.getCfg("proxy");
			}
			if (prx == null) {
				prx = System.getenv("http_proxy");
			}
			if (prx == null) {
				prx = "";
			}
			logs.grava("apt proxy="+prx);
		}
		
		
		
		try {
		
			ed = "/"+(String)pd.ped.get("?endereco");
			
			
			//subst no http e no local
			/*sj 2013 subst(new String[]{
				"www.debian-multimedia.org"
				,"ftp.nerim.net/debian-marillat"
				,"floating3.ucd.ie/debian"
				,"ftp.sunet.se/pub/os/Linux/distributions/debian-multimedia"}
				,"gulus.usherbrooke.ca/pub/distro/debian/debian-multimedia/");
			*/
			
			//subst(new String[]{"ftp.br.debian.org","mirror.debian.pt-alergs.br"},"ftp.debian.org");
		
			//subst(new String[]{"security.debian.org"},"ftp.br.debian.org");

			//ender local
			edLoc = baseLoc+str.troca(str.troca(ed,"://","/"),":","/");
			String ed1 = str.leftAt(ed,":")+"/"+str.substrAtAt(ed,"://","/");
			
			//substituições só no HTTP
			//subst(new String[]{"security.debian.org"},"sft.if.usp.br/debian-security");
			
			//log("aptProxy="+ed+" h="+edLoc+" "+pd.ped);
			File f = new File(edLoc);
			if (ed.indexOf("/http://")==-1) {
				//ESTATISTICAS http do CACHE
				
				//URL u = new URL("http://security.debian.org/pool/updates/main/i/icedove/icedove_1.5.0.13+1.5.0.15b.dfsg1-0etch1_i386.deb");
				//HttpURLConnection uc  = new HttpURLConnection(u);
				
				//para forçar compilação.
				webBx w111 = new webBx("localhost");
				webCab wc = new webCab("");
				
				pd.setCab("HTTP/1.0 200 OK\r\nContent-Type: text/html; charset=iso-8859-1\r\n");
				//pd.on("<hr>Tam: "+webBx.hBx.size()+"<hr>"+str.troca(""+webBx.hBx,",","<br>")+"<hr>");
				
				pd.on("<h2>baixando n="+webBx.hBx.size()+"</h2><table border=1>");
				for (Enumeration e = webBx.hBx.keys();e.hasMoreElements();) {
					String k = (String)e.nextElement();
					webBx w = (webBx)webBx.hBx.get(k);
					pd.on("<tr>"
						+"<td align=right>% <b>"+num.format((1D*w.bxPos/w.bxTam)*100.0D,1)+"</b>"
						+"<td align=right>"+num.format(w.bxTam,0)
						+"<td align=right>"+num.format(w.bxPos,0)
						+"<td>"+str.troca(k,"/"," / ")
					);
					
				}
				pd.on("</table>");
				
				pd.on("<h2>Proxy: "+prx); //+" "+pd.prot+" "+pd.getHttpConf("classeP")+"</h2>");
				pd.on("<h2>Bytes por Cliente</h2>"+eIp+eIpB+"<hr>"+pd
					+"<hr>"+webBx.ec
				);
			} else if (!(new File(baseLoc+ed1)).isDirectory()) {
				pd.setCab("HTTP/1.0 401 Unauthorized\r\nContent-Type: text/html; charset=iso-8859-1\r\n");
				pd.on("endereço: "+ed1
					+" não autorizado..."
				);
				log(0,"P R O I B I D O: "+ed+"\n"+pd+" nãoExiste:"+baseLoc+ed1);
			} else {
				
				if (f.isDirectory()) {
					log(0," ** D I R ** : "+ed+"\n"+pd+" nãoExiste:"+baseLoc+ed1);
					pd.on("<html>dir "+ed+"</html>");
				} else {
					String r="";
					try {
						webBx w =  new webBx(ed.substring(1),prx);
						r = w.gravaPag(edLoc,pd);
						w.fecha();
					} catch (Exception e) {
						r = "ERRO: "+str.erro(e)+r;
					}
					log(0,r+"="+edLoc);
					if (r.length()>0 && r.charAt(0)=='?') {
						//não modificado, manda o q tem
						//log(0,"FIM baixar manda o ORIGINAL");
						//mandaArq(true);
					}
				}
				
			}
			
		} catch (Exception e) {
		}
		
		
		String ip = str.substrAtAt(""+pd,"addr=",",");
		eIp.inc(ip);
		eIpB.inc(ip,(int)pd.getBytes());
		
		
		return true;
	}
	//**************************************************
	public void log(int db,String s) {
		if (db<=debNiv) {
			log(s);
		}
	}
	//**************************************************
	public void log(String s) {
		logs.grava("aptProxy",s);
	}
	//**************************************************
	public boolean runSocket(Socket pPed) {
		return false;
	}
}
