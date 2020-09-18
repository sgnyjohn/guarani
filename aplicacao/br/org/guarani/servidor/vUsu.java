/*
	Signey John ago/2002
	jun/2005 - logon personalizado
	jun/2010 signey - nao manda senha pro bd
*/

package br.org.guarani.servidor;

import java.util.*;
import java.net.*;
import java.io.*;
//import java.text.*;
//import java.security.*;

import br.org.guarani.util.*;
import br.org.guarani.bd.*;
import bd.*;
import pwwws.*;

import br.org.guarani.loader.op;
import br.org.guarani.loader.opcaoC;


//***********************************
//***********************************
public class vUsu implements Prg {
	private static opcaoC opC;
	protected static Hashtable vip;
	private static String tLogon;
	
	protected Pedido ped;
	//protected String us,tipo,ip,sesO,resp,ender,referer;
	protected String referer;
	
	
	/***********************************
	public boolean desligar() {
		return so.linux() &&
				(str.equals(ped.ip,"127.0.0.") 
					|| str.equals(ped.ip,"192.168.")
					|| str.equals(ped.ip,"10.0.0.")
				)
		;
	}
	*/

	//***********************************
	public vUsu() {
		if (opC==null) {
			opC = new opcaoC();
			opC.initA(null);
			//ped.on("opC="+opC.h.size());
			String o;
			try {
				o = opC.get("cliente").toString();
				o = opC.get("3").toString();
				Object o1 = opC.get("tLogon");
				if (o1!=null) {
					tLogon = o1.toString();
				}
			} catch (Exception e) {
				logs.grava(e);
				opcaoC opC1 = opC;
				opC = null;
				o = opC1.get("3").toString();
			}
		}
	}
	//***********************************
	public void tela(String tl) {
		String s = referer;
		if (s!=null) {
			s = str.substrRat(
				str.leftAt(s,".class"),"/");
		}
		if (opC.get(s+"-nome")!=null) {
			s = opC.get(s+"-nome")+"<br>"+opC.get(s+"-ver");
		} else {
			s = "";
		}
		ped.on(str.troca(str.troca(tLogon,"@VER@",s),"@LOGON@",tl));
	}

