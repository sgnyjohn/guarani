/*
		signey / set/2001
	*/


import java.util.*;
import bd.*;
import br.org.guarani.servidor.*;
import br.org.guarani.util.*;

//*****************************************//
//*****************************************//
public class debug extends Pag {

	//*****************************************//
	public boolean run(Pedido pd) {
		String k;
		super.run(pd);
		//logs.grava("teste debug");

		cab("");
		ped.on("Pedido: "+ped);
		Hashtable p1 = ped.getCab();
		//motra pedido completo
		on("<br><b>Proxy: "+ped.proxy+"</b>");
		ped.on("<table border=1>");
		ped.on("<tr><th colspan=2>Pedido");
		for (Enumeration e = p1.keys() ; e.hasMoreElements() ;) {
			k = (String)e.nextElement();
			ped.on("<tr><td>"+k+"<td>"+p1.get(k));
		}
		ped.on("</table>");

		Hashtable p = ped.getParametros();
		ped.on("<table border=1>");
		ped.on("<tr><th colspan=2>Parametros");
		for (Enumeration e = p.keys() ; e.hasMoreElements() ;) {
			k = (String)e.nextElement();
			ped.on("<tr><td>"+k+"<td>"+p.get(k));
		}
		ped.on("</table>");
		rodap();

		return true;
	}

}
