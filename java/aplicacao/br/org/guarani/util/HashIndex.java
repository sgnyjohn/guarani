package br.org.guarani.util;

import java.util.*;


//***********************
//***********************
public class HashIndex {
	public Hashtable h;
	private Hashtable hn;
	//***********************
	public HashIndex() {
		h = new Hashtable();
		hn = new Hashtable();
	}
	//***********************
	public String toString() {
		return ""+h;
	}
	//***********************
	public Object[] getArray() {
		Object r[] = new Object[size()];
		for (short i=0;i<size();i++) {
			r[i] = get(i);
		}
		return r;
	}
	//***********************
	//public void remove(int i) {
	// Object o = getChave(i);
	// hi.remove(""+i);
	// h.remove(o);
	//}
	//***********************
	private void set(int i,Object b) {
		Object o = hn.get(""+i);
		h.put(o,b);
	}
	//***********************
	public void put(Object o,Object b) {
		if (o==null) o = ""+o;
		if (b==null) b = ""+b;
		//corrigido bug 2017/nov - não regravava em hi... criava nova
		if (h.get(o)==null) {
			hn.put(hn.size()+"",o);
		}
		h.put(o,b);
	}
	//***********************
	public String getString(Object ch) {
		return (String)h.get(ch);
	}
	//***********************
	public int getInt(Object ch) {
		return str.inteiro((String)h.get(ch),-1);
	}
	//***********************
	public Object get(Object ch) {
		if (ch==null) return null;
		return h.get(ch);
	}
	//***********************
	public Object get(int i) {
		Object o = hn.get(""+i);
		return h.get(o);
	}
	//***********************
	public Object getChave(int i) {
		return hn.get(""+i);
	}
	//***********************
	public int size() {
		return h.size();
	}
}
