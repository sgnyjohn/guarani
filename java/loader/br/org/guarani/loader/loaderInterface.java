package br.org.guarani.loader;

import java.util.Hashtable;

//**************************************************************
//**************************************************************
public interface loaderInterface {
	//**************************************************************
	public String resUrl(String ch);	
	//**************************************************************
	public void stop(int sinal);
	//**************************************************************
	public void estat(Hashtable h);
	//**************************************************************
	public void run();
	//**************************************************************
	public boolean resMove(String ch,String ch1);
	//**************************************************************
	public boolean resExclui(String ch);
	//**************************************************************
	public boolean resGrava(String ch,String tx);
	//**************************************************************
	public Hashtable resCarregaS(String ch);
	//**************************************************************
	public String resCarrega(String ch);
	//**************************************************************
	public Class loadClass(String nome);
	//**************************************************************
	public boolean reLoad();
	//**************************************************************
	public String compil();
	//**************************************************************
	public boolean reset();
}
