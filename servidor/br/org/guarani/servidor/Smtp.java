package br.org.guarani.servidor;

import java.io.*;
import java.net.*;
import java.util.*;

import br.org.guarani.util.*;

class Smtp extends ProtocoloAbstrato {
	private Socket sp,socketToSMTP;
	private File arqTo;
	private BufferedReader fromMailer;
	private PrintWriter toMailer;
	private String ipMailer,nomeMailer;
	private BufferedReader fromSMTP;
	private PrintWriter toSMTP,toSMTP1;
	private int tpDestino = 0; //servidor 1=diretorio
 
	private String msgId;

	private String permissao,destino,geto="??",sErro;
	protected Hashtable cnf;
	int timeout=0;
 
 
	/*/guarani

	protected String pedido;
	protected Hashtable cnf;
	public boolean rodando;
	public long tempo,in,fi,nPedidos,nBytes;
	public boolean erro;
	String us,ip,permissao,destino,sErro,geto="vazio";
	int timeout=0;
	*/

	//********************************
	public void run() {

		sErro = null;
		nPedidos++;

		String sLin=null,cmd;

		try {

			while(!erro) {

				sp.setSoTimeout(timeout);
				if (tpDestino==1) {
					if (sLin==null) {
						sLin = "220 "+Guarani.getHost()+" SMTP Service Ready";
					}
				} else {
					sLin = fromSMTP.readLine();
				}
				if ( sLin == null ) break;

				toMailer.println(sLin);
				nBytes += sLin.length();
				logs.grava("smtp",sLin);

				// foi enviado data?
				if ( sLin.startsWith("354") ) {
					//le a mensagem

					//adiciona cab cabecalho
					sLin = "Received: from "+nomeMailer+
							" (["+ipMailer+"])\n\tby "+
							Guarani.getHost()+" with SMTP id "+msgId+";\n\t"+data.strSmtp();
					toSMTP1.println(sLin);
					nBytes += sLin.length();

					//le enquanto <> "."
					boolean fim = false;
					do {
						sp.setSoTimeout(timeout);
						sLin = fromMailer.readLine();
						if ( sLin == null ) break;

						fim = sLin.equals(".");
						if (!fim) {
							toSMTP1.println(sLin);
						}
						nBytes += sLin.length();
					} while ( !fim );

					sLin = "250 Message received OK.";
     

					if ( sLin == null ) {
						logs.grava("smtp","ERRO data linha=null");
						break;
					} else {
						logs.grava("smtp","fimDataOK: "+sLin);
					}

				} else  {
					sp.setSoTimeout(timeout);
					sLin = fromMailer.readLine();
					if ( sLin == null ) break;
					toSMTP.println(sLin);
					nBytes += sLin.length();
					if (tpDestino==1) {
						cmd = str.trimm(str.leftAt(sLin+" "," ")).toUpperCase();
						if (cmd.equals("DATA")) {
							sLin = "354 Enter Mail, end by a line with only '.'";
						} else if (cmd.equals("HELLO") | cmd.equals("HELO") | cmd.equals("EHLO")) {
							sLin = "250 "+Guarani.getHost();
						} else if (cmd.equals("QUIT")) {
							toMailer.println("221 GoodBye");
							break;
						} else {
							sLin = "250 OK";
						}
					}
					logs.grava("smtp",sLin);
				}
			}

			logs.grava("smtp","closing...str="+sLin);

		} catch ( IOException e) {
			logs.grava("smtp","ERRO run: "+e);
		} finally {
			//logs.grava("smtp","ERRO run finally: ?");
   
			try {
				toSMTP.close();
				if (tpDestino==1) {
					toSMTP1.close();
				} else {
					socketToSMTP.close();
				}
			} catch ( IOException e ) {
				logs.grava("smtp","erro fechando toSMTP: "+e);
			}
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
	//retorna serversocket
	public ServerSocket criaServerSocket(int porta) throws IOException {
		return new ServerSocket(porta);
	}

	//********************************
	public void init(Hashtable c) {
		rodando = false;
		erro = false;
		cnf = c;
		nBytes = 0;
		permissao = " "+str.seNull((String)cnf.get("permissao"),"127.0.0.1")+" ";
		destino = str.seNull((String)cnf.get("destino"),"127.0.0.1");
		if (str.equals(destino,"servidor:")) {
			tpDestino = 0;
			destino = str.substrAt(destino,":");
		} else if (str.equals(destino,"dir:")) {
			tpDestino = 1;
			destino = str.substrAt(destino,":");
		}
		timeout = Integer.parseInt(str.seNull((String)cnf.get("timeout"),"60000"));
	}

	//********************************
	public void setSocket(Socket s) {
		sp = s;
		erro = false;

		in = System.currentTimeMillis();

		//ip autorizado?
		InetAddress i = sp.getInetAddress();
		ipMailer = i.getHostAddress();
		nomeMailer = i.getHostName();
		geto = nomeMailer+"-"+ipMailer;
		if (!permissao.equals(" * ") &&
			permissao.indexOf(" "+ipMailer+" ")==-1){
			logs.grava("seg","VAL: sem permissão "+permissao+"=="+sp);
			erro = true;
			return;
		}

		try {
			//Mailer origem
			fromMailer = new BufferedReader( new InputStreamReader ( sp.getInputStream()));
			toMailer = new PrintWriter( new BufferedWriter ( new OutputStreamWriter ( sp.getOutputStream())),true);

			//SMTP Destino
			if (tpDestino==1) {
				//socketToSMTP = new Socket(InetAddress.getByName(destino),25);
				String nArq = arquivo.nomeTmp(destino+"/",".emc");
				msgId = str.substrRatAt(nArq,"/",".");
				arqTo = new File(nArq);
				//fromSMTP = new BufferedReader( new InputStreamReader ( arq ));
				//toSMTP = new PrintWriter( new BufferedWriter ( new OutputStreamWriter ( arq)),true);
				toSMTP = new PrintWriter(new FileOutputStream(arqTo),true);
				toSMTP1 = new PrintWriter(new FileOutputStream(arqTo+"-msg"),true);
			} else {
				socketToSMTP = new Socket(InetAddress.getByName(destino),25);
				fromSMTP = new BufferedReader( new InputStreamReader ( socketToSMTP.getInputStream()));
				toSMTP = new PrintWriter(
					new BufferedWriter (
					new OutputStreamWriter (
						socketToSMTP.getOutputStream())),true);
				toSMTP1 = toSMTP;
			}

			logs.grava("smtp","saida="+socketToSMTP);

		} catch (Exception e) {
			logs.grava("smtp","ERRO setSocket: "+e);
			erro = true;
		}

	}

	//metodos guarani
	//********************************
	public String getPedido() {
		return geto;
	}

}
