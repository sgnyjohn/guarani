/**
	* 
	*/

package util;


import br.org.guarani.util.*;
import br.org.guarani.servidor.*;
import util.*;

import java.net.Socket;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import org.xbill.DNS.*;



	/***************************************************************/
	/***************************************************************/
public class smtp extends Pag {
	protected Socket sk;
	protected boolean con;
	protected BufferedReader i;
	protected PrintWriter o;
	protected String ln,cmd;
	boolean aberto = false;

	public String de="",para="",cc="",cco="",
		assunto="",msg="",sep="\r\n",formato="MIME-Version: 1.0";
		//Content-Type: text/html
	public boolean debug = false;
	//***************************************
	public void dns() {
		try {
			Record[] records = new Lookup(param("d","via-rs.net"), Type.MX).run();
			//if (true) return;
			on("<br>Tam: "+records.length);
			for (int i = 0; i < records.length; i++) {
				try {
					MXRecord mx = (MXRecord) records[i];
					on("<br>Host " + mx.getTarget() 
						+ " has preference "+mx.getPriority());
				} catch (Exception e) {
					on("<br>"+records[i].getClass().getName()+" = "+records[i]);
				}
			}
		} catch (Exception e) {
			on(str.erro(e));
		}
	}
	
	//***************************************
	public void testa() {
		String s = param("em");
		//on(s);
		s = str.troca(s,",","\n");
		s = str.troca(s,";","\n");
		String v[] = str.palavraA(s,"\n");
		int ne = 0;
		for (int i=0;i<v.length;i++) {
			String e = str.trimm(v[i]);
			if (e.indexOf("<")!=-1) {
				e = str.substrAt(e,"<");
			}
			e = str.trimm(e,"\r><");
			if (!str.vazio(e)) {
				if (ne%900==0) {
					if (!conecta("localhost")) {
						on("ERRO conect "+ln);
					}
						
					envia("MAIL FROM: <signey@signey.dyndns.org>");
					if (!cmdOk()) {
						ped.on("erro mail from: "+ln);
						return;
					}
				}
				ne++;
				envia("RCPT TO: <"+e+">");
				if (!cmdOk()) {
					on("<br>ERRO "+ne+" "+e+" "+v[i]+" "+ln);
				}	
			}	
		}
		fecha();
		on("<hr>Total testado: "+ne);
	}
	//***************************************
	public void inicio() {
		on("<form method=POST>"
			+"Testar estes emails"
			+"<br><textarea name=em rows=20 cols=70></textarea>"
			+"<input type=hidden name=op value=testa>"
			+"<br><input type=submit>"
			+"</form>"
		);
	}

	//****************************
	public boolean run(Pedido pd)  {
		super.run(pd);
		cab("Teste SMTP msg");
		if (false && !op.equals("envia")) {
			ped.on("Erro, ação inválida!!");
		} else {
			menu("");
			exec();
		}
		rodap();
		return true;
	}

	//****************************
	public void envia()  {
		sep = ped.getString("sep",sep);
		formato = ped.getString("formato",formato);
		de = ped.getString("de");
		para = ped.getString("to");
		cc = ped.getString("cc");
		cco = ped.getString("bco");
		assunto = ped.getString("subject");
		msg = ped.getString("body");
		if (conecta(ped.getString("serv"))) {
			enviaMsg();
		}
	}




	/***************************************************************/
	public boolean enviaMsg() {

		envia("MAIL FROM: "+de);
		if (!cmdOk()) {
			ped.on("erro mail from: "+ln);
			return false;
		}

		if (!dest(para)) {
			ped.on("ERRO em PARA: "+ln);
			return false;
		}
		if (!dest(cc)) {
			ped.on("ERRO em CC: "+ln);
			return false;
		}
		if (!dest(cco)) {
			ped.on("ERRO em CCO: "+ln);
			return false;
		}

		//envia("");
		envia("DATA");
		if (!cmdOk("354")) {
			ped.on("ERRO em DATA: "+ln);
			//return false;
		}

		//cabecalho
		envia("Date: "+data.strHttp());
		envia("From: "+de);
		envia("To: "+str.troca(str.trimm(para),sep,",\r\n\t"));
		envia("Cc: "+str.troca(str.trimm(cc),sep,",\r\n\t"));
		envia("Subject: "+assunto);
		envia(formato);
		envia("");
		envia(str.troca(msg,"\r\n.","\r\n.."));

		envia(".");
		if (!cmdOk()) {
			ped.on("ERRO DATA: "+ln);
			return false;
		}

		if (!fecha()) {
			ped.on("ERRO FECHA: "+ln);
			return false;
		}

		return true;

	}

