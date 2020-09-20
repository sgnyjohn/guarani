package br.org.guarani.util;

//import java.io.*;
import java.util.*;

//***************************************
//***************************************
public class xmlEnum {
	xmlTag tg,rt;
	String sTg;
	int pos;
	//***************************************
	public void remove() {
		tg.remove(pos-1);
	}
	//***************************************
	public xmlTag get() {
		return rt;
	}
	//***************************************
	public boolean next() {
		while ( (rt=tg.get(pos++))!=null) {
			//logs.grava("rt="+rt);
			//logs.grava("rt.nome="+rt.nome+" "+sTg);
			if (rt.nome!=null && rt.nome.equals(sTg) && !rt.removida) {
				return true;
			}
		}
		return false;
	}
	//***************************************
	public xmlEnum(xmlTag tag,String sTag) {
		tg = tag;
		sTg = sTag;
		pos = 0;
	}
}
