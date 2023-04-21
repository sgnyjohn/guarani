/*
	Signey - mai/2003
*/

package br.org.guarani.util;
 
import java.lang.reflect.*;
import java.util.*;
import java.io.*;

import  java.math.BigInteger;  
import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;  
import java.util.zip.*;

//****************************
//****************************
public class arquivo1 extends arquivo {
	ObjectOutputStream oos;
	FileOutputStream oosF;
	FileInputStream oisF;
	ObjectInputStream ois;
	public String csvCampos[];
	public String vR[]; //cabeÃ§alho registro
	char cc[];
	boolean ccFim = false;
	int mxCampos = 32;
	public String dl = "\t"; //CSV delimitador colunas
	public String aspa = "\"";
	/***************************************************************/
	public String leTxt(String charSet) {
		if (!f.exists()) {
			erro("arquivo não existe");
			return null;
		}
		InputStream is = null;
		try {
			is = new BufferedInputStream(new FileInputStream(""+f));
			InputStreamReader rdr = new InputStreamReader(is, charSet);
			StringBuilder contents = new StringBuilder();
			char[] buff = new char[4096];
			int len;
			while ( (len = rdr.read(buff))>0) {
				contents.append(buff, 0, len);
			}
			return contents.toString();
		} catch (Exception e) {
			logs.grava("ERRO","ERRO no ARQ "+f+" "+e);
		} finally {
			try {
				is.close();
			} catch (Exception e) {
				// log error in closing the file
			}
		}
		return null;
	}	
	//****************************************************
	// retorna nome arqs
	public static String[] dir(String r)  {
		return (new File(r)).list();
	}
	//****************************************************
	// le uma linha em r
	public int leLinhaVet(char dl,char aspa,String r[])  {
		if (dl==aspa) { 
			logs.grava("erro","arquivo1.leLinhaCSV: parametros ERRADOS delimitador igual aspa");
			return -1;
		}
		//Hashtable r = new Hashtable();
		if (inp==null) {
			cc = new char[tBf];
			abreInp();
		} else if (ccFim) {
			return -1;
		}
		//le um reg
		int cm = 0; //campo
		int pc = 0; //posiÃ§ao no campo
		boolean agAspa = false, fimCampo = false, fimReg = false; //aguardando aspa
		while (!fimReg) {
			try { 
				int d = inp.read(); 
				char c = (char)d;
				if ( d == -1 ) {
					//fim arquivo
					fimReg = true;
					fimCampo = true;
					ccFim = true;
				} else if ( agAspa ) {
					if ( c == aspa ) {
						//while ( (c = (char)inp.read()) != dl ) {};
						//ignora 1 dl delimitador...
						agAspa = false;
						fimCampo = true;
						d = inp.read();
						if (d == '\n') {
							fimReg = true;
						}
					} else {
						cc[pc++] = c;
					}
				} else if ( pc == 0 && c == aspa ) {
					agAspa = true;
				} else if ( c == dl ) {
					//fim campo
					fimCampo = true;
				} else if ( c == '\n' ) {
					fimReg = true;
					fimCampo = true;
				} else if (c == '\r' ) {
					//ignora
				} else {
					cc[pc++] = c;
				}
			} catch (Exception e) {
				ccFim = true;
				fimCampo = true;
				fimReg = true;
			}
			//fim campo
			if (fimCampo) {
				if (pc!=0) {
					r[cm++] = new String(cc,0,Math.min(pc,tBf));
					pc = 0;
				}
				fimCampo = false;
			}
		}
		return cm;
	}	
	//****************************************************
	public boolean abreInp() {
		if (!str.equals(arq,"zip:")) {
			return super.abreInp();
		}
		//abrir ZIP
		String n[] = str.palavraA(arq,":");
		try {
			ZipFile zf = new ZipFile(n[1]);
			//int tBf = 4096;
			//byte bf[] = new byte[tBf];
			for (Enumeration e = zf.entries() ; e.hasMoreElements() ;) {
				ZipEntry z = (ZipEntry)e.nextElement();
				if ( n[2].equals(""+z) ) {
					inp = zf.getInputStream(z);
					return true;
				}
			}
			erro("nÃ£o encontrou arq="+n[2]+" no zip...");
		} catch (Exception e) {
			sErro = "arquivo1.abreInp: "+arq+"="+n[1]+" e="+str.erro(e);
			logs.grava("erro",sErro);
			return false;
		}		
		return false;
	}

