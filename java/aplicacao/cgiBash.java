/*
 * Signey John set/2020
 * 
 * pelo redirecionamento de um dir e sub dir pela configuração dirClasse...
 * 	executa scripts bash
 */
 
import java.util.*;
import java.io.*;
import java.net.*;

import br.org.guarani.servidor.*;
import br.org.guarani.util.*;

//**************************************************
//**************************************************
public class cgiBash extends PagV {
	static String bash = "bash";
	static Hashtable h;
	//**************************************************
	public void exec(String s) {
		String q = "";
		for (Enumeration e=ped.h.keys();e.hasMoreElements();) {
			String k = (String)e.nextElement();
			if (!k.equals("dir")) {
				q += "&"+str1.encodeParam(k)+"="+str1.encodeParam((String)ped.h.get(k));
			}
		}
		try {
			//log("vai exec q="+ped.queryString+" q="+q+" script = "+s);
			Process pr = Runtime.getRuntime().exec(
				new String[]{bash,s} // bash e script
				,new String[]{"_guarani=1","QUERY_STRING="+(str.vazio(q)?"":q.substring(1))+""} //query
				,new File(str.leftRat(s,"/")) //dir exec
			);
			String l="",line;
			int nl = 0;
			BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			while ((line = in.readLine()) != null) {
				nl++;
				line = str.trimm(line);
				on(line);
			}
			in = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
			while ((line = in.readLine()) != null) {
				nl++;
				line = str.trimm(line);
				on(line);
			}
			if (nl==0) {
				on("<h1>ERRO, script retornou vazio</h1>");
			}
			in.close();
			pr.destroy();			
		} catch (Exception e) {
			erro(str.erro(e));
			on(str.erro(e));
			log("ERRO cgi exec q="+ped.queryString+" q="+q+" script = "+s);
		}
	}
	//**************************************************
	public void erro(String s) {
		logs.grava("erro",s);
		log("ERRO: "+s);
	}
	//**************************************************
	static void log(String s) {
		logs.grava("cgiBash",s); 
	}
	//**************************************************
	public boolean run(Pedido pd) {
		if (!super.run(pd)) {
			return false;
		}
		String script = str.troca(pd.dirRoot()+param("dir"),"//","/");
		//og("script "+script+" "+pd.h);
		String ext = str.substrRat(script,".");
		File f = new File(script);
		if (!f.exists()) {
			erro("script "+script+" not exists!");
		} else if (f.isDirectory()) {
			File ia = new File(f+"/index.html");
			if (ia.exists()) {
				//og("manda arq "+ia);
				//ped.mandaArq(""+ia,null,false);
				// redirect...
				pd.movTemp(param("dir")+"index.html",null);
			} else {
				exec("ls");
			}
		} else if (!ext.equals("sh")) {
			//og("manda arq "+script);
			ped.mandaArq(script,null,false);
		} else if (!f.canExecute()) {
			erro("script "+script+" not executable");
		} else {
			//executa bash
			exec(script);
		}
		return true;
	}
}
