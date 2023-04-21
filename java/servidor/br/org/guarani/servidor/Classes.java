/*
	* Signey John jan/2001
*/

package br.org.guarani.servidor;

import br.org.guarani.util.*;
import br.org.guarani.servidor.*;

import java.io.*;
import java.util.*;
import java.lang.reflect.Method;

//*************************
//*************************
public class Classes extends ClassLoader {
	private static boolean debug = false;
	protected String classe_root,classe_root_java,classe_compil;
	public Hashtable lista;
	protected ClassLoader parent;
	protected arqLog logC;
	//guarda ultimo pedido para findClass de dependentes.
	protected Pedido pedU;
	//*************************
	//inicializa objeto
	protected void inicia(String a,String b,String c) {
		classe_root = a;
		classe_root_java = str.seNull(b,a);
		classe_compil = c;
		Classe.setDirs(c,a,b);

		lista = new Hashtable();
		logC = logs.getOut("class");
		//compila = new Compila();
		//compila.inicia(classe_root,classe_root_java,classe_compil);
	}
	//*************************
	//executa classe
	//0 - não exite
	//1 - ok
	//2 - erro exec
	//3 - recarregar
	//4 - erro compilacao
	//5 - erro carga classe
	protected int exec(String nome,Pedido ped) {
		pedU = ped;
		Classe c = carrega(ped,nome,false);
		if (!c.existe()) {
			return 0;
		}
		/*boolean dp = testa_dependentes(ped);
		if (c.compilar()) {
			if (c.compila(ped)) {
				return 3;
			} else {
				return 4;
			}
		} else if (dp) {
			return 3;
		}
		*/

		if (!carregac(c)) {  
			return 5;
		}

		Prg classL = null;
		try {
			//String iClasse = c.classe.getSuperclass().getName();
			//logs.grava("class","supers="+c.superClasses);
			if (c.superClasses.indexOf("-br.org.guarani.servidor.Prg>")!=-1) {
				classL = (Prg)c.classe.newInstance();
				classL.initPed(ped);
				classL.run();
			} else {
				ped.erro("CLASSE: "+nome+", superClass="+c.superClasses+
					" não tem interface conhecida  conhecida!!",new Exception());
				return 2;
			}
		} catch (ExceptionInInitializerError e) {
			ped.erro("Classe.newInstance(): "+nome+"<hr>"+e);
			return 2;
		} catch (NoClassDefFoundError nc) {
			ped.erro("Classe.newInstance(): "+nome+"<hr>"+nc);
			return 2;
		} catch ( Exception t ) {
			ped.erro(nome,t);
			return 2;
		}
		return 1;
	}
	//*************************
	private synchronized boolean carregac(Classe c) {
		if (c.classe==null) {
			byte[] buf = c.carrega();
			int t = buf.length;
			if (t>0) {
				Class c1 = defineClass(str.troca(c.nome,"/","."),buf,0,t);
				if (debug) {
					logs.grava("define.Class="+c.nome);
				}
				c.classe = c1;
				//ver superClasses
				String ssc="";
				Class sc = c.classe;
				while (sc!=null) {
					Class vc[] = sc.getInterfaces();
					for (int i=0;i<vc.length;i++) {
						ssc += "-"+vc[i].getName()+"> ";
					}
					sc = sc.getSuperclass();
				}
				c.superClasses = ssc;
			} else {
				return false;
			}
		}
		return true;
	}
	//*************************
	//carrega classe
	protected synchronized Classe carrega(Pedido ped,String nome,boolean dp) {
		Classe s;
		s = (Classe)lista.get(nome);
		if (s==null) {
			s = new Classe(ped,nome);
			s.dep = dp;
			lista.put(nome,s);
		}
		return s;
	}
	//*************************
	//carrega classe
	protected synchronized boolean testa(Pedido ped) {
		boolean r = false;
		String cm = "";
		Classe c;
		for (Enumeration e = lista.elements() ; e.hasMoreElements() ;) {
			c = (Classe)e.nextElement();
			//if (c.dep && c.compilar()) {
			if (c.compilar()) {
				cm += c.arquivo()+" ";
			} else if (c.recarregar()) {
				r = true;
			}
		}
		//************************
		if (cm.length()!=0) {
			if (Classe.compila(ped,cm)) {
				r = true;
			}
		}
		if (r) {
   
		}
		return r;
	}
	//*************************
	//jvm chama esta para carregar classes dependentes
	protected Class findClass(String nome) {
		nome = str.troca(nome,".","/");
		Classe c = carrega(pedU,nome,true);
		if (carregac(c)) {
			return c.classe;
		} else {
			return null;
		}
	}
	//*************************
	public String getHtml(String s) {
		Classe c = (Classe)lista.get(s);
		return c.mostra();
	}
}
