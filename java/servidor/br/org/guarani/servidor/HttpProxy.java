/*
	* sjohn@via-rs.net abr/2007 
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
public class HttpProxy extends Http {
	//**************************************************************
	public void run() {
		nPedidos++;
		in = System.currentTimeMillis();
		pd = new Hashtable();
		int x;
		
		String raizWeb="/";

		if (!erro) {
			abreStream();
		} else {
			logs.grava("seg","erro antes de abrir stream!!");
		}


		if (!erro & rodando) {
			lePedido();
			logs.grava((String)pd.get("?endereco"));
			if ((get1 = (String)pd.get("?endereco"))==null) {
				erro = true;
				logs.grava(new Exception("pedido invalido!"),
					"sk="+sp+" pd="+pd,"task");
			} else {
				//logs.grava("g="+get1);
				//dirIgnora - ajp13 e apache proxy
				if (dirIgnora!=null) {
					for (short i=0;i<dirIgnora.length;i++) {
						//logs.grava("dirIg="+dirIgnora[i]+" "+get1);
						if (str.equals(get1,dirIgnora[i])) {
							get1 = "/"+get1.substring(dirIgnora[i].length());
							//logs.grava("dirIgRes="+dirIgnora[i]+" "+get1);
							raizWeb = dirIgnora[i];
							break;
						}
					}
				}
				
			}
		}
  
		if (!erro & rodando) {
			pedido = new Pedido(this,pd);
			pedido.raizWeb = raizWeb;
			pedido.setOut(o,sp);
		}
		
		//******************************************************************************
		//******************************************************************************
		String classeP = str.troca((String)cnf.get("classeP"),".","/")+".class";
		//logs.grava("vai exec: "+classeP+" "+get1);
		Guarani.execClasse("/"+classeP,pedido,o,0);
		//******************************************************************************
		//******************************************************************************
	
		if (erro) {
			logs.grava("ERRO","classe http Proxy");
		}

		geto = data.strSql(in)+" "+geto+"<br>"+sp;
 
		//FLUSH
		try {
			o.flush();
		} catch (Exception e) {
			erro = true;
			logs.grava("task",geto+", o.flush(): "+e);
		}


		//CLOSE
		try {
			o.close();
		} catch (Exception e) {
			//erro = true;
			//logs.grava("task",geto+", o.close(): "+e);
		}
  
		try {
			if (i!=null) i.close();
		} catch (Exception e) {
			//erro = true;
			//logs.grava("task",geto+", i.close(): "+e);
		}
   
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