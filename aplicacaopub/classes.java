/*
	* Signey John jan/2001 jul/2002
	*/

import java.util.*;
import java.io.*;

import br.org.guarani.servidor.*;
import br.org.guarani.util.*;
import bd.*;

//import org.jext.Jext;

//*****************************************//
//*****************************************//
public class classes extends Pag {
	//configurações
	String dirh = "/javadocs/api/"; //caminho WEB
	//String dirc = "/mnt/b/java";
	String dirI = "/usr/share/java/api/"; //caminho Importa
	String dados = "Atalhos";

	Dados dad;
	DadosSet DB;

	//*****************************************//
	public void data() {
		DB = dad.executeQuery("SELECT * from Classes ORDER BY data desc LIMIT 60");
		lista();
	}
 
 
	//*****************************************//

	public boolean run(Pedido pd) {
		super.run(pd);

		ped.on("<html><head><title>CLASSES Java - AJUDA</title></head>");
		ped.on("<body bgcolor=abcff><font size=2>");

		dad = new Dados(dados);
		menu1();
		op = str.seNull(ped.getString("op"),"f");

		if (op.equals("f")) {
			if (execsql("SELECT * FROM Classes WHERE nv>4 ORDER BY data desc,nome")) lista();
			ped.on("<hr>");
			if (execsql("SELECT * FROM Classes WHERE nv>0 and nv<5 ORDER BY nome")) lista();

		} else if (op.equals("fp")) {
			if (execsql("SELECT * FROM Classes WHERE pacote='"+ped.getString("p")+"' ORDER BY nome")) lista();

		} else if (op.equals("ed")) {


		} else if (op.equals("l")) {
			if (execsql("SELECT * FROM Classes order by nome")) lista();

		} else if (op.equals("ab")) {
			abre();

		} else if (op.equals("pq")) {
			if (execsql("SELECT * FROM Classes WHERE concat(pacote,nome) like '%"+ped.getString("palav")+"%' ORDER BY pacote,nome")) lista();

		} else if (op.equals("limpa")) {
			limpa();

			} else if (op.equals("p")) {
			pacotes();

		} else if (op.equals("td")) {
			if (execsql("SELECT * FROM Classes WHERE nv>0 ORDER BY nv desc,nome")) lista();

		} else if (op.equals("l")) {
			lista();
		} else {
			exec();
		}

		if (DB!=null) {
			DB.close();
		}

		rodap();

		return true;

	}

	//*****************************************//
	private void pacotes() {
		String s;
		ped.on("<DIV nowrap>");
		DB = dad.executeQuery("SELECT pacote,count(pacote) FROM Classes GROUP BY pacote");
		while (DB.next()) {
			s = DB.getString("pacote");
			ped.on("<br>"+atalho("op=fp&p="+s,s));
		}
		ped.on("</DIV>");
	}


	//*****************************************//
	private void abre() {
		dad.executeUpdate("UPDATE Classes set nv=nv+1, data='"+
			data.strSql()+"' WHERE cod="+ped.getString("cod"));
		DB = dad.executeQuery("SELECT * from Classes WHERE cod="+ped.getString("cod"));
		if (dad.erro) {
			ped.erro("err",new Exception(dad.sErro));
		}
		if (DB.next()) {
			ped.on("<script>");
			ped.on("window.location = '"+dirh+
				DB.getString("pacote")+DB.getString("nome")+".html';");
			ped.on("</script>");
		}
	}

	//*****************************************//
	private void lista() {
		String n;

		ped.on("<DIV nowrap>");
		int nr = 0;
		while (DB.next()) {
			nr++;
			n = DB.getString("nome");
			ped.on("<br>"+atalho("op=ab&cod="+DB.getString("cod"),
				((DB.getString("tipo")!=null) ? "<i>":"" )+n+"</i>","_blank")+
				" ("+DB.getString("nv")+")");
		}
		ped.on("</DIV>");

	}

	//*****************************************//
	private boolean execsql(String a) {

		DB = dad.executeQuery(a);
		return true;
	}


	//*****************************************//
	public void ddup() {
		String s="",s1;
		DB = dad.executeQuery("SELECT * from Classes ORDER BY pacote,nome");
		while (DB.next()) {
			s1 = DB.getString("pacote")+DB.getString("nome");
			if (s1.equals(s)) {
				ped.on("<br>"+s1+"="+dad.executeUpdate("DELETE FROM Classes WHERE cod="+DB.getString("cod")));
			}
			s = s1;
		}
	}

