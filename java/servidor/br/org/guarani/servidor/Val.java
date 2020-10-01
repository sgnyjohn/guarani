/*
	* sjohn@via-rs.net jan/2001. ago/2002
	recebe autenticação do servidor de autenticação.
*/

package br.org.guarani.servidor;

import br.org.guarani.util.*;

import java.net.*;
import java.io.*;
import java.util.*;

//********************************
//********************************
public class Val extends ProtocoloAbstrato {
	public static boolean debug = true;
	private BufferedReader i;
	private PrintWriter o;

	protected String pedido;
	protected Hashtable cnf;
	String permissao,sErro,geto,tm;
	//public static Hashtable aguarda = new Hashtable();
	//********************************
	public void run() {
		String ip=null,us=null,ses=null,ch=null;

		sErro = null;
		nPedidos++;
		String cmd, arg;

		Random rd = new Random(nPedidos*13+7);

		in = data.ms();

		//testa origem
		if (permissao.indexOf(" "
			+sp.getInetAddress().getHostAddress()+" ")==-1){
			logs.grava("val","VAL: sem permissão "+permissao+"=="+sp);
			erro = true;
		}

		if (!erro & rodando) abreStream();

		while (!erro & rodando) {

			lePedido();
			if (pedido.compareTo("?")!=0) {
				logs.grava("val","VAL=<>?"+sp);
				erro = true;
			}

			if (!erro) {

				ch = ""+(in-rd.nextLong());
				o.println(ch);

				lePedido();
				if (str.leftAt(pedido," ").equals(ch)) {
					ip = str.substrAt(pedido," ");
					ses = str.substrAt(ip,"~");
					ip = str.leftAt(ip,"~");
				} else {
					logs.grava("val","VAL, tentativa quebra segurança pedido=("+pedido+") ch=("+ch+") soket="+sp);
				}

				lePedido();
				if (str.leftAt(pedido," ").equals(ch)) {
					us = str.substrAt(pedido," ");
				} else {
					logs.grava("val","VAL, tentativa quebra segurança="+sp);
				}

				lePedido();
				if (str.leftAt(pedido," ").compareTo(ch)==0) {
					tm = str.substrAt(pedido," ");
				} else {
					logs.grava("val","VAL, tentativa quebra segurança="+sp);
				}

				geto = "ip="+ip+"=us="+us+"=tm="+tm+" sk="+sp;
				//logs.grava("val",geto);
				acum(ip,ses,us,geto);
			}
			rodando = false;
		}

		try {
			o.close();
			i.close();
			sp.close();
			sp = null;
		} catch (IOException ioe) {
			logs.grava("val","VAL: Error. Fechando conexção!" );
			erro = true;
		}
		fi = data.ms();
		tempo += fi-in + Integer.parseInt(tm);
		rodando = false;
	}
	//********************************
	public static void log(String msg) {
		logs.grava("val","Val: "+msg);
	}
	//********************************
	public static synchronized void acum(String ip,String idSess,String us,String msg) {
		
		//sessao
		httpSessao ses = httpSessao.getSessao(idSess);
		if (ses==null) {
			log("ERRO: validação para sessão não existente: "+idSess);
			return;
		}
		
		//logOFF
		Usuario xu = ses.getUsuario();
		if (us.equals("?")) {
			if (xu==null) {
				log("ERRO: logoff de usuario nao setado!");
				return;
			}
			xu.invalida();
			return;
		}
		
		
		//usuário
		Usuario u = Usuario.get(idSess,us);
		u.valida();
		if (debug) { 
			logs.grava("val","logon="+u+" ch="+idSess+" msg="+msg);
		}
		//httpSessao.setUsuario(ip,idSess,u);
		ses.setUsuario(u);
	}
	/********************************
	public static Usuario getUsuario(String ip,String idSess) {
		return (Usuario)usu.get(ip+"~"+idSess);
	}
	//********************************
	public static synchronized boolean aguardeVal(Pedido ped) {
		httpSessao ses = ped.getSessao();
		String ch = ses.getIp()+"~"+ses.getId();
		String s = (String)aguarda.get(ch);

		long t = data.ms();
		if (s!=null) {
			if (false && t-str.longo(s,-1)<2000) {
				return false;
			} else {
				logs.grava("val","aguardeVal duplo, removendo="+ch
					+" t="+(t-str.longo(s,-1))+" "+s+"="+ped);
				aguarda.remove(ch);
			}
		}
		

		Usuario u = (Usuario)usu.get(ch);
		if (u!=null) {
			u.invalida();
		}
		if (debug) {
			logs.grava("val","incluindo aguardeVal="+ch+"="+ped);
		}
		logs.grava("val","Aguardando val de: "+ch);
		aguarda.put(ch,""+t);
		return true;
	}
	*/
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
	}
	//********************************
	public void setSocket(Socket s) {
		sp = s;
		erro = false;
	}
	//********************************
	//atende pedido
	private void abreStream() {
		try {
			i = new BufferedReader(new InputStreamReader( sp.getInputStream() ));
			o = new PrintWriter( sp.getOutputStream(), true );
		} catch( final IOException ioe ) {
			logs.grava("val","VAL: Error. Abrindo Stream" );
			erro = true;
		}

	}
	//********************************
	//le pedido
	private void lePedido() {
		pedido = null;
		try {
			pedido = i.readLine();
			//logs.grava("VAL ped="+pedido);

		} catch (IOException ioe) {
			logs.grava("val","VAL: Error. lendo Pedido="+ioe );
			erro = true;
		}

	}
	//********************************
	public String getPedido() {
		return geto;
	}
}
