/*
		sjohn@via-rs.net ago/2002
*/
package br.org.guarani.servidor;

import java.io.*;
import java.net.*;
import javax.servlet.*;


//***************************//
public class Leitor implements javax.servlet.ServletInputStream {
	//extends BufferedReader
	InputStreamReader in;
	int restaPost=0;
	byte bf[];
	int tmbf = 1024*10;
	//***************************
	public int leB(byte b[]) throws java.io.IOException {
		return read(b,0,1);
	}
	//***************************//
	public void close() throws java.io.IOException {
		in.close();
	}
	//***************************//
	public String readLine() throws java.io.IOException {
		int c1=0,t=0;
		while (c1!=10 && t<tmbf) {
			c1 = in.read();
			if (c1==-1) {
				break;
			}
			bf[t++] = (byte)c1;
		}
		if (t>0 && bf[t-1]=='\n') t--;
		if (t>0 && bf[t-1]=='\r') t--;
		return new String(bf,0,t);
	}
	//***************************//
	public Leitor(InputStreamReader i) {
		bf = new byte[tmbf];
		//super(i);
		in = i;
	}
	//***************************//
	public int read(byte b[],int i,int t) throws java.io.IOException {
		int c1,t1=0;
		while (t1<t) {
			c1 = in.read();
			if (c1==-1) {
				break;
			}
			restaPost--;
			b[t1++] = (byte)c1;
		}
		return t1;
	}
	//***************************//
	// abaixo feito p/compatipilidade com direto 
	// com post-multipart qdo
	// esta usava "extends BufferedReader"
	// talvez restaPost não seje mais necessário...
	//
	public int readLine(byte b[],int i,int t) throws java.io.IOException {
		char c,ch[]=new char[1];
		int ci=0,l=-1;
		t--;
		while (restaPost>0 && l<t && ch[0]!='\n') {
			ci = in.read(ch,0,1);
			restaPost--;
			if (ci==-1) {
				break;
			}
			b[i] = (byte)(ch[0]); 
			i++;
			l++;
		}
		return ((l==-1)?l:l+1);
	}
	//***************************//
	public int read(byte[] buf) throws IOException {
		int ci,l=0,t = buf.length;
		while (restaPost>0 && l<t) {
			ci = in.read();
			restaPost--;
			//if (ci==-1) {
			//	break;
			//}
			buf[l++]=(byte)ci;
		}
		return l;
	}
}
