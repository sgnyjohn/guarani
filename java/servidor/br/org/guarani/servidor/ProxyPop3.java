package br.org.guarani.servidor;

import java.io.*;
import java.net.*;
import java.util.*;

import br.org.guarani.util.*;

class ProxyPop3 extends ProtocoloAbstrato {
	private Socket sCli,sPop3;

	private BufferedReader fromCli;
	private PrintWriter toCli;

	private BufferedReader fromPop3;
	private PrintWriter toPop3;
 
	private String destino,geto,permissao,sErro;
	int timeout=0;
	protected Hashtable cnf;
	private boolean debugp;

	//guarani

	/*protected String pedido;
	protected Hashtable cnf;
	public boolean rodando;
	public long tempo,in,fi,nPedidos,nBytes;
	public boolean erro;
	String us,ip,permissao,destino,sErro,geto="vazio";
	*/

	//********************************
	public void user(String cmd,String st) {
		String er = "-ERR Unknown command: \""+cmd+"\"",s;
		if (cmd.equals("auth")) {
			cliPrintln("+OK Supported SASL mechanisms:");
			cliPrintln("X-NONE-SO-USE-APOP-OR-STLS");
			cliPrintln(".");
		} else if (cmd.equals("user") && st.indexOf("@")!=-1) {
			st = str.trimm(str.substrAt(st," "));
			destino = str.substrAt(st,"@");
			if (socketPop3()) {
				//le ok apos conexção
				logs.grava("pop3",servReadLine());
				//manda user e devolve
				String user = str.leftAt(st,"@");
				servPrintln("USER "+user);
				s = servReadLine();
				if (erro | s==null) {
					logs.grava("pop3","ERRO gravUSER: erro="+erro+" s="+s);
					erro = false;
					destino = null;
					cliPrintln(er);
				} else {
				logs.grava("pop3","OK USER: destino="+destino);
					cliPrintln(s);
				}
			} else {
				logs.grava("pop3","ERRO sockServUSER: destino="+destino);
				cliPrintln(er);
				destino = null;
			}
		} else {
			cliPrintln(er);
		}
	}
 
 
	//********************************
	public void run() {
		boolean fim = false;
		String cm,cmd;
		String st="";
		int nl;
  
		sErro = null;
		destino = null;
		nPedidos++;
  
		//manda ok
		cliPrintln("+OK ready");
  
		while(!erro && !fim) {
   
			cm = cliReadLine();
			if (cm==null) {
				erro = true;
				break;
			}
			cm = str.trimm(cm);
			//cmd = auth uidl noop rset dele retr top stat list user pass quit
			cmd = str.leftAt(cm+" "," ").toLowerCase();
			if (destino==null) {
				user(cmd,cm);
			} else {
				servPrintln(cm);
    
				//multi linha, fim com .?
				if ("-top-retr-auth-uidl-".indexOf("-"+cmd+"-")!=-1 |
				(cmd.equals("list") && cm.indexOf(" ")==-1) ) {
					//logs.grava(cmd+" multi linha");
					nl = 0;
					while (true) {
						st = servReadLine();
						if (st==null) { 
							break;
						}
						cliPrintln(st);
						nl++;
						if (st.equals(".") | 
						(nl==1 && str.leftAt(st," ").equals("-ERR"))) {
							break;
						}
					}
				} else {
					//logs.grava(cmd+" simples linha");
					cliPrintln(servReadLine());
				}
			}
   
			fim = str.trimm(cm).toLowerCase().equals("quit");
   
		}
  
		logs.grava("pop3","closing...str="+st+" fim="+fim);
		try {
			sCli.close();
		} catch (Exception e) {
		}

		if (!fim) {
			abortaPop3();
		}

		try {
			toPop3.close();
		} catch (Exception e) {
			logs.grava("pop3","Erro Fechando toPop3="+e);
		}
		try {
			fromPop3.close();
		} catch (Exception e) {
			logs.grava("pop3","Erro Fechando fromPop3="+e);
		}
		try {
			sPop3.close();
		} catch (Exception e) {
			logs.grava("pop3","Erro Fechando sPop3="+e);
		}
  
		//logs.grava("pop3","");
		fi = System.currentTimeMillis();
		tempo += fi-in;
		//logs.grava("VAL=tmp="+tempo);
		rodando = false;
  
	}
 
