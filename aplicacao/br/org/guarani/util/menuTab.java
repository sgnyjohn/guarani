/* ************************************
*************************************/
package br.org.guarani.util;

import java.util.*;

import br.org.guarani.servidor.*;

	//***********************************
//***********************************
public class menuTab extends menuPag {
	public String subM;
	//***********************************
	public void mostraDiv(Pag pg,String op) {
		pg.on("<table class="+estilo+">"
			+"<tr><td class="+estilo+"Divs>");
		String a[];
		boolean ativo,java;
		for (short i=0;i<hi.size();i++) {
			a = str.palavraA((String)hi.get(""+i),"@@");
			ativo = (a[0]+"&").indexOf(op+"&")!=-1;
			java = str.equals(a[0],"javascript:");
			pg.on(""
				+"<div  onclick=\""
					+(java?a[0]:"desviaSeTag('"+a[0]+"','td',event);")+"\""
					+" class=\""+estilo
					+(ativo?"S "+estilo:"")+"\">&nbsp;"
				+(java?"<font ":"<a href="+a[0])
					+" class=\"men "+estilo+"A"
					+(ativo?"S":"")+"\">"+a[1]
					+(java?"</font>":"</a>")+"&nbsp;"
				+"</div>"
			);
		}
		pg.on((subM==null?""
			:"<tr><td id=menuSub class="+estilo+"Sub colspan="+(hi.size()*2-1)+">"+subM)
			+"<tr><td id=menuDad class="+estilo+"D colspan="+(hi.size()*2-1)+">"
		);
	}
	//***********************************
	public menuTab(String estilo) {
		super(estilo);
	}
	//***********************************
	public menuTab(Hashtable hi,String estilo) {
		super(hi,estilo);
	}
	//***********************************
	public void mostra(Pag pg,String op) {
		pg.on("<table class="+estilo+">"
			+"<tr>");
		String a[];
		int t = 64*hi.size() + 4*(hi.size()-1);
		//pg.ped.on("t="+t);
		String es = Math.round(400.0/t)+"%";
		String td = Math.round(6400.0/t)+"%";
		boolean ativo,java;
		for (short i=0;i<hi.size();i++) {
			a = str.palavraA((String)hi.get(""+i),"@@");
			ativo = (a[0]+"&").indexOf(op+"&")!=-1;
			java = str.equals(a[0],"javascript:");
			pg.on(
				(i==0
					?""
					:"<td class="+estilo+"E width="+es
						+"><font class="+estilo+"E>&nbsp;&nbsp;</font>"
				)
				+"<td align=center onClick=\""
					+(java?a[0]:"desviaSeTag('"+a[0]+"','td',event);")+"\""
					+" width="+td+" class="+estilo
					+(ativo?"S":"")+">&nbsp;"
				+(java?"<font ":"<a href="+a[0])
					+" class=\"men "+estilo+"A"
					+(ativo?"S":"")+"\">"+a[1]
					+(java?"</font>":"</a>")+"&nbsp;"
			);
		}
		pg.on((subM==null?""
			:"<tr><td id=menuSub class="+estilo+"Sub colspan="+(hi.size()*2-1)+">"+subM)
			+"<tr><td id=menuDad class="+estilo+"D colspan="+(hi.size()*2-1)+">"
		);
	}
}
