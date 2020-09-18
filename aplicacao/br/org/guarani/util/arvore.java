/*
	signey fev/2007
	signey jun/2011
*/


package br.org.guarani.util;

import java.util.*;
import java.io.*;

import br.org.guarani.util.*;

//*****************************************
//*****************************************
public class arvore {
	int limite,limiteF,pos=0,siz=0;
	public arvore filho[];
	public String chMin="",vo[][];
	public Hashtable dad;
	//*****************************************
	public void grava(String s) {
		grava(new arquivo1(s));
	}
	//*****************************************
	public void grava(arquivo1 f) {
		f.grava("<fil l=\""+chMin+"\" tm=\""+siz+"\">\n");
		this.grava(f,1);
		f.grava("</fil>\n");
	}
	//*****************************************
	public void grava(arquivo1 f,int nv) {
		Object o = null;
		if (filho!=null) {
			for (int i=0;i<limiteF;i++) {
				f.grava(str.repl(" ",nv)+"<fil l=\""+filho[i].chMin+"\" tm=\""+filho[i].siz+"\">\n");
				filho[i].grava(f,nv+1);
				f.grava(str.repl(" ",nv)+"</fil>\n");
			}
		} else {
			if (vo==null) {
				vo = sort();
			}
			for (int i=0;i<vo.length;i++) {
				f.grava(str.repl(" ",nv)+"<dad ch=\""+vo[i][0]+"\" d=\""+dad.get(vo[i][0])+"\"/>\n");
			}
		}
	}
	//*****************************************
	public Object[] next() {
		if (filho!=null) {
			Object o[] = filho[pos].next();
			if (o==null) {
				pos++;
				if (pos==limiteF) {
					return null;
				}
				return filho[pos].next();
			}
			return o;
		}
		if (vo==null) {
			vo = sort();
		}
		if (pos>=vo.length-1) {
			return null;
		}
		return new Object[]{vo[pos++][0],dad.get(vo[pos++][0])};
	}
	//*****************************************
	public Object get(String ch) {
		if (filho!=null) {
			return getFCh(ch).get(ch);
		} else {
			return dad.get(ch);
		}
	}
	//*****************************************
	public void put(String ch,Object ob) {
		siz++;
		arvore ad = this;
		if (filho!=null) {
			getFCh(ch).put(ch,ob);
		} else if (dad.size()>=limite) {
			divide();
			getFCh(ch).put(ch,ob);
		} else {
			dad.put(ch,ob);
		}
		//atualiza maximo
		if (ch.compareTo(chMin)<0) {
			chMin = ch;
		}
	}
	//*****************************************
	public int size() {
		return siz;
	}
	//*****************************************
	public arvore getFCh(String ch) {
		for (int i=limiteF-1;i>-1;i--) {
			if (ch.compareTo(filho[i].chMin)>=0) {
				return filho[i];
			}
		}
		//retorna o último
		//filho[limiteF-1].chMax = ch;
		return filho[0];
	}
	//*****************************************
	public String[][] sort() {
		int tm = dad.size();
		String sv[][] = new String[tm][1];//Sort1.sortKey(dad);
		int p=0;
		for (Enumeration e = dad.keys(); e.hasMoreElements();) {
			String ch = (String)e.nextElement();
			sv[p++] = new String[]{ch};
		}
		Sort.sort(sv,0,false);
		return sv;
	}
	//*****************************************
	public void divide() {
		filho = new arvore[limiteF];
		String sv[][] = sort();
		int pa=-1,tm=limite/limiteF;
		for (int i=0;i<sv.length;i++) {
			if (i%tm==0) {
				pa++;
				//logs.grava(i+" tm="+tm+sv[i][0]);
				if (filho[pa]==null) {
					filho[pa] = new arvore(limite,limiteF);
					filho[pa].chMin = sv[i][0];
				}
			}
			filho[pa].put(sv[i][0],dad.get(sv[i][0]));
		}
		//dad.destroy();
		dad = null;
	}
	//*****************************************
	public arvore(int limite,int limiteF) {
		this.limite = limite;
		this.limiteF = limiteF;
		dad = new Hashtable(limite);
	}
	//*****************************************
	public arvore() {
		this(512,8);
	}
}
