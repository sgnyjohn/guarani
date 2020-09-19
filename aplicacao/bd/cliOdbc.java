/**
	* cliente ponte com odbc windows
	* Signey jun/2002
	*/

package bd;

import java.util.*;

import br.org.guarani.util.*;
import br.org.guarani.servidor.*;

import java.net.Socket;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;

/***************************************************************/
/***************************************************************/
public class cliOdbc extends DadosSet {
	protected Socket sk;
	protected boolean con;
	protected BufferedReader i;
	protected PrintWriter o;
	public String r,ln;
	public String vc[], vd[];
	protected int nc;
	protected Pedido ped;
	public boolean erro=false;
	public String sErro = "";
	protected String del = "~ç~";
	public boolean fim = false;
	//****************************************
	public boolean fim() {
		return fim;
	}
	//****************************************
	public String sql() {
		return null;
	}

	//****************************************
	public boolean absolute(int reg) {
		return false;
	}
	//****************************
	public int nRegs() {
		return -1;
	}
	//****************************************
	public boolean mostra(Pedido ped) {
		ped.on("Não implementado!!");
		return false;
	}

	//****************************************
	public String getString(String s,String s1) {
		String s2=getString(s);
		if (s2==null) {
			return s1;
		}
		return s2;
	}

	public boolean erro() {
		return erro;
	}

	public boolean mostra(String atalho,String op) {
		String ign=str.opcao(op,"/ign","");
		int lim = str.opcaoInt(op,"/lim",300),nr=0;
		boolean mlinha = str.opcao(op,"/mlin")!=null;

		int nc = vc.length;
		ped.on("<table border=1>");
		//cabec
		ped.on("<tr>");
		if (mlinha) {
			ped.on("<th align=right>#");
		}
		for (int i=0;i<nc;i++) {
			if (ign.indexOf("-"+vc[i]+"-")==-1) {
				ped.on("<th>"+vc[i]);
			}
		}
			//dados
		while (next() && nr<lim && !ped.erro) {
			nr++;
			ped.on("<tr>");
			if (mlinha) {
				ped.on("<td align=right>"+nr);
			}
			int p=0;
			for (int i=0;i<nc;i++) {
				if (ign.indexOf("-"+vc[i]+"-")==-1) {
					p++;
					if (p==1 && atalho!="") {
						ped.on("<td>"+str.troca(atalho,"@@",getString(i+1)));
					} else {
						ped.on("<td>"+getString(i+1));
					}
				}
			}
		}
		ped.on("</table>");
		return true;
	}


	/***************************************************************/
	public int getInteiro(int a) {
		return -1;
	}
	/***************************************************************/
	public double getDuplo(int a) {
		return -1;
	}
	//****************************************
	public java.sql.Timestamp getDateTime(String s) {
		return null;
	}
	/***************************************************************/
	public java.sql.Date getDate(String n) {
		return null;
	}
	/***************************************************************/
	public long getLongo(int a) {
		return -1;
	}

	/***************************************************************/
	public int contaCampos() {
		return -1;
	}

	/***************************************************************/
	public String getNomeCampo(int i) {
		return null;
	}
	//****************************************
	public String[] getVetor() {
		return null;
	}

	/***************************************************************/
	public Hashtable getHashtable() {
		return null;
	}

	/***************************************************************/
	public void estru() {
	}

	//****************************************
	public void erro(String s, Exception e) {
		erro = true;
		s = "DadosSet.class "+s;
		if (ped==null) {
			logs.grava("err",s+"<hr>"+e);
		} else {
			ped.erro(s,e);
		}
	}

	/***************************************************************/
	public cliOdbc(Pedido pd) {
		ped = pd;
	}

	/***************************************************************/
	public cliOdbc(Pedido pd,String delimitador) {
		ped = pd;
		del = delimitador;
	}

 
 
	/***************************************************************/
	public boolean conecta(String host, String db) {

		try {
			sk = new Socket(host,3131);
			i = new BufferedReader(new InputStreamReader( sk.getInputStream() ));
			o = new PrintWriter( sk.getOutputStream(), true );

			envia("con "+db);
			//try {Thread.sleep(3000);} catch (InterruptedException ie) {}
			r = i.readLine();
			if (r.substring(0,4).equals("?ER?")) {
				erro = true;
				sErro = r;
			}
			//System.out.println(r);


		} catch (java.io.IOException eio) {
			erro = true;
			sErro = "cliOdbc class: erro conexção: "+eio;
			//System.out.println(sErro);
		}

		return !erro;

	}

