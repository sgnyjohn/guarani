package br.org.guarani.util;

//import java.util.*;

//***************************************
public class inteiro {
	public int i=0;
	public long ultInc;
	public Object nome;
	//***************************************
	public String toString() {
		return "<td>"+nome+"<td align=right>"+i;
	}
	//***************************************
	public inteiro(Object nome) {
		this.nome = nome;
	}
	//***************************************
	public void inc() {
		ultInc = data.ms();
		i++;
	}
	//***************************************
	public void inc(int n) {
		ultInc = data.ms();
		i+=n;
	}
}