	//***********************************
	// invalida logon e mostra tela 
	private void sair() {
		Usuario u = ped.getSessao().getUsuario();
		String us = "????";
		if (u!=null) {
			us = u.getNome();
			if (u.valido()) {
				logs.grava("logon","LogOFF OK: usu="+us+" "+ped.ip+" "+ped);
				u.invalida();
			}
		}
		//reseta apenas se no relogon o usu for # ped.getSessao().reset();
		String t = ""
			+"<h2>Identificação</h2>"
			+"<b>"+us+"</b>, sua sessão foi Terminada..."
		;
		String r = referer;
		if (r != null) {
			t += "<br>Clique <a href="+r
				+">aqui</a> para <b>RETORNAR</b>";
		}
  
		t += "<br><br>Clique "
			+"<a href=?op=troca&referer="+ped.getString("referer")+"&ref="
			+r+"&_ms="+data.ms()
			+">aqui</a> para <b>ALTERAR</b> sua SENHA";

		/*if (desligar()) { 
			t += 
				"<script>"
				+"function shutDown() {"
				+" if (confirm('Desligar o SERVIDOR?')) {"
				+"  window.location='?op=servidor';"
				+" }"
				+"}"
				+"</script>"
				+"<br><br>Clique <a href=javascript:shutDown()"
				+">aqui</a> para <b>DESLIGAR</b> o SERVIDOR"
			;
		}
		*/
		
		tela(t);
  
	}
	//***********************************
	public void rodap() {
		ped.on("</body></html>");
	}
	//***********************************
	public boolean run(Pedido pd) {
		ped = pd;
		
		String op = ped.getString("op","");
		if (op.equals("logo")) {
			op o = opC.get(ped.getString("logo"));
			ped.img(o.buf);
			return true;
		} else if (str.equals(op,"logo")) {
			op o = opC.get(op);
			if (o==null) {
				o = opC.get("-"+op);
			}
			if (o!=null) {
				ped.img(o.buf);
			} else {
				//logs.grava("opC="+opC+" op="+op);
			}
			return true;
		}
		
		referer = ped.getString("referer");
		//logs.grava("referer cab="+referer);
		if (referer==null || referer.indexOf(str.troca(this.getClass().getName(),".","/")+".class")!=-1) {
			//|| 	) 
			referer = (String)ped.ped.get("referer");
			//logs.grava("referer param="+referer);
		} else if (referer.indexOf(".class?op=sair&segs=")!=-1) {
			referer = str.leftAt(referer,"?");
		}
		
		//logon personalizado?
		String s = ped.getString("tLogon");
		if (s!=null) {
			tLogon = (new arquivo("/home/empresa/documentos/clientes/"+s+"/tLogon.html")).leTxt();
		} else if (tLogon==null) {
			tLogon = "<table class=logonTb width=100% height=99%>"
				+"<tr>"
				+"<td width=20%>"
				+"<td rowspan=3 width=60% align=center valign=center>"
					+"@LOGON@"
				+"<td width=20% valign=top align=right>"
					+"<table><tr><td align=center><img src=?op=logo3>"
					+"<br/><font size=2>"
						+"@VER@"
					+"</font>"
					+"</table>"
				+"</table>"
			;
		}
		
  
		//SETA COOK CFRM FORM VAR USU POR 3 ANOS
		ped.setCookie("usu",null,"a3");

		String dirJs = (String)ped.getHttpConf("dir_js");
		dirJs = (dirJs==null?"/js":dirJs);
		ped.on("<html><head><title>****</title></head>"
			+"<link REL=StyleSheet HREF=/estilos/intranet.css>"
			+"<script language=JavaScript src="+dirJs+"/funcoes.js></script>"
			+"<body class=logon bgcolor1=\"#ffdbd6\">");
   
		if (op.equals("sair")) {
			sair();
			rodap();
			return true;
		/*} else if (op.equals("servidor")) {
			if (desligar()) { 
				logs.grava("logon","Desligando o servidor LINUX por Solicitação: "+ped);
				executa e = new executa();
				e.exec(ped,"shutdown now -h");
			}
		*/
		} else if (op.equals("troca")) {
			//logs.grava("TROCA....");
			if (ped.getString("sena")!=null) {
				troca1();
			} else {
				troca();
			}
			rodap();
			return true;
		}

		Hashtable cabPed = ped.getCab();

		//tem proxy?
		if (false && cabPed.get("x-forwarded-for")!=null || 
						((String)cabPed.get("?protocolo")).indexOf(",")!=-1) {
			logs.grava("logon","prox="+cabPed);
			ped.on("Validação via proxy não implementada...");
			return false;
		}

		String us = ped.getString("usu");
		String sn = ped.getString("sen");
		if (us==null) {
			tela(validaform());
			rodap();
			return true;
		}
		
		///verifica
		return verif(us,sn);
		
	}
	//###########################################
	boolean verif(String us,String sn) {
		////////////////////////////////////////////////////
		//verifica senha
		us = str.trimm(us);
		Object o = Usuario.get(ped.getSessao().getId(),us);
		//ogs.grava("cl="+o.getClass().getName());
		usuario u = (usuario)o;
		//seta funcao SQL de armazenamento pass
		Object fp = ped.getHttpConf("funcSQLPassword");
		//logs.grava("funcSQLPassword"+fp);
		if (fp!=null) {
			u.tpPass = ""+fp;
		}
		sn = str.trimm(sn);
		if (u==null || us.charAt(0)=='~' || !u.validaSenha(ped,sn)) {
			logs.grava("logon","LogON ERRO: usu="+us+" "+ped.ip+" "+ped.getString("usu"));
			ped.on("<script>"
				+"alert('Usuário e/ou senha Inválido');"
				+"</script>"
			);
			tela(validaform());
			return true;
		}

		logs.grava("logon","LogON OK: usu="+us+" "+ped.ip+" "+ped.getString("usu"));
		
		//seta usuario...
		String geto =  "ip="+ped.ip+"=us="+us+"=tm=tm sk=sp";
		Val.acum(ped.ip,ped.sessao.getId(),us,geto);
		
	
		logs.grava(" validacao OK, retornando para ender original: "+referer);
		ped.on("<script>"
			+"window.location='"+referer+"';"
			+"</script>"
		);
		return true;

	}

