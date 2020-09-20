package br.org.guarani.loader;

import java.io.*;
import java.util.*;
import java.net.*;
import java.lang.reflect.*;

//**************************************************************
//**************************************************************
class loader extends ClassLoader {
	private Hashtable hCl = new Hashtable();
	private Hashtable hRs = new Hashtable();
	private int pos;
	private xmlTagL cfg;
	private Thread trh;
	private String sTh;
	private String path;
	private Hashtable pathJ;
	//**************************************************************
	public void estat(Hashtable h) {
		for (Enumeration e=hCl.keys();e.hasMoreElements();) {
			String k = (String)e.nextElement();
			clA r = (clA)hCl.get(k);
			h.put(h.size()+"",new String[]{"cl",""+pos,k
				,dataL.strSql(r.dtc)
				,dataL.strSql(r.dt)
				,dataL.strSql(r.dtj)
				,""+r.nFind
			});
		}
		for (Enumeration e=hRs.keys();e.hasMoreElements();) {
			String k = (String)e.nextElement();
			rsA r = (rsA)hRs.get(k);
			h.put(h.size()+"",new String[]{"cl",""+pos,k
				,dataL.strSql(r.dtc)
				,dataL.strSql(r.dt)
				,""
				,""+r.nFind
			});
		}
	}
	//**************************************************************
	public URL getResource(String nome) {
		logsL.deb(2,"get resource: "+nome);
		return load.getResource(pos,nome);
		//return null;
	}
	//**************************************************************
	protected URL findResource(String nome) {
		logsL.deb(2,"find resource: "+nome);
		rsA res = (rsA)hRs.get(nome);
		if (res!=null) {
			res.nFind++;
			logsL.deb(1,"hRs: "+nome);
			return res.r;
		}
		
		String nomeF = strL.troca(strL.leftRat(nome,"."),".","/")+"."+strL.substrRat(nome,".");
		File f = new File(path+"/"+nomeF); //+".properties");
		if (!f.exists()) {
			logsL.deb(2,"não existe: "+f);
			return null;
		}
		
		URL r = null;
		if (f.exists()) {
			logsL.deb(0,"res achou: "+f);
			try {
				//r = new URL(""+f);
				r = new URL("file", "", ""+f);
			} catch (Exception e) {
				logsL.grava("ERRO new URL: "+f);
			}
		}
		res = new rsA(f,r);
		hRs.put(nome,res);
		return r;
	}
	//**************************************************************
	String testa() {
		String r = null;
		for (Enumeration e = hCl.keys();e.hasMoreElements();) {
			String c = (String)e.nextElement();

			clA cla = (clA)hCl.get(c);
			//logsL.deb(0,"vai testar: "+cla.fj);
			
			//classe existe e foi modificada
			//cla.f = new File(""+cla.f);
			if (cla.f.exists() && cla.f.lastModified()!=cla.dt) {
				r = (r==null?"":r);
			}
			
			//java foi modificado | class não existe | class < java?
			if (cla.fj!=null) {
				if (!cla.f.exists() || cla.f.lastModified()<cla.fj.lastModified() || cla.fj.lastModified()!=cla.dtj) {
					cla.dtj = cla.fj.lastModified();
					r = (r==null?"":r)+" "+cla.fj;
				}
			}
		}
		return r;
	}
	//**************************************************************
	protected xmlTagL cfg() {
		return cfg;
	}
	//**************************************************************
	public Class loadClass(String nome) throws ClassNotFoundException {
		Class r = load.loadClass(pos,nome);
		if (r==null) {
			throw new java.lang.ClassNotFoundException("#sj "+nome);
		}
		return r;
	}
	//**************************************************************
	protected Class findClass(String nome) {
		//logsL.deb(1,"loader find: "+nome);
		
		//findLoadedClass no JVM
		clA cla = (clA)hCl.get(nome);
		if (cla!=null) {
			cla.nFind++;
			logsL.deb(1,"hCl: "+nome);
			return cla.c;
		}
		
		Class c = findLoadedClass(nome);
		if (c!=null) {
			logsL.deb(1,"ja load: "+nome);
			return c;
		}
		
		String nomeF = strL.troca(nome,".","/");
		File fj = java(nomeF);
		File f = new File(path+"/"+nomeF+".class");
		if (!f.exists() && (fj==null || !fj.exists()) ) {
			//on("não existe: "+f);
			return null;
		}
		
		//compila se CLASS nExiste ou data java > data class
		if (!f.exists() || (fj!=null && fj.lastModified()>f.lastModified()) ) {
			//tentar compilar então
			load.compila(pos,""+fj);
			if (!f.exists()) {
				return null;
			}
		}
	
		//java.lang.LinkageError: duplicate class definition: br/org/guarani/servidor/vUsu
		Class r = null;
		if (f.exists()) {
			byte b[] = load.arq.carrega(f);
			logsL.deb(1,"carregou: "+f);
			try {
				r = defineClass(nome,b,0,b.length);
			} catch (java.lang.LinkageError e) {
				//pode ser: duplicate class definition
				// aguarda tempo, pra pegar ela no hash então
				try { Thread.sleep(500); } catch (Exception e1) {};
				cla = (clA)hCl.get(nome);
				if (cla!=null) {
					return cla.c;
				}
				logsL.grava("erro","no defineClass(), duplicate class? nome="+nome
					+" ERR:"+strL.erro(e));
				return null;
			} catch (Throwable e) {
				logsL.grava("erro","no defineClass() class="+nome+" ERR:"+strL.erro(e));
				return null;
			}
		}
		cla = new clA(f,r,fj);
		hCl.put(nome,cla);
		return r;
	}
	//**************************************************************
	boolean thread() {
		//th = new Thread();
		
		loaderInterface li = new loaderI(pos);
		
		sTh = cfg.getAtr("thread");
		Class cl = null;
		try {
			cl = loadClass(sTh);
		} catch (Exception e) {
			on("erro thread "+strL.erro(e));
			return false;
		}
		
		Method m = getMethod(cl,"setClassLoader");
		logsL.deb(1,"thread ok "+cl+" "+m);
		if (m!=null) {
			try {
				m.invoke(m.getDeclaringClass(),new Object[]{li});
			} catch (Exception e) {
				logsL.deb(0,"ERRO set ClassLoader: "+sTh+" "+strL.erro(e));
				logsL.deb(0,"tipo: "+li);
				System.exit(3);
			}
		}
		
		m = getMethod(cl,"main");
		
		//procura parametros
		String pr = null;
		for (int i=0;i<10;i++) {
			String s = load.conf(cfg,"param_"+i);
			if (s==null) {
				break;
			}
			pr = (pr==null?"":pr+"~~")+s;
		}
		
		String vpr[] = new String[0];
		if (!strL.vazio(pr)) {
			vpr = strL.palavraA(pr,"~~");
		}
		
		th tt = new th(m,new Object[]{vpr});
		trh = new Thread(tt);
		trh.start();
		
		
		return true;
	}
	//**************************************************************
	private File java(String nome) {
		for (int i=0;i<pathJ.size();i++) {
			File f = new File(pathJ.get(""+i)+"/"+strL.troca(nome,".","/")+".java");
			//logsL.deb(0,"<br>t="+f);
			if (f.exists()) {
				return f;
			}
		}
		return null;
	}
	//**************************************************************
	protected static void on(String s) {
		load.on(s);
	}
	//**************************************************************
	protected loader(ClassLoader CL,int Pos,xmlTagL x) {
		super(CL);
		pos = Pos;
		//iLoad = lo;
		cfg = x;
		path = load.conf(cfg,"path");
		pathJ = new Hashtable();
		for (int i=0;i<9;i++) {
			String s = load.conf(cfg,"pathJava_"+i);
			if (s==null) {
				break;
			}
			pathJ.put(""+i,s);
		}
		logsL.deb(1,""+pathJ);
	}
	/**************************************************************
	protected static boolean gravaTxt(File arquivo,String tx) {
		int t = tx.length();
		byte[] buf = new byte[t];
		for (int i=0;i<t;i++) {
			buf[i] = (byte)tx.charAt(i);
		}
		try {
			//InputStreamReader r = new InputStreamReader(new FileInputStream(f));
			FileOutputStream r = new FileOutputStream(arquivo);
			r.write(buf,0,t);
			r.close();
		} catch (IOException e ) {
			on("ERRO ARQ "+arquivo+"<br>");
			on(e.toString());
			return false;
		}
		return true;
	}
	//**************************************************************
	protected static String leTxt(File arquivo) {
		return new String(carrega(arquivo));
	}
	//**************************************************************
	protected static byte[] carrega(File arquivo) {
		int read = 0;
		int t = (int)arquivo.length();
		byte[] buf = new byte[t];
		try {
			//InputStreamReader r = new InputStreamReader(new FileInputStream(f));
			FileInputStream r = new FileInputStream(arquivo);
			read = r.read(buf,0,t);
			r.close();
		} catch (IOException e ) {
			on("ERRO ARQ "+arquivo+"<br>");
			on(e.toString());
			return new byte[0];
		}
		return buf;
	}
	*/
	//**************************************************************
	Method getMethod(Class o,String nome) {
		Method[] m = o.getDeclaredMethods(); //.getClass()
		for (short i=0;i<m.length;i++) {
			//on(m[i].getName());
			if (m[i].getName().equals(nome)) {
				return m[i];
			}
		}
		return null;
	}
	//**************************************************************
	//**************************************************************
	class th implements Runnable {
		Method mt;
		Object args[];
		//*********************************
		public void run() {
			try {
				mt.invoke(mt.getDeclaringClass(),args);
			} catch (IllegalAccessException e) {
				on(strL.erro(e));
			} catch (Exception e) {
				on(strL.erro(e));
			}
			on("****************************************************");
			on("main FIM");
		}
		//**************************************************************
		public th(Method Mt,Object Args[]) {
			mt = Mt;
			args = Args;
		}
	}
	//**************************************************************
	//**************************************************************
	class clA {
		File f;
		Class c;
		long dt;
		File fj;
		long dtj,dtc;
		//estatisticas
		int nFind=0;
		//**************************************************************
		protected clA(File F,Class C,File J) {
			dtc = dataL.ms();
			f = F;
			c = C;
			dt = f.lastModified();
			if (J!=null) {
				logsL.deb(1,"java="+J);
				fj = J;
				dtj = fj.lastModified();
			}
		}
	}
	//**************************************************************
	//**************************************************************
	class rsA {
		File f;
		URL r;
		long dt,dtc;
		int nFind=0;
		//**************************************************************
		protected rsA(File F,URL R) {
			dtc = dataL.ms();
			f = F;
			r = R;
			dt = f.lastModified();
		}
	}
}