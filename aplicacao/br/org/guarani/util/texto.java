package br.org.guarani.util;

import java.util.*;

//*********************************************************************
//*********************************************************************
public class texto {
	public final static String acentos  = "áéíóúüàâêôãõñç";
	
	//*********************************************************************
	public static boolean ePalavra(String s) {
		for (int i=0;i<s.length();i++) {
			if (!eCharPalavra(s.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	//*********************************************************************
	public static boolean eCharPalavra(char c) {
		if (!(c>='a' && c<='z') && texto.charAt(acentos,c)==-1) {
			return c=='-';
		}
		return true;
	}
	//*********************************************************************
	public static int charAt(String s,char c) {
		for (int i=0;i<s.length();i++) {
			if (s.charAt(i)==c) {
				return i;
			}
		}
		return -1;
	}
}