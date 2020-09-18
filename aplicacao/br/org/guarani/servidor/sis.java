package br.org.guarani.servidor;

import java.util.*;
import java.io.*;
//import java.net.*;

import br.org.guarani.servidor.*;
import br.org.guarani.util.*;
//import bd.*;
import br.org.guarani.bd.*;
import pwwws.*;
import br.org.guarani.loader.op;
import br.org.guarani.loader.opcaoC;

//***************************************
//***************************************
public class sis extends PagV {
	static String ipeC; //ip modem internet
	static String ipL[]; //tabela de ip's locais
	static Hashtable hPrefs = new Hashtable();
	static long tabj=(long)0;
	static Hashtable tabjH=new Hashtable();
	static String dth = str.leftAt(data.strSql()," ");
 
	protected static opcaoC opC; // = new opcaoC();
	private static boolean ca = false; 

	public Hashtable dados = new Hashtable();

	public int menu=1;

	private String op1;
	public String base,defs;
	//Dados dad;
	public usuario sisUsu;
 
	//config do sistema
	static Class menuSis;
	//static String estilos;
 
	/* //controle acesso
	static boolean acessoTest = false;
	static Class acessoClass;
	*/
	public acesso acessos;
 
	//vieram de arrasto
	public base dad;
	//public String tab1Menu=null;
		
	boolean cabCImpr = false;
	public String cabBD;
	
	String clUrl;
	//***************************************
	public void script(String s) {
		if (false && obj) {
			logs.grava("Ignorando script: "+s);
		} else {
			on("<script>"+s+"</script>");
		}
	}
	//***************************************
	private String copiaS() {
		String r = "";
		String v[] = str.palavraA(str.trimm(""+opC.get("sists"),","),",");
		for (int i=0;i<v.length;i++) {
			r += "<b>"+v[i]+"</b> "+opC.get(v[i]+"-nome")+"<br>";
		}
		return r;
	}
	//***************************************
	public void copia() {
		on("<table border=1>"
			+"<tr><td>Cliente:<td>"+opC.get("cliente")
			+"<tr><td>Sistemas:<td>"+str.troca(""+opC.get("sists"),","," ")
			+"<tr><td>...<td>"+copiaS()
			+"<tr><td>Id:<td>"+opC.get("wwws.cop")
			+"<tr><td>Id:<td>"+opC.get("cp-css")
			+"</table>"
		);
	}
	//***************************************
	public void prefInit() {
	}
	//***************************************
	public void editProp() {
	}
	//***************************************
	public void prefInitSIS() {
		//PREFERENCIA DOS USUáRIOS
		pref = prefs.getPrefs(usu);
		//testa cookies por parâmetros a guardar
		//logs.grava("init pref="+this.getClass().getName()+" == "+dad);
		prefRaiz = cl+"."+dad.nomeInterno;
		prefRaizC = "set_"+cl;
		
		//on("<hr>"+data.ms()+" "+ped);
		String co = ped.getCookie(prefRaizC);
		if (!str.vazio(co)) {
			//logs.grava("setar="+co);
			pref.setar(this,co);
			//exclui o cookie
			ped.setCookie(prefRaizC,"; expires=Sun, 11-Jan-2004 19:14:07 GMT");
		}
		
		//userInterno = "~"+usu.toLowerCase()+"~";
	}
	
	//***************************************
	public boolean run(Pedido pd) {
		return run(pd,true);
	}
	//***************************************
	public boolean run(Pedido pd,boolean mais) {
		long aaa = data.ms();
		if (!super.run(pd)) {
			return false;
		}
		if (usu==null || usu.equals("?")) {
			return true;
		}
		
		//DEFINE A BASE
		clUrl = cl;
		if (cl.indexOf(".")!=-1) {
			cl = str.substrRat(cl,".");
		}
			
	
	
		base = getBase();
		if (base==null) {
			on("<h1>ERRO, não encontrei base para "+cl+"</h1>");
			return false;
		}
		//logs.grava("base="+base);
		dad = new base(this,base);
		String bl = dad.bloqueada();
		if (!str.vazio(bl) && bl.indexOf("base.todas():")==-1) {
			ped.on("<hr><h3>"+base+": Base de DADOS bloqueada!</h3>"
				+bl
				+"<hr>"
			);
			return false;
		}
	
		// - provocado por vencimento prg
		//@dt@
		if (dad.erro) {
			logs.grava("jdbc","1a tentativa falhou, vai pra 2a "+dad.sErro);
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
			}
			//tenta novamente...
			dad = new base(this,base);
			if (dad.erro) {
				ped.on("<hr><h3>Erro servidor dados: Base '"
					+base+"' não existe ou corrompida!</h3>"
					+dad.sErro
					+"<hr>"
				);
				return false;
			}
		}
		//inicializa acesso da base
		dad.initAcesso();
		acessos = dad.acesso;
		
