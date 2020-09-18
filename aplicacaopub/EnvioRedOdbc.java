/*
	*/
 
import java.util.*;
import java.sql.*;

//import util.*;
import br.org.guarani.servidor.*;
import br.org.guarani.util.*;
import bd.*;


//***************************//
//***************************//
public class EnvioRedOdbc extends Pag {
	protected Connection oCon=null;
	protected Connection oConM=null;
	protected Statement stmt;
	protected Statement stmtM;
	protected CallableStatement rsC;

	cliOdbc DB;

	ResultSet rs;
	ResultSet rsM;
	Dados dad;
	DadosSet DS;


	//***************************//
	public boolean run(Pedido pd) {
		super.run(pd);
		cab("Envio Redação");
		menu();

		if (op.equals("emailNotes")) {
			emailNotes();
			rodap();
			return true;
		} else if (op.equals("teste")) {
			teste();
			rodap();
			return true;
		}

		dad = new Dados("EnvioRed");

		try {
			//conecta(0);
			DatabaseMetaData dmd = oConM.getMetaData();

			if (op.compareTo("imp")==0) {
				importa();

			} else if (op.compareTo("lveic")==0) {
				ped.on("<h3>Lista Veiculos</h3>");
				outRS(stmtM.executeQuery("SELECT TpEnvio,Destino,Endereco,count(Destino) "+
					"FROM EnvioRed "+
					"GROUP BY TpEnvio,Destino,Endereco"),"");

			} else if (op.compareTo("veic")==0) {
				ped.on("<h3>Veiculo: "+ped.getString("nveic")+"</h3>");
				outRS(stmtM.executeQuery("select Data,concat(Destino,Endereco) as Para,NroMat,Setor,TpEnvio "+
					"from EnvioRed where Destino like '%"+ped.getString("nveic")+"%' "+
					"or Endereco like '%"+ped.getString("nveic")+"%' "+
					"order by Data desc"),"");

			} else if (op.compareTo("tveic")==0) {
				ped.on("<h3>Veiculo: "+ped.getString("nveic")+"</h3>");
				outRS(stmtM.executeQuery("select concat(Destino,Endereco) as Para,count(NroMat) "+
					"from EnvioRed where Destino like '%"+ped.getString("nveic")+"%' "+
					"or Endereco like '%"+ped.getString("nveic")+"%' "+
					"group by Para"),"");

			} else if (op.compareTo("dia")==0) {
				ped.on("<h3>Data: "+ped.getString("Data")+"</h3>");
				outRS(stmtM.executeQuery("select NroMat,Setor,Inicio,Fim,nMsg,nSegs/nMSG as 'Segs<br>por Msg' "+
					"from EnvioRedT where date_format(Inicio,'%Y/%m/%d %a')='"+ped.getString("Data")+
					"' order by Inicio desc"),"EnvioRed.class?op=lista&NroMat=");

			} else if (op.compareTo("")==0) {
				outRS(stmtM.executeQuery("select date_format(Inicio,'%Y/%m/%d %a') as Data,Setor,count(NroMat) as Matérias, "+
						//"concat(min(Inicio),'<br>',max(Fim)) as Data,"+
						"sum(nMSG) as nMsg,sum(nMSG)/count(NroMat) as 'Msg<br>por Mat',sum(nSegs) as nSegs,sum(nSegs)/sum(nMSG) as  'Segs<br>por Msg' "+
						"from EnvioRedT group by Data,Setor "+
						"order by Data desc, Setor"),"EnvioRed.class?op=dia&Data=");

			} else if (op.compareTo("dia1")==0) {
				outRS(stmtM.executeQuery("select NroMat, Setor, min(Data) as Inicio, max(Data) as Fim, "+
						"count(Data) as nMSG, ((TIME_TO_SEC(max(Data))-TIME_TO_SEC(min(Data)))/count(Data)) as vel "+
						"from EnvioRed group by NroMat,Setor order by Inicio desc "),"EnvioRed.class?op=lista&NroMat=");

			} else if (op.compareTo("lista")==0) {
				rs = stmtM.executeQuery("select Data, concat(Destino,'<br>', Endereco) as Destino,TpEnvio "+
						"from EnvioRed where NroMat="+ped.getString("NroMat")+" order by Data desc");
				//rs.next();
				ped.on("<h3>Matéria: "+ped.getString("NroMat")+"</h3>");
				//rs.beforeFirst();
				outRS(rs,"");

			} else if (op.compareTo("mes")==0) {
				outRS(stmtM.executeQuery("select left(Data,10) as Dia, Setor, min(Data) as Inicio, max(Data) as Fim, "+
						"count(Data) as nMSG, ((TIME_TO_SEC(max(Data))-TIME_TO_SEC(min(Data)))/count(Data)) as vel "+
						"from EnvioRed group by Dia,Setor order by Dia desc, Setor "),"EnvioRed.class?op=lista&mat=");

			} else {
				//outRS(dmd.getColumns("",db,tb,""),"");

			}

		} catch (SQLException se) {
			ped.on(""+se);
		}

		rodap();

		return true;

	}

