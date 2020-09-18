package br.org.guarani.servidor;

import java.util.*;
import java.io.*;

import br.org.guarani.servidor.*;
import br.org.guarani.util.*;



/*
le post MultiPart tnto http qto ajp13
signey jan/2004

2018 - problema com utf 8 ... não resolvido. 


/mozzilla
-----------------------------10746687392026394916316317293
-----------------------------10746687392026394916316317293--
/ie
-----------------------------7d328a1f1ec
-----------------------------7d328a1f1ec--
*/

//***************************************
//***************************************
public class LeitorMP {
	Pedido ped;
	byte fim[],bf[],b1[]=new byte[1];
	int fimt=0,pos=0,rfim=1;
	boolean erro=false,fimB=false;
	String sErro;
	//***************************************
	public boolean setPedido() {
		while (!fim()) {
			Hashtable h = item();
			//logs.grava("mp="+h);
			if (h.get("filename")!=null) {
				//anexo...
				ped.h.put(h.get("name"),h);
			} else {
				//var normal
				//ogs.grava("LeitorMP.setPedido() name="+h.get("name")+" v="+h.get(""));
				//ped.h.put(str.UnEscape((String)h.get("name")),str.UnEscape((String)h.get("")));
				ped.h.put(h.get("name"),h.get(""));
			}
		}
		return true;
	}
	//***************************************
	public boolean fim() {
		return fimB;
	}
	//***************************************
	private File arq() {
		String ar=null;
		FileOutputStream out=null;
		try {
			ar = File.createTempFile("ped_",".tmp")
					.toString();
			out = new FileOutputStream(ar,false);
			byte bb[] = new byte[1];
			byte bb1[] = new byte[1024];
			int ps=0;
			while (le(bb)==1) {
				bb1[ps++] = bb[0];
				if (ps>=1024) {
					out.write(bb1,0,1024);
					ps=0;
				}
			}
			if (ps>0) {
				out.write(bb1,0,ps);
			}
			out.close();
		} catch (Exception e) {
			on("ERRO gravando arq TMP: "+ar+" = "+out+"="+str.erro(e));
			return null;
		}
		return new File(ar);
	}
	//***************************************
	public void teste() {
		int nl = 0;
		String t = "";
		try {
			OutputStream r = new FileOutputStream("/tmp/xx"+data.ms()+".txt");
			byte b[] = new byte[256];
			while ((nl = ped.io.readLine(b,0,256))>0) {
				r.write(b,0,nl);
				logs.grava("leu post:"+nl);
				t += new String(b,0,nl)+"\n";
			}
			r.close();
		} catch (Exception e) {
		}
	}
	//***************************************
	public Hashtable item() {
		//teste();if (true) return null;
		Hashtable h = new Hashtable();
  
		//le o cab...
		try {
			byte b[] = new byte[256];
			while (true) {
				int nl = ped.io.readLine(b,0,256);
				if (nl<1) {
					on("ERRO, fim não esperado..."+ped.io);
					return null;
				}
				//on("<tr><td>"+nl+"@"+(new String(b,0,nl)));
				if (nl==2 && b[0]==13 && b[1]==10) {
					//fim cab
					break;
				} else if (nl==4 && b[0]=='-' && b[1]=='-' 
					&& b[2]==13 && b[3]==10 ) {
					//fim esperado do multipart
					//on("OK: fim esperado do multipart...");
					return null;
				} else if (b[nl-2]!=13 || b[nl-1] != 10) {
					on("ERRO final da linha não CR LF");
					return null;
				}
    
				String l = new String(b,0,nl-2)+"; ";
				if (l.indexOf(": ")==-1) {
					on("<tr><td>Erro, não tem ': ' em "+l.length()+"="+l);
					return null;
				}
				String ch = str.leftAt(l,": ");
				String cn = str.substrAtAt(l,": ","; ");
				h.put(ch,cn);
				l = str.substrAt(l,"; ");
    
				String v[] = str.palavraA(l,"; ");
				//on("<tr><td>RESTA: "+l+" Vet="+v.length);
				for (short i=0;i<v.length;i++) {
					if (v[i].indexOf("=")!=-1) {
						ch = str.leftAt(v[i],"=");
						cn = str.trimm(str.substrAt(v[i],"="),"\"");
						//on("<tr><td>CHAVE: @"+ch+"@"+cn+"@");
						h.put(ch,cn);
					} else {
						//on("<tr><td>Ignorando expr "+i+": "+v[i].length()+"="+v[i]);
					}
				}
			}

			//enche o bf
			pos = 0;
			bf = new byte[fimt];
			if (true) { //ped.io.getClass().getName().indexOf("13")!=-1) {
				//on("Enchendo pelo AJP13!");
				while (pos<fimt) {
					if (ped.io.leB(b1)!=1) {
						break;
					}
					bf[pos++] = b1[0];
				}
			} else {
				pos = ped.io.read(bf,0,fimt);
			}
			//if (ped.io.read(bf,0,fimt)!=fimt) {
			if (pos!=fimt) {
				on("ERRO: enchendo o buff pos="+pos+" fimt="+fimt);
				return null;
			} else {
				//on("OK enchendo o buff pos="+pos+" fimt="+fimt
				// +" bf="+(new String(bf,0,pos))
				//);
			}
			pos=0;

		} catch (Exception e) {
			on("ERRO LENDO: "+h+"<hr>"+str.erro(e));
			return null;
		}
  
		//le o corpo
		boolean aq = h.get("filename")!=null;
		byte bb[] = new byte[1];
		if (aq) {
			h.put("",arq());
		} else {
			//le String;
			byte bb1[] = new byte[1024];
			int ps=0;
			String r = "";
			while (le(bb)==1) {
				bb1[ps++] = bb[0];
				if (ps>=1024) {
					r += new String(bb1,0,1024);
					ps = 0;
				}
			}
			if (ps>0) {
				r += new String(bb1,0,ps);
			}
			//dica em https://stackoverflow.com/questions/546365/utf-8-text-is-garbled-when-form-is-posted-as-multipart-form-data
			// dica não funcionou
			/*try {
				r = new String (r.getBytes ("iso-8859-1"), "UTF-8");
			} catch (Exception e) {
			}
			*/
			h.put("",r);
		}
  
		//é fim
		String f=null;
		try {
			byte b[] = new byte[256];
			int nl = ped.io.readLine(b,0,256);
			if (nl>0) {
				f = new String(b,0,nl);
			}
		} catch (Exception e) {
			on("ERRO LENDO fim: "+str.erro(e));
		}
  
		fimB = f!=null && f.equals("--\r\n");
  
		//on("ret="+h);
		return h;
	}
	//***************************************
	private short le(byte b[]) {
		if (fimI()) {
			return 0;
		}
		b[0] = bf[pos];
		try {
			rfim = ped.io.leB(b1);
			if (rfim==1) {
				bf[pos] = b1[0];
				pos = (pos+1)%fimt;
			} else {
				return (short)rfim;
			}
		} catch (Exception e) {
			on("<hr>LeitorMP.le(): Fim Stream..."+e);
			return -1;
		}
		return 1;
	}
	//***************************************
	public LeitorMP(Pedido pd) {
		ped = pd;
		//le o separador...
		try {
			byte b[] = new byte[256];
			int nl = ped.io.readLine(b,0,256);
			if (nl<1) {
				on("ERRO, fim não esperado...");
				return;
			}
			if (b[nl-2]!=13 || b[nl-1] != 10) {
				on("ERRO final da linha não CR LF");
				return;
			}
			if (b[0]=='-') {
				fimt = nl;
				fim = new byte[fimt];
				fim[0] = 13;
				fim[1] = 10;
				for (int i=0;i<nl-2;i++) {
					fim[2+i] = b[i];
				}
				//on("FIM="+nl+"="+(new String(fim,0,fimt)));
			} else {
				on("ERRO, fim começa com -"+nl+"="+(new String(b,0,nl)));
				return;
			}


		} catch (Exception e) {
			on("ERRO LENDO: "+str.erro(e));
			return;
		}

	}
	//***************************************
	private boolean fimI() {
		for (short i=0;i<fimt;i++) {
			if (fim[i]!=bf[(i+pos)%fimt]) {
				return false;
			}
		}
		return true;
	}
	//***************************************
	public void on(String s) {
		logs.grava(s);
		//ped.on(s);
	}
}
