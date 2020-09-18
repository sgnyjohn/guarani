package br.org.guarani.loader;

import java.io.*;
import java.util.*;

//***************************************
//***************************************
public class xmlParserL {
	public static final String clPdr = "br.org.guarani.loader.xmlTagL";
	public String url;
	String fila[] = new String[30];
	InputStream hi;
	//FileInputStream hi;
	File f;
	//char c,ca,bf[] = new char[1];
	byte c,ca,bf[] = new byte[1],tpA;
	//tamanho buff
	public int atrBufT = 200;
	public int texBufT = 16000;
	buf atrBuf,texBuf;
	boolean sob = false;
	xmlTagL tgU;
	boolean sobT = false;
	public Hashtable hErr;
	boolean fechar = true;
	boolean fim = false;
	public Hashtable tagsSemFim = new Hashtable();
	public int lin=1,col=1,pos=0;
	public String sErro;
	int tp=0;
		
	Class cl;
	String clName;
	
	//***************************************
	public xmlParserL() {
	}
	
	
	//***************************************
	public xmlParserL(InputStream i) {
		hi = i;
		fechar = false;
	}
	//***************************************
	public xmlParserL(String aq) {
		//logsL.grava("aq="+aq);
		url = aq;
		if (strL.equals(aq,"http://")) {
			tp = 1;
		} else if (strL.equals(aq,"dir://")) {
			tp = 2;
		} else if (strL.equals(aq,"dirr://")) {
			tp = 3;
		} else {
			this.f = new File(aq);
		}
	}
	//***************************************
	public xmlParserL(File f) {
		url = ""+f;
		this.f = f;
	}
	//***************************************
	public xmlTagL newTag() {
		//xmlTagL x = new xmlTagL();
		try {
			xmlTagL x = (xmlTagL)cl.newInstance();
			return x;
		} catch (java.lang.InstantiationException e) {
			logsL.grava(e);
		} catch (java.lang.IllegalAccessException e) {
			logsL.grava(e);
		}
		return null;
	}
	//***************************************
	public xmlTagL parse(Object o) {
		return parse(o.getClass().getName());
	}
	//***************************************
	public xmlTagL parse() {
		return parse((String)null);
	}
	//***************************************
	public xmlTagL parse(String s) {
		if (s==null) {
			s = clPdr;
		}
		try {
			Class c = Class.forName(s);
			return parse(c);
		} catch (java.lang.ClassNotFoundException e) {
			sErro = ""+e;
		} catch (Exception e) {
			logsL.grava(e);
			sErro = ""+e;
		}
		return null;
	}
	//***************************************
	public xmlTagL parse(Class cl) {
		this.cl = cl;
		if (fechar) {
			try {
				//hi = new BufferedReader(new FileReader(aq),4096);
				hi = (InputStream)(new FileInputStream(f));
				//hi = new FileInputStream(f);
			} catch (Exception e) {
				erro("Erro lendo XML: "+f.getAbsoluteFile()+" = "+e);
				return null;
			}
		}
		xmlTagL x = newTag();
		atrBuf = new buf(atrBufT);
		texBuf = new buf(texBufT);
		parse(x,0);
		if (fechar) {
			try {
				hi.close();
			} catch (Exception e) {
			}
		}
		x.init();
		return x;
	}
	//***************************************
	public String erroHtml() {
		if (hErr==null) return null;
		String r = "";
		for (int i=0;i<hErr.size();i++) {
			r += "<p class=xmlParserLErro>"+hErr.get(""+i)+"</p>";
		}
		return r;
	}
	//***************************************
	void erro(String er) {
		if (hErr==null) {
			hErr = new Hashtable();
		}
		er += "( <b>LIN="+lin
			+" COL="+col
			+" POS="+pos+"</b> )"
		;
		hErr.put(""+hErr.size(),er);
	}
	//***************************************
	String parse(xmlTagL tgNA,int nvg) {
		xmlTagL tg,tgAn=null;
		int nv=0;
		while ((tg=tagI())!=null) {
			//on("<hr>"+nv+" "+nvg+" "+tg.nome+" "+tg.tex+" "+tg.atr);
			//logsL.grava("tag="+tg.nome);
		
			//tag de fim?
			if (tg.nome.charAt(0)=='/') {
				//tag de fim pode ter texto
				tgNA.texF = tex();
				nv--;
				if (nv<0) {
					//devolve tag
					sobT = true;
					break;
				}
				if (tgAn==null) {
					erro("esperada tag de inicio e não '"+tg.nome+"'");
				} else if (!tg.nome.equals("/"+tgAn.nome)) {
					erro("esperada tag '/"+tgAn.nome+"' <> de '"+tg.nome+"'");
				} else {
					tgAn.nv = nvg;
				}
			} else if (tg.tex==null && tg.nome.charAt(0)!='!') {
				//tag de inicio
				nv++;
				tg.tex = strL.trimm(tex());
				tgNA.put(tg);
				parse(tg,nvg+1);
			} else {
				//tag auto term e pode ter texto
				tg.texF = tex();
				tg.nv = nvg;
				tgNA.put(tg);
			}
			tgAn = tg;
		}
		return null;
	}
	/***************************************
	public void debug(Pedido ped) {
		xmlTagL tg;
		atrBuf = new buf(atrBufT);
		texBuf = new buf(texBufT);
		int nv=0;
		while ((tg=tagI())!=null) {
			ped.on("<hr>"+nv+" "+tg.nome+" "+tg.tex+" "+tg.atr);
			if (tg.nome.charAt(0)=='/') {
				nv--;
			} else if (tg.tex==null && tg.nome.charAt(0)!='!') {
				tg.tex = tex();
				nv++;
			} else { //? auto terminada pode ter texto....?
				tg.tex = tex();
			}
		}
		ped.on("<hr>FIM Nível: "+nv);
	}
	*/
	//***************************************
	String tex() {
		texBuf.pos = 0;
		while(leC()==1 && (c!='<' && ca!='\\')) {
			texBuf.add(c);
		}
		if (c=='<') {
			sob = true;
		}
		return texBuf.toString();
	}
	//***************************************
	xmlTagL tagI() {
		if (sobT) {
			sobT = false;
			return tgU;
		}
		while(leC()==1 && c!='<') {
			if (!br()) {
				erro("esperando < e veio "+((char)c));
				//return null;
			}
		}
		if (c!='<') {
			return null;
		}
		int s = 0; //nome tag
		//1=esp atr
		//2=nome atr
		//3=vlr atr
		//4="
		//5= fim "
		//String nome="",nomea=null,va=null;
		String nomeAtr=null; //,nomeTag=null;
		atrBuf.pos = 0;
		xmlTagL tg = newTag();
		while (leC()==1) {
			if (s==0) { //nome tag
				if (c=='>') {
					//fim ta tab
					tg.nome = atrBuf.toString();
					break;
				} else if (c=='/' 
					|| (atrBuf.pos>0 && atrBuf.b[0]=='?' && c=='?') ) {
					leC();
					if (c=='>') {
						//fim tag autoTerm
						tg.nome = atrBuf.toString();
						tg.tex = "";
						break;
					} else {
						atrBuf.add(ca);
						atrBuf.add(c);
					}
				} else if (!br()) {
					atrBuf.add(c);
				} else {
					//veio branco, fim do nome da tag
					tg.nome = atrBuf.toString();
					if (tg.nome.charAt(0)=='!') {
						//tipo de tag s/attrib e sim texto.?
						atrBuf.add((byte)' ');
						while (leC()==1) {
							if (c=='>') {
								tg.nome = atrBuf.toString();
								tg.tex = "";
								//logsL.grava(tg.nome);
								//sob = true;
								break;
							}
							atrBuf.add(c);
						}
						break;
					}
					s=1;
				}
			} else if (s==1) { //ini nome atr ou fim tag
				if (br()) {
				} else if (c=='>') {
					break;
				} else if (c=='/' || (tg.nome.charAt(0)=='?' && c=='?') ) {
					leC();
					if (c=='>') {
						//fim tag auto term
						tg.tex = "";
						break;
					} else {
						//inicio nome
						atrBuf.pos = 0;
						atrBuf.add(ca);
						sob = true;
						s = 2;
					}
				} else {
					//inicio nome
					atrBuf.pos = 0;
					atrBuf.add(c);
					s = 2;
				}
			} else if (s==2) { //nome atr
				if (br() || c=='=') {
					//va = "";
					nomeAtr = atrBuf.toString();
					atrBuf.pos = 0;
					s = 3;
				} else if (c=='>') {
					//nome atr sem vlr e fim de tag
					tg.putAtr(atrBuf.toString(),"");
					break;
				} else {
					//nomea += ((char)c);
					atrBuf.add(c);
				}
			} else if (s==3) { //" ini vlr atr
				if (br()) {
				} else if (c=='"' || c=='\'') {
					tpA = c;
					s = 4;
				} else {
					sob = true;
					tpA = ' ';
					s = 4;
				}
			} else if (s==4) { //" fim
				if (c==tpA) {
					tg.putAtr(nomeAtr,atrBuf.toString());
					atrBuf.pos = 0;
					s = 1;
				} else if (tpA==' ' && c=='>') {
					tg.putAtr(nomeAtr,atrBuf.toString());
					atrBuf.pos = 0;
					break;
				} else {
					//va += c;
					//va += (char)c;
					atrBuf.add(c);
				}
			}
		}
		tgU = tg;
		return tg;
	}
	//***************************************
	private boolean br() {
		return c==' ' || c=='\t' || c=='\n' || c=='\r';
	}
	//***************************************
	int leC() {
		if (sob) {
			sob = false;
			return 1;
		}
		int r = 0;
		ca = c;
		try {
			r = hi.read(bf,0,1);
			if (r!=1) {
				fim = true;
			}
			c = bf[0];
			if (c=='\n') {
				lin++;
				col=1;
			} else if (c!='\r') {
				col++;
			}
			pos++;
		} catch (Exception e) {
			fim = true;
		}
		return r;
	}
	//***************************************
	public class buf {
		byte b[];
		int t,pos=0;
		//***************************************
		public String toString() {
			return new String(b,0,pos);
		}
		//***************************************
		public void add(byte by) {
			if (pos>=t) {
			} else {
				b[pos++] = by;
			}
		}
		//***************************************
		public buf(int tam) {
			t = tam;
			b = new byte[tam];
		}
	}
	/***************************************
	private xmlTagL tagIA() {
		if (sobT) {
			sobT = false;
			return tgU;
		}
		while(leC()==1 && c!='<') {
			if (!br()) {
				erro("esperando < e veio "+((char)c));
				return null;
			}
		}
		if (c!='<') {
			return null;
		}
		int s = 0; //nome tag
		//1=esp atr
		//2=nome atr
		//3=vlr atr
		//4="
		//5= fim "
		String nome="",nomea=null,va=null;
		xmlTagL tg = new xmlTagL();
		while (leC()==1) {
			if (s==0) { //nome tag
				if (c=='>') {
					tg.nome = nome;
					break;
				} else if (c=='/' || (nome.length()>0 && nome.charAt(0)=='?' && c=='?') ) {
					leC();
					if (c=='>') {
						tg.nome = nome;
						tg.tex = "";
						break;
					} else {
						//nome=""+ca;
						nome = ""+((char)ca); //new String(ca);
						sob = true;
					}
				} else if (!br()) {
					//nome += c;
					nome += ((char)c);
				} else {
					tg.nome = nome;
					s=1;
				}
			} else if (s==1) { //ini nome atr ou fim tag
				if (br()) {
				} else if (c=='>') {
					break;
				} else if (c=='/' || (nome.charAt(0)=='?' && c=='?') ) {
					leC();
					if (c=='>') {
						tg.tex = "";
						break;
					} else {
						//nomea=""+ca;
						nomea=""+((char)ca);
						sob = true;
						s = 2;
					}
				} else {
					nomea=""+((char)c);
					s = 2;
				}
			} else if (s==2) { //nome atr
				if (br() || c=='=') {
					va = "";
					s = 3;
				} else {
					nomea += ((char)c);
				}
			} else if (s==3) { //" ini
				if (br()) {
				} else if (c=='"' || c=='\'') {
					tpA = c;
					s = 4;
				} else {
				}
			} else if (s==4) { //" fim
				if (c==tpA) {
					s = 1;
					tg.putAtr(nomea,va);
				} else {
					//va += c;
					va += (char)c;
				}
			}
			
		}
		tgU = tg;
		return tg;
	}
	*/
}
