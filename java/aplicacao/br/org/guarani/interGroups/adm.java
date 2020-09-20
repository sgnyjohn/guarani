
package br.org.guarani.interGroups;

import br.org.guarani.servidor.*;
import br.org.guarani.util.*;

//*****************************************************
//*****************************************************
public class adm extends PagV {
	//***************************************************
	public void inicio() {
		on(
			"<form>"
			+"Filtrar: <input name=filt value=\""+param("filt","")+"\"size=20>"
			+"<input type=submit>"
			+"</form>"
		);
		String f = param("filt");
		executa e = new executa();
		e.exec(ped,"ls -tlc /var/cache/apt/archives");
		String tx = e.getOut();
		String v[] = str.palavraA(tx,"\n");
		String ri="";
		for (int i=0;i<v.length;i++) {
			if (f==null || v[i].indexOf(f)!=-1) {
				on("<br>"+v[i]);
				if (f!=null) {
					String p = str.substrRat(v[i]," ");
					String p1 = str.leftAt(p,"_");
					String pn = str.substrAtAt(str.substrAt(tx,p),p1,"\n");
					if (!str.vazio(pn)) {
						on("=="+p1+pn);
						ri += " "+p1+pn;
					}
					
				}
			}
		}
		on("<hr>"+ri);
		
	}
	//***************************************************
	public void inicioA() {
		adsl a = new adsl();
		a.pg = this;
		a.passo();
		//adsl.cfg.save();
	}
	//***************************************************
	public void inicioI() {
		/*String aq = Guarani.dirCfg+"/interGroups.xml";
		on("<br>arq cfg="+aq);
		xmlParser xp = new xmlParser(aq);
		xmlTag cfg = xp.parse();
		*/
		xmlTag cfg = monitor.cfg;
		if (cfg==null) {
			on("INATIVO...");
			return;
		}
		on("<br>cfg="+cfg);
		cfg.grava("/tmp/lixo.xml");
		cfg.pos = 0;
		xmlTag x = cfg.listTag("interGroups");
		on("<br>iG="+x);
		on("<br>ch="+cfg.getCh("interGroups.group.ident.email"));
		on("<br>ch1="+cfg.getCh("?xml.version"));
		//x = x.listTag("interGroups");
	}
	//***************************************************
	public boolean run(Pedido ped) {
		super.run(ped);

		//para recompilar
		//monitor x = new monitor();

		cab("interGroups");
		exec();
		rodap();

		return true;
	}
}