		//********************************
	//retorna serversocket
	public ServerSocket criaServerSocket(int porta) throws IOException {
		return new ServerSocket(porta);
	}

	public void init(Hashtable c) {
		rodando = false;
		erro = false;
		cnf = c;
		nBytes = 0;
		permissao = " "+str.seNull((String)cnf.get("permissao"),"127.0.0.1")+" ";
		//destino = str.seNull((String)cnf.get("destino"),"127.0.0.1");
		timeout = Integer.parseInt(str.seNull((String)cnf.get("timeout"),"60000"));
		debugp = str.seNull((String)cnf.get("debugp"),"").equals("on");
	}

	public void setSocket(Socket s) {
		sCli = s;
		erro = false;

		in = System.currentTimeMillis();

		//ip autorizado?
		if (permissao.indexOf(" "+sCli.getInetAddress().getHostAddress()+" ")==-1){
			logs.grava("pop3","VAL: sem permissão "+permissao+"=="+sCli);
			erro = true;
			return;
		}

		try {
			//Mailer origem

			fromCli = new BufferedReader( new InputStreamReader ( sCli.getInputStream()));
			toCli = new PrintWriter( new BufferedWriter ( new OutputStreamWriter ( sCli.getOutputStream())),true);

			sCli.setSoTimeout(timeout);
   
			logs.grava("pop3","setSocket(), cliente="+sCli);

		} catch (Exception e) {
			logs.grava("pop3","setSocket(), ERRO setSocket: "+e);
			erro = true;
		}

	}

	//********************************
	public boolean socketPop3() {
		try {
			//Pop3 Destino
			sPop3 = new Socket(InetAddress.getByName(destino),110);
			fromPop3 = new BufferedReader( new InputStreamReader ( sPop3.getInputStream()));
			toPop3 = new PrintWriter( new BufferedWriter ( new OutputStreamWriter ( sPop3.getOutputStream())),true);
			logs.grava("pop3","socketPop(), saida="+sPop3);

		} catch (Exception e) {
			logs.grava("pop3","socketPop(), ERRO setSocket: "+e);
			erro = true;
		}
  
		return !erro;

	}

	private void erro(String s,Exception e) {
		erro = true;
		logs.grava("pop3","ERRO: "+s+"\n"+e);
		abortaPop3();
	}

	public void statu(String s) {
		geto = destino+"="+sCli+" = "+s;
	}
	//
	//********************************
	public void abortaPop3() {
		if (destino!=null) {
			try {
				destino = null;
				toPop3.println("QUIT");
				sPop3.close();
			} catch (Exception e) {
			}
		}
	}
	//
	//********************************
	public String cliReadLine() {
		statu("lendo cli");
		String s=null;
		try {
			s = fromCli.readLine();
		} catch (Exception e) {
			erro("cliReadLine: "+sCli,e);
		}
		if (s!=null) {
			nBytes += s.length();
			if (debugp) {
				if (str.leftAt(s," ").toLowerCase().equals("pass")) {
					logs.grava("pop3",destino+"=pass *****");
				} else {
					logs.grava("pop3",destino+"="+s);
				}
			}
		}
		statu("fim lendo cli");
		return s;
	}
	//********************************
	public void cliPrintln(String s) {
		statu("gravando cli");
		if (s!=null) {
			nBytes += s.length();
		}
		try {
			toCli.println(s);
		} catch (Exception e) {
			erro("cliPrintln: "+sCli,e);
		}
		statu("fim gravando cli");
	}
	//********************************
	public String servReadLine() {
		statu("lendo serv");
		String s=null;
		try {
			s = fromPop3.readLine();
		} catch (Exception e) {
			erro("servReadLine: "+sPop3,e);
		}
		if (s!=null) {
			nBytes += s.length();
		}
		statu("fim lendo serv");
		//logs.grava("pop3",destino+"="+s);
		return s;
	}
	//********************************
	public void servPrintln(String s) {
		statu("gravando serv0");
		if (s!=null) {
			nBytes += s.length();
		}
		try {
			toPop3.println(s);
		} catch (Exception e) {
			erro("servPrintln: "+sPop3,e);
		}
		statu("fim gravando serv");
	}
	//********************************
	public String getPedido() {
		return geto;
	}
}
