package br.org.guarani.interGroups;

import java.util.*;

import br.org.guarani.servidor.*;
import br.org.guarani.util.*;

//*****************************************************
//*****************************************************
public class adslCable extends staticThread {
	public static gConf cfg;
	//static String getIpHost,getIpUrl,getIpPos,getIpPosGw;
	//static xmlTag getIpHeaders;
	static String ipAtual="",gwAtual="";
	static int nTesta=0,nTestes=0;
	static long mReset=data.ms();
	static boolean ultTest = false;
	//***************************************************
	public boolean ligaProvedor() {
		return false;
	}
	//***************************************************
	public boolean resetModem() {
		return false;
	}
	//***************************************************
	public boolean testaCon() {
		int testa = str.inteiro((String)cfg.get("testaCon.vezes","5"),-1);
		if (testa<1) {
			return true;
		}
		
		nTesta++;
		onD("tot="+testa+" nTesta="+nTesta+" dev="+gConf.dev());
		if (nTesta<testa && ultTest) {
			return true;
		}
		nTesta = 0;
		
		xmlTag p = new xmlTag("urls");
		p.tex = "http://www.via-rs.net/"
			+"\n http://www.uol.com.br/"
			+"\n http://www.terra.com.br/"
		;
		String urls = cfg.getTag("testaCon.urls",p).tex;
		cfg.save();
		
		String v[] = str.palavraA(urls,"\n");
		for (int i=0;i<2;i++) {
			int pos = (nTestes+i)%v.length;
			v[pos] = str.trimm(v[pos]);
			onD("Lendo "+pos+" "+v[pos]);
			web w = new web(v[pos]);
			String t = w.lePag();
			nTestes++;
			if (t!=null) {
				(new arquivo("/tmp/adlsL.html")).gravaTxt(t);
			}
			if (t!=null && t.toLowerCase().indexOf("<html")!=-1) {
				ultTest = true;
				return true;
			}
			on("ERRO lendo: "+v[pos]);
		}
		ultTest = false;
		return false;
	}
	//***************************************************
	public boolean ipPub() {
		String ipPub = (String)cfg.get("ip.pub");
		ipPub=(ipPub==null?"":ipPub);
		if (ipPub.equals(ipAtual)) {
			return true;
		}

		/*if (gConf.dev()) {
			ipPub = ipAtual;
			cfg.put("ip.pub",ipPub);
			cfg.save();
			return true;
		}
		*/
		String pubIpHost = (String)cfg.get("pubIp.host","https://members.dyndns.org");
		String pubIpUrl = (String)cfg.get("pubIp.url","/nic/update?hostname=signey.dyndns.org&backmx=YES&offline=$1&system=dyndns&wildcard=OFF&");

		xmlTag p = new xmlTag("respOK");
		p.tex = "good\r\nnochg";
		String pubIpRespOK = cfg.getTag("pubIp.respOK",p).tex;
		
		
		p = new xmlTag("headers");
		p.tex = "Authorization: Basic c2lnbmV5OmRzZW5oYWQ=";
		String pubIpHeaders = cfg.getTag("pubIp.headers",p).tex;

		cfg.save();
				
		if (!str.equals(pubIpHost,"https://")) {
			on("ERRO: protocolo pub ip somente https");
			return false;
		}
		
		sh s = new sh(null);
		s.debug = true;
		String aq = "/tmp/monR.txt";
		s.cmd("wget -O "+aq+" -S --no-check-certificate --user-agent=\"WGet\" --header \""+p.tex+"\""
			+" "+pubIpHost+pubIpUrl+" >/dev/null 2>/dev/null "
		);
		s.exec();
		
		String x = (new arquivo(aq)).leTxt();
		//good/nochg
		String v[] = str.palavraA(pubIpRespOK,"\n");
		for (int i=0;i<v.length;i++) {
			v[i] = str.trimm(v[i]);
			//onD("<br>"+v[i]);
			if (!str.vazio(v[i]) && x.indexOf(v[i])!=-1) {
				ipPub = ipAtual;
				cfg.put("ip.pub",ipPub);
				cfg.save();
				on("ipPub OK: "+ipAtual);
				return true;
			}
		}
		on("ipPub ERRO: "+ipAtual+" == "+str1.html(x));
		
		return false;
	}
	//***************************************************
	public void init() {
		//le config
		cfg = new gConf(this);
		
		
		//logs.grava("cfg="+cfg.cfg);
		//cfg.put("teste","valor do teste");
		//cfg.save();
		//le configs grupos
		//levanta threads grupos
	}
	//***************************************************
	public void passo() {
		if (cfg==null) {
			init();
		}
		
		onD("<hr>Ip: "+ipAtual);
		String ip=ipAtual;
		int ri = ip();
		if (ri==0) {
			return;
		} else if (ri==1) {
			//publica só se é válido
			if (ip.equals(ipAtual)) {
				onD(" Não mudou...");
			} else {
				on("Mudou para: "+ipAtual);
				ipPub();
			}
		}
		
		//testa conexção
		if (testaCon()) {
			mReset = data.ms();
		}
		resetModem();		
		
	}
	//***************************************************
	//ret 0 = erro lendo pag modem
	//ret 1 = leu ok
	//ret -1 = leu ip invalido (configurado)
	public int ip() {
		executa e = new executa();
		String getIpInterface = (String)cfg.get("getIp.interface","eth1");
		e.exec(null,"/sbin/ip addr show dev "+getIpInterface);
		String s = e.getOut();
		if (s.indexOf("inet")==-1) {
			return 0;
		} else {
			ipAtual =  str.substrAtAt(s,"inet ","/");
		}
		return 1;
	}
	//***************************************************
	boolean eIp(String s) {
		if (str.strConta(s,".")==3) {
			return true;
		}
		return false;
	}
	//***************************************************
	void procuraIp(xmlTag x,Hashtable h) {
		if (x.size()!=0) {
			for (int i=0;i<x.size();i++) {
				procuraIp(x.get(i),h);
			}
		} else {
			//onD("<br>"+x.nome+" "+x.tex+" "+str.strConta(x.tex,"."));
			if (x.tex!=null && eIp(x.tex)) {
				h.put(""+h.size(),x.tex);
			}
			if (x.texF!=null && eIp(x.texF)) {
				h.put(""+h.size(),x.texF);
			}
		}
	}
	//***************************************************
	public void on(String s) {
		onD(s);
		log(s);
	}
	//***************************************************
	public adslCable() {
		pAtu  = 60000;
		//if (!gConf.dev()) {
			inicia();
		//}
	}
}