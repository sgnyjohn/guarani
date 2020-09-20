package br.org.guarani.util;

import br.org.guarani.servidor.*;
import java.io.*;

/*************************************/
/*************************************/
public class executa {
	int i;
	ByteArrayOutputStream tmpErr = new ByteArrayOutputStream(4096);
	ByteArrayOutputStream tmpInput = new ByteArrayOutputStream(4096);
	Process p;
	public boolean erro=false;
	public String sErro;

	/*************************************/
	public String getHtml() {
		String r="";
		if (erro) {
			r = "<hr><font color=red>ERRO: <b>"+sErro+"</b></font><hr>";
		}
		r += str.troca(getOut(),"\n","<br>");
		return r;
	}
 
 
	/*************************************/
	public int exitValue() {
		return p.exitValue();
	}
 
	/*************************************/
	public String getOut() {
		return tmpInput.toString();
	}
 
	/*************************************/
	public String getErr() {
		return tmpErr.toString();
	}

	/*************************************/
	public boolean exec(Pedido ped,String a[]) {
		if (execBack(ped,a)) {
			return analiza();
		} else {
			return false;
		}
	}

	/*************************************/
	public boolean exec(Pedido ped,String a, String b[], File f) {
		if (execBack(ped,a,b,f)) {
			return analiza();
		} else {
			return false;
		}
	}

 	/*************************************/
	public boolean execBack(Pedido ped,String a[]) {
		erro = false;
		try {
			//gcc
			//gcc## p = Runtime.getRuntime().exec(a,b,f);
			p = Runtime.getRuntime().exec(a);
			logs.grava("exec",ped+", OK: "+str.palavraA(a," ~ "));
		} catch (java.io.IOException ie) {
			logs.grava("exec",ped+",ERRO: "+a+"="+ie);
			erro = true;
			return false;
		}
		return true;
	}
	
	/*************************************/
	public boolean execBack(Pedido ped,String a, String b[], File f) {
		erro = false;
		try {
			//gcc
			//gcc## p = Runtime.getRuntime().exec(a,b,f);
			p = Runtime.getRuntime().exec(a,b);
			logs.grava("exec",ped+", OK: "+a+"<->"+b);
		} catch (java.io.IOException ie) {
			logs.grava("exec",ped+",ERRO: "+a+"<->"+b+"="+ie);
			erro = true;
			return false;
		}
		return true;
	}

	/*************************************/
	public boolean execBack(Pedido ped,String a) {
		return execBack(ped,a,null,null);
	}
 
 
	/*************************************/
	public boolean exec(Pedido ped,String a) {
		return exec(ped,a,null,null);
	}
 
 
	/*************************************/
	public boolean analiza() {
		return analiza(null);
	}
	/*************************************/
	public boolean analiza(String manda) {
		boolean r = true;
		try {
   
			BufferedInputStream compilerErr = 
				new BufferedInputStream(p.getErrorStream());
			BufferedInputStream compilerInput = 
				new BufferedInputStream(p.getInputStream());
   
			StreamPumper errPumper = 
				new StreamPumper(compilerErr, tmpErr);
			StreamPumper inputPumper = 
				new StreamPumper(compilerInput, tmpInput);
   
			errPumper.start();
			inputPumper.start();
			
			//gravar input stream?
			PrintStream ps = null;
			if (manda!=null) {
				ps = new PrintStream(p.getOutputStream());
				//byte b[] = manda.getBytes("UTF -8");//"ISO-88 59-1");
				String v[] = str.palavraA(manda,"\n");
				for (int i=0;i<v.length;i++) {
					ps.println(v[i]);
				}
				ps.flush();
				ps.close();
			}
   
			p.waitFor();

			errPumper.join();
			compilerErr.close();
   
			inputPumper.join();
			compilerInput.close();
   
			tmpInput.close();
			tmpErr.close();
			erro = p.exitValue()!=0;
			sErro =  p.exitValue()+"=="+tmpErr.toString();
			
		} catch (IOException ioe) {
			logs.grava("erro","erro exec "+ioe);
			r = false;
   
		} catch (InterruptedException ie) {
			logs.grava("erro","erro exec "+ie);
			r = false;
			
		} catch (Exception ie) {
			logs.grava("erro","erro exec "+ie);
			r = false;
		}
		p.destroy();
		return r;
	}
}


