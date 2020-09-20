/*
	* Signey John jan/2001. 
	*/

package br.org.guarani.util;

import br.org.guarani.util.*;
import java.util.*;


/***************************************************************/
/***************************************************************/
public class conf {
	arquivo aq;

	public conf(String arq) {
		aq = new arquivo(arq);
	}

	public Hashtable getConf() {
		String v[] = str.palavraA(aq.leTxt(),"\n");
		String a=null;
		char c;
		Hashtable h=null,hr=new Hashtable();
		int i,p;

		for (i=0;i<v.length;i++) {
			//System.out.println(v[i]);
			v[i] = str.trimm(v[i]);
			if (v[i].length()==0) v[i]="/";
			c = v[i].charAt(0);
			if (c=='[') {
				if (a!=null) {
					//System.out.println(a);
					hr.put(a,h);
				}
				h = new Hashtable();
				a = v[i].substring(1,v[i].length()-1);
			} else if (c!='/' & (p = v[i].indexOf("="))>0 ) {
				h.put(str.trimm(v[i].substring(0,p)),str.trimm(v[i].substring(p+1,v[i].length())));
			}
		}

		if (a!=null) {
			//System.out.println(a);
			hr.put(a,h);
		}


		return hr;

	}

}
