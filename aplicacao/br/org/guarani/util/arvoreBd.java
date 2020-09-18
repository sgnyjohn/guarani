/*
	signey fev/2007
*/


package br.org.guarani.util;

import java.util.*;
import java.io.*;

import br.org.guarani.util.*;
import bd.*;

//*****************************************
//*****************************************
public class arvoreBd extends arvore {
	//*****************************************
	public void put(String ch,Object ob) {
		super.put(ch(ch),ob);
	}
	//*****************************************
	public static String ch(String ch) {
		return str.tiraAcentos(str.trimm(ch).toLowerCase());
		//ap - aprovado  chc=14
	}
	//*****************************************
	public Object get(String ch) {
		Object r = super.get(ch(ch));
		if	(r==null) {
			//logs.grava("c="+ch+" chc="+ch(ch)+" chc="+(ch(ch).length()));
		}
		return r;
	}
	//*****************************************
	public static arvoreBd fromDadosSet(DadosSet ds) {
		arvoreBd ar = new arvoreBd();
		while (ds.next()) {
			ar.put(ch(ds.getString(1)),ds.getString(2));
		}
		return ar;
	}
	//*****************************************
	//public arvoreBd() {
	//	super();
	//}
}
