/*
	* Signey John jan/2001.
	* fev/2002 - 
	* jun/2013 - se lotado lista tarefas lotadas... e exit 77
	*/

package br.org.guarani.servidor;

import br.org.guarani.util.*;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.lang.reflect.*;

//gcj##
//import org.apache.tomcat.net.*;

public class Servico implements Runnable {
	protected ServerSocket ss; //soket server
	protected Socket sp; //soket pedido
	public boolean rodando;
	protected int port, nTsk, timeOut=0,timeOutTask=0;
	protected Hashtable cnf;
	//protected boolean ssl;


	public String cla;
	public String nome;

	public ProtocoloAbstrato fila[];
	public Thread tarefa[];
	public Socket filaSk[];
	public long tempo[];

	public long inicio;
	protected int prioridadetarefa;

	//*************************************************
	public String Cfg() {
		String r = "",s;
		for (Enumeration e = cnf.keys() ; e.hasMoreElements() ;) {
			s = (String)e.nextElement();
			r += s+": <b>"+cnf.get(s)+"</b> - ";
		}
		return r;
	}
 
	//*************************************************
	public String Status() {
		return "Rodando: "+rodando+"<br>Inicio: "+data.strSql(inicio);
	}

	//*************************************************
	//*************************************************
	public Servico(String cl,Hashtable c) {
		cnf = c;

		nome = cl;
		cla = str.seNull((String)cnf.get("classe"),str.leftAt(nome+":",":"));
		prioridadetarefa = Integer.parseInt(str.seNull((String)cnf.get("prioridadetarefa"),"4"));

		//ssl = cnf.get("ssl")!=null;
		timeOut = Integer.parseInt(str.seNull((String)cnf.get("timeout"),"0"));
		timeOutTask = Integer.parseInt(str.seNull((String)cnf.get("timeouttask"),"0"));
		//setSoTimeout

		logs.grava("servidor","---------------------------------------------------");
		logs.grava("servidor","Classe: "+cla);

		//mostra configuração
		for (Enumeration e = cnf.keys() ; e.hasMoreElements() ;) {
			String s = (String)e.nextElement();
			o(s+"="+cnf.get(s));
		}

		port = Integer.parseInt(str.seNull((String)cnf.get("porta"),"80"));
		nTsk = Integer.parseInt(str.seNull((String)cnf.get("nTask"),"16"));

		//inicializa fila de protocolos
		tarefa = new Thread[nTsk];
		fila = new ProtocoloAbstrato[nTsk];
		filaSk = new Socket[nTsk];
		tempo = new long[nTsk];
		for (int i=0;i<nTsk;i++) {
			try {
				fila[i] =  (ProtocoloAbstrato)Class.forName(cla).newInstance();
				fila[i].init(cnf);
			} catch (IllegalAccessException iae) {
				logs.grava("servidor",cla+", ERRO classe: "+iae);
			} catch (InstantiationException ie) {
				logs.grava("servidor",cla+", ERRO classe: "+ie);
			} catch (ClassNotFoundException cnfe) {
				logs.grava("servidor",cla+", ERRO classe: "+cnfe);
			}

		}

	}

	protected void o(String a) {
		logs.grava("servidor",a);
	}

	//*****************************************//
	// roda task soket server
	public void logTarefasTrancadas() {
		for (int i=0;i<nTsk;i++) {
			logs.grava("servidor",cla+", LOTADO!! detalhe Pedido="+fila[i].detalhePedido());
		}
	}

