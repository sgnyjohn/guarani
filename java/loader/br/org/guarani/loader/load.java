package br.org.guarani.loader;

import java.io.*;
import java.net.*;
import java.lang.reflect.*;
import java.util.*;
import java.nio.charset.Charset;

//import java.security.*;
//import javax.net.ssl.*;


//import java.sql.*;
//import com.mysql.jdbc.Driver;

//**************************************************************
//**************************************************************
class load {
	public static String raiz = ".";
	public static String jvm;
	public static boolean sun;
	public static String so;
	public static String dTmp;
	public static boolean linux;
	public static String nomePrg;
	public static String charset;
	public static Charset charsetO;

	static private loader loaders[];
	//static Object ob;
	private String param[];
	private static File aCfg;
	private static xmlTagL cfg,cfgL;
	static xmlTagL cfgR;
	static int debNv=0;
	static String compil[];
	private static ClassLoader clB;

	static loaderConf opC;
	static arquivoL arq;
	//**************************************************************
	static void initVars() {
		jvm=System.getProperty("java.vm.vendor");
		if (jvm==null) {
			System.out.println("Vm="+jvm);
			jvm = "avian";
		}
		sun=jvm.toLowerCase().indexOf("sun")!=-1
				|| jvm.toLowerCase().indexOf("blackdown")!=-1
				|| jvm.toLowerCase().indexOf("oracle")!=-1
		;
		so=System.getProperty("os.name");
		dTmp = System.getProperty("java.io.tmpdir");
		linux=so.toLowerCase().indexOf("linux")!=-1;
		nomePrg = System.getProperty("gnu.gcj.progname");		
	}
	//**************************************************************
	static void estat(Hashtable r) {
		for (int i=0;i<loaders.length;i++) {
			loaders[i].estat(r);
		}
	}

