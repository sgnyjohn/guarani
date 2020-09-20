package br.org.guarani.util;

import java.text.*;

//**************************************
//**************************************
public class num {
	private final static int[] tM = new int[]{256*256*256,256*256,256,1};
	private static NumberFormat nf = NumberFormat.getInstance();
	private static NumberFormat nF[] = new NumberFormat[8];
	//**************************************
	public static String format(float i,int d) {
		if (nF[d]==null) {
			nF[d] = new DecimalFormat("#,##0"+
				(d==0?"":".00000000".substring(0,d+1)));
		}
		return nF[d].format(i);
	}
	//**************************************
	public static String format(int i,int d) {
		if (nF[d]==null) {
			nF[d] = new DecimalFormat("#,##0"+
				(d==0?"":".00000000".substring(0,d+1)));
		}
		return nF[d].format(i);
	}
	//**************************************
	public static String format(long i,int d) {
		if (nF[d]==null) {
			nF[d] = new DecimalFormat("#,##0"+
				(d==0?"":".00000000".substring(0,d+1)));
		}
		return nF[d].format(i);
	}
	//**************************************
	public static String format(double i,int d) {
		if (nF[d]==null) {
			nF[d] = new DecimalFormat("#,##0"+
				(d==0?"":".00000000".substring(0,d+1)));
		}
		return nF[d].format(i);
		/*String a = nf.format(i);
		int p = a.indexOf(",");
		if (p==-1) {
			return a+","+("000000".substring(0,d));
		} else {
			return (a+"0000000").substring(0,p+d+1);
		}
		*/
	}
 
	//**************************************
	public static byte[] intToByte(int n,int tam) {
		byte r[] = new byte[tam];
		num.intToByte(n,r,0,tam);
		return r;
	}
	//**************************************
	public static void intToByte(int n,byte b[],int pos,int tam) {
		int a, ds=pos+tam-1;
		for (int i=0;i<tam;i++) {
			a = n % 256;
			b[ds-i] = (byte)a;
			n = (n-a)/256;
		}
	}
	//**************************************
	public static int byteToInt(byte b[]) {
		return num.byteToInt(b,0,b.length);
	}
	//**************************************
	public static int byteToInt(byte b[],int pos,int tam) {
		int r = 0,ds=tM.length-tam;
		//try {
			for (int i=0;i<tam;i++) {
				r += (((int)b[pos+i]) & 0xff) * num.tM[i+ds];
			}
		//} catch (Exception e) {
		//	r = -1;
		//	logs.grava("b.l="+b.length+" pos="+pos+" tam="+tam);
		//}
		return r;
	}
	//**************************************
	public static String format(int i) {
		return nf.format(i);
	}
	//**************************************
	public static String format(long i) {
		return nf.format(i);
	}
	//**************************************
	public static String format(double i) {
		return nf.format(i);
	}
	//**************************************
	public static int max(int a,int b) {
		return (a>b?a:b);
	}
	//**************************************
	public static int min(int a,int b) {
		return (a>b?b:a);
	}
}