	/***************************************************************/
	public boolean envia(String cmd) {
		o.println(cmd);
		o.flush();
		return true;
	}

	/***************************************************************/
	public boolean recebe() {
		try {
			ln = i.readLine();
			if (ln==null) ln = "?ER? null";
			if (str.leftAt(ln," ").compareTo("?ER?")==0) {
				sErro = ln;
				erro = true;
				//if (ped!=null) ped.erro(ln,new Exception());
				return false;
			}
		} catch (java.io.IOException eio) {
			System.out.println("cliOdbc class: erro conexção: "+eio);
			return false;
		}
		return true;
	}

	/***************************************************************/
	public boolean close() {
		envia("clo");
		try {
			sk.close();
		} catch (Exception e) {
		}
		return true;
	}



	/***************************************************************/
	public boolean executeQuery(String sq) {
		envia("que "+sq);
		if (recebe()) {
			//System.out.println(ln);
			if (ln.substring(0,4).equals("?ER?")) {
				sErro = ln;
				erro = true;
				return false;
			}
			vc = str.palavraA(str.substrAt(ln.toLowerCase()," "),del);
			nc = vc.length;
			/*if (next()) {
				for (int i=0;i<nc-1;i++) {
					System.out.println("<br>"+i+" - "+vc[i]+" = "+vd[i]);
				}
			}
			*/
			return true;
		}

		System.out.println("erro QUERY="+ln);
		return false;
	}

	/***************************************************************/
	public int executeUpdate(String sq) {
		envia("upd "+sq);
		recebe();
		return 0;
	}

	/***************************************************************/
	public String getString(int cmp) {
		return vd[cmp-1];
	}

	/***************************************************************/
	public String getString(String cmp) {

		cmp = cmp.toLowerCase();
		for (int x=0;x<nc;x++) {
			//System.out.println(cmp+"="+x+"="+vc[x]);
			if (cmp.compareTo(vc[x])==0) {
				return ((vd[x].compareTo("NULL")==0)?"":vd[x]);
			}
		}
		return null;

	}

	/***************************************************************/
	public double getDuplo(String cmp) {
		try {
			return Double.parseDouble(getString(cmp));
		} catch (NumberFormatException e) {
			//System.out.println(cmp+" nao numerico "+e);
			return -1;
		}

	}
	/***************************************************************/
	public long getLongo(String cmp) {
		try {
			return Long.parseLong(getString(cmp));
		} catch (NumberFormatException e) {
			//System.out.println(cmp+" nao numerico "+e);
			return -1;
		}

	}
	/***************************************************************/
	public int getInteiro(String cmp) {
		try {
			return Integer.parseInt(getString(cmp));
		} catch (NumberFormatException e) {
			//System.out.println(cmp+" nao numerico "+e);
			return -1;
		}

	}

	/***************************************************************/
	public boolean next() {

		envia("nex");
		recebe();
		if (ln.substring(0,3).compareTo("fim")==0) {
			fim = true;
			return false;
		}

		int t = 0;
		try {
			t = Integer.parseInt(str.substrAt(ln," "));
		} catch (NumberFormatException e) {
			System.out.println(ln);
			erro = true;
			sErro = "cliOdbc.next(): formato não inteiro: "+ln;
			fim = true;
			return false;
		}

		r = "";
		while (t>r.length()) {
			recebe();
			r += ln+"\r\n";
		}

		vd = str.palavraA(r,del);
		if (vc.length!=vd.length) {
			sErro = "ERRO TAM dados="+vd.length+"<>tam CMP="+vc.length+"=NEXT="+ln+"=";
			erro = true;
			fim = true;
			return false;
		}

		return true;
	}


	//*****************************************//
	public void cabRS(Pedido ped) {
		//cabecaolhos
		for (int i=0;i<vc.length;i++) {
			ped.println("<th>"+vc[i]);
		}
	}


	//*****************************************//
	public void outRS(Pedido ped) {
		int nc;

			ped.on("<table border=1><tr>");
			cabRS(ped);
			int t=0;
			while (next()) {
				t++;
				ped.on("<tr>");
				for (int i=0;i<vc.length;i++) {
						ped.on("<td>"+getString(i));
				}
			}
			ped.on("</table>");
			ped.on("Total: "+t);
	}
}
