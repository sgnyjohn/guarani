/*
	signey maio / 2004
*/

package br.org.guarani.util;

import java.util.*;
import java.io.File;

import br.org.guarani.servidor.*;
import br.org.guarani.util.*;

//***************************
//***************************
public class dynDns extends PagV implements Runnable {
	//***************************
	public void inicio() {
		web w = new web("http://192.168.78.1/MainPage?id=16");
		w.setCab("Authorization: Basic cm9vdDpycHB0");
		String s = w.lePag();
		on(s);
	}
	//***************************
	public boolean run(Pedido ped) {
		super.run(ped);
		if (!doGrupo("adm")) {
			return false;
		}
		cab("dynDns");
		exec();
		rodap();
		return true;
	}
	//***************************
	public void run() {
	}
}
