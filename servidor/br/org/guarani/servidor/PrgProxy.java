/*
	*/

package br.org.guarani.servidor;

import br.org.guarani.util.*;
import java.io.PrintWriter;
import java.net.Socket;

public interface PrgProxy {

	public boolean run(Proxy Pedido);
	public boolean fim(Proxy Pedido);
	
}
