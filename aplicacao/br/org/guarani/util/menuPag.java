/* ************************************
*************************************/
package br.org.guarani.util;

import java.util.*;

import br.org.guarani.servidor.*;

public class menuPag {
	String estilo;
	Hashtable hi;
 
	//***********************************
	public String get(String s) {
		return (String)hi.get(s);
	}
	//***********************************
	public void put(String s) {
		hi.put(hi.size()+"",s);
	}
	//***********************************
	public menuPag() {
	}
	//***********************************
	public menuPag(String estilo) {
		this(new Hashtable(),estilo);
	}
	//***********************************
	public menuPag(Hashtable hi,String estilo) {
		this.estilo = estilo;
		this.hi = hi;
	}
	//***********************************
	public void fim(Pag pg) {
		pg.on("</table>");
	}
	//***********************************
	public void mostra(Pag pg,String op) {
	}
}
