/*
	Signey jan/2005
	classe para conpatib linux
*/

package br.org.guarani.dose;

import java.lang.*;
import br.org.guarani.util.*;
import br.org.guarani.servidor.*;

//****************************************
//****************************************
public class servico {
	public static String er;
	public static String dirT;
	//****************************************
	public static void log(String s) {
		Guarani.log(s);
	}
	//****************************************
	public static void startClasse() {
		Guarani.start();
	}
	//****************************************
	public static void stopClasse() {
		Guarani.stop(0);
	}
	//****************************************
	public static boolean running() {
		return false;
	}
	//****************************************
	public static int stop() {
		return -1;
	}
	//****************************************
	public static int start(String dirTrab, String nome) {
		return -1;
	}
	//****************************************
	public static int install(String nome,String exe) {
		return -1;
	}
	//****************************************
	public static int remove(String nome) {
		return -1;
	}
}