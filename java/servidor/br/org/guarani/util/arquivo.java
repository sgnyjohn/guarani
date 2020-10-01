/*
	*/

package br.org.guarani.util;

import java.io.*;
import java.util.*;
import br.org.guarani.util.*;


/***************************************************************/
/***************************************************************/
public class arquivo {
	public File f;
	protected String arq;
	public OutputStream out;
	protected int tBf = 1024*4;
	protected byte[] buf = new byte[tBf];
	protected InputStream inp=null;
	protected BufferedReader inpB=null;
	public int tBfL=512;
	public String sErro;
	public String charSet = System.getProperty("file.encoding");
	public boolean append = false;
	String lf = "\r\n"; 
	//****************************
	public boolean gravaLinha(String s) {
		return grava(s+lf);
	}
	//****************************
	public boolean grava(char c[],int t) {
		return grava(c,0,t);
	}
	//****************************
	public boolean grava(char c[],int ini,int t) {
		return grava(new String(c,ini,t));
	}
	//****************************
	public boolean grava(String s) {
		if (out==null) abreOut(append);
		byte b[] = s.getBytes();
		try {
			out.write(b);
		} catch (Exception e) {
			logs.grava("ERRO","arquivo1.grava(String s): "+e);
			return false;
		}
		return true;
	}	
	//***************************************
	public static void dirRecursivo(File f,strPesq fil,Hashtable r) {
		File v[] = f.listFiles();
		if (v==null) {
			logs.grava("erro","ERRO: listfiles() em: "+f);
			return;
		}
		for (int i=0;i<v.length;i++) {
			//logs.grava(v[i].getName().toLowerCase()+" "+fil);
			if (fil.testa(v[i].getName())) {
				r.put(""+r.size(),v[i]);
			}
			if (v[i].isDirectory()) {
				dirRecursivo(v[i],fil,r);
			}
		}
	}
	//***************************************
	public static void dirRecursivo(File f,Hashtable h) {
		dirRecursivo(f,null,h);
	}
	//***************************************
	public static Hashtable dirRecursivo(String dr) {
		return dirRecursivo(dr,null);
	}
	//***************************************
	public static Hashtable dirRecursivo(String dr,String Fil) {
		strPesq fil = new strPesq(Fil);
		Hashtable r = new Hashtable();
		dirRecursivo(new File(dr),fil,r);
		return r;
	}
	/***************************************************************/
	public boolean deleta() {
		return f.delete();
	}
	/***************************************************************/
	public boolean existe() {
		return f.exists();
	}
	//***************************************************************/
	public synchronized static String nomeTmp(String aq,String ext) {
		boolean a=true;
		String r="";
		int i=0,i1=0;

		if (aq.indexOf("/")==-1) {
			aq = so.dirTmp()+"/"+aq;
		}
		if (ext.indexOf(".")==-1) {
			ext = "."+ext;
		}
		
  
		while (a) {
			i1++;
			if (i1>30) {
				logs.grava(new Exception(),
					"ERRO geração de arq temporário: tentativas>30"	);
				return null;
			}

			r = aq+System.currentTimeMillis()+ext;
			File fi = new File(r);
			if ((a = fi.exists())) {
				i++;
				if (i>10) {
					logs.grava(new Exception(),
						"ERRO geração de arq temporário: tentativas>10");
					return null;
				}
			} else {
				try {
					fi.createNewFile();
				} catch (Exception e) {
					a = true;
				}
			}

			if (a) {
				try {
					Thread.sleep(27);
				} catch (Exception e) {
				}
			}

		}
		return r;
	} 
	/***************************************************************/
	public arquivo(String aq) {
		arq = aq;
		f = new File(arq);
	}
	/**************************************/
	public String leLinha(int bf) {
		tBfL = bf;
		return leLinha();
	}
	/**************************************/
	public String leLinha() {
		if (inpB==null) abreInpB();
		try {
			return inpB.readLine();
		} catch (Exception e) {
			erro("arq: "+f,e);
			return null;
		}
	}
	/***************************************************************/
	public String leTxt() {
		if (!f.exists()) {
			erro("arquivo não existe");
			return null;
		}
		try {
			InputStream r = new FileInputStream(f);
			int read = 0;
			String rt = "";
			while ((read = r.read(buf,0,tBf)) != -1) {
				rt += new String(buf,0,read);
			}
			r.close();
			return rt;
		} catch (IOException e ) {
			erro("arquivo.leTxt(): "+f,e);
		}
		return null;
	}
	/***************************************************************/
	//abre e grava texto
	public boolean gravaTxt(String txt,boolean adiciona) {
		try {
			if (abreOut(adiciona)) {
				/*utf8 - retirado isto - int tBf = txt.length();
				byte[] bf = new byte[tBf];
				for (int i=0;i<tBf;i++) {
					bf[i] = (byte)txt.charLIXOAt(i);
				}
				out.write(bf,0,tBf);
				*/
				byte bf[] = txt.getBytes();
				out.write(bf,0,bf.length);
				out.close();
				return true;
			}
		} catch (IOException e ) {
			erro("arq: "+f,e);
		}
		return false;
	}
	/***************************************************************/
	public boolean gravaTxt(String txt) {
		return gravaTxt(txt,false);
	}
	/***************************************************************/
	public boolean fecha() {
		if (inp != null) {
			try {
				inp.close();
			} catch (Exception e) {
				erro("Fechando inp:"+f,e);
				return false;
			}
		}
		if (inpB != null) {
			try {
				inpB.close();
			} catch (Exception e) {
				erro("Fechando inp:"+f,e);
				return false;
			}
		}
		if (out != null) {
			try {
				out.close();
			} catch (Exception e) {
				erro("Fechando out:"+f,e);
				return false;
			}
		}
		return true;
	}
	/***************************************************************/
	public boolean abreInp() {
		try {
			inp = new FileInputStream(arq);
			return true;
		} catch (Exception e) {
			erro("arq: "+f,e);
			return false;
		}
	}
	/***************************************************************/
	public boolean abreInpB() {
		try {
			//até 2020 inpB = new BufferedReader(new FileReader(arq),tBfL);
			inpB = new BufferedReader(
				new InputStreamReader(new FileInputStream(arq), charSet)
			,tBfL);
			return true;
		} catch (Exception e) {
			erro("arq: "+f,e);
			return false;
		}
	}
	/***************************************************************/
	public boolean abreOut(boolean adiciona) {
		try {
			if (f.exists()) {
				if (!adiciona) {
					f.delete();
					f.createNewFile();
				}
			} else {
				f.createNewFile();
			}
			out = new FileOutputStream(arq,adiciona);
			return true;
		} catch (IOException e ) {
			erro("arquivo.abreOut():"+f.getPath(),e);
		}
		return false;
	}
	/***************************************************************/
	//copia para outro arq
	public boolean copia(String dest) {
		arquivo d = new arquivo(dest);
		if (!d.abreOut(false)) {
			return false;
		} else {
			try {
				InputStream r = new FileInputStream(f);
				int read = 0;
				while ((read = r.read(buf,0,tBf)) != -1) {
					//rt += new String(buf,0,read);
					d.out.write(buf,0,read);
				}
				r.close();
				d.out.close();
			} catch (Exception e) {
				erro("Erro Cópia "+f+" para "+dest,e);
				return false;
			}
		}
		return true;
	}
	/***************************************************************/
	public void erro(String s) {
		erro(s,new Exception(s));
	}
	/***************************************************************/
	public void erro(String s,Exception e) {
		logs.grava(e,arq+": "+s);
		sErro = s+" "+e;
	}
}
