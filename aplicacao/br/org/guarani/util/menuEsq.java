/* ************************************
*************************************/
package br.org.guarani.util;

import java.util.*;

import br.org.guarani.servidor.*;

public class menuEsq extends menuPag {
	//***********************************
	public void mostra(Pag pg,String op) {
		pg.on("<table class="+estilo+"><tr class="+estilo+">"
			+"<td id=menETd class="+estilo+"cE valign=top>"
			+"<table class="+estilo+"T>"
		);
		String a[];
		boolean ativo,java;
		for (short i=0;i<hi.size();i++) {
			a = str.palavraA((String)hi.get(""+i),"@@");
			ativo = (a[0]+"&").indexOf(op+"&")!=-1;
			java = str.equals(a[0],"javascript:");
			pg.on(""
				+(i==0?"":"<tr class="+estilo+"Sp><td class="+estilo+"Sp>")
				+"<tr class="+estilo+"T><td align=center onClick=\""
					+(java?a[0]:"desviaSeTag('"+a[0]+"','td',event);")+"\""
					+" class="+estilo+(ativo?"S":"")+">"
				+(java?"<font ":"<a href="+a[0])
					+" class=\"men "+estilo+(ativo?"S":"")+"\">"+a[1]
					+(java?"</font>":"</a>")
			);
		}
		pg.on("</table>"
			+"<td valign=top class="+estilo+"cD"
			+" width=100% "+">"
		);
	}
	//***********************************
	public menuEsq(String estilo) {
		super(estilo);
	}
	//***********************************
	public menuEsq(Hashtable hi,String estilo) {
		super(hi,estilo);
	}
}
