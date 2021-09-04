/*
	página WEB Validada
*/
package br.org.guarani.servidor;

import java.util.*;
import java.io.*;

import br.org.guarani.servidor.*;
import br.org.guarani.util.*;


//*****************************************//
//*****************************************//
public class PagV extends Pag {
	public String ip,ids;

	public httpSessao sessao;
	public Usuario usuario;
	//public String grS;
	public String usu;
	Hashtable paramSes;
 
	public boolean logon=true; //efetua logon se não logado?

	String raiz;
	
	protected prefs pref;
	public String prefRaiz,prefRaizC;
	
	//*****************************************//
	public boolean devCSS() {
		return super.devCSS() && doGrupo("dsgn");
	}
	//*****************************************
	public void js() {
		super.js();
		script("_sis.usu='"+usu+"';");
	}
	//*****************************************//
	private final boolean validaX509() {
		if (sessao.validaX509(ped)) {
			usuario = sessao.getUsuario();
			usu = usuario.getNome().toLowerCase();
			return true;
		}
		return false;
		
		/*X509Certificate x509 = ped.getCliCert();		
		if (x509==null) {
			logs.grava("x509","pedido não retornou cert, usuário sem cert?");
			return false;
		}
		String a = x509.getSubjectDN().getName();
		usu = str.substrAtAt(a,"CN=",",");		
		Object o = Usuario.get(ped.getSessao().getId(),usu);
		usuario = (Usuario)o;//sessao.getUsuario();
		if (usuario==null) {
			logs.grava("x509","usuario="+usu+"= não validado class Usuario");
			usu = "?";
			return false;
		}
		usuario.valida();
		(ped.getSessao()).setUsuario(usuario);
		return true;
		*/
	}
	//*****************************************//
	private final boolean valida() {
		long t = data.ms();
  
		String endVal = getValUrl();
 	
		//manda cliente validar
		String ev = endVal+"?seg="+data.ms()
			//+"&referer="+ped.raizWeb+this.getClass().getName()+".class"
		;

		//grava param para executar novamente...
		ped.guardaAmbiente();
  
		//redireciona para validador...
		if (false && ped.redireciona(ev,(String)null)) {
			logs.debug("Header, desviou para valusuario.class="+ev+" "+ped.getCab("?endereco"));
			//return true;
		} else {
			//logs.debug("JavaScript, desviou para "+ev+"=retornar para: "+ped.getCab("?endereco"));
			//redireciona via JS
			if (obj) {
				//script("alert('logon');");
				on("<div class=\"xhrAction\" data-url=\""+ev+"\">logon</div>");
			} else {
				on(
					"<html><head><title>Logon</title></head>"
					+"<body onload=\"desv();\">"
					+"<p>Desviando..."+obj+" "+param("obj")+"</p>"
					+"<script>"
					+"  function desv() {"
					+"  	window.location='"+ev+"&referer='+escape(window.location);\n"
					+"  }"
					+"</script>"
					+"</body></html>\r\n\r\n"
				);
			}
		}
		return true;
	}
	//***************************************
	public void sair() {
		pref.grava();
		String url = getValUrl();
		
		//String ref = (String)ped.ped.get("referer");
		String ref = ped.getString("referer");
		
		script("window.location = '"+url+"?op=sair"
			+"&segs="+data.ms()
			+"&referer="+str1.Escape(str.vazio(ref)?ped.raizWeb+this.getClass().getName()+".class":ref)
			+"';"
		);
	}
	//***************************************
	public String getValUrl() {
		String a = Guarani.getCfg("valClass");
		if (a != null) {
		} else if (ped.msie) {
			a = Guarani.getCfg("ValWin");
		} else {
			a = Guarani.getCfg("ValLinux");
		}
		if (a==null) {
			a = "br/org/guarani/servidor/vUsu.class";
		}
		String b[] = str.palavraA(a+"~2~20","~");
		String url = b[0];
		//tempoVal = b[1];
		//tempoDurVal = b[2];
		if (url.indexOf("://")==-1 && url.charAt(0)!='/') {
			url = ped.raizWeb+url;
		}
		if (url.indexOf("@h@")!=-1) {
			//porta deve ser colocada no endereço
			url = str.troca(url,"@h@",str.leftAt(getHost()+":",":"));
		} 
		//ogs.grava("valClass="+url);
		return url;
	}
	//***************************************
	public void prefInit() {
		//PREFERENCIA DOS USUáRIOS
		pref = prefs.getPrefs(usu);
		//testa cookies por parâmetros a guardar
		if (prefRaiz==null) {
			prefRaiz = cl+(getBase()!=null?"."+getBase():"");
			prefRaizC = "set_"+cl;
		}
		//on("<hr>"+data.ms()+" "+ped);
		String co = ped.getCookie(prefRaizC);
		if (!str.vazio(co)) {
			//logs.grava("setar="+co);
			pref.setar(this,co);
			//exclui o cookie
			ped.setCookie(prefRaizC,"; expires=Sun, 11-Jan-2004 19:14:07 GMT");
		}
		
		if (op.equals("pref")) {
			pref();
		}
		//userInterno = "~"+usu.toLowerCase()+"~";
	}
	
		
	//***************************************
	public String getBase() {
		return null;
	}		
	