		//PREFERENCIAS
		prefInitSIS();
		if (op.equals("pref")) {
			pref();
			return true;
		}

  
		//sisUsu = br.org.guarani.bd.usuario.get(ped,usu);
		Object ou = Usuario.get(sessao.getId(),usu);
		//logs.grava("ou="+ou.getClass().getName());
		sisUsu = (usuario)ou;
		if (sisUsu==null) {
			logs.grava("seguranca","validado mas não existe??!! usu="+usu);
		}

		//exec antes do cab
		if (img()) {
			return true;
		} else if (op.equals("rel")) {
			rel();
			return true;
		}
		
		if (!mais) return true;
			

		if (op.equals("prefsG")) {
			prefsG();
		}

		/*testa cookies por parâmetros a guardar
		co = ped.getCookie("set_"+dad.nomeInterno);
		if (!str.vazio(co)) {
			//logs.grava("setar="+co);
			pref.setar(dad.nomeInterno,co);
			//exclui o cookie
			ped.setCookie("set_"+dad.nomeInterno,"; expires=Sun, 11-Jan-2004 19:14:07 GMT");
		}
		*/
		
		menu=str.inteiro(ped.getString("menu"),1);
		background = "";
		css = "";

		//header da pagina
		boolean popup=param("popUp")!=null;
		//cabecalho
		estilo = cl+" "+cl+"_"+param("__tabela")+" "+cl+"_"+param("__modo");
		if (obj) {
			menu = -2;
		} else if (menu==-1) {
			on("<html><head><title></title></head>");
			if (devCSS()) {
				incluiJs("/jsCSSEditor/jsCSSEditor.js");
			}
			on("<body class=\""+estilo+"\" "+(devCSS()?"onClick=jsCSSEditor(this,event);> ":">"));
		} else if (menu>-1) {
			if (opC.get(cl+"-icone")!=null) {
				icon = ""+opC.get(cl+"-icone");
			}
			cab(""+opC.get(cl+"-nome"),null);
			//on(""+ped.ped);
			if (popup) {
				on("<table id=\"tamPopUp\" class=centra width=\"100%\" height=\"100%\" align=\"center\">"
					+"<tr><td align=\"center\" valign=\"middle\">"
				);
			}
		}

		//javascript & estilo
		if (menu>-2) {
			incluiJs("jan.js");
			incluiJs("redes.js");
			if (dad.def.get("javaScript")!=null) {
				incluiJs(dad.def.get("javaScript"));
			}
			incluiCss(acessos.getUsu("prefs.estilo"),false);
			incluiCss(cl+"_"+dad.nomeInterno+".css",true);
			if (!obj) {
				script("var baseWeb='"+dad.nomeInterno+"';");
			}
		}

		//cabecalho cliente
		campoTabJ2();
		if (menu>0 && param("vlogo")==null) {
			cabC();
		}

		//https://pt-coord071.pt-alergs.br/ap/itbz.class?menu=-2&op=campo&__tabela=Noticia&__campo=Anexo&op1=baixa&_C_Noticia_=40439&chUnica=0
		
		//menu das tabelas do atual sistema
		String tb = ped.getString("__tabela","");
		if (menu>0) {
			menuSis mt=acessos.menu();
			if (mt.erro) {
				on("<h2>Erro montando o menu...</h2>"+mt.sErro);
			}
			if (!str.vazio(op) && op.equals("tabela") && str.vazio(tb)) {
				script("window.location='?op=tabela&__tabela="+dad.get("_tab1")+"';");
			} else {
				mt.mostra(this,"__tabela="+(str.vazio(tb)?(String)dad.get("_tab1"):tb));
				exec();
				mt.fim(this);
			}
		} else {
			if (op.equals("tabela")) {
				try {
					tabela();
				} catch (Exception e) {
					on("<hr>"+str.erro(e));
				}
			} else {
				exec();
			}
		}

		if (obj) {
		} else if (popup) {
			on(
				"</table>" //para resize
				+"</body>\r\n\r\n</html>\r\n\r\n"
			);
		} else if (menu>-2) {
			rodap();
		}
		//log para análise de costume uso
		logs.grava("acesso",(data.ms()-aaa)+"\t"+cl+"\t"+usu+"\t"+param("__modo")
			+"\t"+ped.queryString
			+"\t"+str.substrAt(""+ped.ped.get("referer"),"?")
		);
		
