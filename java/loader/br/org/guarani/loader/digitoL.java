
package br.org.guarani.loader;

import java.util.StringTokenizer;
import java.io.*;
import java.security.*;

//import gnu.java.security.provider.*;


public class digitoL {

	private static MessageDigest mdMd5=null;
	//********************************
	public static String H(byte b[]) {
		int t = b.length;
		char r[] = new char[t*2];
		String tbc = "0123456789abcdef";
		for (int i=0;i<t;i++) {
			int ib = (int)b[i];
			if (ib<0) ib+=128;
			r[i*2] = tbc.charAt(ib/16);
			r[i*2+1] = tbc.charAt(ib%16);
		}
		return new String(r);
	}
	//**************************************/
	public static byte[] md5(String a) {
		int i,t=a.length();
		byte b[] = new byte[t];
		for (i=0;i<t;i++) {
			b[i] = (byte)a.charAt(i);
		}
		return md5(b);
	}
	//**************************************/
	public static byte[] md5(byte b[]) {
		try {
			if (mdMd5==null) {
				mdMd5 = MessageDigest.getInstance("MD5");
			}
			mdMd5.update(b);
			return mdMd5.digest();
		} catch (Exception cnse) {
			System.out.println("erro="+cnse);
			return null;
		}
	}
}
