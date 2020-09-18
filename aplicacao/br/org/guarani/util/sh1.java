package br.org.guarani.util;

import br.org.guarani.servidor.*;
//import br.org.guarani.servidor.*;

import java.util.*;
import java.io.*;

public class sh1 {
	//****************************
	public static void execX11(PagV pg,String cmd) {
		String c =
				"su - "+pg.usu+" -c \""
			+"export XAUTHORITY="+xAuth(pg.usu)+";"
			+"export DISPLAY=:0.0;"
			+cmd
			+"\" &"
		;
		sh s = new sh(pg.ped);
		//s.debug = true;
		s.cmd(c);
		s.exec(true);
	}
	//****************************************
	public static String xAuth(String us) {
		String xa="/home/"+us+"/.Xauthority";
		if ((new File("/tmp/.gdm_socket")).exists()) {
			File f[] = (new File("/tmp")).listFiles();
			for (short i=0;i<f.length;i++) {
				String a = ""+f[i];
				if (str.equals(a,"/tmp/.gdm") && a.indexOf("_")==-1) {
					xa = a;
					break;
				}
			}
		}
		return xa;
	}
}
