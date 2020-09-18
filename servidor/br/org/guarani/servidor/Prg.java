/*
	*/

package br.org.guarani.servidor;

import br.org.guarani.util.*;
import java.io.PrintWriter;
import java.net.Socket;

public interface Prg {
	public Pedido ped=null;
	public String op="";
	//public long tempo=0;

	public boolean run(Pedido Pedido);
	
}
