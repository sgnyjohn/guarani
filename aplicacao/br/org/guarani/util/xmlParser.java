package br.org.guarani.util;

import java.io.*;
import java.util.*;

import br.org.guarani.servidor.*;
/*

	2009 - problema \o/? 
		  <text>oi, lideran&#231;a. bom te ver \o/ #saopaulo</text> 



	problema 1: <!DOCUMENT sdfsdf sdf sdiojsdfiojsdf sdf>
		tag sem fim
		ok, aceita
	problema 2: teste<break-line/>teste
		tag de FIM com texto
		EXEMPLO:
			<table:table-cell table:style-name="Tabela1.A2" table:number-columns-spanned="2" table:value-type="string">
			<text:p text:style-name="Table Contents">
				teste m
				<text:span text:style-name="T1">
					ud
				</text:span>
				a linha
				<text:line-break/>
				con
				<text:span text:style-name="T2">
					tin
				</text:span>
				ua
			</text:p>
		</table:table-cell>
	uma tag de fim tb pode ter texto...


	2016/09 - problema coment <--!  ..<". <p> -->

*/



//***************************************
//***************************************
public class xmlParser {
	public static final String clPdr = "br.org.guarani.util.xmlTag";
	public String url;
	String fila[] = new String[30];
	InputStream hi;
	//FileInputStream hi;
	File f;
	//char c,ca,bf[] = new char[1];
	byte c,ca,c1,bf[] = new byte[1],tpA;
	//tamanho buff
	public int atrBufT = 16000;
	public int texBufT = 16000;
	buf atrBuf,texBuf;
	boolean sob = false;
	xmlTag tgU;
	boolean sobT = false;
	public Hashtable hErr;
	public boolean erro = false;
	boolean fechar = true;
	boolean fim = false;
	public Hashtable tagsSemFim = new Hashtable();
	public int lin=1,col=1,pos=0;
	int tp=0;
		
	Class cl;
	String clName;
	
	//***************************************
	public xmlParser() {
	}
	//***************************************
	public xmlParser(InputStream i) {
		hi = i;
		fechar = false;
	}
	//***************************************
	public xmlParser(String Url) {
		//logs.grava("aq="+aq);
		url = Url;
		if (str.equals(url,"http://")) {
			tp = 1;
		} else if (str.equals(url,"dir://")) {
			tp = 2;
		} else if (str.equals(url,"dirr://")) {
			tp = 3;
		} else if (str.equals(url,"string://")) {
			hi = new ByteArrayInputStream(url.substring(9).getBytes());
		} else if (str.equals(url,"file://")) {
			this.f = new File(url.substring(7));
		} else {
			this.f = new File(url);
		}
	}
	//***************************************
	public xmlParser(File f) {
		url = ""+f;
		this.f = f;
	}
	//***************************************
	public xmlTag newTag() {
		//xmlTag x = new xmlTag();
		try {
			xmlTag x = (xmlTag)cl.newInstance();
			return x;
		} catch (java.lang.InstantiationException e) {
			logs.grava(e);
		} catch (java.lang.IllegalAccessException e) {
			logs.grava(e);
		}
		return null;
	}
	//***************************************
	public xmlTag parse(Object o) {
		return parse(o.getClass().getName());
	}
	//***************************************
	public xmlTag parse() {
		return parse((String)null);
	}
	//***************************************
	public xmlTag parse(String s) {
		if (s==null) {
			s = clPdr;
		}
		try {
			Class c = Class.forName(s);
			if (tp==0) {
				return parse(c);
			} else if (tp==1) {
				xmlParserHtml d = new xmlParserHtml(url);
				return d.parse(c);
			} else if (tp==2) {
				xmlParserDir d = new xmlParserDir();
				d.url = str.substrAt(url,"://");
				return d.parse(c);
			}
		} catch (java.lang.ClassNotFoundException e) {
			//sErro = ""+e;
			erro(str.erro(e));
		} catch (Exception e) {
			logs.grava(e);
			//sErro = ""+e;
			erro(str.erro(e));
		}
		return null;
	}
	//***************************************
	public xmlTag parse(Class cl) {
		this.cl = cl;
		if (fechar) {
			try {
				//hi = new BufferedReader(new FileReader(aq),4096);
				if (hi == null) {
					hi = (InputStream)(new FileInputStream(f));
				}
				//hi = new FileInputStream(f);
			} catch (Exception e) {
				erro("Erro lendo XML: "+f.getAbsoluteFile()+" = "+e);
				return null;
			}
		}
		xmlTag x = newTag();
		atrBuf = new buf(atrBufT,"atributo",this);
		texBuf = new buf(texBufT,"texto",this);
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
	public String erro() {
		return erroHtml();
	}
	//***************************************
	public String erroHtml() {
		if (hErr==null) return null;
		String r = "";
		for (int i=0;i<hErr.size();i++) {
			r += "<p class=xmlParserErro>"+hErr.get(""+i)+"</p>";
		}
		return r;
	}
	//***************************************
	void erro(String er) {
		erro = true;
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
	String parse(xmlTag tgNA,int nvg) {
		xmlTag tg,tgAn=null;
		int nv=0;
		while ((tg=tagI())!=null) {
			//on("<hr>"+nv+" "+nvg+" "+tg.nome+" "+tg.tex+" "+tg.atr);
			//logs.grava("tag="+tg.nome);
		
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
				tg.tex = str.trimm(tex());
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
	//***************************************
	public void debug(Pedido ped) {
		xmlTag tg;
		atrBuf = new buf(atrBufT,"atributo",this);
		texBuf = new buf(texBufT,"tex",this);
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
	//***************************************
	String tex() {
		texBuf.pos = 0;
		//while(leC()==1 && (c!='<' && ca!='\\')) { //não entendi o ca... 2009
		while( leC()==1 && c!='<' ) {
			texBuf.add(c);
		}
		if (c=='<') {
			sob = true;
		}
		return texBuf.toString();
	}
	//***************************************
	xmlTag tagI() {
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
		xmlTag tg = newTag();
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
					if (tg.nome.charAt(0)=='!' && str.equals(tg.nome,"!--")) {
						//comentario, aceita qualquer coisa dentro termina "-->"
						//tipo de tag s/attrib e sim texto.?
						atrBuf.add((byte)' ');
						ca='-';c1=ca;
						while (leC()==1) {							
							if (c=='>' && ca=='-' && c1=='-') {
								tg.nome = atrBuf.toString();
								tg.tex = "";
								//logs.grava(tg.nome);
								//sob = true;
								break;
							}
							c1 = ca;
							ca = c;
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
					//if para aceitar \" dentro do valor atr... (2011 jan-signey)
					if ( c == '\\' ) {
						leC();
						if ( c != '"' ) {
							atrBuf.add((byte)'\\');
							atrBuf.add(c);
						} else {
							atrBuf.add(c);
						}
					} else {
						atrBuf.add(c);
					}
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
		String nome;
		xmlParser x;
		boolean er = false;
		//***************************************
		public String toString() {
			return new String(b,0,pos);
		}
		//***************************************
		public void add(byte by) {
			if (pos>=t) {
				if (!er) {
					x.erro("estouro buffer ignorando dados="+nome+" max="+t);
				}
				er = true;
			} else {
				b[pos++] = by;
			}
		}
		//***************************************
		public buf(int tam,String nome,xmlParser x) {
			this.x = x;
			this.nome = nome;
			t = tam;
			b = new byte[tam];
		}
	}
	/***************************************
	private xmlTag tagIA() {
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
		xmlTag tg = new xmlTag();
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
