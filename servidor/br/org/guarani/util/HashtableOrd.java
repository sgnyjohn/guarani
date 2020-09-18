/*
	* Signey John ago/2002. 
	*/
package br.org.guarani.util;

import java.util.*;


//***************************
//***************************
public class HashtableOrd extends java.util.Hashtable {
	//***************************
	public String options(String pdr) {
		String v[][] = OrdenaV(),r=""; 
		for (int i=0;i<v.length;i++) {
			r += "<option "+(pdr.equals(v[i][0])?"selected":"")+" value=\""+v[i][0]+"\">"+v[i][1];
		}
		return r;
	}
	//***************************
	public Object[] Ordena(String s) {
		Object k[] = new Object[size()]; 	
		Object v[] = new Object[size()]; 	
		int i=0;
		for (Enumeration e = keys(); e.hasMoreElements();) {
			k[i] = e.nextElement();
			v[i] = ((ObjectOrd)get(k[i])).getOrdem(s);
			i++;
		}
		Ordena.Sort(v,k);
		return k;
	}
	//***************************
	public String[][] OrdenaV() {
		String k[][] = new String[size()][2]; 	
		int i=0;
		for (Enumeration e = keys(); e.hasMoreElements();) {
			k[i][0] = ""+e.nextElement();
			k[i][1] = ""+get(k[i][0]);
			i++;
		}
		Ordena.sort(k,1,false);
		return k;
	}
}
