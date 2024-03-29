/*
	*  Signey John jan/2001.
	*/

package br.org.guarani.servidor;

import br.org.guarani.util.*;
import br.org.guarani.dose.*;

import java.security.*;
import java.lang.reflect.*;
import java.util.*;
import java.text.*;
import java.io.*;


/*
	File file = new File(
		getClass().getClassLoader().getResource("database.properties").getFile()
	);

2. getResourceAsStream


	InputStream inputStream = getClass()
			.getClassLoader().getResourceAsStream("database.properties");
*/

import br.org.guarani.loader.loaderInterface;

//***********************************************************
//***********************************************************
public class Guarani {
	//private static final Class class1 = gnu.java.locale.Calendar.class;
	//private static final Class class2 = gnu.java.locale.LocaleInformation.class;
	//para dados de aplicacao
	private static Hashtable Dados = new Hashtable();

	//protected static Classes cl;
	private static loaderInterface vml;
	protected static Hashtable clC=new Hashtable();
	protected static String classe_root, classe_root_java, classe_compil;
	protected static boolean loader;

	//protected String www_root,http_param;
	protected static Mime tipos;
	protected int nServ;
	protected static Hashtable cnf = null;
	public static Hashtable cnf_jdbc=new Hashtable();
	protected static String aLogs, aCfg;

	protected static Thread Tarefas[];
	protected static Servico Servicos[];
	//protected static Usuarios Usuarios;
 
	public static String dirCfg,dirLogs,dirDados;
	private static String aoSairS="";
	private static Hashtable aoSairH = new Hashtable();
 
	//servlets
	public static Hashtable sessoes;

	//versão para parar threads em caso de recarga classes
	public static long classVer=0L;
	
	//servico
	static boolean Servico = false;
	static arquivo ar;
	static String dirEx;
	public static boolean dev = false;
	//***********************
	public static Hashtable classesEstat() {
		Hashtable h = new Hashtable();
		vml.estat(h);
		return h;
	}
	//*********************************
	public static void setClassLoader(loaderInterface cl) {
		//System.out.println("cl="+cl);
		vml = cl;
		//System.out.println("vml="+vml);
	}
	//*********************************
	public static void aoSair(String shCmds) {
		aoSairS += shCmds+"\n\n";
	}
	//*********************************
	public static void aoSair(String metodo,Class cl) {
		Method m = null;
		try {
			m = cl.getMethod(metodo,new Class[]{});
		} catch (Exception e) {
			logs.grava("servidor","metodo não encontrado: "+metodo+" em "+cl.getName()+"\n"+str.erro(e));
			return;
		}
		Guarani.aoSair(m,null);
	}
	//*********************************
	public static void aoSair(Method m,Object o) {
		aoSairH.put(aoSairH.size()+"",new Object[]{m,o});
	}
	//*********************************
	public static String dir(String dir) {
		if (true || dir==null) return dir;
		dir = str.trimm(dir);
		if (dir.length()==0) {
			return Guarani.dirEx;
		}
		if (so.linux()) {
			dir = str.troca(dir,"\\","/");
			if (dir.charAt(0)!='/') {
				dir = Guarani.dirEx+"/"+dir;
			}
		} else {
			dir = str.troca(dir,"/","\\");
			if (dir.charAt(0)=='\\') {
				dir = Guarani.dirEx.substring(0,2)+dir;
			} else if (dir.length()<2) {
				dir = Guarani.dirEx+"\\"+dir;
			} else if (dir.charAt(1)!=':') {
				dir = Guarani.dirEx+"\\"+dir;
			}
		}
		return dir;
	}

