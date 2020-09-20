package br.org.guarani.servidor;

import java.util.*;
import java.io.*;

import br.org.guarani.servidor.*;
import br.org.guarani.util.*;


//****************************************
//****************************************
public class gConf {
	static Hashtable h = new Hashtable();
	String arq,secao;
	public xmlTag cfg;
	boolean alterado=false;
	static int copDev=0;
	//****************************************
	public String toString() {
		return "br.org.guarani.servidor.gConf: arq="+arq+" secao="+secao;
	}
	//****************************************
	public void save() {
		if (alterado) {
			cfg.grava(arq);
			alterado = false;
		}
	}
	//****************************************
	public xmlTag getTag(String ch,xmlTag padrao) {
		Object o = get(ch);
		if (o==null || !o.getClass().getName().equals("br.org.guarani.util.xmlTag")) {
			xmlTag x = (xmlTag)cfg.getCh(secao+str.leftRat(ch,"."));
			alterado = true;
			x.put(padrao);
			return padrao;
		}
		return (xmlTag)o;
	}
	//****************************************
	public Object get(String ch,String padrao) {
		Object o = get(ch);
		if (o==null) {
			o = padrao;
			put(ch,(String)o);
		}
		return o;
	}
	//****************************************
	public Object get(String ch) {
		return cfg.getCh(secao+ch);
	}
	//****************************************
	public void put(String ch,String vlr) {
		if (!get(ch).equals(vlr)) {
			alterado = true;
			cfg.putCh(secao+ch,vlr);
		}
	}
	//****************************************
	public gConf(String arquivo,String sec) {
		arq = arquivo;
		if (arq.indexOf("/")==-1) {
			arq = Guarani.dirCfg+arq;
		}
		secao = sec;
		if (!str.vazio(secao) && secao.charAt(secao.length()-1)!='.') {
			secao += ".";
		}
		cfg = gConf.getConf(arq);
		if (cfg==null) {
			cfg = new xmlTag();
		}
	}
	//****************************************
	public gConf(String classe) {
		this(str.leftRat(classe,"."),str.substrRat(classe,"."));
	}
	//****************************************
	public gConf(Object o) {
		this(o.getClass().getName());
		//logs.grava("arq="+arq+" secao="+secao);
	}
	//****************************************
	public static boolean save(Object o,xmlTag cfg) {
		cfg.grava(gConf.nomeConf(o));
		return true;
	}
	//****************************************
	public static xmlTag getConf(String n) {
		//String n = gConf.nomeConf(o);
		//logs.grava(n);
		Object r[] = (Object[])h.get(n);
		String ua = ""+(new File(n+".xml")).lastModified();
		//ogs.grava("arq="+n+" ua="+ua+" "+(r==null?"":r[0]));
		if (r==null || !ua.equals((String)r[0]) ) {
			xmlParser xp = new xmlParser(n+".xml");
			r = new Object[]{ua,xp.parse()};
			h.put(n,r);
		}
		return (xmlTag)r[1];
	}
	//****************************************
	public static String nomeConf(Object o) {
		return Guarani.dirCfg+"/"
			+str.leftRat(o.getClass().getName(),".")+".xml"
		;
	}
	//****************************************
	public static boolean dev() {
		if (copDev==0) {
			Class c = Guarani.findClass("br.org.guarani.bd.tabelaOper",false);
			copDev = (c==null?-1:1);
		}
		return copDev==1;
	}
}