	/***************************************************************/
	private boolean dest(String d) {
		d = str.trimm(str.seNull(d,""));
		if (d.length()>0) {

			String[] a = str.palavraA(d,sep);
			for (int i=0;i<a.length;i++) {
				if (str.trimm(a[i]).length()>0) {
					envia("RCPT TO: "+a[i]);
					if (!cmdOk()) {
						ped.on("erro RCPT:"+a[i]);
						return false;
					}
				}
			}

		}

		return true;
	}

	/***************************************************************/
	public boolean enviaForm() {
		de = ped.getString("de");
		para = ped.getString("para");
		cc = ped.getString("cc");
		cco = ped.getString("cco");
		ped.println("CCO==="+cco);
		assunto = ped.getString("assunto");
		msg = ped.getString("msg");
		ped.on("Arquivo: "+ped.getString("arq"));

		return enviaMsg();
	}

	/***************************************************************/
	public void form(String a) {

		// ENCTYPE="multipart/form-data"
		ped.on("<form ENCTYPE=\"multipart/form-data\" action='"+a+"' method=post><table border>");
		ped.on("<tr><td align=right>De:<td><input name=de size=55 value='"+de+"'>");
		ped.on("<tr><td align=right>Para:<td><textarea wrap=off rows=2 cols=55 name=para>"+para+"</textarea>");
		ped.on("<tr><td align=right>C.C.:<td><textarea wrap=off rows=2 cols=55 name=cc>"+cc+"</textarea>");
		ped.on("<tr><td align=right>C.C.O.:<td><textarea wrap=off rows=2 cols=55 name=cco>"+cco+"</textarea>");
		ped.on("<tr><td align=right>Assunto:<td><input name=assunto size=55 value=\"\">");
		ped.on("<tr><td align=right>Mensagem:<td><textarea cols=55 rows=12 name=msg></textarea>");
		ped.on("<tr><td align=right>Anexo:<td><input type=file name=arq>");
		ped.on("<tr><td align=center colspan=2><input type=submit value='Envia'>");
		ped.on("</table></form>");
	}

	/***************************************************************/
	public boolean conecta(String host) {
		fecha();
		try {
			sk = new Socket(host,25);
			i = new BufferedReader(new InputStreamReader( sk.getInputStream() ));
			o = new PrintWriter( sk.getOutputStream(), true );

			if (!cmdOk("220")) {
				ped.on("Erro connect "+ln);
				return false;
			}
			envia("HELO "+ped.getSessao().getIp());
			aberto = true;
			return cmdOk();

		} catch (java.io.IOException eio) {
			System.out.println("smtp class: erro conectando a "+host+": "+eio);
			return false;

		}

	}


	/***************************************************************/
	private boolean cmdOk(String r) {
		recebe();
		return (ln.substring(0,r.length()).compareTo(r)==0);
	}

	/***************************************************************/
	private boolean cmdOk() {
		return cmdOk("250");
	}

	/***************************************************************/
	public boolean envia(String c) {
		cmd = c;
		if (debug) ped.println("<br><b>enviou:</b>"+cmd+":");
		o.println(cmd);
		o.flush();
		return true;
	}

	/***************************************************************/
	public boolean recebe() {
		try {
			ln = i.readLine();
			if (debug) ped.on("<br><b>leu:</b>"+ln+":");
		} catch (java.io.IOException eio) {
			System.out.println("smtp class: erro redebe cmd="+cmd+": "+eio);
			return false;
		}
		return true;
	}

	/***************************************************************/
	public boolean fecha() {
		boolean r = false;
		if (aberto) {
			envia("QUIT");
			r = cmdOk("221");
		}
		aberto = r;
		return r;
	}

}
