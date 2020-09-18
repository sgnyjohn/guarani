/*
		everton / mai/2001
*/

package agg;

//import java.util.*;
import bd.*;
import br.org.guarani.servidor.*;
import br.org.guarani.util.*;

//*****************************************//
//*****************************************//
public class Agg extends PagV {

	//String dt;
	cliOdbc DS;

	String grupo;

		public long fatorial(int n) {
			if (n<2) return n;
			return n*fatorial(n-1);
		}


	//*****************************************//
	public boolean run(Pedido pd) {
		super.run(pd);

		if (!acesso()) return false;

		menu();
		margem = 3;
		cab("Agenda Geral de Governo");
		DS = new cliOdbc(ped);
		DS.conecta("172.27.79.6","AGG");

		exec();
		DS.close();
		rodap();

		return true;
	}


	//*****************************************//
	public void pesquisa() {

				ped.on("<center><font size=5><b>Órgãos</b></font><hr>");
				ped.on("<font face=\"Arial\" size=2></center>");


				//ped.on("<font size=4>");
				if (acessoa()) {
					ped.on("<center><a href=Agg.class?op=novo target=aconteudo><b>Adicione<br>Evento</b></a></center><hr>");
				}

				DS.executeQuery("SELECT * FROM age_orgaos WHERE Rec_Env='E' ORDER BY orgao");

				//ped.on("<DIV nowrap><b>");
				ped.on("<b>");
				while (DS.next()) {
					ped.on("<img src=/imagens/p1.gif border=0>");
					ped.on("<a href=\"Agg.class?op=mostra&c_orgao="+
											DS.getString("c_orgao")+"\">"+
											DS.getString("orgao")+"</a><br>");
				}
				//ped.on("</div></b>");
				ped.on("</b>");
				DS.close();
			}

	//**************************
	public void publicar() {
						String grupo = str.seNull(ped.getString("grupo"),"").trim();
						ped.on("<center><font size=5><b>Órgãos</b></font><hr>");
						ped.on("<font face=\"Arial\" size=2></center>");


							DS.executeQuery("SELECT Age_Cad.*, Age_Orgaos.*, Age_Tipo.*, Muns.* FROM Age_Cad,  Age_Orgaos,"+
								"Age_Tipo, Muns Where Age_Cad.c_age_orgao = Age_Orgaos.c_orgao and Age_Cad.c_age_tipo = "+
								"Age_Tipo.c_age_tipo and Age_Cad.c_mun = Muns.C_Mun and dt_ev>=date() ORDER BY orgao,dt_ev");
								int nr = 0;
								grupo = "";
								String g;
								ped.on("<table border=1 width=90% align=center>");

								while (DS.next()) {

									nr++;
									g = DS.getString("orgao");
									if (g.compareTo(grupo)!=0) ped.on("<tr><td colspan=3 align=center bgcolor=rgb(128,128,128)><b>"+g);
									grupo = g;
											ped.on("<tr><td>");
											ped.on(formatData(DS.getString("dt_ev"),10)+"<br>");
											ped.on("<td>"+DS.getString("hr_ev").substring(11,16)); //.substring(11,16));
											ped.on("<td><b>"+DS.getString("descr_age_tipo")+"</b>"+" "+DS.getString("evento")+"<br>"+
											"<b>"+DS.getString("nome_mun")+";</b>"+DS.getString("local")+";"+DS.getString("endereco")+"<br>");
											ped.on("<b>Comitiva: </b>"+DS.getString("comitiva")+"<br>");
											ped.on("<b>Responsável: </b>"+DS.getString("local"));


							}
								ped.on("</table>");
								DS.close();
	}
	//*****************************************//
	public void mostra() {

						String grupo = str.seNull(ped.getString("grupo"),"").trim();
						ped.on("<center><font size=5><b>Órgãos</b></font><hr>");
						ped.on("<font face=\"Arial\" size=2></center>");


							DS.executeQuery("SELECT Age_Cad.*, Age_Orgaos.*, Age_Tipo.*, Muns.* FROM Age_Cad,  Age_Orgaos,"+
								"Age_Tipo, Muns Where Age_Cad.c_age_orgao = Age_Orgaos.c_orgao and Age_Cad.c_age_tipo = "+
								"Age_Tipo.c_age_tipo and Age_Cad.c_mun = Muns.C_Mun and c_orgao="+ped.getString("c_orgao")+
								" ORDER BY dt_ev desc, hr_ev desc");

								int nr = 0;
								grupo = "";
								String g;
								ped.on("<table border=1 width=90% align=center>");

								while (DS.next()) {

									nr++;
									g = DS.getString("orgao");
									if (g.compareTo(grupo)!=0) ped.on("<tr><td colspan=3 align=center bgcolor=rgb(128,128,128)><b>"+g);
									grupo = g;
											ped.on("<tr><td><a href=Agg.class?op=altera&g_c_age_cad="+
												DS.getString("g_c_age_cad")+"&c_age_orgao="+DS.getString("c_age_orgao")+">");
											ped.on(formatData(DS.getString("dt_ev"),10)+"</a><br>");
											ped.on(DS.getString("hr_ev").substring(11,16));
											ped.on("<td><b>"+DS.getString("descr_age_tipo")+"</b>"+" "+DS.getString("evento")+"<br>"+
											DS.getString("comitiva"));
											ped.on("<td><a href=Agg.class?op=altera&c_mun="+DS.getString("c_mun")+">");
											ped.on(DS.getString("nome_mun")+"</a><br>");
											ped.on(DS.getString("local")+"<br>"+
											DS.getString("endereco"));

							}
								ped.on("</table>");
								DS.close();
	}