	//*********************************
	public static void main(String args[]) throws IOException  {
		//inicializa variáveis SO e JVM
		so.init();
		/*Guarani.log("nomePrg: "+so.nomePrg());
		Guarani.log("dirPrg: "+so.dirPrg());
		Guarani.log("dirTmp: "+so.dirTmp());
		Guarani.log("linux: "+so.linux());
		Guarani.log("sun: "+so.sun());
		*/

		String ex=so.nomePrg();
		Guarani.dirEx = str.leftRat(ex,Guarani.sepDir());
		
		//Guarani.log("Início do G");
	
		if (args.length>0 && args[0].charAt(0)=='-') {
			String p0 = args[0],sv="Guarani";
			if (args.length>1) {
				sv = args[1];
				if (args.length>2) {
					ex = args[2];
				}
			}
			if (p0.equals("-install")) {
				int i = servico.install(sv,ex+" -start "+sv);
				if (i!=0) {
					Guarani.log("ERRO instalando o SERVICO: "+servico.er);
				} else {
					Guarani.log("install OK");
				}
				System.exit(0);
	
			} else if (p0.equals("-remove")) {
				int i = servico.remove(sv);
				if (i!=0) {
					Guarani.log("ERRO removendo o SERVICO: "+servico.er);
				} else {
					Guarani.log("remove OK");
				}
				System.exit(0);
	
			} else if (p0.equals("-start")) {
				Guarani.log("tentando levantar serviço="+sv);
				Servico = true;
				gServico g = new gServico();
				Thread t = new Thread(g);
				t.start();
				Guarani.log("Levantou a gServico: "+t.isAlive());
				int i = servico.start(Guarani.dirEx,sv);
				return;
			}
		}
		
		//****************************************************
		start(args);
	}

	//*********************************
	public static Guarani start()  {
		return start(new String[0]);
	}
	//*********************************
	public static Guarani start(String args[])  {
		String c = null, cfg = null;
		so.init();
		String ex=so.nomePrg();


		//argumento 1 arquivo configuração
		try {
			if (args.length>0) {
				cfg = str.trimm(args[0]);
			} else {
				Guarani.dirEx = str.leftRat(ex,Guarani.sepDir());
				cfg = Guarani.dirEx+Guarani.sepDir()+"Guarani.conf";
				Guarani.log("cfg autoN="+cfg+" dirEx="+Guarani.dirEx);
			}
			if (args.length>1) {
				c = str.trimm(args[1]);
			}
			//Guarani.log("vai mem2");
		} catch (Exception e) {
			arquivo xx=new arquivo("/lab/erro");
			xx.gravaTxt("ERRO: "+str.erro(e));
		}

		//rt = Runtime.getRuntime();
		//Runtime.getRuntime().addShutdownHook(new fim());
		// cada classe que necessita pode criar a sua terefa fim...
		Provider p[] = Security.getProviders();
		for (int i=0;i<p.length;i++) {
			Guarani.log(i+" "+p[i]);
		}
		//Guarani.log("vai mem");

		Guarani g=null;
		try {
			mem();
			//Guarani.log("vai new");
			g = new Guarani(cfg,c);
		} catch (InternalError e) {
			Guarani.log("ERRO SAIDA1?: "+e);
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			Guarani.log("ERRO SAIDA2?: "+e);
			e.printStackTrace();
		} catch (StackOverflowError e) {
			Guarani.log("ERRO SAIDA3?: "+e);
			e.printStackTrace();
		} catch (UnknownError e) {
			Guarani.log("ERRO SAIDA4?: "+e);
			e.printStackTrace();
		} catch (VirtualMachineError e) {
			Guarani.log("ERRO SAIDA5?: "+e);
			e.printStackTrace();
		}
		return g;
	}
	
	//*********************************
	public static void log(String s) {
		try {
			if (Guarani.Servico) {
				if (ar==null) {
					ar = new arquivo(Guarani.dirEx+"/log.txt");
					s = s+" *";
				}
				ar.gravaTxt(ar.leTxt()+"\r\n"+data.strSql()+"\t"+s);
			} else {
				System.out.println(s);
			}
		} catch (Exception e) {
			arquivo xx=new arquivo("/lab/erroLOG");
			xx.gravaTxt("ERRO: "+str.erro(e));
		}
		
	}
	
 
	//*********************************
	public static Object get(Object ch) {
		return Dados.get(ch);
	}
	//*********************************
	public static void put(Object ch,Object dad) {
		if (Dados.get(ch)!=null) {
			Dados.remove(ch);
		}
		Dados.put(ch,dad);
	}
	//*********************************
	public static boolean setTimeZone(String id) {
		TimeZone.setDefault(TimeZone.getTimeZone(id)); 
		return false;
	}

