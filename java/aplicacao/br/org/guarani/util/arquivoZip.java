/*
	* Signey John mai/2004 
*/

package br.org.guarani.util;

import br.org.guarani.util.*;
import java.util.*;
import java.util.zip.*;
import java.io.*;

//*************************************
//*************************************
public class arquivoZip {
	public String name,sErro;
	//*************************************
	//dir
	public String dir() {
		String r = "";
		try {
			ZipFile zf = new ZipFile(name);
			for (Enumeration e = zf.entries() ; e.hasMoreElements() ;) {
				ZipEntry z = (ZipEntry)e.nextElement();
				r = r +"\n"+z;
			}
		} catch (Exception e) {
			r += "\nERRO "+e;
		}
		return r;
	}
	//*************************************
	//grava o conteudo de um dir como zip..
	static public boolean grava(String arq,String dir) {
		//dir = str.trimm(dir,"/");
		arquivo az = new arquivo(arq);
		az.abreOut(false);
		ZipOutputStream zo = new ZipOutputStream(az.out);
		
		byte b[] = new byte[2048];
		
		Hashtable h = arquivo1.dirRecursivo(dir);
		for (Enumeration e = h.elements(); e.hasMoreElements(); ) {
			File f = (File)e.nextElement();
			String s = ""+f;
			//logs.grava(s);
			ZipEntry ze = new ZipEntry(s.substring(dir.length()+1));
			if (!f.isDirectory()) {
				try {
					zo.putNextEntry(ze);
					arquivo1 a = new arquivo1(s);
					int l;
					while ((l=a.read(b))!=-1) {
						zo.write(b,0,l);
					}
				} catch (Exception e1) {
					logs.grava(e1);
					return false;
				}
			}
		}
		try {
			zo.close();
		} catch (Exception e) {
			logs.grava(e);
			return false;
		}
		
		return true;
	}
	//*************************************
	public InputStream inputStream(String arq) {
		InputStream r = null;
		ZipFile zf = null;
		try {
			zf = new ZipFile(name);
			int tBf = 4096;
			byte bf[] = new byte[tBf];
   
			for (Enumeration e = zf.entries() ; e.hasMoreElements() ;) {
				ZipEntry z = (ZipEntry)e.nextElement();
				String n = ""+z;
				//logs.grava("n="+n+" a="+arq);
				if (z.isDirectory()) {
				} else if (n.equals(arq)) {
					(new File(str.leftRat(n,"/"))).mkdirs();
					InputStream is = zf.getInputStream(z);
					ByteArrayOutputStream f = new ByteArrayOutputStream(4096);
					int nl,tl=0;
					while ((nl=is.read(bf,0,tBf))>0) {
						f.write(bf,0,nl);
						tl+=nl;
					}
					is.close();
					f.close();
					r = new ByteArrayInputStream(f.toByteArray());
					break;
				}
			}
		} catch (Exception e) {
			sErro = "arquivoZip.inputStream: "+name+" "+str.erro(e);
			logs.grava("erro",sErro);
			return null;
		}
		if (zf!=null) {
			try {
				zf.close();
			} catch (Exception e){};
		}
		return r;
	}
	//*************************************
	public boolean extrai(String dirDest) {
		File dd = new File(dirDest);
		if (!dd.exists()) {
			dd.mkdirs();
		}
		try {
			ZipFile zf = new ZipFile(name);
			int tBf = 4096;
			byte bf[] = new byte[tBf];
   
			for (Enumeration e = zf.entries() ; e.hasMoreElements() ;) {
				ZipEntry z = (ZipEntry)e.nextElement();
				String n = "/"+str.trimm(dirDest,"/")+"/"+z;
				if (z.isDirectory()) {
					(new File(n)).mkdirs();
				} else {
					(new File(str.leftRat(n,"/"))).mkdirs();
					InputStream is = zf.getInputStream(z);
					FileOutputStream f = new FileOutputStream(n);
					int nl,tl=0;
					while ((nl=is.read(bf,0,tBf))>0) {
						f.write(bf,0,nl);
						tl+=nl;
					}
				}
			}
		} catch (Exception e) {
			sErro = "arquivoZip.extrai: "+name+" "+str.erro(e);
			logs.grava("erro",sErro);
			return false;
		}
		return true;
	}
	//*************************************
	public arquivoZip(String nome) {
		name = nome;
	}
	//*************************************
	public static boolean extrai(String zArq,String dirDest) {
		arquivoZip zip = new arquivoZip(zArq);
		return zip.extrai(dirDest);
	}
}

