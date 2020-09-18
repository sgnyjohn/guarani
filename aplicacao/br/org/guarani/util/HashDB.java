package br.org.guarani.util;

import java.util.*;
import bd.*;

//*******************************************************
public class HashDB {
	Hashtable dad = new Hashtable();
	//*****************************************************
	public HashDB(Dados dd,String tb,String ch) {
		DadosSet ds = dd.executeQuery("SELECT * FROM "+tb);
		initDS(ds,ch);
	}
	//*****************************************************
	public Hashtable get(String ch) {
		return (Hashtable)dad.get(ch);
	}
	//*****************************************************
	public String get(String ch,String cmp) {
		Hashtable h = (Hashtable)dad.get(ch);
		String r = null;
		if (h!=null) {
			r = (String)h.get(cmp);
		}
		return r;
	}
	//*****************************************************
	public HashDB(DadosSet ds,String ch) {
		initDS(ds,ch);
	}
	//*****************************************************
	public void initDS(DadosSet ds,String ch) {
		while (ds.next()) {
			Hashtable a = ds.getHashtable();
			dad.put(a.get(ch),a);
		}
	}
}

