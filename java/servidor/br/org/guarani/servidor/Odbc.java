/*
	* Signey John jan/2001.

	servidor quebra galho para compartilhar odbc windows
	jul/2002 = correção conecta não static

	*/

package br.org.guarani.servidor;

import br.org.guarani.util.*;

import java.net.*;
import java.io.*;
import java.util.*;

import java.sql.*;

//atende pedido http - thread
public class Odbc extends ProtocoloAbstrato {
protected String del;
	//protected Socket sp; //soket pedido

	protected BufferedReader i;
	public PrintWriter o;

	protected String pedido;
	protected Hashtable cnf;
	//public boolean rodando;
	//public Pedido pedido;
	//public long tempo,in,fi,nPedidos,nBytes;
	//public boolean erro;

	protected static Hashtable vCon=null;
	//protected static Hashtable vStmt;

	public boolean con;
	protected Connection oCon;
	protected Statement stmt;
	protected ResultSet rs;
	protected ResultSetMetaData rsmd;
	protected int tCmp;
	protected String sErro, sDB, permissao, geto="";
	//protected tReg = 20;

	//********************************
	//retorna serversocket
	public ServerSocket criaServerSocket(int porta) throws IOException {
		return new ServerSocket(porta);
	}


	//********************************
	//inicializa
	public void init(Hashtable c) {
		rodando = false;
		erro = false;
		cnf = c;
		nBytes = 0;
		permissao = " "+str.seNull((String)cnf.get("permissao"),"127.0.0.1")+" ";
		if (vCon == null) {
			vCon = new Hashtable();
			//vStmt = new Hashtable();
		}
	}

	//********************************
	//seta socket
	public void setSocket(Socket s) {
		erro = false;
		sp = s;
	}


	//********************************
	//atende pedido
	public void run() {

		del = "~ç~";
		sErro = null;
		nPedidos++;
		String cmd, arg;

		//rodando é setado pelo controle de tarefas
		//rodando = true;
		in = System.currentTimeMillis();

		//testa origem
		if (permissao.indexOf(" "+sp.getInetAddress().getHostAddress()+" ")==-1){
			logs.grava("ODBC: sem permissão "+permissao+"=="+sp.getInetAddress().getHostAddress());
			erro = true;
		}

		if (!erro & rodando) abreStream();

		while (!erro & rodando) {

			lePedido();
			if (pedido==null) {
				//cliente abortou falta estatistica
				logs.grava("ODBC: cliente ABORTOU odbc!");
				pedido = "clo abort";
			}
			cmd = str.leftAt(pedido," ");
			arg = str.substrAt(pedido," ");

			if (cmd.compareTo("clo")==0) {
				rodando = false;

			} else if (cmd.compareTo("del")==0) {
				del = arg;
    

			} else if (cmd.compareTo("con")==0) {
				exec(cmd,arg);

			} else {

				if (!con) {
					erro = true;
					sErro = "não conectado..";

				} else {
					exec(cmd,arg);

				}

			}

		}

		//logs.grava("FECHANDO ODBC!!"+sErro);
		try {

			if (stmt!=null) stmt.close();
			//if (oCon!=null) oCon.close();
			rsmd = null;
			rs = null;
			stmt = null;
			//oCon = null;

			if (o!=null) o.close();
			//sp.shutdownInput();
			//sp.shutdownOutput();
			sp.close();
			sp = null;

		} catch (SQLException se) {
			sErro = "ERRO fechando: "+se;
			se.printStackTrace();

		} catch (IOException ioe) {
			logs.grava( "ODBC: Error. Fechando conexção!" );
			erro = true;
		}


		fi = System.currentTimeMillis();
		tempo += fi-in;
		rodando = false;

	}



	//********************************
	//atende pedido
	private void abreStream() {

		try {
			i = new BufferedReader(new InputStreamReader( sp.getInputStream() ));
			o = new PrintWriter( sp.getOutputStream(), true );
		} catch( final IOException ioe ) {
			logs.grava( "ODBC: Error. Abrindo Stream" );
			erro = true;
		}

	}

	//********************************
	//le pedido
	private void lePedido() {

		try {
			pedido = i.readLine();
			//logs.grava(pedido);

		} catch (IOException ioe) {
			logs.grava( "ODBC: Error. lendo Pedido="+ioe );
			erro = true;
		}

	}

