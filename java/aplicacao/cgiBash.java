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
	static String[] env = new String[]{
		"CONTENT_LENGTH=0" //0
		,"DOCUMENT_ROOT=/srv/http" //1
		,"GATEWAY_INTERFACE=CGI/1.1" //2 
		,"HTTP_CACHE_CONTROL=Max-age=0" //3
		,"HTTP_CONNECTION=keep-alive" //4
		,"HTTP_DNT=1" //5
		,"HTTP_HOST=intranet.john.lar.art.br:8080" //6
		,"HTTP_SEC_GPC=1" //7
		,"HTTP_UPGRADE_INSECURE_REQUESTS=1" //8
		,"REQUEST_SCHEME=http" //9
		,"SERVER_ADDR=10.11.12.5" //10
		,"SERVER_NAME=intranet.john.lar.art.br" //11
		,"SERVER_PORT=8080" //12
		,"SERVER_PROTOCOL=HTTP/1.1" //13
		,"SERVER_SOFTWARE=lighttpd/1.4.53" //14
		,"HTTP_ACCEPT='text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8'" //15
		,"HTTP_ACCEPT_ENCODING='gzip, deflate’" //16
		,"HTTP_ACCEPT_LANGUAGE='pt-BR,pt;q=0.8,en-US;q=0.5,en;q=0.3’" //17
		,"HTTP_COOKIE='ultd=f; ultt=pesquisa filmes p2p; q=castle; tp_c=true; tp_f=true; opFila=1; cinemaPesq=arm nia; tp_m=true; volLocal=1; clicks=1; usu=signey; jorSel=~0~1~2~3~5~6~7~8~9~10~11~12~13~14~15~; GSESSIONID=d370246563054d31; jor=~0~1~2~3~5~6~7~8~9~10~11~12~13~14~15~; dias=360; palavra=lava jato’" //18
		,"HTTP_USER_AGENT='Mozilla/5.0 (X11; Linux x86_64; rv:82.0) Gecko/20100101 Firefox/82.0’" //19
		,"REDIRECT_STATUS=200" //20
		,"REMOTE_ADDR=10.11.12.10" //21
		,"REMOTE_PORT=35644" //22
		,"REQUEST_METHOD=GET" //23
		,"REQUEST_URI=/player/cgi/set.sh" //24
		,"SCRIPT_FILENAME=/srv/http/player/cgi/set.sh" //25
		,"SCRIPT_NAME=/player/cgi/set.sh" //26
		,"QUERY_STRING=" //27
	};
	//**************************************************
	public void exec(String s) {
		String q = "";
		if (false) {
			for (Enumeration e=ped.h.keys();e.hasMoreElements();) {
				String k = (String)e.nextElement();
				if (!k.equals("dir")) {
					q += "&"+str1.encodeParam(k)+"="+str1.encodeParam((String)ped.h.get(k));
				}
			}
			q = q.substring(1);
		} else {
			q = str.substrAt(""+ped.ped.get("?endereco"),"?");
			//ogs.grava("query="+q);
		}
		try {
			//log("vai exec q="+ped.queryString+" q="+q+" script = "+s);
			Process pr = Runtime.getRuntime().exec(
				new String[]{bash,s} // bash e script
				,new String[]{"_guarani=1"
					,"QUERY_STRING="+(str.vazio(q)?"":q)+""
					,"REMOTE_USER="+usu
					,"REMOTE_ADDR="+ped.getIp()
					,"REQUEST_METHOD="+ped.ped.get("?")
					,"SERVER_PROTOCOL=HTTP/1.1"
					,"HTTP_CONNECTION=close" //4
					,"HTTP_HOST="+ped.ped.get("host") //6
					,"HTTP_ACCEPT='"+ped.ped.get("accept")+"'" //15
					,"HTTP_ACCEPT_ENCODING='"+ped.ped.get("accept-encoding")+"'" //16
					,"HTTP_ACCEPT_LANGUAGE='"+ped.ped.get("accept-language")+"'" //17
					,"HTTP_COOKIE='"+ped.ped.get("cookie")+"'" //18
					,"HTTP_USER_AGENT='"+ped.ped.get("user-agent")+"'" //19

				} //query
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
			erro("script "+script+" not exists! pd="+pd);
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
