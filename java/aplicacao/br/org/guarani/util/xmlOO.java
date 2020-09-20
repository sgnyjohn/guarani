package br.org.guarani.util;

/*
	signey nov/2006
	nome 1: //Easy Data Extractor = EDX
	nome 2: extractor of data = Xdt =  9
	nome 3: "data extractor" = dtX = 234.000 
*/


import java.util.*;
import java.text.*;
import java.io.*;


//***************************************
//***************************************
public class xmlOO extends xmlTag {
	xmlOO pai;
	int ps;
	//***************************************
	//prc OO
	public String tex(xmlOut xO,String s) {
		if (str.vazio(s)) {
			return s;
		}
		if (s.indexOf("@x:X@")!=-1) {
			xO.on("<hr>"+nome+" "+(xO.nt++)+" "+s);
			String v[] = str.palavraA(s,"@x:");
			s = "";
			for (int i=0;i<v.length;i++) {
				String l = v[i];
				if (str.equals(l,"X@")) {
					//Vetor: 
					// 0=X 
					// 1=tag-name (nome da tag a localizar)
					// 2=Before|After (localiza Antes ou Depois)
					// 3=Before|After (insere antes ou Depois)
					String c[] = str.palavraA(str.leftAt(l," "),"@"); 
					xO.on("<hr>params: "+c.length+" "+str.leftAt(l," ")+"<hr>");
					l = str.substrAt(l," ");
					xmlOO ti = fromStr(l);
					xmlOO ta = locTagName(c[1],c[2].equals("Before"));
					if (ta==null) {
						xO.on("<br>ERRO não loc tagname="+c[1]+" "+c[2]);
						return null;
					}
					
					//insTags(ta,ti,c[3].equals("Before"));
					xO.pi.put(xO.pi.size()+"",new Object[]{ta,ti,c[3]});
					
					
				} else {
					s += l;
				}
			}
			xO.on("<hr>SOBROU: "+s+"<hr>");
		}
		
		//expande attribs
		xO.on("<hr>EXPR= "+nome+" "+(xO.nt++)+" "+s);
		if (s.indexOf("&lt;x:")!=-1) {
			xmlOO x = fromStr(s);
			insNaTag(this,x);
			s = "";
		}
		
		return s;
	}
	//***************************************
	//prc OO
	public boolean procOO(xmlOut xO) {
		if (nome!=null && nome.equals("draw:image")) {
			putAtr("xlink:href","/mnt/dados/dados/el/2006/fotosCand/6_4599.jpg");
			nome = "x:tag";
			atr = new Hashtable();
			tex = "draw:image xlink:show=\"embed\" xlink:actuate=\"onLoad\" xlink:href=\"/mnt/dados/dados/el/2006/fotosCand/<x:@codigo :format=\"!m\"/>/<x:$cargo/>_<x:nro/>.jpg\" xlink:type=\"simple\"/";
		}
		tex = tex(xO,tex);
		for (int i=0;i<size();i++) {
			xmlOO x = (xmlOO)get(i);
			x.pai = this;
			x.ps = i;
			x.procOO(xO);
		}
		texF = tex(xO,texF);
		return false;
	}
	//***************************************
	public void insNaTag(xmlOO x,xmlOO y) {
		if (!str.vazio(y.tex)) {
			x.tex += y.tex;
		}
		for (int i=0;i<y.size();i++) {
			x.put(y.get(i));
		}
		if (!str.vazio(y.texF)) {
			x.texF += y.texF;
		}
	}
	//***************************************
	public boolean insSubTree(xmlOO tg,xmlOO ti,boolean antes) {
		//guarda posição
		int p=tg.ps;

		//monta a nova
		xmlOO pn = ti.locTagName("x:OOinclude",false);
		pn.removida = true;
		
		if (antes) {
			//vai para o pai
			tg = tg.pai;
			//guarda antiga da posição
			xmlTag t1 = tg.get(p);
			//armazena nova na posição
			tg.tg.put(""+p,ti.get(0));
			//guarda no nodo marcado a antiga
			pn.pai.put(t1);
			return true;
			
		} else {
			//guarda filhos da atual
			Hashtable t = tg.tg;
			//substitui pelos filhos da nova
			tg.tg = ti.tg;
			//adiciona filhos da antiga no nodo marcado
			for (int i=0;i<t.size();i++) {
				pn.pai.put((xmlTag)t.get(""+i));
			}
		}
		return true;
	}
	//***************************************
	public xmlOO locTagName(String name,boolean antes) {
		if (nome!=null && nome.equals(name)) {
			return this;
		}
		//antes
		if (antes) {
			if (pai!=null) {
				return pai.locTagName(name,antes);
			}
			return null;
		}
		
		//depois
		for (int i=0;i<tg.size();i++) {
			xmlOO x = (xmlOO)get(i);
			x.pai = this;
			x.ps = i;
			x = x.locTagName(name,antes);
			if (x!=null) {
				return x;
			}
		}
		
		return null;
		
	}
	//***************************************
	public xmlOO fromStr(String n) {
		String v[] = new String[]{"&lt;","<","&gt;",">","&quot;","\""};
		for (int i=0;i<v.length;i+=2) {
			n = str.troca(n,v[i],v[i+1]);
		}
		String a = arquivo.nomeTmp("1","xml");
		(new arquivo(a)).gravaTxt(n);
		xmlOO r = (xmlOO)(new xmlParser(a)).parse(this);
		(new File(a)).delete();
		return r;
	}
	
}
