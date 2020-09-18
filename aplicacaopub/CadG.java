/*7
		Nilson - Jul/2002
*/

import java.util.*;

import br.org.guarani.servidor.*;
import br.org.guarani.util.*;
import bd.*;


//*****************************************//
//*****************************************//
public class CadG extends Pag {
	Dados dad;
	DadosSet DS;
	String grupo;

	//*****************************************//
	public boolean run(Pedido pd) {
		super.run(pd);

		dad = new Dados("Cad");

		margem = 3;
		cab("Consulta Cadastro Geral");
		Cad_Geral();
		incluiJs("mz-ns-ie.js");

		exec();
		dad.close();
		rodap();

		return true;
	}

	//*****************************************//
	public void Cad_Geral() {

		ped.on("<br><table border=2 bordercolor=red>");
		ped.on("<font face=\"Arial\" size=2></center>");

		ped.on("<form id=focg action=CadG.class method=post>");
		ped.on(" <input type=hidden name=op value=Pesq_Cad_Geral>");

		ped.on("<tr><td align=right>Tipo:<td>");
		select("C_Tipo_Geral","select C_Tipo_Geral, Descr_Tipo_Geral"+
			" from Tipo_Geral order by Descr_Tipo_Geral",null);

		Muns("Cad_Geral",ped.getString("C_UF","23"),null);
  
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

		String sqlG="SELECT Cad_Geral.Nome_Geral, Cad_Geral.Apelid_Geral, "+
																					"Cad_Geral.C_Cod_Geral, Cad_Geral.Fax, "+
																					"Cad_Geral.Telefone, Cad_Geral.Mail, Descr_Tipo_Geral, "+
																					"Muns.C_UF, Muns.Nome_Mun+' / '+UFs.Nome_UF AS Mun "+
															"FROM UFs, Muns, Tipo_Geral, Cad_Geral, Cargos, Esfera_Geral "+
															"WHERE UFs.C_Uf = Muns.C_UF and Tipo_Geral.C_Tipo_Geral = Cad_Geral.C_Tipo_Geral and "+
																					"Cad_Geral.C_Cod_Geral = Cargos.C_Cod_Ger_2 and "+
																					"Cad_Geral.C_Esfera_Geral = Esfera_Geral.C_Esfera_Geral and "+
																					"Muns.C_Mun = Cad_Geral.C_Mun and Cargos.C_Cod_Ger_1=1";

		DS = dad.executeQuery(sqlG+ ((Nom=="") ? "" : " And (Nome_Geral like '%"+Nom+"%' or "+
											"Apelid_Geral like '%"+Nom+"%')")+
											((Tip.equals("")) ? "" : " And Cad_Geral.C_Tipo_Geral="+Tip)+
											((Esf.equals("")) ? "" : " And Cad_Geral.C_Esfera_Geral="+Esf)+
											((C_M.equals("")) ? "" : " And Cad_Geral.C_Mun="+C_M)+
											" ORDER BY Nome_Mun, Nome_UF, Nome_Geral");
		if (dad.erro) {
			ped.erro(dad.sErro,new Exception());
		}
		ped.on("<br><center><font size=4>");
		if (true) {
			ped.on("<center><a href=GadG.class?op=novo><b>Incluir Novo</b></a></center><hr>");
		}
		ped.on("<table border=2 bordercolor=red width=90% align=center>");
		ped.on("<tr><th>Municipio<th>Nome<br>Fone/mail(s)");

		int nr = 0;

		while (DS.next()) {
			ped.on("<tr><td>"+DS.getString("Mun"));
			ped.on("<td><a href=\"CadG.class?op=altera&C_Cod_Geral="+
							DS.getString("C_Cod_Geral")+"\">"+
							DS.getString("Nome_Geral")+"</a>");
			//ped.on("<td>"+DS.getString("Nome_Geral"));
			if (DS.getString("Apelid_Geral").length()!=0) {
				ped.on(" ( "+DS.getString("Apelid_Geral")+" ) ");
			}
			ped.on(" "+DS.getString("Descr_Tipo_Geral"));
			if (DS.getString("Telefone").length()!=0) {
				ped.on("<br>Fone: "+DS.getString("Telefone"));
			}
			if (DS.getString("Fax").length()!=0) {
				ped.on(" Fax: "+DS.getString("Fax"));
			}
			if (DS.getString("Mail").length()!=0) {
				ped.on("<br>Correio: "+DS.getString("Mail"));
			}

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
	public void altera() {
		novo();
	}

	//*****************************************//
	public void novo() {
		String Nome_Geral="",Apelid_Geral="",C_Esfera_Geral="",C_Tipo_Geral="",CG="",
									C_Fonte_Geral="", C_Dep_Usu="", Endereco_Geral="", Cep_Geral="",
									C_Mun="", Telefone="", Fax="", Mail="",Sexo="",Data_Nascto="",
									C_Tipo_Doc="", Documento="", Moficial="", C_UF="";

		CG = ped.getString("C_Cod_Geral","");

		if (CG!="") {
			DS = dad.executeQuery("SELECT Cad_Geral.*, Muns.C_UF FROM Cad_Geral, Muns "+
																									"where Cad_Geral.C_Mun=Muns.C_Mun And C_Cod_Geral="+CG);
			DS.next();
			Nome_Geral     = DS.getString("Nome_Geral");
			Apelid_Geral   = DS.getString("Apelid_Geral");
			C_Esfera_Geral = DS.getString("C_Esfera_Geral");
			C_Tipo_Geral   = DS.getString("C_Tipo_Geral");
			C_Fonte_Geral  = DS.getString("C_Fonte_Geral");
			C_Dep_Usu      = DS.getString("C_Dep_Usu");
			Endereco_Geral = DS.getString("Endereco_Geral");
			Cep_Geral      = DS.getString("Cep_Geral");
			C_Mun          = DS.getString("C_Mun");
			Telefone       = DS.getString("Telefone");
			Fax            = DS.getString("Fax");
			Mail           = DS.getString("Mail");
			Sexo           = DS.getString("Sexo");
			Data_Nascto    = DS.getString("Data_Nascto");
			C_Tipo_Doc     = DS.getString("C_Tipo_Doc");
			Documento      = DS.getString("Documento");
			Moficial       = DS.getString("Moficial");
			C_UF           = DS.getString("C_UF");
		}

		incluiJs("valida.js");
		ped.on(" <align=Center><font size=4><br><b>"+((CG!="") ? "Altere dados do Cadastro Geral":
				"Inclua no Cadastro Geral")+":</font></center></b><hr>");
		ped.on("<center><font size=3>");
		ped.on("<table border=1 bordercolor=red>");
		ped.on("<font face=\"Arial\" size=2></center>");
		ped.on("<form action=CadG.class method=post>");
		if (CG!="") ped.on("<input name=C_Cod_Geral type=hidden value="+CG+">");
		ped.on("<input name=op type=hidden value=grava>");

		ped.on("<tr><td align=right>Nome: <td>");
		ped.on("<input name=Nome_Geral size=60 maxlength=100 value='"+Nome_Geral+"'>");
  
		//Muns("novo",ped.getString("C_UF"),ped.getString("C_Mun"));
		Muns("novo",C_UF,C_Mun);

		ped.on("<tr><td align=right>Sigla: <td>");
		ped.on("<input name=Apelid_Geral size=30 maxlength=30 value='"+Apelid_Geral+"'>");
		ped.on("<tr><td align=right>Tipo: <td>");
		select("C_Tipo_Geral","select C_Tipo_Geral, Descr_Tipo_Geral "+
									"from Tipo_Geral order by Descr_Tipo_Geral",C_Tipo_Geral);
		ped.on("Esfera: ");
		select("C_Esfera_Geral","select C_Esfera_Geral, Descr_Esfera_Geral "+
									"from Esfera_Geral order by Descr_Esfera_Geral",C_Esfera_Geral);
		ped.on("<tr><td align=right>Endereço: <td>");
		ped.on("<input name=Endereco_Geral size=50 maxlength=50 value='"+Endereco_Geral+"'>");


		ped.on("<tr><td colspan=2 align=center><input type=submit value="+((CG!="") ? "Alterar":"Incluir")+">");
		ped.on(" </form>");
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
	public void Muns(String op,String uf,String mn) {
		ped.on("<script>");
		ped.on("function muf(p) {");
		//ped.on(" alert(window.focg);");
		//ped.on(" alert(p.parent);");
		ped.on(" window.location.href='CadG.class?op="+op+"&C_UF='+p.value;");
		ped.on("}");
		ped.on("</script>");

		ped.on("<tr><td align=right>Municipio:<td>");
		select("C_Mun","select C_Mun, Nome_Mun, C_Class from Muns"+
			" Where C_UF = "+uf+" order by C_Class, Nome_Mun",mn);
		select("<td>C_UF","select C_UF, Nome_UF from Ufs order by Nome_UF",uf,
			" onChange=javascript:muf(this); style=\"width:50;\" ");
	}
 
}

