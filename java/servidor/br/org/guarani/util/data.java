/*
	* Signey John jan/2001. 
	*/

package br.org.guarani.util;

import java.util.*;
import java.text.*;

public class data {
	//public static SimpleDateFormat fus = new SimpleDateFormat ("EEE, dd MMM yyyy hh:mm:ss z",Locale.US);
	public static SimpleDateFormat fbr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US);
	//,Locale.pt_BR);
	//private static Class c1 = gnu.java.locale.Calendar.class;
	//private static Class c2 = gnu.java.locale.LocaleInformation.class;
	private static SimpleDateFormat fusHttp;

	//*********************************
	public static long ms(String st) {
		if (str.vazio(st)) {
			return data.ms(); 
		}
		//lert('strToData: '+str);
		//falta hora?
		int h[] = null;
		if (st.indexOf(" ")==-1) {
			h = new int[]{0,0,0,0};
		} else {
			String v[] = str.palavraA(str.substrAt(st," "),":");
			st = str.leftAt(st," ");
			if (v[2].indexOf(".")!=-1) {
				h[3] = str.inteiro(str.substrAt(v[2],"."),-1);
				h[2] = str.inteiro(str.leftAt(v[2],"."),-1);
			} else {
				h[3] = 0;
			}
			h[0] = str.inteiro(v[0],-1);
			h[1] = str.inteiro(v[1],-1);
			h[2] = str.inteiro(v[2],-1);
		}
		// d/m/y
		if (st.indexOf("/")!=-1) {
			String d[] = str.palavraA(st,"/"); 
			return new GregorianCalendar(
				str.inteiro(d[2],-1),str.inteiro(d[1],-1)-1,str.inteiro(d[1],-1)
				,h[0],h[1],h[2]
			).getTimeInMillis();
		} else {
			String d[] = str.palavraA(st,"-"); 
			return new GregorianCalendar(
				str.inteiro(d[0],-1),str.inteiro(d[1],-1)-1,str.inteiro(d[2],-1)
				,h[0],h[1],h[2]
			).getTimeInMillis();
		}
	}

	//*********************************
	public static String strDataTime(long d) {
		String r;
		//SimpleDateFormat fus = new SimpleDateFormat ("EEE, dd MMM yyyy HH:mm:ss z");
		SimpleDateFormat fus = 
			new SimpleDateFormat ("dd/MM/yyyy HH:mm:ss");
		r = fus.format(new Date(d));
		return r;
	}
	//*********************************
	public static String str() {
		return str(data.ms());
	}
	//*********************************
	public static String str(long d) {
		String r;
		//SimpleDateFormat fus = new SimpleDateFormat ("EEE, dd MMM yyyy HH:mm:ss z");
		SimpleDateFormat fus = 
			new SimpleDateFormat ("dd/MM/yyyy");
		r = fus.format(new Date(d));
		return r;
	}
	public static long ms() {
		return System.currentTimeMillis();
	}
	//*********************************
	//
	public static String strHttp(Date a) {
		String r;
		//Locale l = new Locale("pt_BR");
		/*ResourceBundle res = ResourceBundle.getBundle("gnu.java.locale.LocaleInformation", l,
					 ClassLoader.getSystemClassLoader());
		//ResourceBundle res = ResourceBundle.getBundle("gnu.java.locale.LocaleInformation");
		//ampms = res.getStringArray ("ampms");
		logs.grava("res="+res);
		logs.grava("res="+res.getClass().getName());
		Object eras = res.getObject("eras");
		logs.grava("eras="+eras.getClass());
		logs.grava("eras="+eras.getClass().getName()+"="+eras);
		String v[] = (String[])eras;
		logs.grava("eras="+v.length);
		logs.grava("eras r="+res.getStringArray ("eras"));
		*/
		
		
		//SimpleDateFormat fus = new SimpleDateFormat ("EEE, dd MMM yyyy hh:mm:ss",Locale.US);
		//SimpleDateFormat fus = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z",l);
		if (fusHttp==null) {
			Locale l = Locale.UK;
			fusHttp = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss",l);
			fusHttp.setTimeZone(TimeZone.getTimeZone("GMT+00"));
		}
		//gcj4//
		//fus.setTimeZone(TimeZone.getTimeZone("GMT"));
		//r = fus.format(a)+" GMT";
		r = fusHttp.format(a)+" GMT";
		return r;
	}

	//*********************************
	//
	public static String strSmtp(Date a) {
		String r;
		SimpleDateFormat fus = new SimpleDateFormat ("EEE, dd MMM yyyy HH:mm:ss z",Locale.US);
		r = str.troca(fus.format(a),"GMT","");
		return r;
	}

	//*********************************
	//
	public static String strSmtp() {
		return strSmtp(new Date());
	}

	//*********************************
	//
	public static String strHttp() {
		return strHttp(new Date());
	}

	//*********************************
	//
	public static String strHttp(long a) {
		return strHttp(new Date(a));
	}

	//*********************************
	//
	public static String strSql(Date a) {
		String r;
		if (a==null) return "";
		r = fbr.format(a);
		return r;
	}

	//*********************************
	//
	public static String strSql() {
		return strSql(new Date());
	}

	//*********************************
	//
	public static String strSql(long a) {
		return strSql(new Date(a));
	}

}

