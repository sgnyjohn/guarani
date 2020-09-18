/*
	Signey - mai/2003
*/

package br.org.guarani.util;

import java.lang.reflect.*;
import java.util.*;

public class Sort {
	//*************************************
	public static Object[] sort(Hashtable h) {
		return sort(h,"toString",false);
	}
	//*************************************
	public static Object[] sort(Hashtable h,String campo) {
		return sort(h,campo,false);
	}
	//*************************************
	public static Object[] sort(Hashtable h,String campo,boolean inverso) {
		Object o[] = new Object[h.size()];
		int i = 0;
		for (Enumeration e = h.elements() ; e.hasMoreElements() ;) {
			o[i++] = e.nextElement();
		}
		if (Sort.sort(o,campo,inverso)) {
			return o;
		} else {
			return null;
		}
	}
	//******************************************************
	// ordena vetor de Objeto por campo ou metodo do objeto 
	//******************************************************
	public static boolean sort(Object a[]) {
		return sort(a,"toString",false);
	}
	//*************************************
	public static boolean sort(Object a[],String campo) {
		return sort(a,campo,false);
	}
	//*************************************
	public static boolean sort(Object a[],String campo,boolean inverso) {
		if (a.length<2) return true;
		Field cmp=null;
		Method mth=null;
		val v=null;
		try {
			cmp = a[0].getClass().getDeclaredField(campo);
			v = new val(cmp,a[0]);
		} catch (Exception e) {
			try {
				mth = a[0].getClass().getDeclaredMethod(campo,null);
				v = new val(mth,a[0]);
			} catch (Exception e1) {
				return false;
			}
		}
		v.inverso = inverso;
		QuickSort(a,v,0, a.length - 1);
		return true;
	}
	//*************************************
	private static void QuickSort(Object a[],val v,int lo0,int hi0) {
		int lo = lo0;
		int hi = hi0;
		if ( hi0 > lo0) {
			v.set(a[ ( lo0 + hi0 ) / 2 ]);
			if (v.erro) return;
			while( lo <= hi )    {
				if (v.inverso) {
					while( lo<hi0 && v.maior(a[lo]) ) ++lo;
					while( hi>lo0 && v.menor(a[hi]) ) --hi;
				} else {
					while( lo<hi0 && v.menor(a[lo]) ) ++lo;
					while( hi>lo0 && v.maior(a[hi]) ) --hi;
				}
				if( lo <= hi ) {
					swap(a, lo, hi);
					++lo;
					--hi;
				}
			}
			if( lo0 < hi )
				QuickSort( a, v, lo0, hi );
			if( lo < hi0 )
				QuickSort( a, v, lo, hi0 );
		}
	}
	private static void swap(Object a[], int i, int j) {
		Object T;
		T = a[i];
		a[i] = a[j];
		a[j] = T;
	}
	//********************************************************
	// objeto auxiliar p/ordenar p/campo de objeto ou metodo
	//********************************************************
	static class val {
		Field f;
		Method m;
		short tipo,tipo1;
		boolean inverso = false;
		//para setar
		int inteiro;
		long longo;
		double duplo;
		String stri;
		boolean erro = false;
		String sErro;
		//****************************
		public boolean maior(Object oo) {
			Object o = obj(oo);
			boolean r=false;
			if (tipo1==1) {
				r = ((Integer)o).intValue()>inteiro;
			} else if (tipo1==2) {
				r = ((Long)o).longValue()>longo;
			} else if (tipo1==3) {
				r = ((Double)o).doubleValue()>duplo;
			} else {
				r = o.toString().compareToIgnoreCase(stri)>0;
			}
			return r;
		}
		//****************************
		public boolean menor(Object oo) {
			Object o = obj(oo);
			boolean r=false;
			if (tipo1==1) {
				r = ((Integer)o).intValue()<inteiro;
			} else if (tipo1==2) {
				r = ((Long)o).longValue()<longo;
			} else if (tipo1==3) {
				r = ((Double)o).doubleValue()<duplo;
			} else {
				r = o.toString().compareToIgnoreCase(stri)<0;
			}
			return r;
		}
		//****************************
		public void set(Object oo) {
			Object o = obj(oo);
			if (tipo1==1) {
				inteiro = ((Integer)o).intValue();
			} else if (tipo1==2) {
				longo = ((Long)o).longValue();
			} else if (tipo1==3) {
				duplo = ((Double)o).doubleValue();
			} else {
				stri = o.toString();
			} 
		}
		//****************************
		public Object obj(Object o) {
			try {
				if (tipo==0) {
					return f.get(o);
				} else {
					return m.invoke(o,null);
				}
			} catch (Exception e) {
				sErro = "ERRO....tipo="+tipo+" tipo1="+tipo1+" "+e;
				erro = true;
				return "";
			}
		}
		//****************************
		public short tipo(Object o) {
			try {
				String tp=o.getClass().getName();
				if (tp.equals("java.lang.Long") || tp.equals("long")) {
					return 2;
				} else if (tp.equals("java.lang.Integer") || tp.equals("int")) {
					return 1;
				} else if (tp.equals("java.lang.Double") || tp.equals("double")) {
					return 3;
				}
				return 0;
			} catch (Exception e) {
				return 0;
			}
		}
		//****************************
		public val(Field f,Object o) {
			this.f = f;
			tipo=0;
			tipo1 = tipo(obj(o));
		}
		//****************************
		public val(Method m,Object o) {
			this.m = m;
			tipo=1;
			tipo1 = tipo(obj(o));
		}
	}
	//*********************************************************
	//*********************************************************
	//*********************************************************
	// ordena matriz String indicando coluna e ordem inv
	//*********************************************************
	public static void sort(String a[][],int opos,boolean inv) {
		QuickSort(a, opos, 0, a.length - 1,inv);
	}
	//*********************************************************
	public static void sort(String a[][],int opos) {
		QuickSort(a, opos, 0, a.length - 1,false);
	}
	//*********************************************************
	private static void QuickSort
		(String a[][],int opos, int lo0, int hi0,boolean inv) {
		int lo = lo0;
		int hi = hi0;
		String mid;
		if ( hi0 > lo0) {
			mid = a[ ( lo0 + hi0 ) / 2 ][opos];
			while( lo <= hi ) {
				if (inv) {
					while( lo<hi0 && a[lo][opos].compareTo(mid)>0 ) ++lo;
					while( hi>lo0 && a[hi][opos].compareTo(mid)<0 ) --hi;
				} else {
					while( lo<hi0 && a[lo][opos].compareTo(mid)<0 ) ++lo;
					while( hi>lo0 && a[hi][opos].compareTo(mid)>0 ) --hi;
				}
				if( lo <= hi ) {
					swap(a, lo, hi);
					++lo;
					--hi;
				}
			}
			if( lo0 < hi ) QuickSort( a, opos, lo0, hi, inv);
			if( lo < hi0 ) QuickSort( a, opos, lo, hi0, inv);
		}
	}
	//*********************************************************
	private static void swap(String a[][], int i, int j) {
		String T[];
		T = a[i];
		a[i] = a[j];
		a[j] = T;
	}
}

