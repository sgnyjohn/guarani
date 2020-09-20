/*
	marca tempo gasto com cada "nome"
	
	ao criar o tempo começa a contar
*/

package br.org.guarani.util;

import java.util.*;

//*****************************************//
//*****************************************//
public class cronEstat {
	public Hashtable h = new Hashtable();
	long t = data.ms();
	//*************************************//
	public String toString() {
		String r = "";
		for (Enumeration e = h.keys();e.hasMoreElements();) {
			String k = (String)e.nextElement();
			r += k+"\t"+((Long)h.get(k)).longValue()+"\n";
		}
		return r;
	}
	//*************************************//
	public void fim() {
		t = data.ms();
	}
	//*************************************//
	public long fim(String nome) {
		long tf = data.ms()-t;
		t = data.ms();
		Long l = (Long)h.get(nome);
		if ( l!=null ) {
			tf += l.longValue();
		}
		h.put(nome,new Long(tf));
		return tf;
	}
}
