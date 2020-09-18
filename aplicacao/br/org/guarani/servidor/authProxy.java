/*
 * Signey John dez/2014.
 */

package br.org.guarani.servidor;
 
import java.util.*;
import java.io.*;
import java.net.*;
import java.text.*;

import br.org.guarani.servidor.*;
import br.org.guarani.util.*;

//**************************************************
//**************************************************
public class authProxy implements Prg {
	//**************************************************
	public boolean run(Pedido pd) {
		
		return true;
	}
	//**************************************************
	public void log(String s) {
		logs.grava("authProxy",s);
	}
	//**************************************************
	public boolean runSocket(Socket pPed) {
		return false;
	}
}
