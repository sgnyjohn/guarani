package br.org.guarani.util;

import java.util.*;
//import bd.*;
/*
 * mudança em 2019-10-03 signey
 * avalia nesta ordem: 'and or not'
 * 		mudado para:
 * 		'or and not'
 */

//**************************************
//**************************************
public class strPesq {
	//static String dOu="|",nao="!",dE="&";
	static String dOu="|",nao="-",dE=" ";
	int tmMin=1;
	strPesqAnd vAnd[];
	int tm=0;
	/**************************************
	public String sql(Dados dad,String campos[]) {
		return sql(concatSql(dad,campos));
	}
	//**************************************
	//retorna expressao do bd - fazer outras q não mysql
	public static String concatSql(Dados dad,String campos[]) {
		if (campos.length==1) {
			return campos[0];
		}
		String r = "";
		for (short i=0;i<campos.length;i++) {
			//String r1 = sql(campos[i]);
			r += ","+campos[i];
		}
		//mysql...
		r = "CAST(concat("+r.substring(1)+") as CHAR)";
		//logs.grava(r);
		return r;
	}
	*/
	//**************************************
	public String sql(String campo) {
		String r = "";
		for (short i=0;i<tm;i++) {
			String o = vAnd[i].sql(campo);
			if (o!=null) {
				r += (r.length()==0?"":" or ")+o;
			}
		}
		return r.length()==0?null:"("+r+")";
	}
	//**************************************
	public strPesq(String s) {
		this(s,-1);
	}
	//**************************************
	public strPesq(String s,int tmMinimo) {
		if (tmMinimo>0) {
			tmMin = tmMinimo;
		}
		if (s==null) {
			return;
		}
		s = str.trocaTudo(str.trimm(s).toLowerCase(),"  "," ");
		//s = str.trocaTudo(s,strPesq.nao+" ",strPesq.nao);
		//s = str.trocaTudo(s,strPesq.dOu+" ",strPesq.dOu);
		//s = str.trocaTudo(s," "+strPesq.dOu,strPesq.dOu);
		if (s.length()==0) {
			return;
		}
		String v[] = str.palavraA(s,strPesq.dOu);
		Hashtable h = new Hashtable();
		for (int i=0;i<v.length;i++) {
			String s1 = str.trimm(v[i]);
			if (s1.length()>tmMinimo) {
				strPesqAnd sp = new strPesqAnd(s1);
				if (sp.valid) {
					h.put(""+h.size(),sp);
				}
			}
		}
		tm = h.size();
		vAnd = new strPesqAnd[tm];
		for (int i=0;i<tm;i++) {
			vAnd[i] = (strPesqAnd)h.get(""+i);
		}
	}
	//**************************************
	public boolean testa(String s) {
		if (tm==0) return true;
		if (s==null) return false;
		s = str.trimm(s.toLowerCase());
		if (s.length()==0) return false;
		//faz ou nos and existentes
		for (short i=0;i<tm;i++) {
			if (vAnd[i].testa(s)) {
				return true;
			}
		}
		return false;
	}
	//**************************************
	//**************************************
	public class strPesqAnd {
		public String v[];
		public boolean not[],valid=true;
		int tm;
		//**************************************
		public String sql(String campo) {
			String r = "";
			for (short i=0;i<v.length;i++) {
				r += (r.length()==0?"":" AND ")+campo
					+(not[i]?" NOT":"")+" LIKE '%"+v[i]+"%'";
			}
			return (r.length()==0?null:"("+r+")");
		}
		//**************************************
		public strPesqAnd(String s) {
			String v1[]=str.palavraA(s,strPesq.dE);
			Hashtable h = new Hashtable();
			//logs.grava("tm min="+tmMin);
			for (int i=0;i<v1.length;i++) {
				if (v1[i].length()>=tmMin) {
					String n = v1[i].substring(0,1).equals(strPesq.nao)?"s":"";
					v1[i] = str.trimm(v1[i]," "+strPesq.nao);
					if (v1[i].length()>=tmMin) {
						h.put(h.size()+"",new String[]{v1[i],n});
					}
				}
			}
			tm = h.size();
			v = new String[tm];
			not = new boolean[tm];
			for (int i=0;i<tm;i++) {
				v1 = (String[])h.get(""+i);
				v[i] = v1[0];
				not[i] = v1[1].length()!=0;
			}
		}
		//**************************************
		public boolean testa(String s) {
			boolean p = true;
			for (short i=0;i<v.length;i++) {
				p = s.indexOf(v[i])!=-1;
				//palavra começa com não ?
				if ( (!p && !not[i]) || (p && not[i]) ) {
					return false;
				}
			}
			return true;
		}
	}
}