	//*****************************************//
	// roda task soket server
	public void run() {
		logs.grava("servidor","###run: nTsk="+nTsk+" "+cla+":"+port);
		int atendido = 0, i, mxTsk = -1;
		int lt=0;
		int tAtend = 0;
		inicio = System.currentTimeMillis();

		//inicializa soket server
		try {
			ss = fila[0].criaServerSocket(port);
			rodando = true;
   
		} catch (IOException ioe) {
			logs.grava("servidor","ERRO, criando soket, porta="+port+" err="+ioe.getMessage());
			rodando = false;
		} catch (Exception ioe) {
			logs.grava("servidor","ERRO, criando soket, porta="+port+" err="+ioe.getMessage());
			rodando = false;
		}


		//roda e aguarda pedidos q são
		//redirecionados a tasks
		while (rodando) {

			tAtend++;
			//procura task livre
			atendido = -1;
			while (atendido==-1) {
				for (i=0;i<nTsk;i++) {
					if (atendido!=-1) {
						testaTimeOutTask(i);
					} else if (!fila[i].rodando()) {
						if (mxTsk<i) mxTsk = i;
						atendido = i;
						break;
					} else {
						if (tarefa[i] !=null && !tarefa[i].isAlive()) {
							//tarefa pendurou...
							logs.grava("servidor",new Exception("task PENDUROU"),cla+", "+fila[i].getPedido());
							fila[i].rodando(false);
							try {
								filaSk[i].close();
							} catch (Exception e) {
								logs.grava("servidor",e,"close tarefa:");
							}
						} else if (testaTimeOutTask(i)) {
							i--;
						}
					}
				}
				if (atendido==-1) {
					logs.grava("servidor",nome+"="+cla+", LOTADO!!, tent=("+i+") esperando..."+(lt++)+" sp="+sp);
					if (lt==5) {
						logTarefasTrancadas();
					} else if (lt>200) {
						logTarefasTrancadas();
						System.exit(77);
					}
					espera(250);
				} else {
					lt = 0;
				}
			}

			try {
				//AGUARDA pedido
				sp = ss.accept();
				if (!rodando) {
					break;
				}
				//sp.setSoLinger(true,0);
				//sp.setTcpNoDelay(true);

				if (tarefa[atendido]!=null) {
					if (tarefa[atendido].isAlive()) {
						logs.grava("servidor",cla+" Não RODANDO, mas isAlive!!!"+atendido);
					}
				}

				if (false && filaSk[atendido]!=null 
					&& filaSk[atendido].getSoLinger()==100) {
					logs.grava("servidor","tentando usar soket="+filaSk[atendido]);
				} else {
					if (timeOut!=0) sp.setSoTimeout(timeOut);
					if (filaSk[atendido]!=null) {
      
					}
					filaSk[atendido] = sp;
				}
				fila[atendido].setSocket(filaSk[atendido]);
				if (sp==null) {
					logs.grava("servidor","ss.accept() retornou null?");
				} else if (!fila[atendido].erro) {
					fila[atendido].rodando(true);
					tempo[atendido] = System.currentTimeMillis();
					tarefa[atendido] = new Thread(fila[atendido]);
					tarefa[atendido].setPriority(prioridadetarefa);
					//EXECUTA
					tarefa[atendido].start();
					//ogs.grava(atendido+"="+tempo[atendido]+" ");
				} else {
					logs.grava("servidor","Erro setando socket?");
				}
			} catch( final IOException ioe ) {
				logs.grava("servidor",ioe,"socket="+sp);
				try {
					sp.close();
				} catch(Exception e ) {
					logs.grava("servidor",e,cla+"Fechando Socket:"+sp);
				}
			}
		}

		//fecha soket server
		try {
			rodando = false;
			logs.grava("servidor","stop soket serv");
			ss.close();
		} catch (Exception ioe) {
			logs.grava("servidor","ERRO, stop soket porta="+port+" serv="+
				ioe.getMessage());
			rodando = false;
		}

	}

	public boolean testaTimeOutTask(int i) {
		if (timeOutTask>0 && 
			System.currentTimeMillis()-tempo[i]>timeOutTask) {
			//aborta tarefa = timeout
			//java não possui abortar tarefa??!!
			logs.grava("servidor",cla+", ABORTANDO tempo="+
				(System.currentTimeMillis()-tempo[i])+
				"f="+fila[i].getTempoRodando()+">"+timeOutTask+
				"="+fila[i].getPedido());
			fila[i].rodando(false);
			tarefa[i] = null;
			return true;
		}
		return false;
	}
 
 
	//*****************************************//
	//para o servidor
	public void stop() {
		rodando = false;
	}

	//*****************************************//
	private void espera(int n) {
		try {
			//wait(250);
			Thread.sleep(n);
		} catch (InterruptedException e) {
		}
	}

}