	//**********************************
public void ev_muns() {

						String grupo = str.seNull(ped.getString("grupo"),"").trim();
						ped.on("<center><font size=5><b>Órgãos</b></font><hr>");
						ped.on("<font face=\"Arial\" size=2></center>");

							DS.executeQuery("SELECT Age_Cad.*, Age_Orgaos.*, Age_Tipo.*, Muns.* FROM Age_Cad,  Age_Orgaos,"+
								"Age_Tipo, Muns Where Age_Cad.c_age_orgao = Age_Orgaos.c_orgao and Age_Cad.c_age_tipo = "+
								"Age_Tipo.c_age_tipo and Age_Cad.c_mun = Muns.C_Mun and c_mun="+ped.getString("c_mun")+
								" ORDER BY dt_ev desc, hr_ev desc");

								int nr = 0;
								grupo = "";
								String g;
								ped.on("<table border=1 width=90% align=center>");

								while (DS.next()) {

									nr++;
									g = DS.getString("orgao");
									if (g.compareTo(grupo)!=0) ped.on("<tr><td colspan=3 align=center bgcolor=rgb(128,128,128)><b>"+g);
									grupo = g;
											ped.on("<tr><td><a href=Agg.class?op=altera&g_c_age_cad="+
												DS.getString("g_c_age_cad")+"&c_age_orgao="+DS.getString("c_age_orgao")+">");
											ped.on(formatData(DS.getString("dt_ev"),10)+"</a><br>");
											ped.on(DS.getString("hr_ev").substring(11,16));
											ped.on("<td><b>"+DS.getString("descr_age_tipo")+"</b>"+" "+DS.getString("evento")+"<br>"+
											DS.getString("comitiva"));
											ped.on("<td><a href=Agg.class?op=altera&c_mun="+DS.getString("c_mun")+">");
											ped.on(DS.getString("nome_mun")+"</a><br>");
											ped.on(DS.getString("local")+"<br>"+
											DS.getString("endereco"));

							}
								ped.on("</table>");
								DS.close();
	}
	//*****************************************//
	//*****************************************//
	public void altera() {
		novo();
	}

	//*****************************************//
	public void novo() {

		String ca,cg="",dt_ev="",hr_ev="",c_age_tipo="",c_mun="",local="",endereco="",
									evento="",comitiva="",contato_resp="",c_age_orgao;

		ca = ped.getString("g_c_age_cad","");
		c_age_orgao = ped.getString("c_orgao","13");

		if (ca!="") {
			DS.executeQuery("SELECT * FROM Age_cad where g_c_age_cad="+ca);
			DS.next();
			dt_ev       = formatData(DS.getString("dt_ev"),10);
			hr_ev       = DS.getString("hr_ev").substring(11,16);
			c_age_tipo  = DS.getString("c_age_tipo");
			c_mun       = DS.getString("c_mun");
			local       = DS.getString("local");
			endereco    = DS.getString("endereco");
			evento      = DS.getString("evento");
			comitiva    = DS.getString("comitiva");
			contato_resp= DS.getString("contato_resp");
		}

		//ped.on("<script language=\"JavaScript\" src=\"/js/valida.js\"></script>");
		incluiJs("valida.js");
		ped.on("<table border=5 bordercolor=red>");
		ped.on(" <align=center><font size=4><br><b>"+((ca!="") ? "Altere os dados do evento":
				"Informe os dados do novo evento")+":</font></center></b><hr>");

		ped.on("<form action=Agg.class method=post>");
		if (ca!="") ped.on("<input name=g_c_age_cad type=hidden value="+ca+">");
		ped.on("<input name=c_age_orgao type=hidden value="+c_age_orgao+">");
		ped.on("<input name=op type=hidden value=grava>");

		ped.on("<tr><td align=right>Data:<td>");
		ped.on("<input onBlur=ValData(this) name=dt_ev size=10 maxlength=10 value='"+dt_ev+"'>");
		ped.on("<align=right>Hora:");
		ped.on("<input onBlur=ValHora(this) name=hr_ev size=5 maxlength=5 value='"+hr_ev+"'>");
		ped.on("<tr><td align=right>Tipo:<td>");
		select("c_age_tipo","select c_age_tipo, descr_age_tipo from age_tipo order by descr_age_tipo",c_age_tipo);
		ped.on("<tr><td align=right>Município:<td>");
		select("c_mun","select c_mun, nome_mun from muns WHERE c_uf=23 order by nome_mun",c_mun);
		ped.on("<tr><td align=right>Local:<td>");
		ped.on("<input name=local size=50 maxlength=50 value='"+local+"'>");
		ped.on("<tr><td align=right>Endereço:<td>");
		ped.on("<input name=endereco size=50 maxlength=50 value='"+endereco+"'>");
		ped.on("<tr><td align=right>Evento:<td>");
		ped.on("<textarea name=evento rows=3 cols=43>"+evento+"</textarea>");
		ped.on("<tr><td align=right>Comitiva:<td>");
		ped.on("<textarea name=comitiva rows=3 cols=43>"+comitiva+"</textarea>");
		ped.on("<tr><td align=right>Responsável:<td>");
		ped.on("<textarea name=contato_resp rows=3 cols=43>"+contato_resp+"</textarea>");
		ped.on("<tr><td colspan=2 align=center><input type=submit value="+((ca!="") ? "Alterar":"Incluir")+">");
		ped.on(" </form>");
		ped.on("</table>");
	}

