/*
	* sjohn@via-rs.net dez/2014 
*/
package br.org.guarani.servidor;

	import br.org.guarani.util.*;

	import java.net.*;
	import java.io.*;
	import java.lang.*;
	import java.util.*;
	//import guarani.es.*;

//****************************************************************
//****************************************************************
public class HttpHttpsProxy extends Http {
	//**************************************************************
	public void run() {
		nPedidos++;
		in = System.currentTimeMillis();
		
		//******************************************************************************
		//******************************************************************************
		String classeP = str.troca((String)cnf.get("classeP"),".","/")+".class";
		//logs.grava("vai exec: "+classeP+" "+get1);
		Guarani.execClasse("/"+classeP,pedido,o,0);
		//******************************************************************************
		//******************************************************************************
	
		try {
			if (sp!=null) {
				sp.close();
			}
			sp = null;
		} catch (IOException e) {
			//logs.grava("task",geto+", sp.close(): "+e);
		}

		fi = System.currentTimeMillis();
		nBytes += pedido.getBytes();
		tempo += fi-in;
		rodando = false;
		return;

	}

}
