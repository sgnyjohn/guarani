package br.org.guarani.util;

import br.org.guarani.servidor.*;
import java.util.*;
import java.io.*;

//import static java.nio.charset.StandardCharsets.*;
import java.nio.charset.*;
import java.nio.*;

//**************************************
//**************************************
public class convEncod {
	static String tbIso,tbU8,tbU8c;//,tbIsoU;
	static String tbUtf8[],tbCharE;
	static byte b[] = new byte[512];
	//*****************************************
	public static String isoUtfN(String s) {
		
		String d = "UTF-8";
		//String o = "ISO-8859-1";
		//CharsetDecoder decoder = Charset.forName(o).newDecoder();
		CharsetEncoder encoder = Charset.forName(d).newEncoder();
		ByteBuffer tmp;
		try {
			tmp = encoder.encode(CharBuffer.wrap(s));
		} catch(CharacterCodingException e) {
			return "ERRO "+e;
		}

		return (new String(tmp.array())).substring(0,s.length());
		

		//return (new String(tmp.array(),0,s.length()));
		
		//tmp.rewind();
		//return (new String(tmp.array(),0,tmp.arrayOffset()));
		
		//byte ptext[] = s.getBytes(); 
		//return (new String(ptext,ISO_8859_1))+" -<"; 
		
		//byte ptext[] = s.getBytes(ISO_8859_1); 
		//return new String(ptext, UTF_8); 
		
		//return Charset.forName("UTF-8").encode(s);

		//return isoUtfP(s);


	}
	//*****************************************
	public static String utfIso(String s) {
		return s;//Charset.forName("ISO-8859-1").encode(s);
	}
	//*****************************************
	public static String utfIsoP(String s) {
		init();
		if (s==null) {
			return s;
		}
		int t = s.length(),p=0,n;
		if (b.length<t) {
			b = new byte[t];
		}
		for (int i=0;i<t;i++) {
			char c = s.charAt(i);
			if (tbU8c.indexOf(c)!=-1) {
				//n = tbIsoU.indexOf(s.substring(i+1,i+2));
				n = tbU8.indexOf("~"+s.substring(i,i+2));
				if (n>-1) {
					n = n/3;
					c = tbIso.charAt(n);
					i++;
				} else {
					logs.grava("erro","falta na tabela utf-iso c="+s.substring(i,i+2)+" s="+s);
				}
			}
			b[p++] = (byte)c;
		}
		return new String(b,0,p);
	}
	//*****************************************
	public static String isoUtf(String s) {
		init();
		int t = s.length(),p=0;
		if (b.length<t*2) {
			b = new byte[t*2];
		}
		for (int i=0;i<t;i++) {
			char c = s.charAt(i);
			if ((c>='a' && c<='z') || (c>='A' && c<='Z') || (c>='0' && c<='9') || tbCharE.indexOf(c)!=-1 ) {
				b[p++] = (byte)c;
			} else {
				int x = tbIso.indexOf(c);
				if (x!=-1) {
					b[p++] = (byte)tbUtf8[x].charAt(0);
					b[p++] = (byte)tbUtf8[x].charAt(1);
				} else {
					b[p++] = (byte)' ';
				}
			}
		}
		return new String(b,0,p);
	}
	//**************************************
	public static void init()  {
		if (tbIso!=null) {
			return;
		}
		tbCharE = "";
		tbIso = "";
		//tbIsoU = "";
		tbU8 = "";tbU8c = "";
		String ti = str.troca((new arquivo(Guarani.dirCfg+"/tbISO8859-1.txt")).leTxt(),"\n","");
		String t[] = str.palavraA((new arquivo(Guarani.dirCfg+"/tbUTF8.txt")).leTxt(),"\n");
		//logs.grava("ti="+ti.length()+" tu="+t.length);
		tbUtf8 = new String[ti.length()];
		for (short i=0;i<t.length;i++) {
			if (i>=ti.length()) {
			} else if (t[i].length()==2) {
				tbUtf8[tbIso.length()] = t[i];
				//tbIsoU += t[i].substring(1);
				tbU8 += "~"+t[i];
				tbIso += ti.substring(i,i+1);
				if (tbU8c.indexOf(t[i].substring(0,1))==-1) {
					tbU8c += t[i].substring(0,1);
				}
			} else if (t[i].equals(ti.substring(i,i+1))) {
				tbCharE += t[i];
			} else {
				logs.grava(("modeloSXW: ERRO tabelas: linha="+i //new Exception
					+"...iso8859-1="+ti.substring(i,i+1)+" utf8="+t[i].length()+" "+t[i])
				);
			}
		}
	}
	//**************************************
	public static byte[] convS(byte[] arqO,String encO,String encD) {
		ByteArrayOutputStream  stD=null;
		try {
			//origem
			ByteArrayInputStream stO = new ByteArrayInputStream(arqO);
			InputStreamReader aqO = new InputStreamReader(stO,encO);
			//destino
			stD = new ByteArrayOutputStream(arqO.length);
			OutputStreamWriter aqD = new OutputStreamWriter(stD,encD);
			//grava...
			char[] buffer = new char[2048];
			while (true) {
				int count = aqO.read(buffer);
				if (count < 0) {
					break;
				}
				aqD.write(buffer, 0, count);
			}
			aqO.close();
			aqD.close();
		} catch (Exception e) {
			return null;
		}
		return stD.toByteArray();
	}
	//**************************************
	public static String convArq(String arqO,String encO,String encD) {
		File ao = new File(arqO+"~");
		if (ao.exists()) {
			ao.delete();
		}
		(new File(arqO)).renameTo(ao);
		String r = convEncod.convArq(""+ao,encO,arqO,encD);
		ao.delete();
		return r;
	}
	//**************************************
	public static String conv(String tx,String encO,String encD) {
		try {
			//return new String(tx.getBytes(encO),encD);
			return new String(tx.getBytes(),encD);
		} catch (Exception e) {
			return "convEncod.conv(): ERRO "+e;
		}
	}
	//**************************************
	public static String conv(byte tx[],String encO,String encD) {
		try {
			//return new String(tx.getBytes(encO),encD);
			return new String(tx,encD);
		} catch (Exception e) {
			return "convEncod.conv(): ERRO "+e;
		}
	}
	//**************************************
	public static String convArq(String arqO,String encO,String arqD,String encD) {
		try {
			//origem
			InputStream stO = new FileInputStream(arqO);
			InputStreamReader aqO = new InputStreamReader(stO,encO);
			//destino
			OutputStream  stD = new FileOutputStream(arqD);
			OutputStreamWriter aqD = new OutputStreamWriter(stD,encD);
			//grava...
			char[] buffer = new char[2048];
			while (true) {
				int count = aqO.read(buffer);
				if (count < 0) {
					break;
				}
				aqD.write(buffer, 0, count);
			}
			aqO.close();
			aqD.close();
		} catch (Exception e) {
			return str.erro(e);
		}
		return null;
	}
}
