/*7
		Nilson - Jul/2002
*/

import java.util.*; 

import br.org.guarani.servidor.*;
import br.org.guarani.util.*;
import bd.*;


//*****************************************//
//*****************************************//
public class ProtocoloI extends Pag {
	Dados dad;
	DadosSet DS;
	String grupo;
	int amenu=32;

	//*****************************************//
	public boolean run(Pedido pd) {
		super.run(pd);

		dad = new Dados("Cad");

		margem = 0;

		if (op.equals("")) {
			inicio();
			return true;
		} else if (op.equals("menu")) {
			menu();
			return true;
		}

		cab("Protocolo Interno");
		exec();

		dad.close();
		rodap();

		return true;
	}

	//*****************************************//
	public void inicio() {
		ped.on("<html>");
		ped.on("<head><title>Protocolo Interno</title></head>");
		ped.on(" <frameset rows=\""+amenu+",100%\" border=1 framespacing=1 frameborder=0>");
		ped.on("  <frame src=\"Protocolo.class?op=menu\" scrolling=\"no\" noresize1 name=\"amenu\">");
		ped.on("  <frame src=\"Protocolo.class?op=Pendencia\" scrolling1=\"no\" noresize1 name=\"conteudo\">");
		ped.on("</frameset>");
		ped.on("</html>");
	}

	//*****************************************//
	public void Pendencia() {

		ped.on("<center><font size=3>");
		ped.on("<table border=2 bordercolor=red>");
		ped.on("<font face=\"Arial\" size=2></center>");

		ped.on("<form action=Protocolo.class method=post>");
		ped.on(" <input type=hidden name=op value=pendencia>");

		ped.on("<tr><td colspan=2 align=center><input type=submit value=Consultar>");
		ped.on(" </form></table>");

	}


	//*****************************************//
	public void Novo() {
		Cad_Geral();
	}


	//*****************************************//
	public void Cad_Geral() {

		String C_UF=ped.getString("C_UF","23");

		ped.on("<script>");
		ped.on("function muf(p) {");
		//ped.on(" alert(window.focg);");
		//ped.on(" alert(p.parent);");
		ped.on(" window.location.href='Protocolo.class?op=Cad_Geral&C_UF='+p.value;");
		ped.on("");
		ped.on("");
		ped.on("");
		ped.on("}");
		ped.on("</script>");

		ped.on("<center><font size=6>");
		ped.on("Consulta Cadastro Geral");

		ped.on("<table border=2 bordercolor=red>");
		ped.on("<font face=\"Arial\" size=2></center>");

		ped.on("<form id=focg action=Protocolo.class method=post>");
		ped.on(" <input type=hidden name=op value=Pesq_Cad_Geral>");

		ped.on("<tr><td align=right>Tipo:<td>");
		select("C_Tipo_Geral","select C_Tipo_Geral, Descr_Tipo_Geral"+
			" from Tipo_Geral order by Descr_Tipo_Geral",null);

		ped.on("<tr><td align=right>Municipio:<td>");
		select("C_Mun","select C_Mun, Nome_Mun, C_Class from Muns"+
			" Where C_UF = "+C_UF+" order by C_Class, Nome_Mun",null);
		select("<td>C_UF","select C_UF, Nome_UF from Ufs order by Nome_UF",C_UF,
			" onChange=javascript:muf(this); ");

		ped.on("<tr><td align=right>Nome:<td>");
		ped.on("<input type=text name=Nome_Geral size=50>");

		ped.on("<tr><td align=right>Esfera:<td>");
		select("C_Esfera_Geral","select C_Esfera_Geral, Descr_Esfera_Geral from"+
			" Esfera_Geral order by Descr_Esfera_Geral",null);

		ped.on("<tr><td colspan=2 align=center><input type=submit value=Consultar><input type=reset value=Limpar>");
		ped.on(" </form></table>");

	}

