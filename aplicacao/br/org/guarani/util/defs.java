/*
	Signey - nov/2009
*/

package br.org.guarani.util;

import java.util.*;

//*****************************************************
//*****************************************************
public class defs {
	public String dLin="\n",dVal=":";
	public Hashtable h;
	//****************************************************
	public String get(String ch,String defa) {
		String r = get(ch);
		if (r==null) {
			return defa;
		}
		return r;
	}
	//****************************************************
	public String get(String ch) {
		return (String)h.get(ch);
	}
	//****************************************************
	public String put(String ch,String v) {
		return (String)h.get(ch);
	}
	//****************************************************
	public void init(String tx) {
		h = new Hashtable();
		String v[] = str.palavraA(tx,dLin);
		for (int i=0;i<v.length;i++) {
			if (!str.vazio(v[i])) {
				int p = v[i].indexOf(dVal);
				if (p==-1) {
					h.put(str.trimm(v[i]),"");
				} else {
					h.put( str.trimm(v[i].substring(0,p)) , str.trimm(v[i].substring(p+dVal.length())) );
				}
			}
		}
	}
	//****************************************************
	public defs() {
	}
	//****************************************************
	public defs(String tx) {
		init(tx);
	}
}
