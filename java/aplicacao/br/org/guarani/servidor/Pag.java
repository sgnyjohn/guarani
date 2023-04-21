/*
		Objeto base para página WEB
	*/

package br.org.guarani.servidor;

import java.lang.reflect.*;
import java.util.*;
import java.io.*;

import br.org.guarani.servidor.*;
import br.org.guarani.util.*;
import br.org.guarani.loader.op;
import br.org.guarani.loader.loaderConf;//opcaoC;

//*****************************************//
//*****************************************//
public class Pag extends Prg {
	//private static String Cliente = "3WS Sistemas";//"3WS Sistemas";
	//private static String Setor = "Demonstração";
	private static loaderConf opC; // = new opcaoC();
	private static boolean ca = false; 
 
	//config static
	//protected static boolean iStatic = false;
	//protected static boolean devCSSA = false;

	protected Hashtable js = new Hashtable();
	//public Pedido ped;
	//public String op,cl="?";
	public String cl="?";
	protected long tempo;
	public int margem=10;
	public String bgcolor="ffffff"; //"abcde";
	public String background="/imagens/fundo.gif";
	//public String fonte="Arial";
	public String inicio="inicio";
	public String css="intranet.css";
	private String camposFixos="";
	private String camposFixosA[];
	private Hashtable hArq = new Hashtable();
 
	public String estilo,icon="favicon.ico";
	//mensagens
	static Hashtable msg;
	GravadorHttp gb; //guarda destino pedido anterior
	boolean chuAnt;
	
	//pagina tipo obj não tem js, css, etc cuja url tem &obj=1
	public boolean obj = false;
	
	public static String dirR,dirJs,dirCss,dirImagens;
	public static String dirLJs,dirLCss,dirLImagens;
	
	public String meta = "";
	
	public String charSet = Guarani.getCfg("charset","iso-8859-1");
	boolean cabFoi = false;
	Hashtable cabH = new Hashtable();

	//**************************************************
	public String paramC(String s) {
		return paramC(s,null);
	}
	//**************************************************
	public String paramC(String s,String pdr) {
		String v = ped.getString(s);
		if (v==null) {
			v = ped.getCookie(s);
			if (v==null) {
				v = pdr;
			}
		}
		return v;
	}
	//**************************************
	// dados copia
	public static String opC(String s) {
		return ""+opC.get(s);
	}
	public boolean opCimg(String ch) {
		op o = null;//(op)opC(ch);
		if (o!=null) {
			ped.img(o.buf);
		} else {
			logs.grava("erro","falta imagem op="+ch);
			return false;
		}
		return true;
	}
	//***********************************
	public static BufferedReader arq(String nome) {
		op o = null;//opC(nome);
		if (o==null) {
			return null;
		}
		return o.arq();
	}
	//***********************************
	public static boolean opCExiste(String nome) {
		return opC.get(nome)!=null;
	}
	//***************************************
	public static void initSis() {
		if (ca) {
			return;
		}
		//opC = new opcaoC();
		opC = new loaderConf();
		//opC.initA(null);
		//Cliente = ""+opC.get("cliente");//.toString();
		//Setor = ""+opC.get("setor");
		/*if (!Web.equals(opC.get("3").toString())) {
			System.exit(12);
		}
		*/
		ca = true;
	}
	//**************************************
	//retorna digito para controle de verssao javascript css
	public void print(String s,Object... o) {
		on(String.format(s,o));
	}
	//**************************************
	//retorna digito para controle de verssao javascript css
	class arqDig {
		private String dig;
		long data;
		int tam;
		File f;
		arqDig(String arq) {
			f = new File(arq);
		}
		String dig() {
			if ( !f.exists() ) {
				dig = "Nexiste";
				logs.grava("arqDig: não existe "+f
					+str.erro(new Exception("arqDig1"))
				);
			} else if ( f.lastModified() != data || f.length() != tam ) {
				arquivo1 a = new arquivo1(""+f);
				dig = a.md5sum();
			}
			return dig;
		}
	}
	static Hashtable arqDig = new Hashtable();
	public String arqDig(String arq) {
		arqDig a = (arqDig)arqDig.get(arq);
		if ( a == null ) {
			a = new arqDig(arq);
			arqDig.put(arq,a);
		}
		return a.dig();
	}
	//**************************************
	//desvia resultado para arquivo
	public boolean setOutArq(String arq) {
		//ogs.grava("ped set="+arq);
		if (arq == null) {
			if (gb == null) {
				//on("<h1>ERRO, não ha gravador de Pedido a retornar...</h1>");
				return false;
			}
			if (ped.out!=null) {
				//ogs.grava("ped arq close");
				ped.out.close();
			}
			ped.chunked = chuAnt;
			ped.out = gb;
			ped.outArq = arq;
			gb = null;
		} else {
			if (gb!=null) {
				on("<h1>ERRO, pedido já foi desviado para arquivo...</h1>");
				return false;
			}
		
			//cria gravador ARQ
			GravadorHttp g = null;
			try {
				g = new GravadorHttp(new FileOutputStream(new File(arq)));
			} catch (Exception e) {
				logs.grava("ped arq er"+e);
				return false;
			}
			if (g==null) {
				logs.grava("ped arq er null");
				return false;
			}
		
			//seta novo
			chuAnt = ped.chunked;
			ped.chunked = false;
			ped.outArq = arq;
			gb = ped.out;
			ped.out = g;
		
		}
		return true;
	}
	
