/* ************************************
signey set/2020
*************************************/
package br.org.guarani.loader;

import java.util.*;
import java.io.*;


//***********************************
//***********************************
public class loaderConf {
	xmlTagL cfg;
	protected HashIndexL h = new HashIndexL();
	//***********************************
	public op get(String s) {
		op r = (op)h.get(s);
		if (r==null && s.charAt(0)=='-') {
			//pesquisa terminada em... 
			
			//String t = s+" === ";
			for (Enumeration e = h.h.keys(); e.hasMoreElements();) {
				String ch = (String)e.nextElement();
				if (strL.equalsR(ch,s)) {
					r = (op)h.get(ch);
					break;
				}
				//t += " * "+ch;
			}
			//logsL.grava("<br>h="+t);
		}
		if (r==null) {
			//logsL.grava("h="+s);
		}
		return r;
	}	
	/*public String get(String ch) {
		return (String)cfg.getCh(ch);
	}*/
	boolean init(xmlTagL Cfg) {
		cfg = Cfg;
		return true;
	}
}

