/*
signey / 2001 
* signey fev 2019
* 		no guarani.conf o nome da classe deve ser listado na 
* 				opção "init" para rodar ao levantar o servidor.
* 		ao iniciar a classe que implementa staticThread deve 
* 			setar pAtu  = 60000 e chamar o método inicia();
* 		deve ter o metodo passo
*/
package br.org.guarani.servidor;

import java.util.*;
import java.io.*;
import java.net.*;
import java.lang.*;

import br.org.guarani.servidor.*;
import br.org.guarani.util.*;
import pwwws.*;

//***************************
public abstract class staticThread implements Runnable {
	//controle de reexecução e estatisticas
	public String[] staticT;
	public Pag pg;
	
	//instancia
	public String Classe;
	long classVer;
	public long pAtu=60000; //periodicidade
	public long nr; //número de reinicio
	public long ne; //número de execuções
	public long uNe; //última execução
	public long mNe; //média entre execuções
	public long mxMs; //tempo maximo  execuções
	public long tT; //tempo total
	public long nEr; //numero de erros
	public long uExOK; //ultima exec OK
 
	public boolean rodando = false,aborta = false,dupla=false;

	//***************************
	public boolean ativa() {
		//ogs.grava("prox ex "+data.strSql(uNe+pAtu)+" > "+data.strSql(data.ms()-(pAtu*4)));
		// prox exec > agora - 1/4 periodicidade
		return uNe+pAtu>data.ms()-pAtu/4;
	}
	
	//***************************
	public void onD(String s) {
		if (pg!=null) {
			pg.on("<br>"+s);
		}
	}
	//***************************
	public void inicia() {
		inicia(false);
	}
	//***************************
	public String ch() {
		if (Classe==null)  {
			Classe = this.getClass().getName();
		}
		return Classe;
	}
	//***************************
	public void inicia(boolean seNaoExiste) {
		staticThread ta = (staticThread)staticThread.get(ch());
		if (ta!=null && !dupla && !ta.aborta) {
			if (!seNaoExiste) {
				log("Thread dupla não permitida: "+ch());
			}
		} else {
			Thread t = new Thread(this);
			t.start();
		}
	}
	//***************************
	public void log(String s) {
		logs.grava("sThread",s);
		if (str.equals(s,"ERRO:")) {
			logs.grava("erro",s);
		}
	}
	//***************************
	public String toString() {
		return ""+classVer;
	}
	//***************************
	public abstract void on(String s);

	//***************************
	public abstract void passo();
		
	//***************************
	public void aborta() {
		aborta = true;
	}
	//***************************
	public final void run() {
		
		String Ch = ch();
		
		//recupera dados exec anterior?
		if (staticT==null) {
			//lista de threads
			Hashtable lt = (Hashtable)Guarani.get("staticThread");
			if (lt==null) {
				lt = new Hashtable();
				Guarani.put("staticThread",lt);
			}
			lt.put(Ch,this);
			
			//dados desta thread
			staticT = (String[])Guarani.get("staticThread."+Ch);
			if (staticT!=null) {
				try {
					nr = str.longo(staticT[0],-1);
					ne = str.longo(staticT[1],-1);
					uNe = str.longo(staticT[2],-1);
					mNe = str.longo(staticT[3],-1);
					tT = str.longo(staticT[4],-1);
					nEr = str.longo(staticT[5],-1);
					uExOK = str.longo(staticT[6],-1);
					mxMs = str.longo(staticT[7],-1);
				} catch (Exception e) {
				}
			}
		}
		
		
		String sv="0";
		classVer = Guarani.classVer;
		log("INICIO thread "+Ch
			+" ver"+sv+"="+classVer
		);
		
		//executa
		rodando = true;
		nr++;
		if (uNe == 0) {
			uNe = data.ms()-pAtu;
		}
		while (rodando && classVer==Guarani.classVer && !aborta) {
			long nNe = data.ms();
			if (nNe-uNe>=pAtu) {
				try {
					passo();
					uExOK = nNe;
					//java.lang.ExceptionInInitializerError
				} catch (java.lang.ExceptionInInitializerError e) {
					nEr++;
					log("ERRO: no passo thread: "+Ch+" = "+str.erro(e));
				} catch (Exception e) {
					nEr++;
					log("ERRO: no passo thread: "+Ch+" = "+str.erro(e));
				} catch (Throwable e) {
					nEr++;
					log("ERRO: no passo thread: "+Ch+" = "+str.erro(e));
				}
				long tp = data.ms()-nNe;
				mxMs = Math.max(mxMs,tp);
				tT += tp;
				ne++;
				//if (ne!=1) {
					//tempo médio de execução
					mNe = tT/ne;//(mNe*(ne-1)+(nNe-uNe))/ne;
				//}
				uNe = nNe; 
			}
			try {
				long t = Math.min(pAtu,Math.min(2000,nNe-uNe));
				if (t>0) {
					Thread.sleep(t);
				}
			} catch (Exception e) {
				log("ERRO sleep thread: "+Ch+" = "+e);
			}
		}
		rodando = false;
		log("FIM thread "+Ch
			+" ver"+sv+"="+classVer+" aborta="+aborta
		);
		
		//reinicia apos parada por compilação
		if (!aborta) {
			//guarda ambiente
			Guarani.put("staticThread."+Ch,
				new String[]{""+nr,""+ne,""+uNe,""+mNe,""+tT,""+nEr,""+uExOK,""+mxMs}
			);
			//staticThread.nova(Ch);
			nova(Ch);
		} 
	}
	//****************************************************************
	//****************************************************************
	// estáticas
	//****************************************************************
	//***************************
	public static Object get(String s) {
		Hashtable h = (Hashtable)Guarani.get("staticThread");
		if (s==null) {
			//retorna todas
			return h;
		}
		Object r = null;
		if (h!=null) {
			r = h.get(s);
			if (r!=null) {
				if (!(""+Guarani.classVer).equals(""+r)) {
					r = null;
				}
			}
		}
		return r;
	}
	//***************************
	public static void cmd(String cl_Ch,Pedido ped) {
		Object o = staticThread.get(cl_Ch);
		if (cl_Ch!=null) {
			staticThread t = (staticThread)o;
			if (t.aborta) {
				logs.grava("sThread","Start em "+cl_Ch+" "+ped);
				nova(cl_Ch);
			} else {
				logs.grava("sThread","Stop em "+cl_Ch+" "+ped);
				t.aborta();
			}
		}
	}
	//***************************
	public static boolean nova(String classe) {
		Class c = Guarani.findClass(classe);
		if (c!=null) {
			try {
				staticThread o = (staticThread)c.newInstance();
				return true;
			} catch (Exception e) {
				logs.grava("sThread","erro RE/INICIANDO "+classe+" "+e);
			}
		} else {
			logs.grava("sThread","CLASSE não encontrada?="+classe);
		}
		return false;
	}
	//***************************
	public static boolean ativa(String cl_Ch) {
		staticThread st = (staticThread)staticThread.get(cl_Ch);
		if (st==null) {
			return false;
		}
		return st.ativa();
	}
}
