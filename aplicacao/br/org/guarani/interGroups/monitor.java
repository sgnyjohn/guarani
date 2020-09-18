
package br.org.guarani.interGroups;

import br.org.guarani.servidor.*;
import br.org.guarani.util.*;

//*****************************************************
//*****************************************************
public class monitor extends staticThread {
	static xmlTag cfg;
	static monitorGroup mG[];
	//***************************************************
	public void init() {
		//le config
		xmlParser x = new xmlParser(Guarani.dirCfg+"/interGroups.xml");
		cfg = x.parse();
		//le configs grupos
		//levanta threads grupos
	}
	//***************************************************
	public void passo() {
		if (cfg==null) {
			init();
		}

		//on("cfg="+cfg);

		//xmlTag x = cfg.listTag("interGroups");
		//on("iG="+x);
		//x = x.listTag("interGroups");
		
		
	}
	//***************************************************
	public void on(String s) {
		logs.grava("Groups",s);
	}
	//***************************************************
	public monitor() {
		pAtu  = 20000;
		inicia();
	}
}