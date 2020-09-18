package br.org.guarani.servidor;

import java.io.*;
import java.net.*;
import java.util.*;

import br.org.guarani.util.*;

class ProxySmtp extends ProtocoloAbstrato {
	private Socket socketToSMTP;
	private BufferedReader fromMailer;
	private PrintWriter toMailer;
	private PrintWriter toSMTP;
	private BufferedReader fromSMTP;

	//guarani

	protected String pedido;
	protected Hashtable cnf;
	//public boolean rodando;
	//public long tempo,in,fi,nPedidos,nBytes;
	//public boolean erro;
	String us,ip,permissao,destino,sErro,geto="vazio";
	int timeout=0;
	//********************************
	//retorna serversocket
	public ServerSocket criaServerSocket(int porta)
		throws IOException {
		return new ServerSocket(porta);
	}
	//********************************
	public void init(Hashtable c) {
		rodando = false;
		erro = false;
		cnf = c;
		nBytes = 0;
		permissao = " "
			+str.seNull((String)cnf.get("permissao"),"127.0.0.1")+" ";
		destino = str.seNull((String)cnf.get("destino"),"127.0.0.1");
		timeout = Integer.parseInt(
			str.seNull((String)cnf.get("timeout"),"60000"));
	}
	//********************************
	public void setSocket(Socket s) {
		sp = s;
		erro = false;

		in = System.currentTimeMillis();

		//ip autorizado?
		if (permissao.indexOf(
			" "+sp.getInetAddress().getHostAddress()+" ")==-1){
			logs.grava("smtp","VAL: sem permissão "+permissao+"=="+sp);
			erro = true;
			return;
		}

		try {
			//Mailer origem
			fromMailer = new BufferedReader(
				new InputStreamReader ( sp.getInputStream())
			);
			toMailer = 
				new PrintWriter(
					new BufferedWriter (
						new OutputStreamWriter ( sp.getOutputStream())
					)
				,true);

			//SMTP Destino
			socketToSMTP = new Socket(InetAddress.getByName(destino),25);
			fromSMTP = new BufferedReader(
				new InputStreamReader ( socketToSMTP.getInputStream())
			);
			toSMTP = 
				new PrintWriter(
					new BufferedWriter (
						new OutputStreamWriter ( socketToSMTP.getOutputStream())
					)
				,true);

			logs.grava("smtp","saida="+socketToSMTP);

		} catch (Exception e) {
			logs.grava("smtp","ERRO setSocket: "+e);
			erro = true;
		}

	}
	//********************************
	public void run() {
		sErro = null;
		nPedidos++;

		String str=null;

		try {

			while(!erro) {

				sp.setSoTimeout(timeout);
				str = fromSMTP.readLine();
				if ( str == null ) break;

				toMailer.println(str);
				nBytes += str.length();
				logs.grava("smtp",str);

				// foi enviado data?
				if ( str.startsWith("354") ) {

					//grava cabecalho
					str = "Received: from "+Guarani.getHost()
							+" ([127.0.0.1]) by "
							+Guarani.getHost()+" (Beta 2) with SMTP; "+data.strSmtp();
					toSMTP.println(str);
					nBytes += str.length();

					do {
						sp.setSoTimeout(timeout);
						str = fromMailer.readLine();
						if ( str == null ) break;
						toSMTP.println(str);
						nBytes += str.length();

					//} while ( !str.startsWith(".") );
					} while ( !str.equals(".") );
					if ( str == null ) {
						logs.grava("smtp","ERRO data linha=null");
						break;
					} else {
						logs.grava("smtp","fimDataOK: "+str);
					}

				} else  {
					sp.setSoTimeout(timeout);
					str = fromMailer.readLine();
					if ( str == null ) break;
					toSMTP.println(str);
					nBytes += str.length();
					logs.grava("smtp",str);
				}
			}

			logs.grava("smtp","closing...str="+str);

		} catch ( IOException e) {
			logs.grava("smtp","ERRO run: "+e);
		} finally {
			//logs.grava("smtp","ERRO run finally: ?");
			try {
				sp.close();
			} catch ( IOException e ) {
			}
		}

		logs.grava("smtp","");
		fi = System.currentTimeMillis();
		tempo += fi-in;
		//logs.grava("VAL=tmp="+tempo);
		rodando = false;

	}
	//********************************
	public String getPedido() {
		return geto;
	}

}