	//**************************************************************
	static URL getResource(int pos,String nome) {

		//1 - procura no atual...
		URL u = loaders[pos].findResource(nome);
		if (u!=null) {
			return u;
		}
		
		//2 - procura nos outros...
		for (int i=0;u==null && i<loaders.length;i++) {
			if (i!=pos) {
				u = loaders[i].findResource(nome);
				if (u!=null) {
					logsL.deb(2,"res: "+(i==pos?"ATU":(i>pos?"SUP":"INF"))+": "+nome);
					return u;
				}
			}
		}
		
		if (strL.equals(nome,str.str("br.org.guarani."))) {
			logsL.grava("res: nao poderia, loader alterado e não compilado "+nome);
		}
		logsL.deb(2,pos+"res: load MAQ: "+nome);
		try {
			u = loaders[0].getParent().getResource(nome);
		} catch (Exception e) {
		}
		
		return u;
	}
	//**************************************************************
	static String compila(int pos,String j) {
		testa();
		compil[pos] = (compil[pos]==null?"":compil[pos]+" ")+j;
		return compila();
	}
	//**************************************************************
	static String compila() {
		String r = "";
		String cp = "";
		int rec = -1;
		for (int i=0;i<loaders.length;i++) {
			cp += ":"+conf(i,"path");
			if (!strL.vazio(compil[i])) {
				//compila
				String cm = conf(cfg,"compil.cmd");
				if (load.sun) {
					r += exec(cm,new String[]{"dest="+conf(i,"path")+"","cp="+cp.substring(1),"java="+compil[i]});
				} else {
					r += exec(cm+" "+conf(i,"path")+" "+cp.substring(1)+" "+strL.trimm(compil[i])+" ",null);
				}
				logsL.deb(0,"v COMPILAR: "+i+" "+cm+" "+cp+" "+compil[i]);
				//testa se compilou ok
				String rs = loaders[i].testa();
				if (rs==null) {
					logsL.deb(0,"algo errado.... = ou classe nova?");
				} else if (rs.equals("")) {
					logsL.deb(0,"compilou OK");
					rec = (rec==-1?i:rec);
				} else {
					logsL.deb(0,"erro na compilação res="+rs+"=");
				}
			} else if (compil[i]!=null) {
				logsL.deb(0,"so recarregar: "+i);
				rec = (rec==-1?i:rec);
			}
		}
		
		//recarregar 
		if (rec!=-1) {
			init(rec);
		}
		
		return r;
	}
	//**************************************************************
	static int testa() {
		int r = -1;
		//logsL.deb(0,"vai testar...");
		for (int i=0;i<loaders.length;i++) {
			compil[i] = null;
			String c1 = loaders[i].testa();
			if (c1!=null) {
				r = (r==-1?i:r);
				compil[i] = c1;
				if (!strL.vazio(c1)) {
					logsL.deb(0,"COMPILAR: "+i+" "+compil[i]);
				}
			}
		}

		return r;
	}
	//**************************************************************
	static Class loadClass(int pos,String nome) {

		//1 - procura no atual...
		Class c = loaders[pos].findClass(nome);
		if (c!=null) {
			return c;
		}
		
		//2 - procura nos outros...
		for (int i=0;c==null && i<loaders.length;i++) {
			if (i!=pos) {
				c = loaders[i].findClass(nome);
				if (c!=null) {
					logsL.deb(1,(i==pos?"ATU":(i>pos?"SUP":"INF"))+": "+nome);
					return c;
				}
			}
		}
		
		if (strL.equals(nome,str.str("br.org.guarani."))) {
			logsL.deb(0,"nao poderia "+nome);
		}
		logsL.deb(2,pos+" load MAQ: "+nome);
		try {
			c = loaders[0].getParent().loadClass(nome);
		} catch (Exception e) {
			System.out.println("ERRO: load.class="+nome+" no JVM "+e);
		}
		if (c==null) {
			try {
				System.out.println("FALTA: nem no JVM. load.class="+nome
					+" no JVM "+strL.erro(new Exception("teste"))
				);
			} catch (Exception e) {
			}
			//throw new java.lang.ClassNotFoundException(" classe "+nome+" nem no JVM");
		}
		
		return c;
	}
	//**************************************************************
	static String conf(int i,String ch) {
		xmlTagL x = loaders[i].cfg();
		return conf(x,ch);
	}
	//**************************************************************
	static String conf(xmlTagL cfg,String ch) {
		String r = (String)cfg.getCh(ch);
		if (r==null) {
			return r;
		}
		if (r.indexOf("[raiz]")!=-1) r = strL.troca(r,"[raiz]",raiz);
		if (r.indexOf("[mq]")!=-1) r = strL.troca(r,"[mq]",load.sun?"sun":"gcj");
		return r;
	}
	//**************************************************************
	private String verifPath(String p) {
		//n("verif="+p);
		if (p==null) return null;
		return (new File(p)).exists()?null:"path not exists: "+p;
	}
	//**************************************************************
	private boolean verifCfg(xmlTagL cfg) {
		boolean c = true;
		String er;
		for (int i=0;i<cfg.size();i++) {
			xmlTagL x = cfg.get(i);
			String ph = load.conf(x,"path");
			//n("=="+ph+" "+x.nome+" "+verifPath(ph)+" "+x.atr);
			if ("-resource-class-".indexOf("-"+x.nome+"-")!=-1 && ph!=null && (er = verifPath(ph))!=null  ) {
				on("ERRO: path em "+x.nome+" "+er);
				c = false;
			}
		}
		return c;		
	}
	//**************************************************************
	private boolean init() {
		
		
		clB = this.getClass().getClassLoader();
		
		//conf DEBUG/LOG
		logsL.inicia(load.dTmp+"/load-",".");
		//logsL.debNv = debNv;
		
		//carrega configuraçoes do loader
		xmlParserL p = new xmlParserL(aCfg);
		cfg = p.parse();
		if (p.sErro!=null) {
			on("ERRO parse cfg: "+p.sErro);
		}
		cfg = (xmlTagL)cfg.getCh("Loader");
		if (cfg==null) {
			on("ERRO cfg sem tag Loader: "+aCfg);
		}
		
		//init class loader
		arq = new arquivoL(null);

		//init conf cliente
		initConf();

		//default LOCALE
		charset = (String)cfg.getAtr("charset","UTF-8");
		System.out.println("charset="+charset);
		charsetO = Charset.forName(charset);
		String lg = (String)cfg.getCh("language");
		if (lg==null) {
			//lg = "pt";
			//System.out.println("Assumindo language: "+lg);
		}
		String pa = (String)cfg.getCh("country","BR");
		if (pa==null) {
			//pa = "BR";
			//System.out.println("Assumindo country: "+pa);
		}
		System.out.println("locale: "+lg+"_"+pa);
		if (pa!=null && lg!=null) { 
			try {
				System.out.println("Locale.getDefault="+Locale.getDefault());
				Locale.setDefault(new Locale(lg,pa));
			} catch (NoSuchMethodError e) {
				System.out.println("ERRO Locale Default="+e);
			} catch (Exception e) {
				System.out.println("ERRO Locale Default="+e);
			}
			//language, String country
		
			//nivel de debug
			//System.out.println("Locale.US="+Locale.US);
		}
		System.out.println("deb="+cfg.getCh("debug"));
		debNv = strL.inteiro((String)cfg.getCh("debug"),0);
		
		//config classes
		cfgL = (xmlTagL)cfg.getCh("class");
		if (cfgL==null || cfgL.size()==0) {
			on("ERRO: nenhuma tag <class/> em config "+aCfg);
			return false;
		}
	
		//config resources
		cfgR = (xmlTagL)cfg.getCh("resource");
		if (cfgR==null || cfgR.size()==0) {
			on("ERRO: nenhuma tag <resource/> em config "+aCfg);
			return false;
		}
		
		//consistencia path
		if (!verifCfg(cfgL) || !verifCfg(cfgR)) {
			on("ERRO(s)");
			System.exit(1);
		}		
		
		//CRIA LOADERS
		int nc = cfgL.size();
		loaders = new loader[nc];
		compil = new String[nc];
		
		return init(0);
		
	}
	//**************************************************************
	static private boolean init(int pos) {
		
		ClassLoader CL = clB;
		for (int i=0;i<cfgL.size();i++) {
			xmlTagL x = cfgL.get(i);
			if (i>=pos) {
				loaders[i] = new loader(CL,i,x);
			}
			CL = loaders[i];
		}
		
		//inicia threads dos loaders
		for (int i=0;i<loaders.length;i++) {
			xmlTagL x = loaders[i].cfg();
			if (x.getAtr("thread")!=null && i>=pos) {
				//on("thread");
				loaders[i].thread();
			}
		}
	
		return true;
	}
	//**************************************************************
	protected load(String a[]) {
		//on("load, núm parametros: "+a.length);
		aCfg = new File(a[0]);
		raiz = a[0].substring(0,a[0].lastIndexOf("/"));
		//dir ant
		raiz = raiz.substring(0,raiz.lastIndexOf("/"));
		
		param = new String[a.length-1];
		for (int i=1;i<a.length;i++) {
			param[i-1] = a[i];
		}
	}
	//**************************************************************
	boolean initConf() {
		///////////////////////////////////////////////////////////////////////
		// sendo GCJ ou não
		// verifica integridade VM - arquivo gcj desatualizado inclusive
		opC = new loaderConf();
		nomePrg = ""+aCfg;
		on(nomePrg+" - Maq sun?:"+sun+" "+System.getProperty("gnu.gcj.progname")
			+" cfg="+cfg//.getCh("config")
			+" opC="+opC
		);
		if (!opC.init((xmlTagL)cfg.getCh("config"))) {
			on("erro no arquivo de configuração..."+nomePrg);
			System.exit(10);
		}
		
		/*String id = opC.get("version").toString();
		on("tam="+id.length());
	
		arq = new arquivoL(id);
		
		a[0] = strL.trimm(a[0]);
		if (a.length<1) {
			on("ERRO: parametro 1 é arq xml config...");
			System.exit(1);
		}
		if (!(new File(a[0])).exists()) {
			on("ERRO: parametro 1 xml config "+a[0]+" não existe!");
			on("Sair 1");
			System.exit(1);
		}
		*/

		return true;
	}

