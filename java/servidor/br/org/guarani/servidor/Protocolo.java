/*
	* Signey John jan/2001.
	*/

package br.org.guarani.servidor;

import br.org.guarani.util.*;

	import java.net.*;
	import java.io.*;
	import java.lang.*; 
	import java.util.*;

public interface Protocolo extends Runnable {
	//*************************************************
	public String detalhePedido();
	//********************************
	public ServerSocket criaServerSocket(int porta) throws IOException;

	//********************************
	public void init(Hashtable c);

	//********************************
	public void setSocket(Socket s);

	//********************************
	public void run();

	//********************************
	public boolean rodando();

	//********************************
	public long getTempo();

	//********************************
	public long getTempoRodando();

	//********************************
	public long getNPedidos();

	//********************************
	public long getBytes();
	//********************************
	public long getKeepAlive();

	//********************************
	public String getPedido();

	//********************************
	public boolean rodando(boolean r);

	//********************************
	public void stop();

}
