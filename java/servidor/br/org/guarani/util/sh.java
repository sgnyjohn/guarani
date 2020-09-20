package br.org.guarani.util;

import java.util.*;
import java.io.*;
import br.org.guarani.util.*;
import br.org.guarani.servidor.*;
//import guarani.es.*;


//***********************
//preve windows
//***********************
//***************************//

//***************************//
public class sh {
	public String ar,sh;
	String ln = so.linux()?"\n":"\r\n";
	Pedido ped;
	executa e;
	public boolean debug = false;
	//***************************//
	public sh(Pedido pd) {
		ped = pd;
		sh = "";
		try {
			ar = File.createTempFile("ssh",(so.linux()?".sh":".bat"))
				.toString();
		} catch (Exception e) {
		}
	}
	//***************************//
	public void cmd(String cmd) {
		if (so.linux()) {
			sh += cmd+ln;
		} else {
			//converte para acentuação DOSe
			sh += str.toDos(cmd)+ln;
		}
	}
	//***************************//
	public String exec(boolean segundo) {
		if (!so.linux()) {
			sh = "set PATH="+so.dirPrg()+"\\bin;%PATH%"+ln+sh;
		} else if (!debug) {
			cmd("rm -f $0");
		}
		//grava shell
		arquivo arq = new arquivo(ar);
		arq.gravaTxt(sh);
		e = new executa();
		String r;
		if (segundo) {
			e.execBack(ped,(so.linux()?"sh ":"")+ar);
			r = "execução segundo plano!!";
		} else {
			e.exec(ped,(so.linux()?"sh ":"")+ar);
			r = e.getOut();
		}
		//elimina sh?
		if (!so.linux() && !debug) {
			try { Thread.sleep(2000); } catch (Exception e) {};
			arq.f.delete();
		}
		return r;
	}
	//***************************//
	public String getErr() {
		return e.getErr();
	}
	//***************************//
	public String getOut() {
		return e.getOut();
	}
	//***************************//
	public String getHtml() {
		return e.getHtml();
	}
	//***************************//
	public String exec() {
		return exec(false);
	}
}