	//***************************//
	private void importa() {
		String dt,x,tbMat=",";


		conecta(1);

		try {

		ped.on("<br>Bloq Arq!");
		ped.on("tmout="+stmtM.getQueryTimeout());
		stmtM.setQueryTimeout(1);
		ped.on("tmout="+stmtM.getQueryTimeout());
		stmtM.executeUpdate("lock tables EnvioRed WRITE, EnvioRedT WRITE");
		ped.on("<br>Bloq Arq OK!");
		//stmtM.executeUpdate("unlock tables");
		//ped.on("<br>desBloq Arq OK!");
		//if (true) return;

		//ult data

		rsM = stmtM.executeQuery("select max(Data) as DataMX from EnvioRed");
		dt = "1980-01-01 00:00:00";
		if (rsM.next()) dt = str.seNull(rsM.getString("DataMX"),dt);
		rsM.close();

		rs = stmt.executeQuery("select * from Exporta"); // where _1>\""+dt+"\"");
		ped.on("<br>NOTES OK!!"+dt);

		int ti = 0;
		ped.on("<pre>");
		while (rs.next()) {

			if (rs.getString(1).compareTo(dt)<=0) {
				break;
			}

			if (ti%50==0) ped.o("\r\n"+ti+" ");
			ped.o(".");
			ti++;

			//acum matérias
			String xx=cmp(6);
			if (xx==null) {
				xx="Fax Manual";
			}
			if (xx.length()>10) {
				xx=xx.substring(0,10);
			}
			x = "'"+cmp(4)+"-"+xx+"'";
			if (tbMat.indexOf(","+x+",")<0) {
				tbMat += x+",";
			}

			stmtM.executeUpdate("insert into EnvioRed (Data,Destino,Endereco,NroMat,Setor,TpEnvio) "+
				" values('"+cmp(1)+"','"+cmp(2)+"','"+
					cmp(3)+"',"+cmp(4,"0")+",'"+
					xx+"','"+cmp(7)+"')");
		}
		ped.on("</pre>Total imp="+ti);
		stmt.close();

		//del mat import
		if (ped.getString("ztot")!=null) {
			stmtM.executeUpdate("delete from EnvioRedT");
		}
		rs = stmtM.executeQuery("select count(Inicio) from EnvioRedT");
		rs.next();
		if (tbMat.length()<4 & rs.getInt(1)>0) {
			stmtM.executeUpdate("unlock tables");
			return;
		}

		if (rs.getInt(1)>0) {
			tbMat = tbMat.substring(1,tbMat.length()-1);
			stmtM.executeUpdate("delete from EnvioRedT "+
				"where concat(NroMat,'-',Setor) in ("+tbMat+")");

			//totais por matéria
			x = "select NroMat,Setor,min(Data) as Inicio,max(Data) as Fim,count(NroMat) as nMsg,"+
						" 0 as nSegs "+
						" from EnvioRed where concat(NroMat,'-',Setor) in ("+tbMat+") group by NroMat,Setor";
		} else {
			x = "select NroMat,Setor,min(Data) as Inicio,max(Data) as Fim,count(NroMat) as nMsg,"+
						"0 as nSegs "+
						//" TIME_TO_SEC(max(Data))-TIME_TO_SEC(min(Data)) as nSegs "+
						" from EnvioRed group by NroMat, Setor";
		}
		ped.on(x);
		rs = stmtM.executeQuery(x);
		ped.on("<br>Fim total sql!!");

		int na=0;
		while (rs.next()) {
			//stmtM.executeUpdate("delete from EnvioRedT where NroMat="+cmp(3));
			na++;
			stmtM.executeUpdate("insert into EnvioRedT (NroMat,Setor,Inicio,Fim,nMsg,nSegs) "+
				" values("+cmp(1)+",'"+cmp(2)+"','"+
					cmp(3)+"','"+cmp(4)+"',"+cmp(5)+","+
					cmp(6)+")");
		}
		ped.on("<br>Fim GRAVACAO total sql nt="+na);
		stmtM.executeUpdate("update EnvioRedT set "+
			"nSegs=TIME_TO_SEC(Fim)-TIME_TO_SEC(Inicio) "+
			"WHERE nSegs=0");

		stmtM.executeUpdate("unlock tables");

		} catch (SQLException se) {
			ped.on("<hr>TESTE:"+se+"<hr>");
			se.printStackTrace();
		}
		try {
			stmtM.executeUpdate("unlock tables");
		} catch (Exception e) {
		}

	}