	//**************************************************************
	// p1 arquivo conf
	// p2 parametros classe a ser carregada
	//**************************************************************
	public static void main(String a[]) {
		//System.out.println("sair com 11");
		//System.exit(11);
		
		//TESTA CLASSPATH válido
		//gnu.gcj.runtime.VMClassLoader.library_control=false;
		//gnu.classpath.home.url // gnu.classpath.vm.shortname // java.library.path // gnu.classpath.home
		
		/* teste sql codepage. o probema era LC_ALL setado para pt_br
		 * try {
			/*DadosSet rs = dad.executeQuery(
				"SELECT * FROM Fonte WHERE Fonte like '%rio ga%'"
			);
			* /
			
			
			Class.forName(com.mysql.jdbc.Driver.class.getCanonicalName());
			String url = "jdbc:mysql://localhost/Noticias?user=root&password=my@teste";
			Connection conn = DriverManager.getConnection(url);//, config.toProperties());
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery("SELECT * FROM Fonte WHERE Fonte like '%rio ga%'");			
			
			
			while (rs.next()) {
				on("<hr>"+"@@@@ aviação "+rs.getString("Fonte"));
				System.out.println("@@@@ aviação "+rs.getString("Fonte"));
			}
		} catch (Exception e) {
		}		
		*/
		
		//classObriga.aaa();
		
		// carrega vars que define ambiente JVM da execução.
		initVars();
		on("JVM="+jvm);

		// se GCJ
		String v[] = strL.palavraA("java.library.path~java.class.path~java.home~gnu.classpath.home.url~gnu.classpath.home","~");
		for (int i=0;!load.sun && i<v.length;i++) {
			String ch = System.getProperty(v[i]);
			on(v[i]+"="+ch);
			if (ch!=null) {
				String cp[] = strL.palavraA(ch,":");
				for (int x=0;x<cp.length;x++) {
					File f = new File(cp[x]);
					if (f.exists() && !load.sun) {
						on(v[i]+"=="+cp[x]);
						//System.exit(2);
					}
				}
			}
			//System.setProperty(v[i],"...\n...");
			//on(v[i]+"="+System.getProperty(v[i]));
		}
		
		//Inicia
		load l = new load(a);
		//mostra ClassLoaders
		if (false) {
			ClassLoader cl = l.getClass().getClassLoader();
			while (cl!=null) {
				on("parent: "+cl.getClass().getName());
				Method m[] = cl.getClass().getDeclaredMethods();
				for (int i=0;i<m.length;i++) {
					on("  method="+m[i].getName());
				}
				Field[] f =	cl.getClass().getDeclaredFields();
				for (int i=0;i<f.length;i++) {
					on("  field="+f[i].getName());
				}
				
				cl = cl.getParent();
			}
		}
		l.init();
	
		/*fica fazendo cera....
		boolean x = true;
		System.out.println("Fazendo cera...");
		while (x) {
			try {
				Thread.sleep(500);
			} catch (Exception e) {
			}
		}
		System.out.println("FIM Fazendo cera...");
		*/
	
	}
	//**************************************************************
	static void on(String n) {
		System.out.println("====>>>>	"+n);
	}
	/*************************************/
	private static String exec(String a,String b[]) {
		ByteArrayOutputStream tmpErr = new ByteArrayOutputStream(4096);
		ByteArrayOutputStream tmpInput = new ByteArrayOutputStream(4096);
		Process p = null;
		
		String r = null;
		
		try {
			p = Runtime.getRuntime().exec(a,b,new File(load.dTmp));
		} catch (Exception e) {
			logsL.deb(0,"ERRO no exec "+strL.erro(e)+"\na="+a+"\nb="+b);
			return null;
		}
		
		boolean erro;
		String sErro = "";
  
		try {
   
			BufferedInputStream compilerErr = 
				new BufferedInputStream(p.getErrorStream());
			BufferedInputStream compilerInput = 
				new BufferedInputStream(p.getInputStream());
   
			StreamPumperL errPumper = 
				new StreamPumperL(compilerErr, tmpErr);
			StreamPumperL inputPumper = 
				new StreamPumperL(compilerInput, tmpInput);
   
			errPumper.start();
			inputPumper.start();
   
			p.waitFor();
   
			errPumper.join();
			compilerErr.close();
   
			inputPumper.join();
			compilerInput.close();
   
			tmpInput.close();
			tmpErr.close();
			erro = p.exitValue()!=0;
			sErro =  p.exitValue()+"=="+tmpErr.toString();
			
			r = erro?sErro:tmpInput.toString();
   
			p.destroy();

		} catch (IOException ioe) {
			//System.out.println("erro exec "+ioe);
			return null;
   
		} catch (InterruptedException ie) {
			//System.out.println("erro exec "+ie);
			return null;
		}
		return r;
	}
}


/*
gcj 4.2.1 - amd64
====================================
parent: gnu.gcj.runtime.SystemClassLoader
  method=addClass
  method=findClass
  method=init
parent: gnu.gcj.runtime.ExtensionClassLoader
  method=init
  method=initialize

gcj (GCC) 3.3.2 - compilado por mim no sarge 
====================================
parent: gnu.gcj.runtime.VMClassLoader
  method=init
  method=findClass

amd64 java version "1.4.2-03"
Java(TM) 2 Runtime Environment, Standard Edition (build Blackdown-1.4.2-03)
Java HotSpot(TM) 64-Bit Server VM (build Blackdown-1.4.2-03, mixed mode)
===================================
parent: sun.misc.Launcher$AppClassLoader
  method=loadClass
  method=getPermissions
  method=getContext
  method=getAppClassLoader
parent: sun.misc.Launcher$ExtClassLoader
  method=findLibrary
  method=getContext
  method=getExtClassLoader
  method=addExtURL
  method=getExtDirs
  method=getExtURLs

*/