	//***********************************
	public String validaform() {
		if (referer==null) {
			return "logon invalido, falta para onde ir...";
		}
		
		//ped.on(""+ped.ped+"<hr>"+ped+"<hr>"+" "+ped.sessao.getIp()+"<hr>");
		
		String ipPrxInv = " "+Guarani.getCfg("ipProxyInvalido")+" ";
		if (ipPrxInv.indexOf(" "+ped.sessao.getIp()+" ")!=-1) {
			return (new arquivo(Guarani.dirCfg+"/configBrowse.txt")).leTxt();
		}
		
		return ""
			+"<h2>Identificação</h2>" //r="+r
			+"<table align=center class=logonTb1>"
			+"<form id=fl method=post>"
			+"<input type=hidden name=temSenha value=1>"
			+"<input type=hidden value=\""+referer+"\" name=referer>"
			+"<tr><td align=right>Usuario:<td>"
			+ "<input name=usu onblur=\"this.value=trimm(this.value);\" value="+ped.getCookie("usu","")+">"
			+"<tr><td align=right>Senha:<td>"
			+ "<input type=password name=sen>"
			+"<tr><td align=center colspan=2>"
			+ "<input STYLE=\"background: #5abade; "
				+"font-size:20 \" type=submit value=OK>"
			+"</form></table>"
			+"<script>"
			+" var fr=0;"
			+" window.onload = fload;"
			+" function fload() {"
			//+"  alert('pg load');"
			+"  fr = browse.getId('fl');"
			+"  if (!browse.ie) {"
			+"   setTimeout('fr.sen.focus();',500);"
			+" 	} else {"
			+"   fr.usu.focus();"
			+"  }"
			//+"  setTimeout('fr.usu.focus();',500);"
			+" }"
			+"</script>"
			//+"</body></html>\r\n\r\n\r\n"
		;
	}
	//***********************************
	// formulario troca senha
	public void troca() {
		String t = ""
			+"<h2>Troca de Senha</h2>"
			+"<table align=center>"
			+"<form action=?aa="+data.ms()+" method=post>"
			+"<input type=hidden name=temSenha value=1>"
			+"<input type=hidden value=troca name=op>"
			+"<input type=hidden value=\""+referer+"\" name=referer>"
			+"<tr><td align=right>Usuario:<td>"
			+ "<input name=usu value="+ped.getCookie("usu","")+">"
			+"<tr><td align=right>Senha Atual:<td>"
			+ "<input type=password name=sena>"
			+"<tr><td align=right>Nova Senha:<td>"
			+ "<input type=password name=sen1>"
			+"<tr><td align=right>Repita Nova Senha:<td>"
			+ "<input type=password name=sen2>"
			+"<tr><td align=center colspan=2>"
			+ "<input STYLE=\"background: #5abade; "
			+ "font-size:20 \" type=submit value=OK>"
			+"</form></table>"
		;
		tela(t);
	}
	//***********************************
	//grava senha trocada
	public void troca1() {
		String us = str.trimm(ped.getString("usu"));
		String sn = str.trimm(ped.getString("sena"));
		String sn1 = str.trimm(ped.getString("sen1"));
		String sn2 = str.trimm(ped.getString("sen2"));
		String referer = ped.getString("referer");
		usuario u = (usuario)Usuario.get(ped.getSessao().getId(),us);
		//seta funcao SQL de armazenamento pass
		Object fp = ped.getHttpConf("funcSQLPassword");
		//logs.grava("funcSQLPassword"+fp);
		if (fp!=null) {
			u.tpPass = ""+fp;
		}
		String al="?";
		if (u==null || !u.validaSenha(ped,sn)) {
			logs.grava("logon","Nova Senha ERRO Logon: usu="
				+us+" "+ped.ip);
			al="Usuário atual e/ou senha atual Inválido";
		} else if (!sn1.equals(sn2)) {
			logs.grava("logon","Nova Senha ERRO DIFERE: usu="
				+us+" "+ped.ip);
			al="Senha nova não repetida corretamente...";
		} else if (u.gravaSenha(sn1)) {
			logs.grava("logon","Nova Senha ALTERADA: usu="
				+us+" "+ped.ip);
			al=us+", Sua senha foi Alterada...";
		} else {
			logs.grava("logon","Nova Senha ERRO INTERNO: usu="
				+us+" "+ped.ip);
			al="ERRO alterando Senha";
		}
		ped.on("<script>"
				+"alert('"+al+"');"
				+"window.location='"+referer+"';"
				+"</script>"
		);
	}

