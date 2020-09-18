package br.org.guarani.util;

//import java.util.*;

//***************************************
public class duplo {
	public double i=0;
	public long ultInc;
	public Object nome;
	//***************************************
	public String toString() {
		return "<td>"+nome+"<td align=right>"+i;
	}
	//***************************************
	public duplo(Object nome) {
		this.nome = nome;
	}
	//***************************************
	public void inc() {
		ultInc = data.ms();
		i++;
	}
	//***************************************
	public void inc(double n) {
		ultInc = data.ms();
		i+=n;
	}
}

