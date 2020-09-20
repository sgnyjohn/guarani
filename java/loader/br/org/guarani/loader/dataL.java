package br.org.guarani.loader;

import java.util.*;
import java.text.*;

public class dataL {
	//public static SimpleDateFormat fus = new SimpleDateFormat ("EEE, dd MMM yyyy hh:mm:ss z",Locale.US);
	public static SimpleDateFormat fbr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US);
	//,Locale.pt_BR);
	//private static Class c1 = gnu.java.locale.Calendar.class;
	//private static Class c2 = gnu.java.locale.LocaleInformation.class;

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
		return str(dataL.ms());
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
		SimpleDateFormat fus = new SimpleDateFormat ("EEE, dd MMM yyyy HH:mm:ss z",Locale.US);
		//SimpleDateFormat fus = new SimpleDateFormat ("EEE, dd MMM yyyy hh:mm:ss",Locale.US);
		fus.setTimeZone(TimeZone.getTimeZone("GMT"));
		//r = fus.format(a)+" GMT";
		r = fus.format(a);
		return r;
	}

	//*********************************
	//
	public static String strSmtp(Date a) {
		String r;
		SimpleDateFormat fus = new SimpleDateFormat ("EEE, dd MMM yyyy HH:mm:ss z",Locale.US);
		r = strL.troca(fus.format(a),"GMT","");
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