	//*********************************
	public static String getHost() {
		return (String)cnf.get("host");
	}
	//*********************************
	public static String getHostIp() {
		return (String)cnf.get("ip");
	}

	//*********************************
	public static void stop(int sinal) {
		
		//para os servicos
		try {
			for (int i=0;i<Servicos.length;i++) {
				Servicos[i].rodando = false;
				try {
					Servicos[i].ss.close();
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
			logs.grava("servidor","Guarani.stop("+sinal+"): erro parando servicos..."+str.erro(e));
		}
		
		try {
			//executa methods ao sair
			for (int i=0;i<aoSairH.size();i++) {
				Object o[] = (Object[])aoSairH.get(""+i);
				Method m = (Method)o[0];
				try {
					m.invoke(o[1],null);
					logs.grava("servidor","Guarani.stop(): OK method "+m); 
				} catch (Exception e) {
					logs.grava("servidor","Guarani.stop(): ERRO method "+m);
				}
			}
		} catch (Exception e) {
			logs.grava("servidor","Guarani.stop("+sinal+"): erro parando metodos..."+str.erro(e));
		}
		
		//mata subprocessos - gcj deixa pendurado
		//linsh ux se perde nos prop das portas...

		//executa cmds shell ao sair
		if (aoSairS!=null && aoSairS.length()!=0) {
			sh s = new sh(null);
			s.cmd(aoSairS);
			s.exec();
		}
		logs.grava("servidor","Saindo a pedido pelo Guarani.stop="+sinal);
		if (vml!=null) {
			vml.stop(sinal);
		} else {
			System.exit(sinal);
		}
	}

	public static void mem() {
		/*Runtime r = Runtime.getRuntime();
		Guarani.log("Memória Total: "+r.totalMemory());
		Guarani.log("- Livre: "+r.freeMemory());
		System.gc();
		Guarani.log("Memória Total: "+r.totalMemory());
		Guarani.log("- Livre: "+r.freeMemory());
		*/
	}

	//*********************************
	public Guarani(String cfg, String sOut) {
		String s;

		Guarani.log("iniciando Guarani **************************");
		mem();
		//Guarani.log("datap: "+(new Date()));

		String os = System.getProperties().getProperty("os.name");
		if (cfg==null) { 
			cfg = "../conf/guarani."+os.substring(0,5).toLowerCase();
			Guarani.log("assumindo Arquivo .conf="+cfg); 
		}
		//abre arquivo configuração
		aCfg = cfg;
		File f = new File(aCfg);
		if (f.exists()) {
			try {
				Guarani.log("Arquivo .conf: "+f.getCanonicalPath());
			} catch (IOException ioe) {
				Guarani.log("ERRO abrindo, Arq .conf: "+aCfg);
			}
		} else {
			Guarani.log("Arq .conf nao existe: "+aCfg);
			System.exit(1);
		}
  
  
  
		conf xx = new conf(aCfg);
		Hashtable h1, h = xx.getConf();
		cnf = (Hashtable)h.get("Guarani");
		if (cnf==null) {
			cnf = (Hashtable)h.get("guarani");
		}
		if (cnf==null) {
			Guarani.log("Arquivo .conf INVALIDO, falta seção [Guarani]!!"+aCfg);
			System.exit(1);
		}

		//seta propriedades do sistema
		String tz = (String)cnf.get("timezone");
		setTimeZone(tz==null?"America/Sao_Paulo":tz);
		for (Enumeration e = cnf.keys() ; e.hasMoreElements() ;) {
			s = (String)e.nextElement();
			if (str.equals(s,"System.")) {
				Guarani.log("System: "+s+"="+cnf.get(s));
				System.getProperties().put(str.substrAt(s,"."), cnf.get(s));
			}
		}
  
		//validacao
		String v[] = str.palavraA((String)cnf.get("ValLinux")+"~5~5","~");
		//Usuario.venceP = str.longo(v[1],5)*60*1000;
	
		//rodando no modo desenvolvimento?
		dev = (""+cnf.get("dev")).equals("1");

		dirCfg = (String)cnf.get("dircfg");  
		if (dirCfg==null) {
			dirCfg = str.leftRat(aCfg,Guarani.sepDir());
		}
		dirCfg = str.dir(dirCfg);

		dirDados = str.seVazio((String)cnf.get("dados"),dirCfg+"/dados");

		dirLogs = (String)cnf.get("logs");  
		if (dirLogs==null) {
			dirLogs = Guarani.dirEx+"/logs";
		}
		dirLogs = str.dir(Guarani.dir(dirLogs));
  
		aLogs = dirLogs+data.strSql().substring(0,10)+"-";
		Guarani.log("Logs: "+aLogs);
		Guarani.log("Debug: "+sOut);
		logs.inicia(aLogs,sOut);
		Guarani.log("Logs INICIADO...");

		//System.setErr(new PrintStream());

		logs.grava("servidor","O.S.: "+os);
		logs.grava("servidor","dirCfg: "+dirCfg);
		logs.grava("servidor","***************************************************");
		logs.grava("servidor","G U A R A N I   v1.0");
		logs.grava("servidor","por 2001 Signey John - sjohn@via-rs.net");

		tipos = new Mime();

		logs.grava("servidor","data: "+data.strHttp());


		//instala servicos
		nServ = h.size();
		Tarefas = new Thread[nServ];
		Servicos = new Servico[nServ];
		int i=0;
		for (Enumeration e = h.keys() ; e.hasMoreElements() ;) {
			s = (String)e.nextElement();
			if (s.toLowerCase().compareTo("guarani")==0) {
				//cnf = (Hashtable)h.get(s);
			} else if (s.toLowerCase().compareTo("jdbc")==0) {
				cnf_jdbc = (Hashtable)h.get(s);
			} else {
				h1 = (Hashtable)h.get(s);
				int nt = Integer.parseInt(str.seNull((String)h1.get("nTask"),"4"));
				if (nt>0) {
					Servicos[i] = new Servico(s,h1);
					Tarefas[i] = new Thread(Servicos[i]);
					int p = Integer.parseInt(str.seNull((String)h1.get("prioridade"),"5"));
					Tarefas[i].setPriority(p);
					//Tarefas[i].setPriority(2);
					i++;
					try {Thread.sleep(1000);} catch (InterruptedException ie) {}
				}
			}
			//ogs.grava("<hr>"+s);
		}
		nServ = i;

		//configurações guarani
		if (cnf==null) {
			logs.grava("servidor","Alerta Inicialização: sem classes aplicativas");
		} else {
			classe_root = Guarani.dir((String)cnf.get("classe_root"));
			loader = classe_root.length()>0;
			if (!loader) {
				logs.grava("servidor","sem loader");
			}
			classe_root_java = Guarani.dir((String)cnf.get("classe_root_java"));
			classe_compil =  Guarani.dir((String)cnf.get("classe_compil"));
			resetClasses();
		}

		logs.grava("servidor","***************************************************");
		logs.grava("servidor","Numero de Serviços: "+nServ);
		logs.grava("servidor","***************************************************");

		if (getCfg("linguagem")!=null) {
			Locale.setDefault(new Locale(getCfg("linguagem"),getCfg("pais")));
		}
		logs.grava("servidor","locale: "+Locale.getDefault());
		
		//charset..
		System.setProperty("sun.jnu.encoding",getCfg("charset"));
		System.setProperty("file.encoding",getCfg("charset"));

		//
		//Usuarios = new Usuarios();

		//inicia tarefas
		for (i=0;i<nServ;i++) {
			//ogs.grava("###nserv="+i+" nome="+Servicos[i].cla+" nome="+Servicos[i]);
			Tarefas[i].start();
		}
  
		//inicializar classe?
		String ini = getCfg("init");
		logs.grava("servidor","classes init="+ini);
		if (ini!=null) {
			String iniv[] = str.palavraA(str.trimm(ini)," "); 
			for (i=0;i<iniv.length;i++) {
				try {
					if (!str.vazio(iniv[i])) {
						Class c = Guarani.findClass(iniv[i]);
						Object b = c.newInstance();
						iniv[i] = b.toString();
					}
				} catch (Exception e) {
					logs.grava("servidor","ERRO classe init="+iniv[i]+" "+e);
				}
			}
		}
  
	}

	//*********************************
	public static String sepDir() {
		return (so.linux()?"/":"\\");
	}

	//*********************************
	private static void resetClasses() {
		/*ld*if (loader) {
			cl = new Classes();
			classVer = data.ms();
			cl.inicia(classe_root,classe_root_java,classe_compil);
		}
		*/
	}
	//*********************************
	public static boolean compilar() {
		if (vml!=null) {
			if (vml.reset()) {
				String s = vml.compil();
				logs.grava("servidor","Guarani.compilar(): restart: "+s);
				stop(0);
				return false;
			} else if (vml.reLoad()) {
				String s = vml.compil();
				classVer = data.ms();
				logs.grava("servidor","Guarani.compilar(): compilado: "+s);
				return true;
			}
		}
		return false;
	}
	
	//*********************************
	protected static boolean execClasse(String nom, Pedido ped, Gravador o, int nv) {
		int i=0;
		String nome = nom.substring(1,nom.length()-6);
		int r;
		
		if (vml!=null) {
			if (vml.reset()) {
				//tentou compilar classe de nível que necessita reiniciar servidor
				String s = vml.compil();
				ped.on("<html>"
					+"<head><title>Reiniciando SERVIDOR</title></head>"
					+"<body><h1>Reiniciando Servidor</h1>"
					+"<div class=compil><p>"+str.troca(s,"\n","</p>\n<p>")+"</p>\n</div>"
					//+"<script language=\"JavaScript\" src=\"/js/jan.js\"></script>"
					//+"<script>function aaa(){var p=new pedido();p.set('__segs',ms());window.location=p.atalho();};setTimeout('aaa()',3000);</script>"
					+"</body></html>"
				);
				stop(0);
				return false;
			} else if (vml.reLoad()) {
				String s = vml.compil();
				classVer = data.ms();
				ped.on("<div class=compil><p>"+str.troca(s,"\n","</p>\n<p>")+"</p>\n</div>");
			}
		}

 
		if (!loader || findClassC(nome)) {
			//usar classloader JVM
			return execClasse(nome,ped,o);
		}
		return false;
	}
	//*********************************
	// usando classloader JVM
	private static boolean execClasse(String nom, Pedido ped, Gravador o) {
		Class csl = null;
		nom = str.troca(nom,"/",".");
		try {
			csl = Guarani.findClass(nom);
			if (csl==null) {
				return false;
			}
			Prg classL = null;
			try {
				classL=(Prg)csl.newInstance();
				classL.initPed(ped);
				classL.run();
				return true;
			} catch ( Exception t ) {
				logs.grava("servidor","Guarani.execClasse(): "+t);
				t.printStackTrace();
				return false;
			}
			//.newInstance();
		} catch (Exception e) {
			ped.on("Não Existe!!"+nom);
			logs.grava("servidor","Não Existe CLASSE: "+
				"execClasse(String nom, Pedido ped, Gravador o)="+nom+"<br>"+ped);
			return false;
		}
	}
	
	//************************************************
	// P U B L I C A S
	//*********************************
	public static Class findClass(String n) {
		return findClass(n,true);
	}
	//*********************************
	public static Class findClass(String n,boolean Log) {
		try {
			if (vml==null) {
				return Class.forName(n);
			}
			
			//ogs.grava("vai carregar pelo loader: "+vml);
			return vml.loadClass(n);
			//.newInstance();
		} catch (Exception e) {
			if (Log) {
				logs.grava("servidor","Guarani.findClass() vmLoader: "+n+", não existe: "+e);
			}
			return null;
		}
	}
	//********************************************
	//classe é compilada no módulo
	public static boolean findClassC(String n) {
		//ogs.grava("procura classe comp: "+n);
		String s = (String)clC.get(n);
		if (s!=null) {
			return s.equals("1");
		}
		try {
			Class.forName(str.troca(n,"/","."));
			s = "1";
		} catch (ClassFormatError e) {
   
			logs.grava("seguranca","guarani.findClassC: "+str.erro(e));
			s = "0";
		} catch (Exception e) {
			s = "0";
		}
		clC.put(n,s);
		return s.equals("1");
	}

	//*********************************
	public static String getCfg(String a,String b) {
			return str.seNull(getCfg(a),b);
	}

	//*********************************
	public static String getCfg(String a) {
		return (String)cnf.get(a);
	}

	//*********************************
	public static String getCfgTemp() {
		return getCfg("temp");
	}

	//*********************************
	public static Servico[] getServicos(Pedido ped) {
		/*if (!ped.segura) {
			//ogs.grava("seg","getServicos"+ped);
			return null;
		}
		*/
		return Servicos;
	}

	//********************************
	public static Classes getClasses(Pedido ped) {
		return null;
		/*ld*return cl;//.lista;*/
	}

	//*********************************
	public static Thread[] getTarefas(Pedido ped) {
		/*if (!ped.segura) {
			//ogs.grava("seg","getTarefas!"+ped);
			return null;
		}
		*/
		return Tarefas;
	}

	/*********************************
	public static Usuarios getUsuarios(Pedido ped) {
		/*if (!ped.segura) {
			//ogs.grava("seg","getUsuarios"+ped);
			return null;
		}
		return Usuarios;
	}
	*/

	//*********************************
	public static void resetClasses(Pedido ped) {
		/*if (!ped.segura) {
			//logs.grava("seg","resetClasse!"+ped);
			return;
		}
		*/
		resetClasses();
	}
	//***********************
	// resources
	//***********************
	//***********************
	public static boolean resMove(String ch,String ch1) {
		return vml.resMove(ch,ch1);
	}
	//***********************
	public static boolean resExclui(String ch) {
		return vml.resExclui(ch);
	}
	//***********************
	public static boolean resGrava(String ch,String tx) {
		return vml.resGrava(ch,tx);
		/*ogs.grava("ch="+df.chave());//+" nome="+df.nome);
		String aq = raiz+"/"+str.troca(df.chave(),".","/")+".res"; //dir(df);
		(new File(aq)).mkdirs();
		l ogs.grava("ch="+df.chave()+" gr aq="+aq);
		return (new arquivo(aq)).gravaTxt(df.defs());
		//return false;
		*/
	}
	//***********************************
	public static String resUrl(String ch) {
		return vml.resUrl(ch);
	}
	//***********************************
	public static Hashtable resCarregaS(String ch) {
		return vml.resCarregaS(ch);
		/*
		File fd = new File(raiz+"/"+str.troca(ch,".","/"));
		File vf[] = fd.listFiles();
		Hashtable r = new Hashtable();
		if (vf==null) {
			//ogs.grava("não achei="+fd);
			return r;
		}
		for (int i=0;i<vf.length;i++) {
			if (!vf[i].isDirectory()) {
				arquivo f = new arquivo(""+vf[i]);
				if (f.f.exists()) {
					String c = str.leftRat(f.f.getName(),".");
					Hashtable d = new Hashtable();
					d.put("defs",f.leTxt());
					d.put("ch",ch+"."+c);
					r.put(""+r.size(),d);
				}
			}
		}
		return r;
		*/
	}
	/***********************
	public static String resCarrega(def df) {
		return vml.resCarrega(df.chave());
	}
	*/
	//***********************
	public static String resCarrega(String ch) {
		return vml.resCarrega(ch);
		/*File f = new File(raiz+"/"+str.troca(ch,".","/")+".res");
		if (!f.exists()) {
			return null;
		}
		return (new arquivo(""+f)).leTxt();
		*/
	}
}
