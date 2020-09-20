package br.org.guarani.loader;

import java.util.*;


//***********************
//***********************
public class HashIndexL {
	public Hashtable h;
	public Hashtable hi;
	//***********************
	public HashIndexL() {
		h = new Hashtable();
		hi = new Hashtable();
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
	public void set(int i,Object b) {
		Object o[] = (Object[])hi.get(""+i);
		o[1] = b;
		Object o1 = o[0];
		h.remove(o1);
		h.put(o1,b);
	}
	//***********************
	public void put(Object o,Object b) {
		if (o==null) o = ""+o;
		if (b==null) b = ""+b;
		hi.put(h.size()+"",new Object[]{o,b});
		h.put(o,b);
	}
	//***********************
	public String getString(Object ch) {
		return (String)h.get(ch);
	}
	//***********************
	public int getInt(Object ch) {
		return strL.inteiro((String)h.get(ch),-1);
	}
	//***********************
	public Object get(Object ch) {
		if (ch==null) return null;
		return h.get(ch);
	}
	//***********************
	public Object get(int i) {
		return ((Object[])hi.get(""+i))[1];
	}
	//***********************
	public Object getChave(int i) {
		return ((Object[])hi.get(""+i))[0];
	}
	//***********************
	public int size() {
		return h.size();
	}
}