	//**************************************
	public void outCodHtml(String s) {
		for (int i=0;i<s.length();i++) {
			char c = s.charAt(i);
			if (c=='<') {
				ped.o("&lt;");
			} else if (c=='>') {
				ped.o("&gt;");
			} else {
				ped.o(s.substring(i,i+1));
			}
		}
	}
	//**************************************
	public String imagem(String s) {
		return "\""+dirImagens+"/"+s+"\"";
	}
	//**************************************
	public boolean ipLocal() {
		return Guarani.getCfg("ipLocal","").indexOf(ped.getIp())!=-1;
	}
	//**************************************
	//enchambra - deveria ser não estático da sessao
	public void msg() {
		msg(ped.getSessao().getId());
	}
	public void msg(String ch) {
		if (msg==null || ch==null) {
			return;
		}
		Hashtable h = (Hashtable)msg.get(ch);
		//logs.grava("msg ch="+ch+" = "+h);
		if (h==null || h.size()==0) {
			return;
		}
		int t=h.size();
		for (Enumeration e = h.keys(); e.hasMoreElements();) {
			String s = ""+e.nextElement();
			String msg = ""+h.get(s);
			if (str.equals(msg,"&(")) {
				String s1 = str.substrAtAt(msg,"&(",")&");
				script(
					"alert('"+str.substrAt(msg,")&")+"');"
					+s1+";"
				);
			} else {
				alert("MSG: "+msg);
			}
			h.remove(s);
		}
		//alert(h.size()+"");
	}
	//**************************************
	public void msgPut(String idSess,String m) {
		Hashtable h = (Hashtable)msg.get(idSess);
		if (h==null) {
			h = new Hashtable();
			msg.put(idSess,h);
		}
		h.put(h.size()+"",m);
	}
	//**************************************//
	public Pag() {
		super();
		msg = (Hashtable)Guarani.get("msg");
		if (msg==null) {
			msg = new Hashtable();
			Guarani.put("msg",msg);
		}
		initSis();
	}
	//**************************************//
	public HashtableOrd sessoes() {
		return httpSessao.sessoes;
	}
	//**************************************//
	public void alert(String s) {
		alert(s,null);
	}
	//**************************************//
	public void alert(String s,String cmd) {
		script("alert('"+str1.strJava(s)+"');"
			+(cmd==null?"":cmd));
	}
	//**************************************//
	public void script(String s) {
		on("<script>"+s+"</script>");
	}
	//**************************************//
	public String atalhoIgual() {
		String r="";
		for (Enumeration e=ped.h.keys();e.hasMoreElements();) {
			String a = (String)e.nextElement();
			r += "&"+a+"="+param(a);
		}
		return atalho()+"?"+(r.length()==0?"":r.substring(1));
	}
	//**************************************//
	public void paramToClasse(Object ob) {
		for (Enumeration e=ped.h.keys();e.hasMoreElements();) {
			String a = (String)e.nextElement();
			String b = param(a);
			try {
				Field f = ob.getClass().getDeclaredField(a);
				String t = f.getType().getName();
				if (t.equals("java.lang.String")) {
					f.set(ob,b);
				} else if (t.equals("java.lang.Integer")
						|| t.equals("int")) {
					f.setInt(this,str.inteiro(b,-1));
				} else if (t.equals("java.lang.Boolean") 
					|| t.equals("boolean")) {
					f.setBoolean(this,b.equals("true"));
				}
			} catch (Exception e1) {
			}
		}
	}
	//**************************************//
	public void paramToForm() {
		for (Enumeration e=ped.h.keys();e.hasMoreElements();) {
			String a = (String)e.nextElement();
			o("<input type=hidden name="+a+" value=\""+
				param(a)+"\">");
		}
	}
	//**************************************//
	public void paramPut(String nome,String v) {
		if (ped.getString(nome)!=null) {
			paramRemove(nome);
		}
		ped.h.put(nome,v);
	}
	//**************************************//
	public void paramRemove(String nome) {
		String v[] = str.palavraA(nome,",");
		for (int i=0;i<v.length;i++) {
			ped.h.remove(v[i]);
		}
	}
	//**************************************//
	public String param(String nome,String pdr) {
		return ped.getString(nome,pdr);
	}
	//**************************************//
	public String param(String nome) {
		return ped.getString(nome,null);
	}
	//**************************************//
	public Object paramObj(String nome) {
		return ped.h.get(nome);
	}
	//**************************************//
	public void erro(String s) {
		on(">\"</table></option></form><hr>"
			+"<font color=red><b>ERRO: </b>"+s+"</font><hr>"
		);
	}
	//**************************************//
	public void on(String a,String tag) {
		ped.on(a,tag);
	}
	//**************************************//
	public void on(String a) {
		ped.on(a);
	}
	//**************************************//
	public void o(String a) {
		ped.o(a);
	}
	/* *************************************
	public boolean adm(int x) {
		return "172.27.79.18".indexOf(ped.getSessao().getIp())!=-1;
	}
	*/
	//**************************************//
	public void menu() {
		menu(this,"");
	}
	//**************************************//
	public void menu(String ignora) {
		menu(this,ignora);
	}
	//**************************************
	// 2007 nov - procura metodo em extendidas
	public static Method getMethod(Object o,String nome) {
		Class c = o.getClass();
		while (c!=null) {
			Method m[] = c.getDeclaredMethods();
			for (short i=0;i<m.length;i++) {
				if (m[i].getName().equals(nome)) {
					return m[i];
				}
			}
			//logs.grava("c="+c.getName());
			c = c.getSuperclass();
		}
		/*Class vc[] = o.getClass().getInterfaces();//.getDeclaredClasses(); //getClasses();
		logs.grava(o.getClass().getName()+" me="+vc.length);
		for (int x=0;x<vc.length;x++) {
			logs.grava("me="+vc[x].getName());
			m = vc[x].getDeclaredMethods();
			for (short i=0;i<m.length;i++) {
				if (m[i].getName().equals(nome)) {
					return m[i];
				}
			}
		}
		*/
		return null;
	}
	//**************************************//
	private Hashtable menuOp(Object o,String ignora) {
		Method[] m = o.getClass().getDeclaredMethods();
		ignora = " "+str.trimm(ignora)+" ";
		Hashtable a = new Hashtable();
		for (int i=0;i<m.length;i++) {
			String n = m[i].getName();
			//se publico apenas
			if (!Modifier.isPublic(m[i].getModifiers())) {
			} else if (m[i].getParameterTypes().length==0
				&& n.charAt(0)!='_'
				&& ignora.indexOf(" "+n+" ")==-1
				&& (""+m[i].getReturnType()).equals("void")
				){
				a.put(""+a.size(),n); //+"~"+m[i].getReturnType()
			}
		}	
		return a;
	}
	//**************************************//
	public void menuPop() {
		Hashtable a = menuOp(this,"");
		if (a.size()>1) {
			String op = param("op","");
			on("<table class=menuPop");
			for (int i=0;i<a.size();i++) {
				String n = (String)a.get(""+i);
				on("<tr><td class=\"menu_td"+(op.equals(n)?" menu_tdSel":"")+"\" align=center>"
					+"<a class=menu_a href=?op="+n+">"
					+n+"</a>"
				);
			}
			ped.on("</table>"); 
		}
	}
	//**************************************//
	private void menu(Object o,String ignora) {
		Hashtable a = menuOp(o,ignora);
		if (a.size()>1) {
			String op = param("op","");
			on("<table class=menu_table border=1 width=100%>");
			for (int i=0;i<a.size();i++) {
				if (i%5==0) {
					on("<tr>");
				}
				String n = (String)a.get(""+i);
				on("<td class=\"menu_td"+(op.equals(n)?" menu_tdSel":"")+"\" align=center>"
					+"<a class=menu_a href=?op="+n+">"
					+n+"</a>"
				);
			}
			ped.on("</table>"); 
		}
	}
	//**************************************//
	public String atalhoInput() {
		String r="";
		for (int i=0;i<camposFixosA.length;i++) {
			if (ped.getString(camposFixosA[i])!=null) {
				r += "<input type=hidden value=\""
					+ped.getString(camposFixosA[i])
					+"\" name="+camposFixosA[i]+">";
			}
		}
		return r;
	}
	//**************************************//
	public void atalhoFixaCampo(String campos) {
		atalhoFixaCampo(str.palavraA(campos,"~"));
	}
	//**************************************//
	public void atalhoFixaCampo(String campos[]) {
		camposFixosA = campos;
		camposFixos = "";
		for (int i=0;i<camposFixosA.length;i++) {
			if (ped.getString(camposFixosA[i])!=null) {
				camposFixos += "&"+camposFixosA[i]
					+"="+ped.getString(campos[i]);
			}
		}
	}
	//**************************************//
	public String atalho(String op,String tx) {
		return "<a CLASS=atalho href=\""+atalho()+
			"?"+op+camposFixos+"\">"+tx+"</a>";
	}
	public String atalho(String op,String tx,String dest) {
		return "<a class=atalho href=\""+atalho()+
			"?"+op+camposFixos+"\" target="+dest+">"+tx+"</a>";
	}
	public String atalho() {
		//return "/"+str.troca(Classe(),".","/")+".class";
		return "";
	}
	//*****************************************//
	public boolean run() {
		op = param("op","");
		//logs.grava("run pd="+ped);
		incluiJs("func.js");
		incluiJs("funcoes.js");
		obj = ped.getString("obj")!=null;
		dirs();

		/*if (Web==null) {
			Web = (!str.vazio(Guarani.getCfg("Web"))?
				Guarani.getCfg("Web"):"Guarani.org.br Intranet");
			Setor = !str.vazio(Guarani.getCfg("Cliente"))?
				Guarani.getCfg("Cliente"):Setor;
		}
		*/
		tempo = System.currentTimeMillis();
		Classe();
		op = str.seNull(ped.getString("op"),"");
		return run(ped);
	}
	//*****************************************//
	public boolean run(Pedido pd) {

		return true;
	}
	//*****************************************//
	public void cab(String titulo) {
		cab(titulo,titulo);
	}
	//*****************************************//
	public void dirs() {
		if (dirJs!=null) {
			return;
		}
		String dir = ped.dirRoot();
		dirR = dir;
		dirJs = str1.seNulo(ped.getHttpConf("dirJs"),"/js");
		dirLJs = str1.seNulo(ped.getHttpConf("dirLJs"),dir+dirJs);
		dirCss = str1.seNulo(ped.getHttpConf("dirCss"),"/estilos");
		dirLCss = str1.seNulo(ped.getHttpConf("dirLCss"),dir+dirCss);
		dirImagens = str1.seNulo(ped.getHttpConf("dirImagens"),"/imagens");
		dirLImagens = str1.seNulo(ped.getHttpConf("dirLImagens"),dir+dirImagens);
		
	}
	//*****************************************//
	public void js() {
		//dirs();
		//on("<script>//TTTTM sj</script>");
		
		//js e css faltantes no head...
		//inclui outros arqs css js ...
		for (int i=0;i<cabH.size();i++) {
			//on("<!-- "+i+" "+cabH.get(""+i)+" -->");
			ped.o("\n"+cabH.get(""+i));
		}		
		
		/*incluiJs("funcoes1.js");
		incluiJs("dialogo.js");
		incluiJs("jan.js");
		incluiJs("valida.js");
		incluiJs("tecl.js");
		script("_sis.raizWeb='"+ped.raizWeb+"';"
			+"_sis.classeWeb='"+cl+"';"
			+"_sis.dirJs='"+dirJs+"';"
			+"_sis.dirCss='"+dirCss+"';"
			+"_sis.dirImagens='"+dirImagens+"';"
		);
		*/
	}
	//*****************************************//
	public void devCSS() {
		String e = "jsCSSEditor/jsCSSEditor.js";
		if ((new File(dirLJs+"/"+e)).exists()) {
			incluiJs(e);
		}
	}
	//*****************************************//
	public void cab(String titulo,String tit) {
		if (cabFoi) return;
		cabFoi = true;
		on(""
			//+"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">"
			//+"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\">"
			//+"<html>"
			//2020-08 +"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//BR\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
			//2020-08 +"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"
			+"<!DOCTYPE html>"
			+"\n<html>"
			+"\n<head>"
			+"\n<title>"+((titulo==null)?"":titulo)+"</title>"
			+"\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset="+charSet+"\">"
			//2020-08
			+"\n<meta name=\"viewport\" content=\"width=device-width,initial-scale=1,minimal-ui\">"
			+"\n<link rel=\"shortcut icon\" href=\"/"+icon+"\"/>"
			+"\n<link rel=\"icon\" href=\"/"+icon+"\"/>"
			+meta
		);

		incluiCss(css,false);
		js();

		devCSS();
		
		ped.on("\n</head>\n");
		
		ped.on(
			"<body "
			+(estilo==null?"":"class=\""+estilo+"\"")
			+">"
		);

		if (!str.vazio(tit)) {
			ped.on("<p class=p_titPag>"+tit+"</p>");
		}
		//verifica mensagens
		msg();
	}
	//*****************************************//
	public Object getCampo(Object o,String sm) {
		try {
			return o.getClass().getDeclaredField("nome").get(o);
		} catch (Exception e) {
			return "ERRO: "+e;
		}
		//return null;
	}
	//*****************************************//
	public boolean execMethod(Object o,String sm) {
		try {
			cl = o.getClass().getName();
			Method m = o.getClass().getMethod(sm,(new Class[] {}) );
			m.invoke(o,new Object[] {});
			return true;
		} catch (NoSuchMethodException e) {
			on("Método ("+sm+") não existe no objeto: "+o+"<br>"+ped+"\n"+str.erro(e));
			//ped.erro("ERRO, execMethod() exec método <b>"+sm+"()</b>"+
			// ", classe=<b>"+cl+"</b>",e);
		} catch (InvocationTargetException e) {
			//ped.erro("ERRO, execMethod() no Método <b>"+sm+"()</b>"+
			//	", classe=<b>"+cl+"</b>",e);
			ped.on("ERRO, execMethod() no Método <b>"+sm+"()</b>"+str1.html(e));
		} catch (Exception e) {
			ped.erro("ERRO, execMethod() exec método <b>"+sm+"()</b>"+
				", classe=<b>"+cl+"</b>",e);
		}
		return false;
	}
	/**************************************
	public static String html(Throwable e) {
		String stv[] = str.erroA(e);
		String	st = "<p class=erroTr>"+stv[0]+"</p>";
		int ni=0;
		for (int i=1;i<stv.length;i++) {
			String a = str.leftAt(str.trimm(stv[i])," ");
			if (!a.equals("at")) {
				st += "<p class=erroTr>"+stv[i]+"</p>";
			} else {
				if (ni==0) {
				st += "<p class=erroTr1>"+stv[i]+"</p>";
				} else {
					st += "<p class=erroTr2>"+stv[i]+"<p>";
				}
				ni++;
			}
		}
		return st;
	}
	*/
	//*****************************************//
	public String Classe() {
		try {
			cl = this.getClass().getName();
		} catch (Exception e) {
			ped.erro(cl,e);
		}
		return cl;
	}
	//*****************************************//
	public String ClasseC() {
		return "/"+str.troca(Classe(),".","/")+".class";
	}
	//*****************************************//
	public boolean exec() {
		//on("<h1>TESTE</h1>");
		try {
			if (ped.getString("op")==null) op=inicio;
			return execMethod(this,op);
		} catch (Exception e) {
			logs.grava("ERRO "+e);
			return false;
		}
	}
	//*****************************************//
	public void rodap() {
		ped.o("\n\n<hr class=hrPadrao>"
			+"\n<center><font size=2><b>"
				+opC("setor")
				+" <a href=javascript:chatAbre();>CHAT</a>"
			+"\n</b></font></center>"
			+"\n</body>"
			+"\n\n</html>\r\n\r\n"
		);
		ped.flush();
	}
	//*****************************************//
	public long tempo() {
		return System.currentTimeMillis()-tempo;
	}
	//*****************************************//
	public void negado() {
		negado("?");
	}
	//*****************************************//
	public void negado(String sp) {
		String s =  "Acesso NEGADO: "+ped;
		logs.grava("negado",sp+"="+s);
		ped.on("<hr><p align=center>Problema de Acesso,"
			+"<br>Contato Ramal <b>4248</p></b><hr>");
	}
	//*****************************************//
	public void incluiCss(String e) {
		incluiCss(e,false);
	}
	//*****************************************//
	public void incluiCss(String e,boolean seExiste) {
		if (str.vazio(e)) {
			return;
		} else if (obj) {		
			return;
		} else if (hArq.get(e)!=null) {
			return;
		}
		hArq.put(e,e);
		
		if (seExiste && !(new File(dirLCss+"/"+e)).exists()) {
			return;
		}
		
		String s = "<link rel=\"StyleSheet\" href=\""+dirCss+"/"+e+"?md5="+arqDig(dirLCss+"/"+e)+"\">";
		if (cabFoi) {
			ped.o("\n"+s);
		} else {
			cabH.put(""+cabH.size(),s);
		}
	}
	//*****************************************//
	public void incluiJs(String e) {
		dirs();//logs.grava(cabH.size()+" "+e);
		if (e.charAt(0)!='/') e = dirJs + "/" + e;
		
		if (obj) {		
			return;
		} else if (hArq.get(e)!=null) {
			return;
		}
		hArq.put(e,e);
		String ad = arqDig(dirR+e);
		/*/existe?
		if (ped==null) {
			logs.grava("incluiJs(): js="+js+" ped=null hArq="+hArq);
		} else if (ad!=null) {
			ped.on("");
		*/
			String s = "<script charset=\"UTF-8\"  type=\"text/javascript\"" //language=\"JavaScript\""
				+" src=\""+e+(ad!=null?"?md5="+ad:"")+"\">"
				+"</script>"
			;
			if (cabFoi) {
				ped.o("\n"+s);
			} else {
				cabH.put(""+cabH.size(),s);
			}
		//}
	}
}
