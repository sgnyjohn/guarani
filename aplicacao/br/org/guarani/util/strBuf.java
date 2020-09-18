/*
signey abr/2009
*/

package br.org.guarani.util;

/*
	CARACTERES

					"%u2022","-" //enumeração bolinha
					,"%u2018","`" //abre e fecha aspas simples
					,"%u2019","''"
					,"%u2014","-" //travessao inicio frase - falando...
					,"%u2013","-" //E2 80 93
					,"%u201C","\""	//E2 80 9C abre e fecha aspas duplas
					,"%u201D","\""	//E3 80 9D

*/



//************************************
public class strBuf {
	public byte bf[];
	public int t,p = 0;
	//************************************
	public synchronized boolean upg() {
		logs.grava("strBuf estorou, upgrade para o DOBRO: "+t+" -> "+t*2);
		t = t*2;
		byte bf1[] = new byte[t];
		for (int i=0;i<p;i++) {
			bf1[i] = bf[i];
		}
		bf = bf1;
		return true;
	}
	//************************************
	public void reset() {
		p = 0;
	}
	//************************************
	public boolean addTex(String s) {
		int tm = s.length();
		while (p+tm>t) {
			if (!upg()) {
				return false;
			}
		}
		for (int i=0;i<tm;i++) {
			char c = s.charAt(i);
			if (c<='z') {
			} else if (c==150) {
				//copia da web traz caracteres estranhos tipo travessão que
				//é diferente do ífem e outros - tenho uma tabela (utf 8? %u...
				//para ser usada quando estes chars são colados no editor grafico 
				//do browser - se encontra em ppes.Envio
				c = '-';
			} else if ("º".indexOf(c)!=-1) {
			} else if (str.acentos.indexOf(c)==-1 ) {
				logs.grava("CHAR INV:"+c+":");
				c = '_';
			}
			bf[p++] = (byte)c;
		}
		return true;
	}
	//************************************
	//add contrabarra nas string bf cujos chars estão em tbt
	public static String addContra(String bf,String tbt) {
		int tm = bf.length(),tma = 0;
		for (int i=0;i<tm;i++) {
			if (tbt.indexOf(bf.charAt(i))!=-1) {
				tma++;
			}
		}
		char r[] = new char[tm+tma];
		int p = 0;
		for (int i=0;i<tm;i++) {
			char c = bf.charAt(i);
			if (tbt.indexOf(c)!=-1) {
				r[p++] = '\\';
				if (c=='\n') {
					c = 'n';
				} else if (c=='\r') {
					c = 'r';
				} else if (c=='\t') {
					c = 't';
				}
			}
			r[p++] = c;
		}
		return new String(r,0,tm+tma);
	}
	//************************************
	public String toString() {
		return new String(bf,0,p);
	}
	//************************************
	public boolean add(String s) {
		int tm = s.length();
		while (p+tm>t) {
			if (!upg()) {
				return false;
			}
		}
		for (int i=0;i<tm;i++) {
			bf[p++] = (byte)s.charAt(i);
		}
		return true;
	}
	//************************************
	public strBuf(int t) {
		this.t = t;
		bf = new byte[t];
	}
}