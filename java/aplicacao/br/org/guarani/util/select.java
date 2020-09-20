/* ************************************

*************************************/
package br.org.guarani.util;

import java.util.*;

import br.org.guarani.servidor.*;

//***********************************
//***********************************
public class select {
	Hashtable op = new Hashtable();
	public String nome,ops="";
	//***********************************
	public void mostra(Pag pg) {
		String val = pg.param(nome);
		pg.on("<select name="+nome+" "+ops+">");
		for (int i=0;i<op.size();i++) {
			String v[] = (String[])op.get(""+i);
			pg.on("<option "+(val==null||!val.equals(v[0])?"":"selected")
				+" value='"+v[0]+"'>"+v[1]
			);
		}
		pg.on("</select>");
	}
	//***********************************
	public String select(String val) {
		String r = "<select name="+nome+" "+ops+">";
		for (int i=0;i<op.size();i++) {
			String v[] = (String[])op.get(""+i);
			r += "<option "+(val==null||!val.equals(v[0])?"":"selected")
				+" value='"+v[0]+"'>"+v[1];
		}
		return r+"</select>";
	}
	//***********************************
	public String select(Pag pg) {
		return select(pg.param(nome));
	}
	//***********************************
	public void add(String v,String o) {
		op.put(""+op.size(),new String[]{v,o});
	}
	//***********************************
	public void add(String o) {
		add(o,o);
	}
	//***********************************
	public select(String nom) {
		nome = nom;
	}
	//***********************************
	public select(String nom,String ops) {
		nome = nom;
		String v[] = str.palavraA(ops,"\n");
		for (int i=0;i<v.length;i++) {
			String v1[]=str.palavraA(v[i]+"=="+v[i],"==");
			op.put(op.size()+"",v1);
		}
	}
	//***********************************
	public static String options(String va,Hashtable op) {
		String r = "";
		for (int i=0;i<op.size();i++) {
			r += ("<option value="+i+(va!=null && va.equals(""+i)?
				" selected":"")+">"+op.get(""+i));
		}
		return r;
	}
	//***********************************
	public static void options(Pag pg,String va,String op) {
		String v[] = str.palavraA(op,",");
		for (int i=0;i<v.length;i++) {
			pg.on("<option value="+i+(va!=null && va.equals(""+i)?
				" selected":"")+">"+v[i]);
		}
	}
}