	//*****************************************//
	private void exec(String cmd, String arg) {

		//logs.grava("cmd="+cmd+" arg="+arg);

		String r="",cmp;
		int i;

		try {
			if (cmd.compareTo("con")==0) {
				conecta(this,cmd,arg);
				if (con) {
					con = false;
					stmt = oCon.createStatement();
					con = true;
					r = "OK! "+sDB;
				}
				geto = r;

			} else if (cmd.compareTo("del")==0) {
				r = "OK! del="+del;
				geto = cmd+"->"+arg;

			} else if (cmd.compareTo("que")==0) {
				nPedidos++;
				rs = stmt.executeQuery(arg);
				rsmd = rs.getMetaData();
				r = "OK! ";
				tCmp = rsmd.getColumnCount();
				for (i=1;i<=tCmp;i++) {
					r += rsmd.getColumnName(i)+del;
				}
				geto = cmd+"->"+arg;

			} else if (cmd.compareTo("nex")==0) {
				//logs.grava("valor rs="+rs);
				r = "";
				if (rs.next()) {
					for (i=1;i<=tCmp;i++) {
						try {
							cmp = rs.getString(i);
							if (cmp==null) {
								r += "NULL"+del;
							} else if (cmp.compareTo("")==0) {
								r += " "+del;
							} else {
								r += cmp+del ;
							}
						} catch (SQLException se) {
							r += "NULL"+del;
						}

					}
					r = "OK! "+r.length()+"\r\n"+r;
				} else {
					r = "fim";
					rs.close();
				}

			} else if (cmd.compareTo("upd")==0) {
				nPedidos++;
				int nr = stmt.executeUpdate(arg);
				r = "OK! "+nr;
				geto += "<br>"+cmd+"->"+arg;

			} else {
				sErro = cmd+", comando inválido!";
				geto += "<br>"+cmd+"->"+arg;

			}

		} catch (SQLException se) {
			sErro = "ERRO cmd="+cmd+": "+se+"<hr>"+arg;
			se.printStackTrace();
		}

		if (sErro != null) {
			logs.grava("ODBC: "+sErro);
			erro = true;
			geto += "<br>ERRO: "+cmd+"->"+arg;
			r = "?ER? "+sErro;
		}

		nBytes += r.length()+2;
		o.println(r);

	}

	//********************************
	private static synchronized void conecta(Odbc odbc,String cmd, String arg) {

		odbc.sDB = arg;
		String sCon = "jdbc:odbc:" + odbc.sDB;
		odbc.oCon = (Connection)vCon.get(sCon);

		odbc.con = true;

		try {
			if (odbc.oCon != null) {
				if (odbc.oCon.isClosed()) {
					//deleta
					vCon.remove(sCon);
				} else {
					return;
				}
			}
		} catch (SQLException se) {
			try {
				odbc.stmt.close();
			} catch (SQLException se1) {
			}
			try {
				vCon.remove(sCon);
				odbc.oCon.close();
			} catch (SQLException se1) {
			}
		}

		try {
			odbc.con = false;
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver").newInstance();
			odbc.oCon = DriverManager.getConnection(sCon);
			vCon.put(sCon,odbc.oCon);
			//logs.grava(vCon);
			odbc.con = true;

		} catch (IllegalAccessException iae) {
			odbc.sErro = "ERRO cmd="+cmd+": "+iae+"<hr>"+arg;
			iae.printStackTrace();
		} catch (InstantiationException ie) {
			odbc.sErro = "ERRO cmd="+cmd+": "+ie+"<hr>"+arg;
			ie.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			odbc.sErro = "ERRO cmd="+cmd+": "+cnfe+"<hr>"+arg;
			cnfe.printStackTrace();
		} catch (SQLException se) {
			odbc.sErro = "ERRO cmd="+cmd+": "+se+"<hr>"+arg;
			se.printStackTrace();
		}

	}

	//********************************
	public String getPedido() {
		return geto;
	}

	/********************************
	public boolean rodando() {
		return rodando;
	}
	//********************************
	public boolean rodando(boolean r) {
		rodando = r;
		return rodando;
	}

	//********************************
	public long getTempo() {
		return tempo;
	}

	//********************************
	public long getBytes() {
		return nBytes;
	}

	//********************************
	public long getTempoRodando() {
		if (rodando) {
			return System.currentTimeMillis()-in;
		}
		return fi-in;
	}

	//********************************
	public long getNPedidos() {
		return nPedidos;
	}

	//********************************
	public void stop() {
		rodando = false;
	}
	*/

}
