package br.org.guarani.servidor;

import java.util.*;
import java.io.*;

import br.org.guarani.servidor.*;
import br.org.guarani.util.*;

import br.org.guarani.loader.opcaoC;


//***************************************
//***************************************
public class hora implements Runnable {
	sis pg;
	//***************************************
	public void run() {
		boolean db=false;
		if (db) logs.grava("hora run...");
  
		//acerta a hora
		if (so.linux()) {
			arquivo f = new arquivo(so.dirTmp()+"/hora.txt");
			if (f.f.exists() && data.ms()-f.f.lastModified()<6*60*60*1000) {
			} else if (Guarani.get("hora")==null) {
				executa e = new executa();
				e.exec(null,"sh /bin/hora");
				Guarani.put("hora","ok");
				f.gravaTxt("aaa");
				f.fecha();
			}
		}

		//ip livre?
		String ipc = ""+sis.opC.get("ip");
		if (ipc.equals("*")) {
			logs.grava("uso",str.troca(""+sis.tabjH," ",""));
			return;
		}
		if (db) logs.grava("ip="+ipc);
  
		//recupera ip do Cliente na internet
		String pip = ""+sis.opC.get("ipC");
		web w = new web(pip);
		w.setTimeOut(3000);
		String l = ""+w.lePag();
		sis.ipeC = str.trimm(l,"\r\n \t");
		if (db) logs.grava("ip recup="+sis.ipeC);
		w.fecha();
		if (db) logs.grava(pip+" ipC="+sis.ipeC+" "+w.erro());

		//é o IP esperado
		if (sis.ipeC.equals(ipc)) {
			logs.grava("uso",str.troca(""+sis.tabjH," ",""));
			if (db) logs.grava("ip é o esperado, saindo");
			return;
		}

		//publica na 3WS
		String h = ""+sis.opC.get("monit");
		String x = sis.opC.get("monitE")
			+str.trimm((Guarani.getHost()+"   ").substring(0,3))
			+"&op="+str.troca(data.strSql()," ","_")+"&cop="+sis.opC.get("wwws.cop")
			+"&sit="+opcaoC.vex(1);
		w = new web(h+x);
		w.setTimeOut(3000);
		String t = w.lePag();
		w.fecha();
	}
	//***************************************
	public hora(sis pg) {
		this.pg = pg;
	}

}
