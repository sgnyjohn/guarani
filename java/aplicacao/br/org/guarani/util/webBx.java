package br.org.guarani.util;

import java.net.*;
import java.io.*;
import java.util.*;

import br.org.guarani.util.*;
import br.org.guarani.servidor.*;

//***************************************
//***************************************
public class webBx extends web implements Runnable {
	/*
	FASES:
	conecta
	pede
	leCab
	lePag
	*/

	
	//arquivos QUE ESTÃO SENDO baixados 
	public static Hashtable hBx = new Hashtable();
	//estatistica cache / não cache
	public static estat ec = new estat("cache");
	//baixar thread
	Pedido tPed;
	cache tCach;
	//posicão onde baixando...
	public long bxTam,bxPos;
	
	
	//****************************************
	//envia o q está sendo baixado como THREAD
	public String mandaBx(Pedido pd,cache c) {
		int nv = 0;
		while (!c.f.exists()) {
			nv++;
			if (nv>10) {
				logs.grava(nv+", não existe, esperando..."+c.f);
				return " | timeout, bx mas não existe!!";
			}
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
			}
			logs.grava(nv+", não existe, esperando..."+c.f);
		}
		
		String r = "";
		c.mandaCab(pd);
		File ff= new File(""+c.getArquivo().f);
		long ti=data.ms();
		int tLid=0;
		try {
			FileInputStream fi = new FileInputStream(ff);
			int tb=1024;
			byte b[] = new byte[tb];
			int lid;
			while (tLid<c.inf.tam) {
				lid=fi.read(b,0,tb);
				c.msg = "tlid="+tLid+" de "+c.inf.tam+" em "+data.ms();
				if (lid>0) {
					int m = pd.write(b,0,lid);
					/*pd.flush();
					if (m!=lid) {
						r += " | abortado cliente caiu";
						break;
					}
					*/
					tLid += lid;
				} else {
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
					}
				}
			}
			fi.close();
		} catch (Exception e) {
			r += " | erro mandaBx(): "+str.erro(e);
			logs.grava(r);
		}
		ec.inc("bx env ",1);
		ec.inc("bx env bytes",tLid);
		r += " | fim mandaBx(): tam="+c.inf.tam+" transf="+tLid;
		logs.grava("fim thread manda baixa "+r);
		c.msg = r;
		return r;
	}
	//****************************************
	public void run() {
		//run é para execução em MODO thread
		mandaBx(tPed,tCach);
	}
	//****************************************
	//envia o arquivo do CACHE
	public String manda(Pedido pd,cache c) {
		String r = "";
		c.mandaCab(pd);
		//File ff= c.getArquivo().f;
		long ti=data.ms();
		try {
			FileInputStream f = new FileInputStream(c.getArquivo().f);
			int tb=1024*8;
			byte b[] = new byte[tb];
			int lid,tLid=0;
			while ((lid=f.read(b,0,tb))>0) {
				tLid += lid;
				int m = pd.write(b,0,lid);
				if (false && tLid>100000) {
					tLid = 0;
					pd.flush();
					if (m!=lid) {
						r += " | abortado cliente caiu";
						break;
					}
				}
			}
			f.close();
		} catch (Exception e) {
			r += " | erro manda(): "+e;
		}
		return r;
	}
		
	//****************************************
	// verifica se precisa baixar ou não
	public String gravaPag(String aq,Pedido p) {
		String r = "", a,l;
		
		cache cach = new cache(aq);
		if (hBx.get(aq)!=null) {
			//baixando, envia o que esta sendo baixado
			return mandaBx(p,cach);
		}
		//logs.grava("val="+cach.val+" não baixando "+aq);
		
		String dt = "?";
		if (cach.val()) {
			//cliente ja tem verão, quer saber se tem nova?
			dt = cach.inf.sData;
			if (p.ped.get("if-modified-since")!=null) {
				String dt1 = data.strSql(data1.http(""+p.ped.get("if-modified-since")));
				r += " | dcli="+dt1+" | dcach="+data.strSql(cach.inf.data);
				//logs.grava("dt1="+dt1+" dt="+data.strSql(cach.inf.data));
				//foi modificado?
				if (dt1.compareTo(data.strSql(cach.inf.data))>0) {
					dt = ""+p.ped.get("if-modified-since");
					r += " | dp="+dt;
				}
			}
			if (cach.inf.sData!=null) {
				r += " | dm="+dt;
				cab += "If-Modified-Since: "+dt+nl;  
			}
		}
	

		debug = aq.indexOf("/updates/main/binary-i386/Packages.bz2")!=-1;
		//LE O CABECALHO
		if (!leCab()) {
			r += " | erro lendo CAB";
			erro("lendo o cab: ");
			//al.fecha();
			//ar.fecha();
			return r;
		}
		webCab cResp = new webCab(tCab);
		if (debug) {
			logs.grava(aq+" cab ped="+tCab+"\n\n"+cab);
		}

		//não modificado...
		if (cach.val() && cResp.nRet == 304 ) {
			r += " | 304 nao modif cache";
			if (p.ped.get("if-modified-since")==null) {
				//manda o original
				r += " | manda orig ";
				r += manda(p,cach);
				ec.inc("do cache",1);
				ec.inc("do cache bytes",cach.tam());				
				return r;
			} else if (!p.ped.get("if-modified-since").equals(dt)) {
				//manda do cache, atualiza cliente
				r += " | atualiz cli ";
				ec.inc("do cache at",1);
				ec.inc("do cache at bytes",cach.tam());
				r += manda(p,cach);
				return r;
			} else {
				//datas ='s manda o cab
				cach.mandaCab(p,tCab);
				r += " | manda cab "+p.ped.get("if-modified-since");
				ec.inc("sem at",1);
				ec.inc("sem at bytes",cach.tam());
				return r;
			}
		}
	
		//nao existe no cache ou atualizado
		if (cach.val()) {
			//renomeia anterior
			cach.renomeia();
		}
		
		//se timeout 
		if (cResp.nRet == 504 ) {
			r += " | 504 timeout";
			if (cach.val()) {
				//deb("timeout (manda do disco)="+aq);
				r += " | manda disco";
				r += manda(p,cach);
				return r;
			}
			r += " | manda cab";
			cach.mandaCab(p,tCab);
			//p.on("<h1>"+tCab+"</h1>");
			return r;
		}

		//se não existe
		if (cResp.nRet == 404) {
			r += " | 404 n existe manda cab";
			cach.mandaCab(p,tCab);
			String aa = lePag();
			p.write(aa.getBytes(),0,aa.length());
			//ar.gravaTxt(tCab);
			//al.fecha();
			//ar.fecha();
			return r;
		}
	
		r += " | baixa e manda";
		//grava inform
		cach.gravaInf(tCab);
	
		hBx.put(aq,this);
		bxPos = 0;
		bxTam = -1;
		//thread separada envia o q está sendo baixado
		webBx w;
		Thread t;
		try {
			//logs.grava("criar thread");
			w = new webBx("localhost");
			w.tPed = p;
			w.tCach = cach;
			t = new Thread(w);
			//logs.grava("thread="+t);
		} catch (Exception e) {
			return "ERRO criando thread..: | "+str.erro(e)+" "+r;
		}
		r += " | disp Thread";
		boolean tr = false;

		//baixa e thread acima criada envia...
		int nlll=0;
		arquivo1 ar = cach.getArquivo();
		int tm = str.inteiro(""+hCab.get("content-length"),-1),jl=0,nl;
		try {
			bxTam = tm;
			if (ignoraTam) {
				tm = -1;
			}
			while ((nl=in.read(bufc,0,tBf))>0) {
				ar.grava(bufc,0,nl);
				bxPos += nl;
				//logs.grava("lido web: "+nl);
				if (t!=null) {
					ar.out.flush();
					//ativar a thread?
					if (!tr) {
						//logs.grava("init thread bx "+aq+" "+ar.f.exists());
						t.start();
						tr = true;
					}
				}
				if (sleep!=0) {
					try {
						//logs.grava("dormir "+sleep+" nl="+nl);
						Thread.sleep(sleep);
					} catch (Exception e) {
					}
				}
				//logs.grava("nlidos="+nl);
				jl += nl;
				if (tm!=-1 && jl>=tm) {
					//logs.grava("fim por content-length");
					break;
				}
			}
			//logs.grava("nlll="+nlll+" tm="+tm+" jl="+jl);
		} catch (java.io.IOException eio) {
			ar.fecha();
			erro("erro conexção ou lendo corpo="+eio);
			r += " | erro baixando WEB: "+eio;
			return r;
		}
		ec.inc("bx ",1);
		ec.inc("bx bytes",jl);	
		hBx.remove(aq);
		ar.fecha();
	
		//espera thread envio cli terminar (nao pode fechar pedido);
		if (t!=null) {
			while (t.isAlive()) {
				//logs.grava("esperando fim thread: "+cach.msg);
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}
			}
			r += " || "+cach.msg;
		}

		return "fim - "+r;
	}
	//****************************************
	public webBx(String a) {
		super(a);
	}
	//****************************************
	public webBx(String a,String b) {
		super(a,b);
	}
	//****************************************
	//****************************************
	class cache {
		public File f,fi;
		public String aq;
		public boolean val;
		webCab inf;
		public String sCab,msg;
		//****************************************
		//tam
		int tam() {
			if (f!=null && f.exists()) {
				return (int)f.length();
			}
			return 0;
		}
		//****************************************
		//tem nova versao, renomeia versao anterior
		void renomeia() {
			f = renomeia(f);
			fi = renomeia(fi);
		}
		//****************************************
		//tem nova versao, renomeia versao anterior
		File renomeia(File f) {
			File r = new File(""+f);
			if (!f.exists()) {
				return r;
			}
			int i = 1;
			while ((new File(f+"~"+i)).exists()) {
				i++;
			}
			f.renameTo((new File(f+"~"+i)));
			return r;
		}
		//****************************************
		void mandaCab(Pedido ped,String tx) {
			/*HTTP/1.1 200 OK
			Via: 1.1 MELANTO
			Content-Length: 102222
			Date: Mon, 18 Feb 2008 11:45:32 GMT
			Age: 1051
			Content-Type: text/plain; charset=iso-8859-1
			Server: Apache/1.3.33 (Debian GNU/Linux)
			Last-Modified: Sat, 16 Feb 2008 14:19:22 GMT
			Etag: "1b90006-18f4e-47b6f0ea"
			Accept-Ranges: bytes
			Keep-Alive: timeout=15, max=98
			Content-Encoding: x-gzip
			*/
			String v[] = str.palavraA(str.trimm(tx),"\n");
			String r = "HTTP/1.0 "+str.substrAt(str.trimm(v[0])," ")+nl;
			for (int i=1;i<v.length;i++) {
				String l = str.trimm(v[i]);
				String ch = str.leftAt(l,":").toLowerCase();
				if (ch.equals("via")) {
					r += "Via: 1.1 aptProxy"+nl;
					
				} else 	if (ch.equals("date")) {
					r += "Date: "+data1.http(new Date())+nl;
					
				} else 	if (ch.equals("age")) {
					
				} else 	if (ch.equals("accept-ranges")) {
					
				} else 	if (ch.equals("keep-alive")) {
					
				} else 	if (ch.equals("etag")) {
					
				} else 	if (ch.equals("server")) {
					r += "Server: Guarani 0.1"+nl;
					
				} else if (!str.vazio(l)) {
					r += l+nl;
					
				}
			}
			//logs.grava(ped.ped+"\n====================\n"+r);
			r += nl;
			ped.write(r.getBytes(),0,r.length());
		}
		//****************************************
		void mandaCab(Pedido ped) {
			String s = str.trimm(str.trocaTudo(sCab,"\nOK\r","\r\n"))+nl+nl;
			mandaCab(ped,s);
		}
		//****************************************
		arquivo1 getArquivo() {
			return new arquivo1(""+f);
		}
		//****************************************
		void gravaInf(String tx) {
			sCab = tx;
			arquivo a = new arquivo(""+fi);
			a.gravaTxt(sCab);
			a.fecha();
			inf = new webCab(sCab);
		}	
		//****************************************
		boolean val() {
			return val;
		}
		//****************************************
		public cache(String aq) {
			this.aq = aq;
			
			//dir existe?
			f = new File(str.leftRat(aq,"/"));
			if (!f.exists()) {
				f.mkdirs();
			}
			
			f = new File(aq);
			fi = new File(aq+".inf");
			val = true;
			if (!f.exists() || !fi.exists()) {
				val = false;
			}
			if (fi.exists()) {
				sCab = (new arquivo(""+fi)).leTxt();
				inf = new webCab(sCab);
				if (inf.tam != f.length()) {
					val = false;
				}
			} else {
				val = false;
				inf = new webCab("");
			}
		}
	}
}
