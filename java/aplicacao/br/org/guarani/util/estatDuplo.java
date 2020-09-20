package br.org.guarani.util;
import br.org.guarani.servidor.*;

import java.util.*;

//***************************************
public class estatDuplo {
	public Hashtable h = new Hashtable();
	//permite recuperar a chave pela ordem
	public Hashtable hO = new Hashtable();
	public String nome,at;
	public duplo tot;
	public int nDec=0;
	public int ordem;
	//***************************************
	public Object getChave(int i) {
		return hO.get(""+i);
	}
	//***************************************
	public String select(Pedido pd) {
		return (pd!=null?nome:"")
			+":<select name="+nome+"><option>"+options(pd)+"</select>";
	}
	//***************************************
	public String toH() {
		return ""+h;
		/*String r = "";
		for (int i=0;i<h.size();i++) {
			inteiro oi = (inteiro)h.get(i);
			r += ","+oi.nome+"="+oi.i;
		}
		return "{"+r.substring(1)+"}";
		*/
	}
	//***************************************
	public String options() {
		return options(null);
	}
	/* **************************************
	public String options(Pag pg) {
		return options(pg.pd);
	}
	*/
	//***************************************
	public String options(Pedido pd) {
		String r = "";
		//if (h.h==null) return r;
		Object o[]=Sort.sort(h,"nome",false);
		for (short i=0;i<o.length;i++) {
			duplo ii = (duplo)o[i];
			r += "<option "
				+(pd!=null && ii.nome.equals(pd.getString(nome,""))?"selected":"")
				+" value='"+ii.nome+"'>"+ii.nome+" ("+ii.i+")";
		}
		return r;
	}
	//***************************************
	public String toTxt() {
		Object o[]=null;
		if (ordem==0) {
			o = Sort.sort(h,"i",true);
		} else {
			o = Sort.sort(h,"nome",false);
			//o = new Object[h.size()];
			//for (int i=0;i<h.size();i++) {
			// o[h.size()-1-i] = h.get(i);
			//}
		}
		String r="*Tot "+nome
			+"\t\t"+o.length+"";
		duplo ii;
		for (short i=0;i<o.length;i++) {
			ii = (duplo)o[i];
			if (at==null) {
				r += "\n\t"
					+ii.nome+"\t"+num.format(ii.i,nDec);
			} else if (at.indexOf("<")!=-1) {
				r += "\n\t"
					+ii.nome+"\t"+num.format(ii.i,nDec)
					+str.troca(at,"@@",""+ii.nome);
			} else {
				r += "\n\t"+ii.nome+"\t"+num.format(ii.i,nDec);
			}
		}
		r += "\n\t"
			+tot.nome+"\t"+num.format(tot.i,nDec);
		r += "";
		return r;
	}
  //***************************************
	public String toString() {
		Object o[]=null;
		if (ordem==0) {
			o = Sort.sort(h,"i",true);
		} else {
			o = Sort.sort(h,"nome",false);
			//o = new Object[h.size()];
			//for (int i=0;i<h.size();i++) {
			// o[h.size()-1-i] = h.get(i);
			//}
		}
  
		String r="<table border=1><tr><th colspan=2>"+nome
			+"("+o.length+")";
		duplo ii;
		for (int i=0;i<o.length;i++) {
			ii = (duplo)o[i];
			if (at==null) {
				r += "<tr><td>"
					+ii.nome+"<td align=right>"+num.format(ii.i,nDec);
			} else if (at.indexOf("<")!=-1) {
				r += "<tr><td>"
					+ii.nome+"<td align=right>"+num.format(ii.i,nDec)
					+str.troca(at,"@@",""+ii.nome);
			} else {
				r += "<tr><td><a href=\""+at+ii.nome+"\">"
					+ii.nome+"</a><td>"+num.format(ii.i,nDec);
			}
		}
		r += "<tr><td><b>"
			+tot.nome+"<td align=right>"+num.format(tot.i,nDec);
		r += "</table>";
		return r;
	}
	//***************************************
	public estatDuplo() {
		this("estat",null);
	}
	//***************************************
	public estatDuplo(String nome) {
		this(nome,null);
	}
	//***************************************
	public estatDuplo(String nome,String at) {
		this.nome=nome;
		this.at=at;
		tot = new duplo("Total");
		h = new Hashtable();
	}
	//***************************************
	public double inc(String ch) {
		tot.inc();
		ch = ""+ch;
		duplo i = (duplo)h.get(ch);
		if (i==null) {
			i = new duplo(ch);
			h.put(ch,i);
			hO.put(hO.size()+"",ch);
		}
		i.inc();
		return i.i;
	}
	//***************************************
	public double inc(String ch,double in) {
		tot.inc(in);
		ch = ""+ch;
		duplo i = (duplo)h.get(ch);
		if (i==null) {
			i = new duplo(ch);
			h.put(ch,i);
			hO.put(hO.size()+"",ch);
		}
		i.inc(in);
		return i.i;
	}
}
 
