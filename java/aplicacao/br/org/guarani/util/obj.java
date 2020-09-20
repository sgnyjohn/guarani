package br.org.guarani.util;

import java.util.*;

//*************************************
//*************************************
public class obj {
	//*************************************
	public static boolean tipo(Object o,String s) {
		return tipo(o).equals(s);
	}
	//*************************************
	public static String tipo(Object o) {
		String r=null;
		try {
			r = o.getClass().getName();
		} catch (Exception e) {
		}
		return r;
	}
}
