package br.org.guarani.loader;

import java.io.*;
import java.util.*;
import java.text.*;

//***************************************
//***************************************
public class xmlTagL {
	public String nome;
	public Hashtable atr=new Hashtable();
	public Hashtable tg=new Hashtable();
	public String tex,texF;
	public int nv=-1,pos=0;
	public boolean removida = false;
	//***************************************
	public String textDad() {
		String r = tex;
		if (getAtr("href")!=null) {
			r += "\t"+getAtr("href");
		}
		if (getAtr("onclick")!=null) {
			r += "\t"+getAtr("onclick");
		}
		for (int i=0;i<tg.size();i++) {
			r += "\t"+get(i).textDad();
		}
		return r+"\t"+texF;
	}
	//***************************************
	public void getElementsByTagName(String s,xmlTagL x) {
		for (int i=0;i<tg.size();i++) {
			xmlTagL p = get(i);
			if (p.nome.equals(s)) {
				x.put(p);
			}
			p.getElementsByTagName(s,x);
		}
	}
	//***************************************
	public xmlTagL getElementsByTagName(String s) {
		xmlTagL x = new xmlTagL("getElementsByTagName");
		x.putAtr("tag",s);
		getElementsByTagName(s,x);
		return x;
	}
	//***************************************
	//ordena tags
	public boolean init() {
		return true;
	}
	//***************************************
	//ordena tags
	public boolean sort(String at) {
		return sort(at,"asc");
	}
	//***************************************
	//ordena tags
	public boolean sort(String atr,String ord) {
		return sort(atr,ord!=null && ord.equals("desc"),0,size()-1);
	}
	//***************************************
	//ordena tags
	public boolean sort(String atr,boolean inverso,int lo0,int hi0) {
		int lo = lo0;
		int hi = hi0;
		int at = 0;
		try {
		if ( hi0 > lo0) {
			at = ( lo0 + hi0 ) / 2;
			String v = get(at).getAtr(atr);
			while( lo <= hi )    {
				if (inverso) {
					//while( lo<hi0 && v.maior(a[lo]) ) ++lo;
					//while( hi>lo0 && v.menor(a[hi]) ) --hi;
					while( lo<hi0 && v.compareTo(get(lo).getAtr(atr))<0 ) ++lo;
					while( hi>lo0 && v.compareTo(get(hi).getAtr(atr))>0 ) --hi;
				} else {
					while( lo<hi0 && v.compareTo(get(lo).getAtr(atr))>0 ) ++lo;
					while( hi>lo0 && v.compareTo(get(hi).getAtr(atr))<0 ) --hi;
				}
				if( lo <= hi ) {
					//swap(a, lo, hi);
					Object x = get(lo);
					tg.put(""+lo,get(hi));
					tg.put(""+hi,x);
					++lo;
					--hi;
				}
			}
			if( lo0 < hi ) {
				sort(atr, inverso, lo0, hi);
			}
			if( lo < hi0 ) {
				sort(atr, inverso, lo, hi0);
			}
		}
		} catch (Exception e) {
			logsL.grava("ERRO: xmlTagL.sort at="+at+" lo="+lo+" hi="+hi
				+" lo0="+lo0+" hi0="+hi0+" size="+size()+" n="+nome+" "+e
			);
		}
		return false;
	}
	//***************************************
	//merge sub-tag de tag name
	public void merge(String tagName) {
		if (nome.equals(tagName)) {
			for (int i=0;i<size();i++) {
				xmlTagL t = get(i);
				for (Enumeration e=t.atr.keys();e.hasMoreElements();) {
					String c = (String)e.nextElement();
					putAtr(t.nome+"_"+c,t.getAtr(c));
				}
				if (!strL.vazio(t.tex)) {
					putAtr(t.nome+"__TEX",t.tex);
				}
			}
			tg = new Hashtable();
		} else {
			for (int i=0;i<size();i++) {
				get(i).merge(tagName);
			}
		}
	}
	//***************************************
	//grava chave
	public void putChA(String s,String atrNome,Object val,String del) {
		del = del==null?".":del;
		int p = s.indexOf(del);
		String sp;
		if (p==-1) {
			//logsL.grava("tipo: "+s+"~"+val.getClass().getName());
			if (val.getClass().getName().equals(getClass().getName())) {
				xmlTagL vl = (xmlTagL)val;
				String a = vl.getAtr(atrNome);
				if (a==null) {
					put(vl);
					return;
				}
				sp = vl.nome+"~"+a;
			} else {
				putAtr(s,(String)val);
				return;
			}
		} else {
			sp =  s.substring(0,p);
		}
		//procura nas tags
		int tm = tg.size();
		for (int i = 0;i<tm;i++) {
			xmlTagL x = get(i);
			String a = x.getAtr(atrNome);
			a = x.nome+(a==null?"":"~"+a);
			if (x!=null && a.equals(sp)) {
				if (p==-1) {
					put((xmlTagL)val,i);
				} else {
					x.putChA(s.substring(p+1),atrNome,val,del);
				}
				return;
			}
		}
		
		if (p==-1) {
			put((xmlTagL)val);
			return;
		}
		
		
		//não encontrou, inclui
		xmlTagL x;
		if (sp.indexOf("~")!=-1) {
			x = new xmlTagL(strL.leftAt(sp,"~"));
			x.putAtr(atrNome,strL.substrAt(sp,"~"));
		}  else {
			x = new xmlTagL(sp);
		}
		put(x);
		x.putChA(s.substring(p+1),atrNome,val,del);
	}
	//***************************************
	//grava chave
	public void putCh(String s,String val) {
		putCh(s,val,".");
	}
	//***************************************
	//grava chave
	public void putCh(String s,String val,String del) {
		del = del==null?".":del;
		int p = s.indexOf(del);
		if (p==-1) {
			putAtr(s,val);
			return;
		}
		//procura nas tags
		String sp = s.substring(0,p);
		int tm = tg.size();
		for (int i = 0;i<tm;i++) {
			xmlTagL x = get(i);
			if (x!=null && x.nome.equals(sp)) {
				x.putCh(s.substring(p+1),val,del);
				return;
			}
		}
		//não encontrou, inclui
		xmlTagL x = new xmlTagL(sp);
		put(x);
		x.putCh(s.substring(p+1),val,del);
	}
	//***************************************
	//recebe parametros separados por . e retorna objeto
	//q pode ser xmlTagL ou Atributo
	public Object getCh(String v[]) {
		xmlTagL rn,r = this;
		for (int i=0;i<v.length;i++) {
			int p=0;
			while ((rn=r.get(p++))!=null) {
				if (rn.nome.equals(v[i])) {
					break;
				}
			}
			if (rn==null) {
				if (i==v.length-1) {
					//retorna atributo
					return r.getAtr(v[i]);
				}
				return null;
			}
			r = rn;
		}
		return r;
	}
	public Object getCh(String s) {
		return getCh(s,".");
	}
	public Object getCh(String s,String del) {
		String v[] = strL.palavraA(s,del);
		return getCh(v);
	}
	//***************************************
	public boolean grava(String nome) {
		return grava(nome,false);
	}
	//***************************************
	public boolean gravaTag(String nome) {
		return grava(nome,true);
	}
	//***************************************
	public boolean grava(String nome,boolean tag) {
		return grava(new xmlOutL(nome),tag);
	}
	//***************************************
	public boolean gravaSubTags(xmlOutL xO,int nv) {
		for (int i=0;i<size();i++) {
			xmlTagL xt = get(i);
			if (!xt.removida) {
				//xt.dd = dd;
				xt.gravaTag(xO,nv);
			}
		}
		return true;
	}
	//***************************************
	public boolean grava(xmlOutL xO,boolean tag) {
		//logsL.grava("buf="+buf);
		//if (true) return true;
		try {
			if (tag || getCh("?xml")==null) {
				xO.on("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");
				xO.on("");
			}
			if (tag) {
				gravaTag(xO,0);
			} else {
				//sub-tags
				gravaSubTags(xO,0);
				/*for (int i=0;i<size();i++) {
					xmlTagL xt = get(i);
					if (!xt.removida) {
						xt.dd = dd;
						xt.gravaTag(xO,0);
					}
				}
				*/
			}
			xO.close();
		} catch (Exception e) {
			logsL.grava("erro","xmlTagL.grava:"+strL.erro(e));
			return false;
		}
		return true;
	}
	//***************************************
	public boolean gravaTag(xmlOutL xO,int nv) {
		
		/*if (X(xO,nv)) {
			return true;
		}
		*/
		
		//tag e atributos
		//String endn = (xO.tb==null || nv<1?"":strL.repl("\t",nv));
		String l = "<"+nome;
		String fm = "/",fmc="</"+nome+">";
		if (nome!=null) {
			if (nome.charAt(0)=='?') {
				fm = "?";
				fmc = "<"+nome+">";
			} else if (nome.charAt(0)=='!') {
				fm = "";
				fmc = "<"+nome+">";
			}
		}
		for (Enumeration e=atr.keys();e.hasMoreElements();) {
			String k = (String)e.nextElement();
			l += " "+k+"=\""+getAtr(k)+"\"";
		}

		int t = size();
		boolean tx = !strL.vazio(tex);
		boolean fechaTag = (tx || t>0);
		
		//grava tex
		if (fechaTag) {
			//se so tem texto sem nova linha e não tem filhos
			if (tg.size()==0 && tx && strL.vazio(texF) && tex.indexOf("\n")==-1) {
				xO.on(l+">"+tex+fmc,nv);
				return true;
			} else {
				xO.on(l+">",nv);
				if (tx) {
					String v[] = strL.palavraA(tex,"\n");
					for (int i=0;i<v.length;i++) {
						xO.on(strL.trimm(v[i]),nv+1);
					}
				}
			}
		} else {
			xO.on(l+fm+">",nv);
		}
		
		//sub-tags
		/*for (int i=0;i<t;i++) {
			xmlTagL xt = get(i);
			xt.dd = dd;
			if (!xt.removida) {
				xt.gravaTag(xO,nv+1);
			}
		}
		*/
		gravaSubTags(xO,nv+1);
		
		//fecha tag
		if (fechaTag) {
			xO.on(fmc,nv);
		}
		
		//texto no fim da tag?
		if (!strL.vazio(texF)) {
			String v[] = strL.palavraA(texF,"\n");
			for (int i=0;i<v.length;i++) {
				xO.on(strL.trimm(v[i]),nv+1);
			}
		}
		return true;
	}
	//***************************************
	public int size() {
		return tg.size();
	}
	//***************************************
	public void remove(int i) {
		xmlTagL r = (xmlTagL)tg.get(""+i);
		if (r!=null) r.removida = true;
	}
	//***************************************
	public xmlTagL get(int i) {
		xmlTagL r = (xmlTagL)tg.get(""+i);
		if (r!=null) r.pos = 0;
		return r;
	}
	//***************************************
	public xmlTagL listTag(String tag) {
		xmlTagL r=null;
		while ((r=get(pos++))!=null) {
			//logsL.grava(r.nome+tag);
			if (tag==null || r.nome.equals(tag)) {
				break;
			}
		}
		//logsL.grava("r="+pos+" "+r);
		return r;
	}
	//***************************************
	public void putTag(String a,String b) {
		xmlTagL t = new xmlTagL(a);
		t.tex = b;
		put(t);
	}
	//***************************************
	public void put(xmlTagL tg1) {
		put(tg1,tg.size());
	}
	//***************************************
	public void put(xmlTagL tg1,int ps) {
		tg.put(""+ps,tg1);
	}
	//***************************************
	public String getAtr(String s,String p) {
		String r = getAtr(s);
		if (r==null) return p;
		return r;
	}
	//***************************************
	public String getAtr(String s) {
		return (String)atr.get(s);
	}
	//***************************************
	public void putAtr(String n,String v) {
		atr.put(n,v);
	}
	//***************************************
	public xmlTagL() {
	}
	//***************************************
	public xmlTagL(String nome) {
		this.nome = nome;
	}
}