		//ped.close();
		
		return true;
	}
	//***************************************
	public String getBase() {
		Object o = opC.get(cl+"-base");
		//logs.grava(cl+"-base  "+o);
		String b;
		if (param("base")!=null) {
			//passa como param - sis_NomeBase
			b = param("base");
		} else if (o==null) {
			b = cl;
		} else {
			b = ""+o;
		}
		return b;
	}
	
	//***************************************
	public sis() {
		super();
		try {
			cl = str.substrRat(this.getClass().getName(),".");
			//logs.grava(cl+" "+this.getClass().getName());
		} catch (Exception e) {
		}
		initSis();
	}

	//***************************************
	public static void initSis() {
		if (ca) {
			return;
		}
		opC = new opcaoC();
		opC.initA(null);
		Setor = ""+opC.get("cliente");//.toString();
		if (!Web.equals(opC.get("3").toString())) {
			System.exit(12);
		}
		ca = true;
	}
	//***********************************
	public boolean acessoGravacaoIp() {
		String acessoE = sis.opC("acessoExterno");
		if (acessoE==null) {
			acessoE = "ro";
		}
		if (!ipLocal()) {
			//acesso externo
			if (!acessoE.equals("rw")) {
			//if (acessoE.equals("ro")) {
				return false;
			}
		}
		return true;
	}
	//***********************************
	public boolean ipLocal() {
		if (ped==null) {
			logs.grava("seguranca","sis.ipLocal(): ped="+ped
				+" atualização da base sem ped?");
		}
		//logs.grava("ped.ip="+ped.ip);
		boolean r = false;
		if (ipL==null) {
			String s = sis.opC("ipLocal");
			if (str.vazio(s)) {
				ipL = new String[0];
			} else {
				//na copia é permitido proibir alteração pelo .conf do servidor gurarani
				//   adicionando ponto. 
				if ((" "+s+" ").indexOf(" . ")==-1) {
					s += str.trocaTudo(" "+Guarani.getCfg("ipLocal",""),"  "," ");
				}
				logs.grava("seguranca","IPlocais: "+s);
				ipL = str.palavraA(s," ");
			}
		}
		String s = (String)sessao.get("ipLocal");
		if (s!=null) {
			return s.equals("true");
		}
  
		if (ipL.length<1) {
			//sem definição/restrição
			r = true;
		} else {
			for (short i=0;i<ipL.length;i++) {
				//ogs.grava("LOCAL? "+ped.ip+" "+ipL[i]);
				if (ipL[i].indexOf(".")!=-1 && str.equals(ped.ip,ipL[i])) {
					r = true;
				}
			}
		}
		sessao.put("ipLocal",""+r);
		return r;
	}
	//***********************************
	public static BufferedReader arq(String nome) {
		op o = opC.get(nome);
		if (o==null) {
			return null;
		}
		return o.arq();
	}
	//***********************************
	public static boolean opCExiste(String nome) {
		return opC.get(nome)!=null;
	}
	//***********************************
	public static String opC(String nome) {
		op o = opC.get(nome);
		if (o==null) return "";
		return o.toString();
	}
	//***************************************
	public String getCabC() {
		op o = opC.get(cl+"-cabC");
		if (o==null) {
			return null;
		} else {
			return ""+o;
		}
	}
	//***************************************
	public boolean cabC() {
		if (cabCImpr) return false;
		cabCImpr = true;
		String c=getCabC();
		if (c==null) {
			String no = ""+opC.get(cl+"-nome");
			c = "<table><tr><td width=100>"
				+"<img  src=?op=logo3 align=left>" 
				+"<td valign=middle><font size=4><b>"
				//+no+" ("+no.length()+")</b></font>"
				+no+"</b></font>"
				+"<br>D E M O N S T R A Ç Ã O"
				+"</table>"
			;
		}
		//@dt1@
		on(c);
		//menu de sistemas... 
		if (param("print")==null) {
			String d = ""+opC.get("sists");
			on("<!-- "+d+" -->");
			script("menuSis('"+cl+"','"+acessos.acessoSists(d)+"','"+d+"');");
		}
		return true;
	}
	//***************************************
	public void reset() {
		dad.reset();
		on("<h2>RESET....</h2>");
		script("setTimeout('window.location=\""+cl+".class\";',1000);");
		return;
	}
	//***************************************
	public void inicio() {
		//String tbp = ""+opC.get(cl+"-tabelaP");
		//if (tbp==null) return;
		String pdr = (String)dad.get("_tab1");
		if (pdr==null) {
			on("Acesso Negado..."
				+(doGrupo("dev")?" cl="+cl:"")
			);
			return;
		} 
		tabela(pdr);
	}
	//***************************************
	public void campo() {
		
		try  {
		
			if (!param("op1","").equals("baixa") && param("obj")==null) {
				incluiJs("valida.js");
			}
			String tb = param("__tabela");
			if (str.vazio(tb)) {
				on("ERRO campo.exec(): tabela não informada...");
				return;
			}
			tabelaHtml th = tabelaHtml.getTabela(this,tb);
			
			//posiciona?
			th.posiciona(param("_"+th.tDef.chavePrimaria+"_"));
			

			String cm = param("__campo");
			if (str.vazio(cm)) {
				on("ERRO campo.exec(): campo não informado...");
				return;
			}
	  
			campo c = th.getC(cm); //(campo)th.camposH.get(cm);
			if (c==null) {
				on("ERRO campo.exec(): campo '"+cm+"' não EXISTE na tab "
					+tb
				);
				return;
			}
	  
			c.exec();
		
		} catch (Exception e) {
			logs.grava("erro","sis.campo(): "+str.erro(e));
		} catch (Throwable e) {
			logs.grava("erro","sis.campo(): "+str.erro(e));
		}
  
	}
	//***************************************
	public void tabela() {
		String tb = param("__tabela");
		if (tb!=null) {
			tabela(tb);
		}
	}
	//***************************************
	public void tabela(String tbe) {
		//logs.grava("tbe="+tbe);
		tabelaHtml tb = tabelaHtml.getTabela(this,dad,tbe);
		if (tb==null) {
			on("ERRO: tabela não existe: "+tbe);
		} else if (true || !tb.erro) {
			String cm = param("__campo"); 
			String md = param("__modo","P");
			try {
				//n("sis.tabela...");
				//ogs.grava("sis.tabela...");
				tb.exec(md,cm);
			} catch (Exception e) {
				on("<hr>t"+str.erro(e));
			}
		} else {
			on("sis.tabela(nome) ERRO: "+tb.sErro);
		}
	}
	//***************************************
	public void rel() { 
		String dr =  tabelaRelPdf.dirTmp();
		String rel = dr+"/"+param("rel");
		//param("rel")
		mandaArq(rel,param("nome")); //não funciona: ,URLEncoder.encode(param("nome")));
		File v[] = new File(dr).listFiles();
		for (int i=0;i<v.length;i++) {
			if (data.ms()-v[i].lastModified()>1200000) {
				v[i].delete();
			}
		}
	}
	//***************************************
	public void mandaArq(String arq) { 
		mandaArq(arq,str.substrRat(arq,"/"));
	}
	//***************************************
	public void mandaArq(String arq,String nome) { 
		mandaArq(arq,nome,false);
	}
	//***************************************
	public void mandaArq(String arq,String nome,boolean attach) { 
		/*File f = new File(arq);
		if (false && !f.exists()) {
			cab("ERRO:");
			incluiCss(acessos.getUsu("prefs.estilo"),false);
			on("<h2>O arquivo não encontrado!</h2>"
				+(doGrupo("dev")?arq:nome)
			);
			rodap();
			logs.grava("Arquivo p/mandar não existe: "+arq);
			return;
		}
		*/
		ped.mandaArq(arq,nome,attach);
	}
	//***************************************
	public boolean img() { 
		if (str.equals(op,"logo3")) {
			op o = (op)opC.get(op);
			if (o!=null) {
				ped.img(o.buf);
			} else {
				logs.grava("erro","falta imagem op="+op);
			}
			return true;
		} else if (str.equals(op,"logo")) {
			op o = (op)opC.get(cl+"-"+op);
			if (o!=null) {
				ped.img(o.buf);
			} else {
				logs.grava("erro","falta imagem op="+op);
			}
			return true;
		} else if (op.equals("logo")) {
			op o = (op)opC.get(param("logo"));
			if (o!=null) {
				ped.img(o.buf);
			} else {
				logs.grava("erro","falta imagem op="+op);
			}
			return true;
		}
		return false;
	}
	//***************************************
	public String id() {
		return opC.get("wwws.cop")+"-"+opC.get("wwws.copv");
	}
	//***************************************
	public void uso() {
		//long m = 1000*60*60*24;
		//short i=12531; //(short)((data.ms()/m)+267);
		on("<table border=1><tr><td>"+
			str.troca(str.troca(str.troca(
				str.trimm(""+tabjH,"{}")
				,",","<tr><td>"),"=","<td>"),"-","<td>")
			+"</table>"+"@dt@"+" "+opC.get("func")
			+"<br>"+opC.get("wwws.cop")
			+"<br>"+opC.get("wwws.copv")
			+"<br>"+ipeC
			//+"<br>"+(data.ms()/m)
			//+"<br>"+i
			//+"<br>"+data.strSql(i*m)+" "+opcaoC.vex(3)
		);
	}
	//***************************************
	public String prefRegs(tabelaHtml tb,String cod) {
		if (tb.tDef.estatRegs<1) return "";
		String ch = tb.tDef.nome+".regs";
		//logs.grava("regs: "+ch);
		String va = prefGet(ch);
		//logs.grava("regs: "+ch+" va="+va);
		if (cod==null) {
			//está pedindo a lista...
			return va;
		}
		//lista vazia
		if (va==null) {
			prefPut(ch,","+cod+",");
			return null;
		} 
		
		int p = va.indexOf(","+cod+",");
		if (p==-1) {
			va += cod+",";
			//limitar tamanho
			String v[] = str.palavraA(str.trimm(va,","),",");
			while (v.length>tb.tDef.estatRegs) {
				va = ","+str.substrAt(va.substring(1),",");
				v = str.palavraA(str.trimm(va,","),",");
			}
			prefPut(ch,va);
			//atualiza contador incluidos
			int c = str.inteiro(prefGet(ch+"_nI"),0);
			prefPut(ch+"_nI",""+(c+1));
			return null;
		}
		
		//mudar a ordem..
		int c = str.inteiro(prefGet(ch+"_nA"),0);
		prefPut(ch+"_nA",""+(c+1));
		va = str.troca(va,","+cod+",",",")+cod+",";
		prefPut(ch,va);
		return null;
	}
	//***************************************
	void prefsG() {
		String o1 = param("op1");
		String s = param("v");
		if (s.equals("0")) {
			s = dad.def.get(o1);
		}
		acessos.putUsu("prefs."+o1,s);
		op = "inicio";
	}
	//***************************************
	public void prefs() {
		select me = new select("menuSis"
			,"0==Padrao\nH==Horizontal\nV==Vertical");
		me.ops = "onChange=prefs(this);";
		select es = new select("estilo",
			"0==Padrao\ndados.css==Verde\nlab.css==Preto&Branco");
		es.ops = "onChange=prefs(this);";
		on(
				"<script>"
			+"document.title = 'Preferências do Usuário|'+document.title;"
			+"function prefs(ob) {"
			+" var e = leftAt(''+window.location,'?')+'?op=prefsG';"
			+" e += '&op1='+ob.name+'&v='+ob.value;"
			+" top.opener.location = e;"
			+"}"
			+"</script>"
			+"<table id=tamJ class=prefsU>"
			+"<tr><th colspan=2 class=prefsU>Preferências do Usuário"
			+"<tr><td class=prefsU>Menu"
				+"<td>"+me.select(
					acessos.getUsu("prefs.menuSis").indexOf("V")==-1?"H":"V")
			+"<tr><td class=prefsU>Tema"
				+"<td>"+es.select(acessos.getUsu("prefs.estilo"))
			+"</table>"
		);
	}
	//***************************************
	public void campoTabJ2() {
		String d = str.leftAt(data.strSql()," ");

		//mudou dia - reinicia estat clicks
		if (!d.equals(dth)) {
			//campoTabJ3();
			dth = d;
			tabjH = new Hashtable();
		}

		//acumula clicks
		String ch = ip+"-"+usu;
		acessoEst i = (acessoEst)tabjH.get(ch);
		if (i==null) {
			i = new acessoEst();
			tabjH.put(ch,i);
		}
		i.i++;
		i.dt = data.ms();

		//atualiza na pag 3ws?
		long hl = str.longo(""+opC.get("monitT"),1);
		if (data.ms()-tabj<1000*60*60*hl) {
			//logs.grava("deveria sair...");
			return;
		}
		tabj = data.ms();
		campoTabJ3(); 
	}
	//***************************************
	private void campoTabJ3() {
		//colocar tudo em Task...
		hora hr = new hora(this);
		Thread tr = new Thread(hr);
		tr.start();
  
	}
	//**********************************************
	//**********************************************
	public class acessoEst {
		int i;
		public long dt;
		public String toString() {
			return ""+i+"<td>"+str.troca(data.strSql(new Date(dt)),"-","/");
		}
	}
}
