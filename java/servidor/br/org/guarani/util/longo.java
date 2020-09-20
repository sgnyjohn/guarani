package br.org.guarani.util;

//**************************************
//**************************************
public class longo {
	public long val;
	//**************************************
	public String toString() {
		return ""+val;
	}
	//**************************************
	public longo() {
	}
	//**************************************
	public void inc(long l) {
		val += l;
	}
	//**************************************
	public void set(long l) {
		val = l;
	}
	//**************************************
	public long get() {
		return val;
	}
	//**************************************
	public longo(long l) {
		val = l;
	}
}