	//*****************************************//
	public void Pesq_Cad_Geral() {

		String Tip="",C_M="", Nom="", Esf="";
		Tip = ped.getString("C_Tipo_Geral","");
		C_M = ped.getString("C_Mun","");
		Nom = ped.getString("Nome_Geral","");
		Esf = ped.getString("C_Esfera_Geral","");

		String sqlG="SELECT Cad_Geral.Nome_Geral, Cad_Geral.Telefone, Descr_Tipo_Geral, "+
																					"Muns.Nome_Mun+' / '+UFs.Nome_UF AS Mun "+
															"FROM UFs, Muns, Tipo_Geral, Cad_Geral, Cargos, Esfera_Geral "+
															"WHERE UFs.C_Uf = Muns.C_UF and Tipo_Geral.C_Tipo_Geral = Cad_Geral.C_Tipo_Geral and "+
																					"Cad_Geral.C_Cod_Geral = Cargos.C_Cod_Ger_2 and "+
																					"Cad_Geral.C_Esfera_Geral = Esfera_Geral.C_Esfera_Geral and "+
																					"Muns.C_Mun = Cad_Geral.C_Mun and Cargos.C_Cod_Ger_1=1";

		DS = dad.executeQuery(sqlG+ ((Nom=="") ? "" : " And (Nome_Geral like '%"+Nom+"%' or "+
											"Apelid_Geral like '%"+Nom+"%')")+
											((Tip.equals("")) ? "" : " And Cad_Geral.C_Tipo_Geral="+Tip)+
											((Esf.equals("")) ? "" : " And Cad_Geral.C_Esfera_Geral="+Esf)+
											((C_M.equals("")) ? "" : " And Cad_Geral.C_Mun="+C_M));
											//+
											//" ORDER BY Mun, Nome_Geral");
		if (dad.erro) {
			ped.erro(dad.sErro,new Exception());
		}

		ped.on("<center><font size=6>");
		ped.on("Consulta Cadastro Geral");
		ped.on("<table border=2 bordercolor=red width=90% align=center>");
		ped.on("<tr><th>Municipio<th>Nome<br>Fone/mail(s)<td>s");

		int nr = 0;

		while (DS.next()) {
			ped.on("<tr><td>"+DS.getString("Nome_Geral"));
			ped.on("<td>"+DS.getString("Telefone"));
			ped.on("<td>"+DS.getString("Descr_Tipo_Geral"));
			nr++;
			if (nr>100) {
				nr = -nr;
				break;
			}
		}
		ped.on("<tr><td colspan=3 align=center><b>Listados "+((nr>0)?""+nr:(-nr)+" (limite)"));
		ped.on("</table>");

	}

	//*****************************************//
	private void select(String nome, String query) {
		select(nome,query,null,null);
	}
	//*****************************************//
	private void select(String nome, String query,String ant) {
		select(nome,query,ant,null);
	}

	//*****************************************//
	private void select(String nome, String query, String sel,String par) {
		String s;

		ped.on("<select "+((par==null)?"":par)+" name="+nome+">");
		if (sel==null) {
			ped.on("<option value=></option>");
			sel="";
		}
		DS = dad.executeQuery(query);
		if (dad.erro) {
			ped.erro(dad.sErro);
			return;
		}

		while (DS.next()) {
			s = DS.getString(1);
			ped.on("<option "+((sel.equals(s)) ? "selected": "" ));
			ped.on(" value="+s+">"+DS.getString(2));
		}
		ped.on("</select>");
	}


		//*****************************************//
		public void menu() {
			cab(null);
			ped.on("<center>");

			ped.on("<table class=menu_table width=100% height="+amenu+" CELLSPACING=0 CELLPADDING=0>");
			ped.on("<tr class=menu_tr VALIGN=CENTER>");
			ped.on("<td class=menu_td><a class=menu_a href=Protocolo.class?op=Novo target=conteudo><b>Novo</a>");
			ped.on("<td class=menu_td><a class=menu_a href=Protocolo.class?op=Consulta target=conteudo><b>Consulta</a>");
			ped.on("<td class=menu_td><a class=menu_a href=Protocolo.class?op=Localizar target=conteudo><b>Localizar</a>");
			ped.on("<td class=menu_td><a class=menu_a href=Protocolo.class?op=Pendencia target=conteudo><b>Pendências</a>");

			ped.on("</table>");
			ped.on("</center>");
	}


}
