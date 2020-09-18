package javax.servlet.http;

import java.io.*;
import java.util.*;
import javax.servlet.*;

import br.org.guarani.servidor.*;
import br.org.guarani.util.*;


public class Cookie {
	String nome,valor,idade;
	public void setMaxAge(int i) {
		idade = ""+i;
	}
	public String getName() {
		return nome;
	}
	public String getValue() {
		return valor;
	}
	public Cookie(String pNome,String pValor) {
		nome = pNome;
		valor = pValor;
	}
	public void setPath(String path) {
	}
}