	/***************************************
	private boolean valida(String dom,String us1) { 	
		return enviaVal(dom+"\\\\"+us1);
	}
	//***********************************
	private String finger(String ip) {
		executa e = new executa();
		e.exec(ped,"finger @"+ip);
		String s[] = str.palavraA(e.getOut(),"\n");
		if (s.length<3) {
			return "?";
		} else {
			return str.leftAt(s[2]," ");
		}
	}
	//**************************************
	private boolean enviaVal(String us) {
		String ln,ch;
		long t = System.currentTimeMillis();
		Socket sk=null;
		int p = str.inteiro(Guarani.getCfg("valPorta"),3133);
		try {
			sk = new Socket(resp,p);
			BufferedReader i = 
				new BufferedReader(
					new InputStreamReader( sk.getInputStream()
					)
				);
			PrintWriter o = new PrintWriter(sk.getOutputStream(), true );

			o.println("?");
			ch = i.readLine();

			o.println(ch+" "+ip+"~"+sesO);
   
			o.println(ch+" "+us);
			//logs.grava("us="+us);

			o.println(ch+" "+(data.ms()-t));

			//redireciona pedido
			//logs.grava("valusuario","OK ped="+ped);
			ped.on(
					"<script>"
				+"function rediLog() {"
				+" location.href = '"+str1.java(ender)+"?_retorna_"+"';"
				+"}"
				+"setTimeout('rediLog();',1000);"
				+"</script>"
				+"</body></html>\r\n\r\n"
			);
			sk.close();

		} catch (java.io.IOException eio) {
			logs.grava("logon","ERRO conex: serv: "+resp+" porta="+p+" "+ped+" sk="+sk+" "+eio);
			return false;
		}
		return true;
	}
	*/

	//**************************************************
	public boolean runSocket(Socket pPed) {
		return false;
	}

}


/*

max-forwards=10
cookie=agendaPessoa=2893; agendaRecepcao=; usu=signey; GSESSIONID=b957d02fe381075c6313
accept-encoding=gzip,deflate
x-forwarded-server=pt-coord071.pt-alergs.br
?=GET
accept=text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,* / *;q=0.5
x-forwarded-host=pt-coord071
accept-charset=ISO-8859-1,utf-8;q=0.7,*;q=0.7
accept-language=pt-br,pt;q=0.5
?endereco=/apl/vUsu.class?tipo=form&resp=localhost&ses=b957d02fe381075c6313&tempo=20&segs=1182256569969&ender=http://pt-coord071/apl/casa.class
x-forwarded-for=172.30.1.53
user-agent=Mozilla/5.0 (X11; U; Linux x86_64; es-ES; rv:1.8.1.3) Gecko/20070217 Iceape/1.1.1 (Debian-1.1.1-2)
?protocolo=HTTP/1.0, 
host=localhost:88}


*/
