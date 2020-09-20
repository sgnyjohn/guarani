
package br.org.guarani.interGroups;

import java.util.*;

import br.org.guarani.servidor.*;
import br.org.guarani.util.*;

//*****************************************************
//*****************************************************
public class acessoExterno extends staticThread {
	public static gConf cfg;
	long alt=0;
	arquivo arq;
	//***************************************************
	public void init() {
		//le config
		cfg = new gConf(this);
		
		
		//logs.grava("cfg="+cfg.cfg);
		//cfg.put("teste","valor do teste");
		//cfg.save();
		//le configs grupos
		//levanta threads grupos
	}
	//***************************************************
	public void passo() {
		if (cfg==null) {
			init();
			arq = new arquivo(
				(String)cfg.get("apacheLog","/var/log/apache2/accessSSL.log")
			);
			alt = arq.f.lastModified();
		}
		
		long n = arq.f.lastModified();
		if (n==alt) {
			return;
		}
		alt = n;
		on("<br>Alterado...");
	}
	//***************************************************
	public void on(String s) {
		log(s);
	}
	//***************************************************
	public acessoExterno() {
		pAtu  = 10000;
		//if (!gConf.dev()) {
			inicia();
		//}
	}
}