	//***************************//
	private String cmp(int i) {
		return cmp(i,"");
	}

	//***************************//
	private String cmp(int i,String p) {
		try {
			return str.troca(str.seNull(rs.getString(i),p),"'","\\'");
		} catch (SQLException se) {
			ped.on("<hr>"+se+"<hr>");
			se.printStackTrace();
		}
		return p;
	}

	//*****************************************//
	private void outRS(ResultSet rss,String at) {
		int nc;

		try {

			ped.on("<table border=1><tr>");

			//cabecaolhos
			ResultSetMetaData rsmd = rss.getMetaData();
			nc = rsmd.getColumnCount();
			for (int i=1;i<=nc;i++) {
				ped.on("<th>"+rsmd.getColumnName(i));
			}

			int nl=0;
			while (rss.next() & nl<500) {
				nl++;
				ped.on("<tr>");
				int t=0;
				for (int i=1;i<=nc;i++) {
					if (at!="" & t==0 & rss.getString(i).compareTo("")!=0) {
						t = 1;
						ped.on("<td><a href='"+at+rss.getString(i)+"'>"+rss.getString(i)+"</a>");
					} else {
						ped.on("<td>"+rss.getString(i));
					}
				}
			}
			ped.on("</table>");
		} catch (SQLException se) {
			ped.on("erro: "+se);
		}
		ped.on("</table>");

	}

	//*****************************************//
	private boolean conecta(int i) {
		boolean r = false;

		try {
			r = false;

			if (i==1) {
				if (oCon != null) if (!oCon.isClosed()) r = true;
				if (!r) {
					ped.on("<br>Tentando conectar NOTES!!");
					Class.forName("sun.jdbc.odbc.JdbcOdbcDriver").newInstance();
					oCon = DriverManager.getConnection("jdbc:odbc:EnvioRed");
				}
				stmt = oCon.createStatement();

			} else {
				if (oConM != null) if (!oConM.isClosed()) r = true;
				if (!r) {
					Class.forName("org.gjt.mm.mysql.Driver").newInstance();
					oConM = DriverManager.getConnection("jdbc:mysql://172.27.79.7:4387/LogsWWW?user=root&password=404064");
					//oConM = DriverManager.getConnection("jdbc:mysql://172.27.79.7:4387/LogsWWW?user=root&password=");
				}
				stmtM = oConM.createStatement();
			}

			r = true;

		} catch (IllegalAccessException iae) {
			ped.on("ERRO con: "+iae);
		} catch (InstantiationException ie) {
			ped.on("ERRO con: "+ie);
		} catch (ClassNotFoundException cnfe) {
			ped.on("ERRO con: "+cnfe);
		} catch (SQLException se) {
			ped.on("ERRO con: "+se);
		}

		return r;
	}

