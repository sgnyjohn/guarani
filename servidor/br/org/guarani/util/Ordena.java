/*
	* Signey John ago/2002. 
	*/
package br.org.guarani.util;

//***************************
//***************************
public class Ordena {
		//ordena vetores...
	//***************************
	//***************************
	public static void Sort(Object a[],Object d[]) {
		Sort(a,d,0,a.length-1);
	}
	//***************************
	public static void Sort(Object a[],Object d[], int lo0, int hi0) {
		int lo = lo0;
		int hi = hi0;
		String mid;
		if ( hi0 > lo0)   {
			mid = a[ ( lo0 + hi0 ) / 2 ].toString();
			while( lo <= hi ) {
				while( ( lo < hi0 ) && ( a[lo].toString().compareTo(mid)<0 )) {
					++lo;
				}
				while( ( hi > lo0 ) && ( a[hi].toString().compareTo(mid)>0 )) {
					--hi;
				}
				if( lo <= hi ) {
					swap(a,d,lo,hi);
					++lo;
					--hi;
				}
			}

			if( lo0 < hi ) {
				Sort(a,d,lo0,hi);
			}

			if( lo < hi0 ) {
				Sort(a,d,lo,hi0);
			}
		}
	}
	//***************************
	private static void swap(Object a[],Object d[],int i, int j) {
		Object T;
		T = a[i];
		a[i] = a[j];
		a[j] = T;
		T = d[i];
		d[i] = d[j];
		d[j] = T;
	}

	//***************************
	//***************************
	//***************************
	//***************************
	//***************************
	//***************************
	private static void QuickSort(String a[][],int opos, int lo0, int hi0,boolean inv)
	{
		int lo = lo0;
		int hi = hi0;
		String mid;

		if ( hi0 > lo0) {
			mid = a[ ( lo0 + hi0 ) / 2 ][opos];
			while( lo <= hi ) {
				if (inv) {
					while( ( lo < hi0 ) && ( a[lo][opos].compareTo(mid)>0 )) ++lo;
					while( ( hi > lo0 ) && ( a[hi][opos].compareTo(mid)<0 )) --hi;
				} else {
					while( ( lo < hi0 ) && ( a[lo][opos].compareTo(mid)<0 )) ++lo;
					while( ( hi > lo0 ) && ( a[hi][opos].compareTo(mid)>0 )) --hi;
				}
				if( lo <= hi ) {
					swap(a, lo, hi);
					++lo;
					--hi;
				}
			}

			if( lo0 < hi )
				QuickSort( a, opos, lo0, hi, inv);

			if( lo < hi0 )
				QuickSort( a, opos, lo, hi0, inv);

		}
	}

	private static void swap(String a[][], int i, int j) {
		String T[];
		T = a[i];
		a[i] = a[j];
		a[j] = T;
	}

	public static void sort(String a[][],int opos,boolean inv) {
		QuickSort(a, opos, 0, a.length - 1,inv);
	}
}