	//*****************************************//
	private void menu1() {
		ped.on(
			"<table bgcolor=yellow><tr><td><font size=2>"
			+atalho("op=f","+usadas")
			+" - "+atalho("op=data","data")
			+" - "+atalho("op=p","pacotes")
			+" - "+atalho("op=l","todos")
			+" - "+atalho("op=importa","importa")
			+" - "+atalho("op=limpa","limpa")
			+" - <a href=/javadocs/tutorial/ target=_blank>Tutorial</a>"
			+"<form action=\"?\" method=GET>"
			+"<input type=hidden name=op size=15 value=pq>"
			+"<input type=text name=palav size=15 value="+str.seNull(ped.getString("palav"),"")+">"
			+"<input type=submit value='>>>'>"
			+"</form></table>"
		);

	}

	//*****************************************//
	//elimina não existentes 
	public void limpa() {
		DadosSet ds = dad.executeQuery(
			"SELECT * FROM Classes ORDER BY pacote,nome"
		);
		int pe=0,pn=0;
		String ne = "";
		while (ds.next()) {
			String a = dirI+ds.getString("pacote")+ds.getString("nome")+".html";
			if ((new File(a)).exists()) {
				on("<br>"+a+" OK");
				pe++;
			} else {
				on("<br>"+a+" Não Existe");
				ne += ","+ds.getString("cod");
				pn++;
			}
		}
		if (ne.length()>0) {
			dad.executeUpdate("DELETE FROM Classes WHERE cod IN ("+ne.substring(1)+")");
		}
		on("<hr>existe: "+pe+" não "+pn);
	}
	
	//*****************************************//
	public void importa() {
		String dir=dirI; //dirc+dirh;
		int ni=0;
		executa e = new executa();
		e.exec(ped,"find "+dir+" -name package-frame.html");
		String vp[] = str.palavraA(e.getOut(),"\n");
		ped.on("<h3>Pesquisando<br>package-frame.html em:<br>"+dir+
			" Pacotes: "+vp.length+"</h3>");
		for (int i=0;i<vp.length;i++) {
			ni += importa1(vp[i]);
		}
		ped.on("<hr>FIM, importados="+ni);
	}
	//*****************************************//
	private int importa1(String pk) {
		if (dirI.length()>=pk.length()) {
			ped.on("<hr>ERRO Pacote: <b>"+pk+"</b>");
			return 0;
		}
		String npk=str.leftRat(pk.substring(dirI.length()),"/")+"/";
		ped.on("<hr>Pacote: <b>"+npk+"</b>");
		arquivo a = new arquivo(pk);
		String tp,h,c,t = a.leTxt();
		int p,ni=0,tc=0;
		while ((p=importa2(t,"<a "))!=-1) {
			t = t.substring(p+3);
			h = str.substrAtAt(t,"\"","\"");
			h = str.leftRat(h,".");
			t = t.substring(t.indexOf(">")+1);
			c = t.substring(0,importa2(t,"</a>"));
			tp = "";
			if (c.indexOf("<")!=-1) {
				tp = str.substrAtAt(c,"<",">");
				c = str.trimm(str.substrAtAt(c,">","<"));
			}
			if (h.indexOf("/..")==-1) {
				tc++;
				DadosSet ds = dad.executeQuery(
					"SELECT * FROM Classes WHERE pacote='"+npk+"'"
					+" and nome='"+h+"'"
					//+" and tipo='"+tp+"'"
				);
				if (ds.next()) {
				} else if (dad.executeUpdate("INSERT INTO Classes (pacote,nome,tipo) "+
					"VALUES ('{0}','{1}','{2}')",new String[]{npk,h,tp})!=-1) {
					ni++;	
					ped.on("<br>"+h+"->"+c+"("+tp+")");
				} else {
					ped.on("<br>"+h+"->"+c+"("+tp+") DUPLO");
				}
			}
		}
		ped.on("<br><b>Total importado: "+ni+" de "+tc+"</b>");
		return ni;
	}
	//*****************************************//
	private int importa2(String t,String p) {
		int i = t.indexOf(p);
		int i1 = t.indexOf(p.toUpperCase());
		if (i!=-1 && i<i1) {
			return i;
		}
		return i1;
	}

}