	//***************************//
	public void menu()  {
		ped.on("<a href=EnvioRed.class?op=emailNotes>emailNotes</a>");
		ped.on("<a href=EnvioRed.class?op=teste>teste</a>");
		ped.on("<table border=1 bgcolor=yellow width=100% >");
		ped.on("<tr>");
		//ped.on("<td HEIGHT=15 align=center><a href=EnvioRed.class?op=dia><b>Dia</a>");
		//ped.on("<td align=center><a href=EnvioRed.class?op=dia1><b>Dia1</a>");
		ped.on("<td align=center><a href=EnvioRed.class><b>Inicio</a>");
		//ped.on("<td align=center><a href=EnvioRed.class?op=tveic><b>Tot Veic</a>");
		ped.on("<td align=center><a href=EnvioRed.class?op=imp><b>Importa NOTES</a>");
		ped.on("<td align=center><a href=//piratini/scripts/fax/default.idc?><b>Servidores Fax</a>");
		ped.on("<td><form action=EnvioRed.class>Veículo:<input name=nveic value='"+ped.getString("nveic","")+"'>");
		ped.on("<select name=op><option>veic<option>tveic</select>");
		ped.on("<input type=submit value=pesquisa>");
		ped.on("<a href=EnvioRed.class?op=lveic>Lista</a>");
		ped.on("</form>");
		//ped.on("<td align=center><a href=EnvioRed.class?op=mes><b>Mes</a>");
		ped.on("</table>");
	}

	//***************************//
	public void emailNotes() {
		String tx = ped.getString("tx");
		if (tx==null) {
			ped.on("<form action=EnvioRed.class method=post>");
			ped.on("<input type=hidden name=op value=emailNotes>");
			ped.on("<textarea name=tx rows=5 cols=70></textarea>");
			ped.on("<input type=submit value=testar>");
			ped.on("</form>");
			return;
		}

		//String s,v[] = str.palavraA(str.troca(tx,",","\n"),"\n");
		String a,e,s,v[],v1[];
		if (tx.indexOf(",")==-1) {
			tx = str.troca(tx,"\r","");
			v = str.palavraA(tx,"\n");
		} else {
			v = str.palavraA(tx,",");
		}
		int t;
		for (int i=0;i<v.length;i++) {
			//v[i] = str.trimm(v[i]);
			s = str.trimm(v[i]);
			e = "";
			ped.on("<br>"+i+" - "+s);
			if (s.indexOf(".@")>-1 | s.indexOf("@.")>-1 | s.indexOf("..")>-1) {
				e += " .@ ou @. ";
			}
			a = s.substring(0,1);
			if (".@-_".indexOf(a)>-1) {
				e += " INI ";
			}
			t = s.length();
			a = s.substring(t-1,t);
			if (".@-_".indexOf(a)>-1) {
				e += " FIM ";
			}

			v1 = str.palavraA(s,"@");
			if (v1.length!=2) {
				e += " nro@ ";
			}


			for (int i1=0;i1<s.length();i1++) {
				char c = s.charAt(i1);
				if (c>='a' && c<='z') {
				} else if (c>='0' && c<='9') {
				} else if (c>='A' && c<='Z') {
				} else if ("._-@".indexOf(c)>-1) {
				} else {
					e += "#"+c+i1;
					//Character.getNumericValue(c);
				}
			}
			if (e!="") {
				ped.on("<font color=red>"+e+"</font>");
			} else {
				ped.on("<font color=green>OK!</font>");
			}
		}


	}

	//***************************//
	public void teste() {
		ped.on("TESTE1");
		DB = new cliOdbc(ped);
		if (!DB.conecta("172.27.79.6","EnvioRed")) {
			ped.on("<br>Erro: "+DB.sErro);
			return;
		}
		ped.on("<br>Conectou: "+DB.sErro);
		//if (true) return;
		if (!DB.executeQuery("select * from Exporta")) {
			ped.on("<br>Executou: "+DB.sErro);
			return;
		}
		int i=0;
		ped.on("<table border=1>");
		DB.cabRS(ped);
		while (DB.next() && i<20) {
			ped.on("<tr><td>"+(i++)+
				"<td>"+DB.getString(1)+
				"<td>"+DB.getString(2)+
				"<td>"+DB.getString(3)+
				"<td>"+DB.getString(4)+
				"<td>"+DB.getString(5)
				);
		}
		ped.on("</table>");
	}


}
