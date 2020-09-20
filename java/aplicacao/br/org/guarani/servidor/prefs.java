/* ************************************

*************************************/
package br.org.guarani.servidor;

import java.util.*;
import java.io.*;

import br.org.guarani.util.*;
import br.org.guarani.servidor.*;

//***********************************
//***********************************
public class prefs {
	//staticas
	public static String cookDel = "~:";
	private static Hashtable hPrefs;
	
	//de instância
	String aq,usu;
	boolean alt=false;
	long uAlt = data.ms();
	public xmlTag xml;
	//***********************************
	//seta cfrme cookie
	public synchronized void grava() {
		if (alt) {
			xml.gravaTag(aq);
			alt = !alt;
		}
	}
	//***********************************
	//seta cfrme cookie/string separada por : e =
	public void setar(PagV pg,String cook) {
		//logs.grava("set pref: "+pg.prefRaiz+" coo="+cook);
		String v[] = str.palavraA(cook,cookDel);
		for (int i=0;i<v.length;i++) {
			if (v[i].indexOf('=')!=-1) {
				put(pg,str.leftAt(v[i],"="),str.substrAt(v[i],"="));
			}
		}
	}
	//***********************************
	public String get(PagV pg,String ch,String pdr) {
		String r = get(pg,ch);
		if (r==null) return pdr;
		return r;
	}
	//***********************************
	public String get(PagV pg,String ch) {
		//logs.grava("pref get="+pg.prefRaiz+"."+ch);
		return (String)xml.getCh(pg.prefRaiz+"."+ch);
	}
	//***********************************
	public xmlTag getX(PagV pg,String ch) {
		return (xmlTag)xml.getCh(pg.prefRaiz+"."+ch);
	}
	//***********************************
	public void put(PagV pg,String ch,String vlr) {
		String a = get(pg,ch);
		if (vlr==null || (a!=null && a.equals(vlr))) {
			return;
		}
		//logs.grava("set pref: "+pg.prefRaiz+" ch="+ch+" v="+vlr);
		xml.putCh(pg.prefRaiz+"."+ch,vlr,".");
		alt = true;
		if (data.ms()-uAlt>10000) { //1 minutos
			grava();
			uAlt = data.ms();
		}
	}
	//***********************************
	public prefs(String usu) {
		this.usu = usu;
		aq = Guarani.dirCfg+"prefs_"+usu+".xml";
		if (!(new File(aq)).exists()) {
			novo();
		} else {
			xmlParser x = new xmlParser(aq);
			xmlTag t = x.parse();
			xml = t.get(1);
		}
		if (xml==null || !xml.nome.equals("prefsUsuario")) {
			novo();
		}
	}
	//***********************************
	public void novo() {
		xml = new xmlTag("arquivo");
		xmlTag x1 = new xmlTag("prefsUsuario");
		x1.putAtr("nome",usu);
		xml.put(x1);
		//logs.grava("prefs criando="+aq);
		xml.grava(aq);
		xml = xml.get(0);
		//logs.grava("prefs criando="+aq+" n="+xml.nome);
	}
	//***********************************
	public static prefs getPrefs(String usu) {
		if (hPrefs==null) {
			Guarani.aoSair("gravaTudo",prefs.class);
			hPrefs = new Hashtable();
		}
		
		prefs r = (prefs)hPrefs.get(usu);
		if (r==null) {
			r = new prefs(usu);
			hPrefs.put(usu,r);
		}
		return r;
	}
	//***********************************
	public static void gravaTudo() {
		for (Enumeration e = hPrefs.elements();e.hasMoreElements();) {
			prefs p = (prefs)e.nextElement();
			//logs.grava("saindo, gravando prefs="+p.aq);
			p.grava();
		}
	}
}