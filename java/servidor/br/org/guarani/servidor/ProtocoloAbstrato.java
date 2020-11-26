/*
	* Signey John jan/2001.
	*/

package br.org.guarani.servidor;

import br.org.guarani.util.*;

	import java.net.*;
	import java.io.*;
	import java.lang.*;
	import java.util.*;

public abstract class ProtocoloAbstrato implements Protocolo {
	public static final String lf = "\r\n";
	protected Socket sp; //soket pedido
	protected boolean rodando,erro;
	protected long tempo,in,fi,nPedidos,nBytes;
	protected long keepAlive;

	//*************************************************
	public String detalhePedido() {
		return " Socket("+sp
			+" getInetAddress()="+sp.getInetAddress()
			+")"
		;
	}
	
	//********************************
	//inicializa
	public abstract void init(Hashtable c);
 
	//********************************
	public abstract String getPedido();

	//********************************
	//seta socket
	public void setSocket(Socket s) {
		sp = s;
		erro = false;
	}

	//********************************
	//atende pedido
	//public abstract void run();

	//********************************
	//para
	public void stop() {
		rodando = false;
	}

	//********************************
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
	public long getKeepAlive() {
		return keepAlive;
	}
	//********************************
	public long getNPedidos() {
		return nPedidos;
	}

	//********************************
	public long getTempoRodando() {
		if (rodando) {
			return System.currentTimeMillis()-in;
		}
		return fi-in;
	}

	//********************************
	//retorna serversocket
	public ServerSocket criaServerSocket(int porta) throws IOException {
		return new ServerSocket(porta);
	}

}
