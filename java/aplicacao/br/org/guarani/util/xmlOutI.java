/*
	* Signey John dez/2006 
	
	exige validaçao... PagV
	
*/
package br.org.guarani.util;


import br.org.guarani.servidor.*;
import java.util.*;
//import java.util.zip.*;
import java.io.*;

//*************************************
//*************************************
public class xmlOutI extends xmlOut {
	PagV pg;
	web w;
	//*************************************
	public boolean setOutArq(String at) {
		this.tBf = tBf;
		try {
			f = new FileOutputStream(at);
			tBf = 2048;
			buf = new byte[tBf];
		} catch (Exception e) {
			logs.grava("erro","setOutArq: "+at+" "+str.erro(e));
			return false;
		}
		return true;
	}
	//*************************************
	public xmlTag arqHttp(String an,String cl) {
		if (w==null) {
			w = new web("http://localhost");
		}
		w.post = null;
		if (an.indexOf("//POST:")!=-1) {
			w.post = str.substrAt(an,"?");
			an = str.leftAt(str.troca(an,"//POST:","//"),"?");
		}
		//web w = new web(an,);
		w.init(an,"127.0.0.1:8080");
		w.debug = true;
		logs.grava("vai ler: "+an);
		String t = w.lePag();
		String tx = "<server";
		for (Enumeration e = w.hCab.keys();e.hasMoreElements();) {
			String c = (String)e.nextElement();
			tx += " "+c+"=\""+w.hCab.get(c)+"\"";
		}
		tx += "/>\r\n<cookie ";
		for (Enumeration e = w.hCook.keys();e.hasMoreElements();) {
			String c = (String)e.nextElement();
			tx += " "+c+"=\""+w.hCook.get(c)+"\"";
		}
		tx += "/>";
		String aq = "/tmp/bx.html";
		//x.grava(aq);
		logs.grava("fim ler: "+an);
		(new arquivo(aq)).gravaTxt(tx+t);
		
		xmlParserHtml p = new xmlParserHtml(aq);
		p.pg = pg;
		xmlTag r = p.parse(cl);
		return r;
	}
	
	//*************************************
	public xmlTag arq(String an,String cl) {
		if (str.equals(an,"dir://")) {
			xmlParserDir d = new xmlParserDir();
			d.url = str.substrAt(an,"://");
			return d.parse(cl);
			
		} else if (str.equals(an,"http://")) {
			return arqHttp(an,cl);
		}
		return super.arq(an,cl,true);
	}

	//*************************************
	public String dad(String g) {
		//logs.grava("dad="+g);
		if (g.equals("usuario")) {
			return pg.usu;
		} else if (g.equals("grupos")) {
			pg.doGrupo("aa");
			return str.trimm(pg.usuario.gruposS," ,");
		}
		return "# intraO.dad: "+g+"#";
	}
	//*************************************
	public String doGrupo(String g) {
		return ""+pg.doGrupo(g);
		//return "OK "+g;
	}
	//*************************************
	public xmlOutI(PagV p,java.lang.String s,java.lang.String s1) {
		super(p,s,s1);
		pg = p;
	}
	//*************************************
	public xmlXtd1 xtd1(String an) {
		Object o = xml(an,"br.org.guarani.util.xmlXtd1");
		//logs.grava("c="+o.getClass().getName());
		xmlXtd1 r = null;
		try {
			r = (xmlXtd1)o;
		} catch (Exception e) {
			on(an+" xmlXtd1 <> "+o.getClass().getName());
		}
		return r;
	}
}

