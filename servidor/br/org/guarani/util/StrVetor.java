/*
	*/

package br.org.guarani.util;

import java.util.*;
import java.text.*;

//********************************//
//********************************//
public class StrVetor {
	private Vector idx,dad;

	//********************************//
	public StrVetor() {
		idx = new Vector();
		dad = new Vector();
	}

	//********************************//
	public void put(Object o,Object d) {
		int i = idx.indexOf(o);
		if (i<0) {
			idx.add(o);
			dad.add(d);
		} else {
			dad.set(i,d);
		}
	}

	//********************************//
	public void put(int i,Object d) {
		dad.set(i,d);
	}
  
	//********************************//
	public Object get(Object o) {
		int i = idx.indexOf(o);
		if (i<0) return null;
		return dad.get(i);
	}

	//********************************//
	public int geti(Object o) {
		int i = idx.indexOf(o);
		return i;
	}

	//********************************//
	public Object getc(int i) {
		return idx.get(i);
	}
 
 
	//********************************//
	public Object get(int i) {
		return dad.get(i);
	}

	//

	//********************************//
	public void ordenaChave() {
		boolean o=true;
		Object oo,oc;
		while (o) {
			o = false;
			for (int i=0;i<idx.size()-1;i++) {
				if (idx.get(i).toString().compareTo(idx.get(i+1).toString())>0) {

					o = true;

					oo = idx.get(i);
					idx.set(i,idx.get(i+1));
					idx.set(i+1,oo);

					oc = dad.get(i);
					dad.set(i,dad.get(i+1));
					dad.set(i+1,oc);
				}
			}
		}
	}

	//********************************//
	public void ordenaConteudo() {
		boolean o=true;
		Object oo,oc;
		while (o) {
			o = false;
			for (int i=0;i<idx.size()-1;i++) {
				if (dad.get(i).toString().compareTo(dad.get(i+1).toString())>0) {

					o = true;

					oo = idx.get(i);
					idx.set(i,idx.get(i+1));
					idx.set(i+1,oo);

					oc = dad.get(i);
					dad.set(i,dad.get(i+1));
					dad.set(i+1,oc);
				}
			}
		}
	}
	//********************************//
	public void ordenaConteudoV(int pos) {
		boolean o=true;
		Object oo,oc;
		while (o) {
			o = false;
			for (int i=0;i<idx.size()-1;i++) {
				if ( ( (String[])dad.get(i) )[pos].compareTo(
					( (String[])dad.get(i+1) )[pos])>0) {

					o = true;

					oo = idx.get(i);
					idx.set(i,idx.get(i+1));
					idx.set(i+1,oo);

					oc = dad.get(i);
					dad.set(i,dad.get(i+1));
					dad.set(i+1,oc);
				}
			}
		}
	}

	//********************************//
	public int size() {
		return idx.size();
	}

	//********************************//
	public StrVetor copia() {
		StrVetor c = new StrVetor();
		c.idx = (Vector)idx.clone();
		c.dad = (Vector)dad.clone();
		return c;
	}

}

