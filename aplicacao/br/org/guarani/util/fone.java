package br.org.guarani.util;

import java.util.*;
import java.io.*;

import br.org.guarani.servidor.*;
import br.org.guarani.bd.*;
import bd.*;


//***************************************
//***************************************
public class fone {
	public String fo,ddd="",fone="",obs="";
	public String sErro = "";
	//***************************************
	public boolean celular() {
		return fone.length()>0 && (fone.charAt(0)=='9' || fone.charAt(0)=='8');
	}
	//***************************************
	public boolean fax() {
		return (obs.toLowerCase().indexOf("fax")!=-1);
	}
	//***************************************
	public String toString() {
		if (sErro.length()!=0) {
			//logs.grava("fone="+fo);
			return str.trimm(fo);
		}
		return (ddd.length()==0?"":"("+ddd+") ")
			+(fone.length()==8?fone.substring(0,4)+" "+fone.substring(4)
				:fone.length()!=7?fone:fone.substring(0,3)+" "+fone.substring(3))
			+(obs.length()==0?"":" ("+obs+")")
			+(sErro.length()==0?"":" erro: "+sErro);
	} 
	//***************************************
	public fone(String s,fone pdr) {
		fo = s;
		if (str.vazio(s)) {
			sErro = "Vazio";
			return;
		}
		
		s = str.trimm(s);
		if (s.charAt(0)=='(') {
			ddd = str.trimm(str.leftAt(s,")"),"(");
			s = str.trimm(str.substrAt(s,")"));
		}

		if (str.vazio(s)) {
			sErro = "Vazio";
			return;
		}

		//tira zero esquerda
		if (s.charAt(0)=='0') {
			s = s.substring(1);
		}
		
		if (s.length()>3 && s.toLowerCase().substring(0,3).equals("fax")) {
			s = str.trimm(s.substring(3))+" "+s.substring(0,3);
		}
		
		
		int i = 0;
		char c;
		while (i<s.length()) {
			c=s.charAt(i);
			if (c>='0' && c<='9') {
				fone += s.substring(i,i+1);
			} else if (c==' ' || c=='-' || c=='.') {
			} else {
				obs = str.trimm(s.substring(i)," ()-");
				break;
			}
			i++;
		}
		
		if (fone.length()==9 || fone.length()==10) {
			ddd = fone.substring(0,2);
			fone = fone.substring(2);
		}
		
		//DDD padr
		if (ddd.length()==0) {
			if (pdr!=null) {
				ddd = pdr.ddd;
			}
		} else if (ddd.charAt(0)=='0') {
			ddd = ddd.substring(1);
		}
		//PREFIXO
		if (fone.length()>1 && fone.length()<5 && pdr!=null && pdr.sErro.length()==0) {
			fone = pdr.fone.substring(0,pdr.fone.length()-fone.length())
				+fone;
			sErro += " - ADD";
		}

		
		if (fone.length()!=8 && fone.length()!=7) {
			sErro += "tam telefone: "+fone.length();
		}
	}
}