/*
signey / set/2002
*/

package br.org.guarani.util;

import br.org.guarani.servidor.*;

//*****************************************//
//*****************************************//
public class menuPopup {
	//Pag pag;
	public String estilo = "menPop";
	public String nome,tit;
	public String op="",ops="/nnova";
	public boolean posEsq = false;
	public int nOp=0;
	
	//****************************//
	public menuPopup(String n,String tt) {
		//pag = pg;
		nome = n;
		tit = tt;
	}
	//****************************//
	public void opcao(String nome,String at) {
		nOp++;
		op += nome+"~~"+at+"\n";
	}
	//****************************//
	public void ativa(Pag pag) {
		pag.incluiJs("jan.js");
		pag.ped.on(
				"<script>\n"
			+" var "+nome+"_v;"
		);
		if (str.trimm(op).length()!=0) {
			String v[] = str.palavraA(str.trimm(op),"\n");
			pag.on(nome+"_v= new Array;\n");
			for (int i=0;i<v.length;i++) {
				pag.ped.on(" "+nome+"_v["+i+"] = '"
					+str.troca(v[i],"'","\\'")+"';");
			}
		}
		pag.ped.on(" var "+nome
			+" = new menuPopUp('"
			+nome+"','"+tit+"',"+nome+"_v"
			+",'"+ops+"','"+estilo+"');\n"
			+nome+".posEsq = "+(posEsq?1:0)+";\n"
			+"</script>\n");
	}
	//****************************//
	public String atalho(String cod) {
		return nome+".abre(this,'"+cod+"',event)";
	}
	//****************************//
	public String atalho(String mostra,String cod) {
		return "<a class="+estilo+" onClick="+nome+".abre(this,'"+cod+"',event)>"+mostra+"</a>";
	}
}
