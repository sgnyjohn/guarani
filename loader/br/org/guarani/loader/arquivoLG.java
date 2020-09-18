package br.org.guarani.loader;

import java.io.*;
import java.util.*;
import java.net.*;
import java.lang.reflect.*;

import gnu.crypto.cipher.*;

//import br.org.guarani.util.*;
//import br.org.guarani.servidor.*;



//**************************************************************
//**************************************************************
public class arquivoLG {
	private String algo,ch;
	private Object k;
	private BaseCipher t;
	private int tK = 32,tBf = 16;
	public String sErro;
	//**************************************************************
	public arquivoLG() {
	}
	//**************************************************************
	public void init(String s,String algo) {
		this.algo = algo;
		for (int i=0;i<ts.length;i++) {
			if (ts[i][0].equals(algo)) {
				tBf = strL.inteiro(strL.substrRat(","+ts[i][2],","),-1);
				tK = strL.inteiro(strL.substrRat(","+ts[i][3],","),-1);
			}
		}
		//on();
		while (s.length()<tK) {
			s += digitoL.H(digitoL.md5(s));
		}
		s = s.substring(0,tK);
		this.ch = s;
		on("s="+s+" algo=("+algo+") tBf="+tBf+" tK="+tK);
		try {
			t = (BaseCipher)(Class.forName("gnu.crypto.cipher."+algo)).newInstance();
			//String bl[] = s(t.blockSizes());
			//String ks[] = s(t.keySizes());
			k = t.makeKey(ch.getBytes(),tBf);
		} catch (Exception e) {
			sErro = strL.erro(e);
			on(sErro);
		}
	}
	//**************************************************************
	public boolean gravaTxt(File arquivo,String tx) {
		int t = tx.length();
		byte[] buf = new byte[t];
		for (int i=0;i<t;i++) {
			buf[i] = (byte)tx.charAt(i);
		}
		return grava(arquivo,buf);
	}
	//**************************************************************
	public boolean grava(File arquivo,byte buf[]) {
		int t = buf.length;
		try {
			//InputStreamReader r = new InputStreamReader(new FileInputStream(f));
			FileOutputStream r = new FileOutputStream(arquivo);
			r.write(buf,0,t);
			r.close();
		} catch (IOException e ) {
			on("ERRO ARQ "+arquivo+"<br>");
			on(strL.erro(e));
			return false;
		}
		return true;
	}
	//**************************************************************
	void on(String s) {
		System.out.println("pwwws.arquivoLG: "+s);
	}
	//**************************************************************
	public byte[] carrega(File arquivo) {
		int read = 0;
		int t = (int)arquivo.length();
		byte[] buf = new byte[t];
		try {
			//InputStreamReader r = new InputStreamReader(new FileInputStream(f));
			FileInputStream r = new FileInputStream(arquivo);
			read = r.read(buf,0,t);
			r.close();
		} catch (IOException e ) {
			on("ERRO ARQ "+arquivo+"<br>");
			on(e.toString());
			return new byte[0];
		}
		return buf;
	}
	//**************************************************************
	public byte[] dcry(byte bo[],String ch) {
		//initK("teste");
		//dcrypt
		byte b[] = new byte[bo.length];
		for (int i=0;i<b.length;i+=tBf) {
			t.decrypt(bo,i,b,i,k,tBf);
		}
		int tm = strL.inteiro(new String(b,0,9),-1);
		if (tm==-1 || tm>b.length-9) {
			on("ch="+ch+" tm="+tm+" "+(new String(b,0,9)));
			return null;
		}
		byte r[] = new byte[tm];
		for (int i=9;i<tm+9;i++) {
			r[i-9] = b[i];
		}
		return r;
	}
	//**************************************************************
	public byte[] cry(byte b[]) {
		//crypt
		int tb = b.length+9;
		tb += tBf-tb%tBf;
		byte r[] = new byte[tb];
		String tm = strL.right("00000000"+b.length,9);
		for (int i=0;i<9;i++) {
			r[i] = (byte)tm.charAt(i);
		}
		for (int i=0;i<b.length;i++) {
			r[i+9] = b[i];
		}
		byte r1[] = new byte[tb];
		for (int i=0;i<r.length;i+=tBf) {
			t.encrypt(r,i,r1,i,k,tBf);
		}
		return r1;
	}
	//**************************************
	String[] s(Iterator i) {
		String r = "";
		int m=0;
		while (i.hasNext()) {
			int a = strL.inteiro(""+i.next(),-1);
			m = Math.max(m,a);
			r += ","+a;
		}
		return new String[]{r.length()==0?r:r.substring(1),""+m};
	}
	String ts[][] = strL.palavraA("Anubis OK 16 16,20,24,28,32,36"
		+"~Blowfish OK 8 8,16,24,32,40,48,56"
		+"~Cast5 OK 8 5,6,7,8,9,10,11,12,13,14,15,16"
		+"~DES OK 8 8"
		+"~Khazad OK 8 16"
		+"~NullCipher OK 8,16,24,32 8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63"
		+"~Rijndael OK 16,24,32 16,24,32"
		+"~Serpent OK 16 16,24,32"
		+"~Square OK 16 16"
		+"~TripleDES OK 8 24"
		+"~Twofish OK 16 8,16,24,32"
	,"~"," ");
}