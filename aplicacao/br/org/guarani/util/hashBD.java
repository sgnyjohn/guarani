/*
	Signey - nov/2014
*/

package br.org.guarani.util;

import java.util.*;

//*****************************************************
//*****************************************************
public class hashBD {
	public Hashtable hErro = null;
	public Hashtable bd = new Hashtable();
	public Hashtable reg;
	public char dl = '\t';
	public char aspa = '"';
	public String chaveUnica = "cod";
	public String nome = "nome";
	//**************************************************
	public boolean fromCSV(String nome) {
		boolean r = true;
		arquivo1 ar = new arquivo1(nome);
		ar.csvCabLer(dl,aspa);
		Hashtable l;
		while ( ( l = ar.csvLinhaLer(dl,aspa) ) != null ) {
			String ch = (String)l.get(chaveUnica);
			if (ch==null) {
				erro("chave null "+l);
				r = false;
			} else {
				if (bd.get(ch)!=null) {
					erro("chave dupla assumindo nova "+ch+" bd="+toString(ch)+" nova="+toString(l));
					r = false;
				}
				bd.put(ch,l);
			}
		}
		return r;
	}
	//**************************************************
	public String toString() {
		return toString(reg);
	}
	//**************************************************
	public String toString(String ch) {
		reg = get(ch);
		return toString(reg);
	}
	//**************************************************
	public String toString(Hashtable reg) {
		return reg.get(chaveUnica)+" "+reg.get(nome);
	}
	//**************************************************
	public String nome(String ch) {
		reg = get(ch);
		if (reg==null) {
			return null;
		}
		return (String)reg.get(nome);
	}
	//**************************************************
	public Hashtable get(String ch) {
		return (Hashtable)bd.get(ch);
	}
	//**************************************************
	void erro(String s) {
		if (hErro==null) {
			hErro = new Hashtable();
		}
		hErro.put(""+hErro.size(),s);
	}
}
