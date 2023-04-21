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
public class authProxy extends Prg {
	//**************************************************
	public boolean run() {
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