	//***************************************
	public void prefPut(String ch,String v) {
		pref.put(this,ch,v);
	}
	//***************************************
	public String prefGet(String ch,String padr) {
		return pref.get(this,ch,padr);
	}
	//***************************************
	public String prefGet(String ch) {
		return pref.get(this,ch);
	}
	//***************************************
	public void prefJS(xmlTag x,String ch) {
		//atrs
		for (Enumeration e=x.atr.keys();e.hasMoreElements();) {
			String c = (String)e.nextElement();
			o(prefs.cookDel+(str.vazio(ch)?"":ch+".")+c+"="+x.atr.get(c));
		}
		//subtags
		for (int i=0;i<x.size();i++) {
			xmlTag x1 = x.get(i);
			prefJS(x1,(str.vazio(ch)?"":ch+".")+x1.nome);
		}
	}
	//***************************************
	public void pref() {
		String raiz = param("raiz");
		if (str.vazio(raiz)) {
			raiz = prefRaiz;
		} else {
			if (raiz.charAt(0)=='/') {
				raiz = raiz.substring(1);
				if (!str.vazio(raiz) && raiz.charAt(0)=='.') {
					raiz = raiz.substring(1);
					raiz = cl+(str.vazio(raiz)?"":"."+raiz);
				}
			} else {
				raiz = prefRaiz+"."+raiz;
			}
		}
		prefs	pr = prefs.getPrefs(usu);
		xmlTag x = pr.getX(this,raiz);
		if (x!=null) {
			prefJS(x,"");
		} else {
			on(	"nada configurado em "+prefRaiz+"."+param("raiz"));
		}
	}
	//*****************************************//
	public String id() {
		return "PagV";
	}
	//*****************************************//
	public void msg() {
		super.msg(usu);
	}
	//*****************************************//
	public PagV() {
		super();
	}
	//*****************************************//
	public boolean doGrupo(String g) {
		if (usuario==null || !usuario.valido() || str.vazio(g)) {
			return false;
		}
		/*if (grS==null) {
			Hashtable u = (Hashtable)sessao.get("usu");
			if (u==null) {
				return false;
			}
			grS = ","+u.get("_XXgruposS")+",";
		}
		*/
		return usuario.gruposS.indexOf(","+g+",")!=-1;
	}
	//*****************************************//
	public boolean run(Pedido ped) {
		
		long t = data.ms();
		//é retorno apos validação = recupera dados...
		if (ped.getString("_retorna_")!=null) {
			ped.recuperaAmbiente();
		}
	
		super.run(ped);

		//vars instancia
		sessao = ped.getSessao();
		ip = sessao.getIp();
		ids = sessao.getId();
		usuario = sessao.getUsuario();
		//logs.grava("usu="+usuario.valido()+" "+usuario);

		if (ped.naoSessao()) {
			return true;
		} else {
			//logs.grava("logon="+usuario.valido());
			if (usuario!=null && usuario.valido() && !usuario.validoS()) {
				cab("Limite usuários!!");
				on("<h1>Limite de usuários do servidor foi atingido...</h1><p>Sua sessão foi invalidada.</p>");
				rodap();
				usuario.invalida();
				return false;
			} 
			if (usuario==null || !usuario.valido()) {
				//logs.grava("usuario="+usuario);
				if (ped.x509Val()) {
					validaX509();
				} else if (valida()) {
					//pedido val aceita e será executada...
					return false;
				} else {
					//pedido val não aceito (já em curso) tem um prazo em algum lugar
					//não aceita + de 1 val em x tempo (por sessão?)
					try {Thread.sleep(3000);} catch (Exception e) {};
					usuario = sessao.getUsuario();
					if (usuario==null || !usuario.valido()) {
						valida();
					}
				}
			}
		}

		//teste final
		if (usuario==null || !usuario.valido()) {
			if (logon) {
				usu = "?";
				logs.grava("sessao","FALHOU validacao!! "+ped);
				cab("falhou validacao!!");
				rodap();
			} else {
				usu = null;
			}
			return false;
		}

		//ultimo acesso do usuário: rever ideal seria no fim do pedido?
		usuario.setDataA(t);
		
		//seta pedido para mostrar erros
		//logs.grava("dog="+usuario.valido());
		ped.debug = doGrupo("dev")?2:1;
		
		usu = usuario.getNome().toLowerCase();
		//logs.grava("dog="+usu);
		
		prefInit();
		//logs.grava("dog=pref-"+usu);
		
		return true;

	}
	//*****************************************
	public final void rodap() {
		ped.on("<center><hr class=hrPadrao>"
			+"<font size=1><b>"+opC("cliente")+" x</b></font>"
			+"<br><b><font size=1>"
			+"<a href=\"http://www.3wsistemas.com.br/?classe="
			+this.getClass().getName()
			+"&cop="+id()
			+"\">"+opC("setor")+"</a></font></b>"
		);
		if (doGrupo("dev") && cl!=null) {
			String a = str.troca(cl,".","\\")+".java";
			on(" - <a href=javascript:chatAbre()>CHAT</a>"
				+"<font size=1> ("+((System.currentTimeMillis()-tempo))+")"
				+"<br>"
				//+"<a href='pprsjv:\\\\sbbir001\\bluej\\Proj\\Aplicacao\\"+a+"'>"+a+"</a>"
				//+" - <a href='pprsjv:pub:"+a+"'>Pub</a>"
				+"<a href=javascript:objNav(document);>nav</a>"
			);
		}
		ped.on("</center></body>");
		ped.on("</html>\r\n\r\n");
	}

