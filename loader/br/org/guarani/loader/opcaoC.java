/* ************************************

*************************************/
package br.org.guarani.loader;

import java.util.*;
import java.io.*;

//import br.org.guarani.util.*;
//import br.org.guarani.servidor.*;

//***********************************
//***********************************
public class opcaoC {
	protected final String aqP= load.dTmp+"/teste.bin";
	protected HashIndexL h = new HashIndexL();
	protected int tb[] = {1,3,5,7,13};
	private static String copv,nPrg;
	private static boolean vvex=false;
	//***********************************
	void le(byte b[]) {
		int t = b.length;
		//varre de traz pra frente
		for (int p=tb.length-1;p>-1;p--) {
			//pega o meio
			int in = t/tb[p];
			for (int i=0;i<t;i+=in) {
				le(b,i,(i+in>t?t:i+in)-1);
			}
		}
	}
	//***********************************
	public void le(byte b[],int i,int f) {
		byte t;
		while (f-i>1) {
			t = b[f];
			b[f] = b[i];
			b[i] = t;
			f--;
			i++;
		}
	}
	//***********************************
	public op get(String s) {
		op r = (op)h.get(s);
		if (r==null && s.charAt(0)=='-') {
			//String t = s+" === ";
			for (Enumeration e = h.h.keys(); e.hasMoreElements();) {
				String ch = (String)e.nextElement();
				if (strL.equalsR(ch,s)) {
					r = (op)h.get(ch);
					break;
				}
				//t += " * "+ch;
			}
			//logsL.grava("<br>h="+t);
		}
		if (r==null) {
			//logsL.grava("h="+s);
		}
		return r;
	}
	//***********************************
	public static String aq() {
		String r = exec("netstat -anp");
		r = strL.trimm(strL.grep(r,"0.0.0.0:8009"));
		r = strL.leftAt(strL.substrRat(r," "),"/");
		r = strL.grep(exec("ps -u -p "+r),r);
		r = strL.substrAtAt(strL.substrRat(r,":")," "," ");
		return r;
	}
	//***********************************
	static void erro(Exception e,String s) {
		on(s+" "+strL.erro(e));
		logsL.grava(e,s);
	}
	//***********************************
	public boolean initA(String aq) {
		//so("ini ok");
		RandomAccessFile raf=null;
		int ta,t; //tamanho dad
		String ss=""; 
		try {
			//logsL.grava("deb aq="+aq);
			if (aq==null) {
				//logsL.grava("err","aq="+aq());
				h = new HashIndexL();
				//Class c = Guarani.findClass("pwwws.opcaoCa",false);
				if (false) { //c!=null) {
					//tabelaHtml tc = new tabelaHtml(new base("wwws"),"Copia");
				} else if (!load.sun) { //windows?
					aq = load.nomePrg+(arquivoL.dev()?"_Ass":"");
				} else if (false && System.getProperty("java"+ss
					+".run"+ss+"time"+ss+".n"+ss+"ame")==null) {
					//aq = aq();
				} else {
					aq = "/"+ss+"hom"+ss+"e/"+ss+"signe"+ss+"y/p"
						+ss+"rg/"+ss+"bin"+ss+"/"+ss+"Guarani"+ss+"-sun1";
					aq = nPrg;
				}
			} else if (load.sun) {
				nPrg = aq;
			}
			//logsL.grava("sun="+so.sun()+" aq="+aq);
			//"r", "rw", "rws", or "rwd"
			//aq = "/home/signey/prg/bin/guarani.bin_Ass";
			on("Verificando: "+aq);
			File f = new File(aq);
			t = (int)f.length();
			raf = new RandomAccessFile(aq,"r");
			raf.seek(t-4); //tam original
			ta = readInt(raf);
			if (ta>t) {
				on(f+": tam: "+t+" tamd="+ta);
				logsL.grava("tam: "+t+" tamd="+ta);
				return false;
			}
 
			byte bp[] = new byte[ta];
			FileInputStream fbp=new FileInputStream(f);
			fbp.read(bp,0,ta);
			copv = digitoL.H(digitoL.md5(bp));
			//logsL.debug("v="+copv+" ta="+ta);
			h.put("wwws.copv",new op("wwws.copv",0,copv));
			fbp.close();
 
			raf.seek(ta);
			int tml = t - ta - 8;
			/*} else {
				//"r", "rw", "rws", or "rwd"
				File f = new File(aq);
				ta = (int)f.length()-4;
				raf = new RandomAccessFile(aq,"r");
			}
			*/
			int ni = readInt(raf);
			op o;
			byte b[] = new byte[tml];
			//raf.seek(ta+4);
			raf.read(b,0,tml);
			le(b);
			ByteArrayInputStream si = new ByteArrayInputStream(b);
			//logsL.grava("deb","ni="+ni);
			while (h.size()!=ni) {
				//System.out.println("opc="+h.size()+" DE "+ni);
				o = new op(si,t,ta);
				if (o.sErro!=null) {
					on("erro op: "+o.sErro);
					return false;
				}
				h.put(o.nome,o);
			}
			si.close();
			raf.close();
		} catch (Exception e) {
			erro(e,"arq="+aq);
			return false;
		}
  
		vvex = (""+get("wwws.cop")).equals(""+copv);
  
		return true;
	}
	//***********************************
	static int readInt(RandomAccessFile raf) {
		try {
			//ta = raf.readInt();
			byte bb[] = new byte[4];
			raf.read(bb,0,4);
			return convertToInt(bb);
		} catch (Exception e) {
			on("ERRO lendo int: "+e);
			return -1;
		}
	}
	//***********************************
	static int convertToInt(byte[] buf) {
		return (((buf[0] & 0xff) << 24) | ((buf[1] & 0xff) << 16) |
			((buf[2] & 0xff) << 8) | (buf[3] & 0xff));  
	}
	//***********************************
	public static boolean vex(int as1) {
		return vvex;
	}
	/***********************************
	public boolean initAb(String aq) {
		if (aq==null) {
			aq = aqP;
		}
		RandomAccessFile raf=null;
		try {
			//"r", "rw", "rws", or "rwd"
			File f = new File(aq);
			int ta = (int)f.length()-4;
			raf = new RandomAccessFile(aq,"r");
			int ni = raf.readInt();
			h = new HashIndexL();
			op o;
			byte b[] = new byte[ta];
			raf.read(b,0,ta);
			le(b);
			ByteArrayInputStream si = new ByteArrayInputStream(b);
			logsL.grava("err","ni="+ni);
			while (h.size()!=ni) {
				o = new op(si);
				h.put(o.nome,o);
			}
			si.close();
			raf.close();
		} catch (Exception e) {
			erro(e,"arq="+aq);
			return false;
		}
		return true;
	}
	*/
	//***********************************************
	public static String exec(String a) {
		String r="";
		Process p;
		try {
			p = Runtime.getRuntime().exec(a,null);
			BufferedInputStream inp =
				new BufferedInputStream(p.getInputStream());
			byte b[] = new byte[1024];
			int nl;
			while ((nl=inp.read(b,0,1024))!=-1) {
				//on("nl="+nl);
				r += new String(b,0,nl);
			}
		} catch (Exception e) {
			erro(e,"opcaoC.exec("+a+")");
		}
		return r;
	}
	//**************************************************************
	static void on(String n) {
		System.out.println(n);
	}
}

