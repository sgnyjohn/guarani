/* ************************************

*************************************/
//package br.org.guarani.util;
package br.org.guarani.loader;


import java.util.*;
import java.io.*;

//import br.org.guarani.util.*;
//import br.org.guarani.servidor.*;
//***********************************
//***********************************
public class op {
	public String nome,sErro;
	int tipo;
	public int tam;
	int na=23;
	public byte buf[];
	//***********************************
	void erro(Exception e,String s) {
		logsL.grava(e,s);
	}
	//***********************************
	public BufferedReader arq() {
		BufferedReader outb=null;
		try {
			outb = new BufferedReader(
				new InputStreamReader(
				new ByteArrayInputStream(buf)));
		} catch (Exception e) {
		}
		return outb;
	}
	//***********************************
	public String toString() {
		return new String(buf,0,tam);
	}
	//***********************************
	String leStr(ByteArrayInputStream raf,int t) {
		byte b[] = new byte[t];
		raf.read(b,0,t);
		String r = null;
		//try {
			r =  new String(b);//2018...,0,t);
		//} catch (Exception e) {
		//	System.out.println(" op.op(raf)=="+strL.erro(e));
		//}
		return r;
	}
	//***********************************
	public op(ByteArrayInputStream raf,int ta,int td) {
		try {
			tipo = strL.inteiro(leStr(raf,1),0);
			tam = strL.inteiro(leStr(raf,6),0);
			if (tam>td) {
				erro(new Exception("op"),"tam="+tam);
			} else {
				buf = new byte[tam];
				raf.read(buf,0,tam);
				nome = new String(buf);//2018...,0,tam);
				//System.out.println("nome="+nome);
				tam = strL.inteiro(leStr(raf,6),0);
				buf = new byte[tam];
				raf.read(buf,0,tam);
			}
		} catch (Exception e) {
			System.out.println("tipo="+tipo+" tam="+tam+" op.op(raf)=="+strL.erro(e));
			erro(e,e+"op.op(raf)=="+strL.erro(e));
		}
	}
	//***********************************
	public op(String nome,int tp,String s) {
		this.nome = nome;
		this.tipo = tipo;
		if (tp==1) {
			File f = new File(s);
			logsL.grava("op tipo arq: "+s);
			tam = (int)f.length();
			try {
				RandomAccessFile raf = new RandomAccessFile(""+f,"r");
				buf = new byte[tam];
				raf.read(buf,0,tam);
			} catch (Exception e) {
				sErro = "Erro criando op: "+s+" "+e;
				erro(e,sErro);
			}
		} else {
			buf = s.getBytes();
			tam = buf.length;
			/*2018 
			tam = s.length();
			buf = new byte[tam];
			for (int i=0;i<tam;i++) {
				buf[i] = (byte)s.charAt(i);
			}
			*/
		}
	}
	//***********************************
	void gravaStr(ByteArrayOutputStream raf,String st) {
		int t = st.length();
		/* o tamanho não funciona com utf8 = chars visíveis... ?
		byte b[] = new byte[t];
		for (int i=0;i<t;i++) {
			b[i] = (byte)st.charAt(i);
		}
		*/
		byte b[] = st.getBytes();
		raf.write(b,0,t);
	}
	//***********************************
	public boolean grava(ByteArrayOutputStream raf) {
		try  {
			gravaStr(raf,""+tipo);
			gravaStr(raf,strL.strZero(nome.length(),6));
			gravaStr(raf,nome);
			gravaStr(raf,strL.strZero(tam,6));
			raf.write(buf,0,tam);
		} catch (Exception e) {
			erro(e,"Erro op.grava(raf)");
			return false;
		}
		return true;
	}
	/* **********************************
	public boolean grava(ByteArrayOutputStream raf,String s) {
		int t = s.length(); 
		byte bf[] = new byte[t];
		for (int i=0;i<t;i++) {
			bf[i] = (byte)s.charAt(i);
		}
		try {
			raf.writeInt(t);
			raf.write(bf,0,t);
		} catch (Exception e) {
			erro(e,"");
			return false;
		}
		return true;
	}
	*/
}