	/* ****************************************
	private boolean eUsu(String s) {
		return s.indexOf(userInterno)!=-1;
	}
	//*****************************************
	public final void mval() {
		ped.on("<font size=2>"+ped+", acesso OK.</font>");
	}
	//*****************************************
	public final boolean adm(int nv) {
		boolean r=false;
		if (adm==null) return false;
		for (int i=0;i<=nv;i++) {
			if (adm[i]) {
				r = true;
				break;
			}
		}
		return r;
	}
	*/
	//*****************************************
	// retorna parametro do pedido e se não 
	// existe parametro da sessão do usuário
	public String paramS(String v,String p) {
		if (paramSes==null) {
			paramSes = (Hashtable)sessao.get("__paramSes");
			if (paramSes==null) {
				paramSes = new Hashtable();
				sessao.put("__paramSes",paramSes);
			}
		}
		String r = param(v);
		if (r==null) {
			r = (String)paramSes.get(v);
		} else {
			//guarda parametro usuario
			paramSes.put(v,r);
		}
		return (r==null?p:r);
	}
	//*****************************************
	public void paramSPut(String v,String p) {
		paramS(v);
		paramSes.put(v,p);
	}
	//*****************************************
	public String paramS(String v) {
		return paramS(v,null);
	}
	//*****************************************
	public String getHost() {
		String h = (String)ped.ped.get("x-forwarded-host");
		return h==null?(String)ped.ped.get("host"):h;
	}
	
}