	//*****************************************//
	private void select(String nome, String query, String sel) {
		String s;

		ped.on("<select name="+nome+">");
		DS.executeQuery(query);

		while (DS.next()) {
			s = DS.getString(1);
			ped.on("<option "+((sel.compareTo(s)==0) ? "selected": "" ));
			ped.on(" value="+s+">"+DS.getString(2));
		}
		ped.on("</select>");
		//DS.close();
	}

	//*****************************************//
	public void grava() {
		String ca;
		ca = ped.getString("g_c_age_cad","");

		if (!acessoa()) {
			logs.grava("seg","Agg: "+ped);
			return;
		}

		String u;
		if (ca=="") {
			u = "Insert into Age_cad (c_age_orgao, dt_ev, hr_ev, c_age_tipo, c_mun, "+
				"local, endereco, evento, comitiva, contato_resp)"+
				" values ("+ped.getString("c_age_orgao")+",'"+ped.getString("dt_ev")+"','"+ped.getString("hr_ev")+"',"+
				ped.getString("c_age_tipo")+","+ped.getString("c_mun")+",'"+
				ped.getString("local")+"','"+ped.getString("endereco")+"','"+
				ped.getString("evento")+"','"+ped.getString("comitiva")+"','"+
				ped.getString("contato_resp")+"')";
		} else {
			u = "update age_cad set dt_ev='"+ped.getString("dt_ev")+"',"+
				"hr_ev='"+ped.getString("hr_ev")+"',"+
				"c_age_tipo="+ped.getString("c_age_tipo")+","+
				"c_mun="+ped.getString("c_mun")+","+
				"local='"+ped.getString("local")+"',"+
				"endereco='"+ped.getString("endereco")+"',"+
				"evento='"+ped.getString("evento")+"',"+
				"comitiva='"+ped.getString("comitiva")+"',"+
				"contato_resp='"+ped.getString("contato_resp")+"'"+
				" where g_c_age_cad="+ped.getString("g_c_age_cad");
		}
		DS.executeUpdate(u);
		ped.on("<hr>"+u+"<hr>");
		ped.on("Erro="+DS.sErro);

	}

	public void inicio() {
	}

	//***************************//
	public static String formatData(String a,int b)  {
		String r = a.substring(8,10)+"/"+a.substring(5,7)+"/"+a.substring(0,4)+a.substring(10);
		return r.substring(0,b);
	}



	//***************************//
	public void menu()  {
		ped.on("<table border=1 bgcolor=yellow width=100% >");
		ped.on("<tr>");
		ped.on("<td HEIGHT=15 align=center><a href=Agg.class?op=novo><b>Novo</a>");
		ped.on("<td align=center><a href=Agg.class?op=pesquisa><b>Listar</a>");
		ped.on("<td align=center><a href=Agg.class?op=publicar><b>Publicar</a>");
		ped.on("</table>");
	}

	//***************************//
	private boolean acesso() {
		String gusu = "";
		String gadm = "-everton-signey john-nilson meyer-";
		int i=0;

		grupo = "???";
		if (gusu.indexOf("-"+(""+usu).toLowerCase()+"-")>-1) grupo = "usu";
		if (gadm.indexOf("-"+(""+usu).toLowerCase()+"-")>-1) grupo = "adm";
		return "-adm-usu-".indexOf("-"+grupo+"-")>-1;
	}

	//*****************************************//
	//acesso a alteração?
	private boolean acessoa() {
		return "-signey john-nilson meyer-everton-marcelo-".indexOf("-"+usu.toLowerCase()+"-")>-1;
	}

}
