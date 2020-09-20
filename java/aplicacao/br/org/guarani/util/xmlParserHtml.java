package br.org.guarani.util;

import java.io.*;
import java.util.*;

import br.org.guarani.servidor.*;

//***************************************
//***************************************
public class xmlParserHtml extends xmlParser {
	public int atrBufT = 16000;
	public int texBufT = 64000;
	public Hashtable tagsSemFim = xmlParserHtml.tabSemTag();
		//new Hashtable();
	Hashtable tagPilha = new Hashtable();
	Hashtable hTT,hT = new Hashtable();
	public Pag pg;
	String tbH = "-table-/table-tbody-/tbody-tr-/tr-td-/td-";
	//tabela se abre novamente assume fecha (falta implementar)...
	String tbAN = "-p-";
	//***************************************
	void onD(String s) {
		if (pg!=null) {
			pg.on(s);
		}
	}
	//***************************************
	static Hashtable tabSemTag() {
		String tSF[]  = str.palavraA("link>img>br>hr>meta>base>input>param>!DOCTYPE",">");
		Hashtable x = new Hashtable();
		for (int i=0;i<tSF.length;i++) {
			x.put(tSF[i],tSF[i]);
		}
		return x;
	}
	//***************************************
	void empil(xmlTag t,boolean existe) {
		//if (t.name.charAt(0)=='/') {
		int s = tagPilha.size();
		if (!existe) {
			//proibe procurar tex...
			t.tex = "";
			t.texF = "";
		}
		tagPilha.put(""+s,t);
		onD("<br>emp"+s+"="+t.nome);
	}
	//***************************************
	xmlTag dEmpil() {
		//if (t.name.charAt(0)=='/') {
		int s = tagPilha.size()-1;
		if (s>-1) {
			xmlTag t = (xmlTag)tagPilha.get(""+s);
			onD("<br>dEmp"+s+"="+t.nome);
			tagPilha.remove(""+s);
			return t;
		}
		return null;
	}
	//***************************************
	xmlTag tagI() {
		//devolvida
		if (sobT) {
			sobT = false;
			return tgU;
		}
		//empilhada
		int t = tagPilha.size();
		if (t>0) {
			tgU = dEmpil();
			return tgU;
		}
		
		//le outra
		tgU = super.tagI();
		if (tgU!=null && tgU.nome!=null) {
			tgU.nome = tgU.nome.toLowerCase();
			String n = tgU.nome;
			if (tagsSemFim.get(n)!=null) {
				tgU.tex = "";
			} else if (tbH.indexOf("-"+n+"-")!=-1) {
				onD("<br>tabI:"+n+" h="+hT);
				if (n.charAt(0)=='/') {
					//se o anterior não é equival cria os necessários
					String tn = n.substring(1);
					int th = hT.size();
					for (int i=th-1;i>-1;i--) {
						String x = (String)hT.get(""+i);
						if (x.equals(tn)) {
							//empilha invertido e remove da pilha...
							for (int y=i;y<th;y++) {
								empil(new xmlTag("/"+hT.get(""+y)),y==i);
								hT.remove(""+y);
							}
							break;
						}
					}
					tgU = dEmpil();
				} else {
					if (hTT == null) {
						hTT = new Hashtable();
						hTT.put("td","-tr-");
						hTT.put("tr","-tbody-table-");
						hTT.put("tbody","-table-");
					}
					//String n = tgU.nome;
					String v = (String)hTT.get(n);
					if (v==null) {
						hT.put(""+hT.size(),n);
						return tgU;
					}
					int th = hT.size();
					for (int i=th-1;i>-1;i--) {
						String x = (String)hT.get(""+i);
						if (v.indexOf("-"+x+"-")>-1) {
							//empilha invertido e remove da pilha...
							empil(tgU,true);
							//empilha fins necessários
							for (int y=i+1;y<th;y++) {
								empil(new xmlTag("/"+hT.get(""+y)),false);
								hT.remove(""+y);
							}
							hT.put(""+hT.size(),tgU.nome);
							tgU = dEmpil();
							return tgU;
						}
					}
					hT.put(""+hT.size(),tgU.nome);
				}
			}
		}
		return tgU;
	}
	//***************************************
	String parse(xmlTag tgNA,int nvg) {
		xmlTag tg,tgAn=null;
		int nv=0;
		while ((tg=tagI())!=null) {
			//on("<hr>"+nv+" "+nvg+" "+tg.nome+" "+tg.tex+" "+tg.atr);
			//logs.grava("tag="+tg.nome);
			
			//auto terminado?
			//if (tagsSemFim.get(tg.nome)!=null) 
			//	tg.tex = "";
			//
			onD("<br>tg:"+nv+" "+str1.html(tg.nome));

		
			//tag de fim?
			if (tg.nome.charAt(0)=='/') {
				tgNA.texF = tex();
				//tag de fim pode ter texto
				nv--;
				if (nv<0) {
					//devolve tag
					//nv=0;
					sobT = true;
					onD("<br>Sobrou...nv="+nv+" tg="+tg.nome+" tgA="+tgNA.nome);
					break;
				}
				/*if (tg.nome.equals("/strong")) {
					logs.grava("f0="+tg.texF);
				}
				if (tg.texF==null) {
					tg.texF = tex();
				}
				if (tg.nome.equals("/strong")) {
					logs.grava("f="+tg.texF);
				}
				*/
				if (tgAn==null) {
					erro("esperada tag de inicio e não '"+tg.nome+"'");
				} else if (!tg.nome.equals("/"+tgAn.nome)) {
					erro(str1.html(
						"esperada tag '/"+tgAn.nome+"' <> de '"+tg.nome+"'"
						+" tgNA="+tgNA.nome
					));
					//sobT = true;
					//return null;
					//19/07/2007 - comentei este return e foi...
					
					
					//poderia testar se é estperado este fim e em caso
					//de positivo fechar todas até ele...?
					//nv++;
				} else {
					tgAn.nv = nvg;
				}
			} else if (tg.tex==null && tg.nome.charAt(0)!='!') {
				//tag de inicio
				nv++;
				if (tg.nome.equals("script") || tg.nome.equals("style")) {
					tg.tex = texScr(tg.nome);
					//onD("<hr>"+str1.html("fim SCRIPT atr="+tg.atr+" tex="+tg.tex.length()));
					tgNA.put(tg);
					//parse(tg,nvg+1);
				} else {
					tg.tex = str.trimm(tex());
					tgNA.put(tg);
					parse(tg,nvg+1);
				}
			} else {
				//tag auto term e pode ter texto
				if (tg.texF==null) {
					tg.texF = tex();
				}
				tg.nv = nvg;
				tgNA.put(tg);
				//mantem anterior =
				tg = tgAn;
			}
			tgAn = tg;
		}
		return null;
	}
	//***************************************
	void erro(String s) {
		super.erro(s);
		onD("<hr>aaa="+hErr.get(""+(hErr.size()-1)));
	}
	//***************************************
	public xmlParserHtml(InputStream i) {
		hi = i;
		fechar = false;
	}
	//***************************************
	public xmlParserHtml(String aq) {
		this(new File(aq));
	}
	//***************************************
	public xmlParserHtml() {
	}
	//***************************************
	public xmlParserHtml(File f) {
		this.f = f;
	}
	//***************************************
	String texScr(String tg) {
		texBuf.pos = 0;
		String r;
		int tt = tg.length();
		while (true) {
			while(leC()==1 && (c!='<' && ca!='\\')) {
				texBuf.add(c);
			}
			texBuf.add(c);
			while(leC()==1 && (c!='>' &&c!='<' && ca!='\\')) {
				texBuf.add(c);
			}
			r = texBuf.toString();
			onD("<hr>"+r.length()+" "+str1.html(r)
				+(r.length()>=tt?" r="+str1.html(str.right(r,tt)):"")
			);
			if (r.length()>=tt+2
				&& str.right(r,tt+2).toLowerCase().equals("</"+tg)) {
				r = str.trimm(r.substring(0,r.length()-tt-2));
				break;
			}
			texBuf.add(c);
		}
		empil(new xmlTag("/"+tg),true);
		if (c!='>') {
			sob = true;
		}
		return r;
	}
}
