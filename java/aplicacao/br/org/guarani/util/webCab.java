package br.org.guarani.util;

import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;

import br.org.guarani.util.*;
import br.org.guarani.servidor.*;

//***************************************
public class webCab {
	public Hashtable h = new Hashtable();
	public String sErro = "";
	public boolean erro = false;
	public int nRet = -1;
	public String sRet="";
	public long tam = -1;
	public Date data;
	public String sData;
	//***************************************
	public webCab(String txt) {
		String v[] = str.palavraA(str.trimm(txt),"\n");
		String l = str.trimm(v[0]);
		if (!str.leftAt(l,"/").equals("HTTP")) {
			sErro += "1 linha não HTTP ': '="+l+"<br/>";
			erro = true;
		} else {
			sRet = str.substrAtAt(l," "," ");
			nRet = str.inteiro(sRet,-2);
		}
		
		for (int i=1;i<v.length;i++) {
			l = str.trimm(v[i]);
			if (str.vazio(l)) {
			} else if (l.indexOf(": ")==-1) {
				sErro += "linha sem ': '="+l+"<br/>";
				erro = true;
			} else {
				String ch = str.trimm(str.leftAt(l,": ")).toLowerCase();
				String vl = str.trimm(str.substrAt(l,": "));
				h.put(ch,vl);
				if (ch.equals("last-modified")) {
					sData = vl;
					data = data1.http(vl);
				}
				if (ch.equals("content-length")) {
					tam = str.longo(vl,-2);
				}
			}
		}
	}
}