	//****************************************************
	public String toString() {
		return ""+f;
	}
	//****************************************************
	// retorna md5 sum
	public String md5sum() {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			InputStream is = new FileInputStream(f);
			byte[] buffer = new byte[8192];
			int read = 0;
			while( (read = is.read(buffer)) >= 0) {
				md.update(buffer, 0, read);
			}
			is.close();
			//byte[] md5sum = digest.digest();
			BigInteger hash = new BigInteger(1, md.digest());  
			String sen = hash.toString(16); 
			while (sen.length()<32) {
				sen = "0"+sen;
			}
			return sen;
		} catch (Exception e) {
			logs.grava("erro","arquivo1.md5sum("+f+") ="+e);
		}
		return null;
	}
	//****************************************************
	// le uma linha CSV
	public Hashtable leLinhaCSV(char dl,char aspa)  {
		return csvLer(dl,aspa);
	}
	//****************************************************
	// le uma linha CSV
	public void csvCabLer(char dl,char aspa)  {
		Hashtable h = csvLer(dl,aspa);
		csvCampos = new String[h.size()];
		for (int i=0;i<h.size();i++) {
			csvCampos[i] = (String)h.get(""+i);
		}
	}
	//****************************************************
	// le uma linha CSV
	public Hashtable csvLer()  {
		return csvLer(dl.charAt(0),aspa.charAt(0));
	}
	public Hashtable csvLer(char dl,char aspa)  {
		if (dl==aspa) { 
			logs.grava("erro","arquivo1.leLinhaCSV: parametros ERRADOS delimitador igual aspa");
			return null;
		}
		Hashtable r = new Hashtable(mxCampos);
		if (cc==null) {
			cc = new char[tBf];
		}
		if (inp==null) {
			abreInp();
		} else if (ccFim) {
			return null;
		}
		//le um reg
		int cm = 0; //campo
		int pc = 0; //posiÃ§ao no campo
		boolean agAspa = false, fimCampo = false, fimReg = false; //aguardando aspa
		while (!fimReg) {
			try { 
				int d = inp.read(); 
				char c = (char)d;
				if ( d == -1 ) {
					//fim arquivo
					fimReg = true;
					fimCampo = true;
					ccFim = true;
				} else if ( agAspa ) {
					if ( c == aspa ) {
						//while ( (c = (char)inp.read()) != dl ) {};
						//ignora 1 dl delimitador...
						agAspa = false;
						fimCampo = true;
						d = inp.read();
						if (d == '\n') {
							fimReg = true;
						}
					} else {
						cc[pc++] = c;
					}
				} else if ( pc == 0 && c == aspa ) {
					agAspa = true;
				} else if ( c == dl ) {
					//fim campo
					fimCampo = true;
				} else if ( c == '\n' ) {
					fimReg = true;
					fimCampo = true;
				} else if (c == '\r' ) {
					//ignora
				} else {
					cc[pc++] = c;
				}
			} catch (Exception e) {
				ccFim = true;
				fimCampo = true;
				fimReg = true;
			}
			//fim campo
			if (fimCampo) {
				//tem nome campos
				if (csvCampos!=null) {
					r.put(cm>=csvCampos.length?""+cm:csvCampos[cm],new String(cc,0,Math.min(pc,tBf)));
					cm++;
				} else {
					//logs.grava("aaaar="+r);
					r.put(""+(cm++),new String(cc,0,Math.min(pc,tBf)));
				}
				pc = 0;
				fimCampo = false;
			}
		}
		mxCampos = Math.max(mxCampos,cm+10);
		return r;
	}	
	//****************************
	public static boolean isLink(File file) {
		try {
			if (!file.exists()) {
				return true;
			} else {
				String cnnpath = file.getCanonicalPath();
				String abspath = file.getAbsolutePath();
				return !abspath.equals(cnnpath);
			}
		} catch(IOException ex) {
			//System.err.println(ex);
			return true;
		}
	} 
	/****************************
	public boolean grava(String s) {
		//byte b[],int i,int f) 
		return grava(s.toBytes(),0,s.length());
	}
	*/
	//****************************
	static int delTree1(String dir) {
		int r = 0;
		String s = str.substrRat(dir,"/");
		if (s.length()==0 || s.equals(".") || s.equals("..")) {
			logs.grava("arquivo1.delTree1: n del "+s);
			return 0;
		}
		File v[] = (new File(dir)).listFiles();
		for (int i=0;i<v.length;i++) {
			if (v[i].isDirectory()) {
				r += delTree1(""+v[i]);
			} else {
				//logs.grava("del="+v[i]);
				r += v[i].delete()?1:0;
			}
		}
		//logs.grava("del="+dir);
		r += (new File(dir)).delete()?1:0;
		return r;
	}
	//****************************
	public static int delTree(String dir) {
		return delTree(dir,false);
	}
	//****************************
	public static int delTree(String dir,boolean nTmp) {
		if (!nTmp && dir.toLowerCase().indexOf("/tmp")==-1 && dir.toLowerCase().indexOf("/temp")==-1) {
			logs.grava(new Exception("delTree sem /tmp ou /temp"));
			return -1;
		}
		return delTree1(dir);
	}
	//****************************
	public Hashtable leReg(String Dl) {
		dl = Dl;
		return leReg();
	}
	//****************************
	public Hashtable<String,String> leReg() {
		String ln = leLinha();
		if (ln==null) {
			return null;
		}
		String v[] = str.palavraA(ln,dl);
		//primeira linha - cab?
		if (vR==null) {
			vR = v;
			ln = leLinha();
			if (ln==null) {
				return null;
			}
			v = str.palavraA(ln,dl);
		}
		Hashtable r = new Hashtable();
		for (int i=0;i<Math.max(v.length,vR.length);i++) {
			r.put(vR.length<=i?"cmp_"+i:vR[i],v.length<=i?"":v[i]);
		}
		return r;
	}
	//****************************
	public int read(byte b[]) {
		try {
			if (oisF==null) {
				oisF = new FileInputStream(arq);
			}
			return oisF.read(b);
		} catch (Exception e) {
			logs.grava(e);
		}
		return -1;
	}
	//****************************
	public int leChar() {
		if (inp==null) {
			abreInp();
		}
		try {
			return inp.read();
		} catch (Exception e) {
		}
		return -1;
	}
	//****************************
	public boolean grava(byte b[],int i,int f) {
		if (out==null) abreOut(false);
		try {
			out.write(b,i,f);
		} catch (Exception e) {
			erro("ERRO grava: "+f,e);
			return false;
		}
		return true;
	}
	//***************************************************************
	//adiciona ao final
	public boolean append(String tex) {
		return gravaTxt(tex,true);
	}
	//***************************************************************
	//copia para outro arq
	public boolean copia(String dest) {
		if (super.copia(dest)) {
			(new File(dest)).setLastModified(f.lastModified());
			return true;
		}
		return false;
	}
	//***************************************
	public Object leObj() {
		Object r = null;
		try {
			if (ois==null) {
				oisF = new FileInputStream(arq);
				ois = new ObjectInputStream(oisF);
			}
			r = ois.readObject();
		} catch (Exception e) {
			erro("lendo OBJ:"+f,e);
		}
		return r;
	}
	//***************************************
	public boolean grava(Object o) {
		try {
			if (oos==null) {
				oosF = new FileOutputStream(arq);
				oos = new ObjectOutputStream(oosF);
			}
			oos.writeObject(o);
			oos.flush();
		} catch (Exception e) {
			erro("Gravando OBJ:"+f,e);
			return false;
		}
		return true;
	}
	/***************************************************************/
	public boolean fecha() {
		try {
			if (oisF!=null) {
				oisF.close();
			}
			if (oosF!=null) {
				oosF.close();
			}
			if (inp != null) {
				inp.close();
			}
			if (out != null) {
				out.close();
			}
		} catch (Exception e) {
			erro("Fechando out:"+f,e);
			return false;
		}
		return true;
	}
	//***************************************
	public int write(byte[] b,int i,int t) {
		try {
			out.write(b,i,t);
		} catch (Exception e) {
			return -1;
		}
		return t;
	}
	//****************************
	public boolean setBuf(int tam) {
		tBf = tam;
		buf = new byte[tBf];
		return true;
	}

	/****************************
	public boolean grava(char c[],int ini,int t) {
		if (out==null) abreOut(false);
		int pb=0;
		for (int i=ini;i<t;i++) {
			buf[pb++] = (Pyte)c[i];
			if (pb==tBf) {
				grava(pb);
				pb=0;
			}
		}
		grava(pb);
		return true;
	}
	*/
	/****************************
	public boolean gravaLIXO(String s) {
		if (out==null) abreOut(false);
		int t = s.length(),pb=0;
		for (int i=0;i<t;i++) {
			buf[pb++] = (Pyte)s.charAt(i);
			if (pb==tBf) {
				grava(pb);
				pb=0;
			}
		}
		grava(pb);
		return true;
	}
	*/
	//****************************
	public boolean grava(int t) {
		try {
			out.write(buf,0,t);
		} catch (Exception e) {
			erro("ERRO grava: "+f,e);
			return false;
		}
		return true;
	}
	//****************************
	public arquivo1(String s) {
		super(s);
	}
}

