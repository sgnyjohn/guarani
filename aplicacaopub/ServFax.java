/*
	*/

import java.util.*;

import br.org.guarani.servidor.*;
import br.org.guarani.util.*;
import bd.*;

//***************************//
//***************************//
public class ServFax extends Pag {
	protected Dados dad,dadn;
	protected DadosSet rs,DB;

	protected static int bloqueio=0;


	//***************************//
	public boolean run(Pedido pd) {
		super.run(pd);
		cab("Servidores Fax");
		menu();

		dad = new Dados(ped,"Logs");

		try {

			if (op.compareTo("imp")==0) {
				importa();


			} else if (op.compareTo("fone")==0) {
				ped.on("<h3>Fone: "+ped.getString("nveic")+" </h3>");
				outRS(dad.executeQuery("SELECT date_format(IS_DtProcessed,'%d/%m/%Y %H:%m:%s')"+
						"as Data, Subject as Setor, IS_RequestStatus as Situação,"+
						"SendTo as Destino,IS_FaxPhone as Fax, IS_RequestOriginator as Operador "+
						"FROM Fax where IS_FaxPhone like '%"+ped.getString("nveic")+"%' "+
						"order by IS_DtProcessed desc"),"");
      
			} else if (op.compareTo("destino")==0) {
				ped.on("<h3>Destino: "+ped.getString("nveic")+" </h3>");
				outRS(dad.executeQuery("SELECT date_format(IS_DtProcessed,'%d/%m/%Y %H:%m:%s')"+
						"as Data, Subject as Setor, IS_RequestStatus as Situação,"+
						"SendTo as Destino,IS_FaxPhone as Fax, IS_RequestOriginator as Operador "+
						"FROM Fax where SendTo like '%"+ped.getString("nveic")+"%' "+
						"order by IS_DtProcessed desc"),"");
 
			} else if (op.compareTo("situ")==0) {
				ped.on("<h3>Situação: "+ped.getString("nveic")+" </h3>");
				outRS(dad.executeQuery("SELECT date_format(IS_DtProcessed,'%d/%m/%Y %H:%m:%s')"+
						"as Data, Subject as Setor, IS_RequestStatus as Situação,"+
						"SendTo as Destino,IS_FaxPhone as Fax, IS_RequestOriginator as Operador "+
						"FROM Fax where IS_RequestStatus like '%"+ped.getString("nveic")+"%' "+
						"order by IS_DtProcessed desc"),"");
 
			} else if (op.compareTo("setor")==0) {
				ped.on("<h3>Situação: "+ped.getString("nveic")+" </h3>");
				outRS(dad.executeQuery("SELECT date_format(IS_DtProcessed,'%d/%m/%Y %H:%m:%s')"+
						"as Data, Subject as Setor, IS_RequestStatus as Situação,"+
						"SendTo as Destino,IS_FaxPhone as Fax, IS_RequestOriginator as Operador "+
						"FROM Fax where Subject like '%"+ped.getString("nveic")+"%' "+
						"order by IS_DtProcessed desc"),"");
      
			} else if (op.compareTo("operador")==0) {
				ped.on("<h3>Situação: "+ped.getString("nveic")+" </h3>");
				outRS(dad.executeQuery("SELECT date_format(IS_DtProcessed,'%d/%m/%Y %H:%m:%s')"+
						"as Data, Subject as Setor, IS_RequestStatus as Situação,"+
						"SendTo as Destino,IS_FaxPhone as Fax, IS_RequestOriginator as Operador "+
						"FROM Fax where IS_RequestOriginator like '%"+ped.getString("nveic")+"%' "+
						"order by IS_DtProcessed desc"),"");
      
			} else if (op.compareTo("")==0) {
				outRS(dad.executeQuery("SELECT date_format(IS_DtProcessed,'%Y/%m')"+
						"as AM, Subject as Setor, "+
						"sum(if(IS_RequestStatus='Completed',1,0)) as Fax_ok, "+
						"sum(if(IS_RequestStatus<>'Completed',1,0)) as Total_Erros, "+
						"sum(if(IS_RequestStatus='Number dialed was busy',1,0)) as Ocupado, "+
						"sum(if(IS_RequestStatus='No dial tone.',1,0)) as Sem_Sinal, "+
						"sum(if(IS_RequestStatus<>'Number dialed was busy' and "+
										"IS_RequestStatus<>'No dial tone.' and "+
										"IS_RequestStatus<>'Completed',1,0)) as Out_Erros "+
						"FROM Fax where Subject = 'Redacao' OR Subject = 'Editoria do Interior' "+
						"group by AM, Setor "+
						"order by AM desc, Setor"),atalho("op=mes&AM=@@","@@"));
       
			} else if (op.compareTo("mes")==0) {
				outRS(dad.executeQuery("SELECT date_format(IS_DtProcessed,'%Y/%m/%d')"+
						"as Data, Subject as Setor, "+
						"sum(if(IS_RequestStatus='Completed',1,0)) as Fax_ok, "+
						"sum(if(IS_RequestStatus<>'Completed',1,0)) as Total_Erros, "+
						"sum(if(IS_RequestStatus='Number dialed was busy',1,0)) as Ocupado, "+
						"sum(if(IS_RequestStatus='No dial tone.',1,0)) as Sem_Sinal, "+
						"sum(if(IS_RequestStatus<>'Number dialed was busy' and "+
										"IS_RequestStatus<>'No dial tone.' and "+
										"IS_RequestStatus<>'Completed',1,0)) as Out_Erros "+
						"FROM Fax where (Subject = 'Redacao' OR Subject = 'Editoria do Interior') "+
										"and date_format(IS_DtProcessed,'%Y/%m')='"+ped.getString("AM")+
						"' group by Data, Setor "+
						"order by Data desc, Setor"),atalho("op=dia&Data=@@","@@"));
      
			} else if (op.compareTo("dia")==0) {
				outRS(dad.executeQuery("SELECT date_format(IS_DtProcessed,'%Y/%m/%d %hh:%mm:%ss')"+
						"as Data, Subject as Setor, IS_RequestStatus as Situção, "+
						"SendTo as Destino, IS_FaxPhone as Fax, IS_RequestOriginator as Operador "+
						"FROM Fax where (Subject = 'Redacao' OR Subject = 'Editoria do Interior') "+
										"and date_format(IS_DtProcessed,'%Y/%m/%d')='"+ped.getString("Data")+
						"' order by Data desc, Setor"),atalho("op=dia&Data=@@","@@"));

			} else {
				//outRS(dmd.getColumns("",db,tb,""),"");

			}

		} catch (Exception se) {
			ped.on(""+se);
		}

		rodap();

		return true;

	}

	//***************************//
	private void importa() {
		String dt,x,tbMat=",";
		if (bloqueio!=0) {
			ped.on("<h1>Bloqueado, já importando!!!</h1>");
			return;
		}
		bloqueio=1;

		ped.on("<hr>Servidor Fax 2<hr>");

  
		rs = dad.executeQuery("select max(IS_DtProcessed)"+
			" as DataMX from Fax");
		if (dad.erro) {
			ped.on("<br>ERRO max data: "+dad.sErro);
			bloqueio=0;
			return;
		}
		dt = "1980-01-01 00:00:00";
		if (rs.next()) dt = str.seNull(rs.getString("DataMX"),dt);
		ped.on("<hr>Ult Data Atualizada: "+dt+"<hr>");

		//conecta notes
		dadn = new Dados(ped,"Fax2");
		if (dadn.erro) {
			ped.on("<br>Erro: "+dadn.sErro);
			bloqueio=0;
			return;
		}
		DB = dadn.executeQuery("select * from Exporta"); // WHERE IS_DtProcessed>'"+dt+"'");
		if (dadn.erro) {
			ped.on("<br>ERRO: "+dadn.sErro);
			bloqueio=0;
			return;
		}
		//DB.mostra("","/lim=2000");
		//bloqueio = 0;
		if (dadn.erro) {
			ped.on("<br>ERRO: "+dadn.sErro);
			bloqueio=0;
			return;
		}
		//if (true) return;
  
		ped.on("<br>NOTES OK!!"+dt);

		int ti = 0;
		ped.on("<pre>");
		while (DB.next()) {

			if (DB.getString(1).compareTo(dt)<=0) {
				ped.on("<br>NOTES FIM!! data máxima: my="+dt+" notes="+DB.getString(1));
				break;
			}

			if (ti%50==0) ped.o("\r\n"+ti+" ");
			ped.o(".");
			ti++;

			String ss="2";

			//dad.executeUpdate("insert into Fax SELECT Exporta.*, '1' as Servidor, null as ch FROM Exporta WHERE IS_DtProcessed>'"+dt+"'";
			dad.executeUpdate("insert into Fax (IS_DtProcessed ,IS_DtReceived,"+
				"IS_RequestStatus,Subject,IS_FaxNumPages,IS_FaxNumRetries,"+
				"IS_RequestOriginator,SendTo,IS_FaxPhone,Servidor) "+
				" values('"+cmp(1)+"','"+cmp(2)+"','"+cmp(3)+"','"+cmp(4)+"','"+
					cmp(5)+"','"+cmp(6)+"','"+cmp(7)+"','"+cmp(8)+"','"+cmp(9)+"','"+ss+"')");

			if (dad.erro) {
				ped.on("<br>ERRO max data: "+dad.sErro);
				bloqueio=0;
				return;
			}
   
		}

		ped.on("</pre>Total imp="+ti);
		DB.close();

		bloqueio=0;
		return;
	}

	//***************************//
	private String cmp(int i) {
		return cmp(i,"");
	}

	//***************************//
	private String cmp1(int i) {
		return str.troca(str.seNull(rs.getString(i),""),"'","\\'");
	}

	//***************************//
	private String cmp(int i,String p) {
		return str.troca(str.seNull(DB.getString(i),p),"'","\\'");
	}

	//*****************************************//
	private void outRS(DadosSet rss,String at) {
		rss.mostra(at,"/lim=1500 /mlin=1");
	}

	//***************************//
	public void menu()  {
		ped.on("<table border=1 bgcolor=yellow width=100% >");
		ped.on("<tr>");
		ped.on("<td align=center><a href=ServFax.class><b>Inicio</a>");
		ped.on("<td align=center><a href=ServFax.class?op=imp><b>Atualiza Fax</a>");
		ped.on("<td align=center><a href=EnvioRed.class?><b>Envio Redação</a>");
		ped.on("<td><form action=ServFax.class>Veículo:<input name=nveic value='"+ped.getString("nveic","")+"'>");
		ped.on("<select name=op><option>fone<option>destino<option>operador<option>setor<option>situ</select>");
		ped.on("<input type=submit value=Consulta>");
		ped.on("<a href=ServFax.class?op=Estat>Lista</a>");
		ped.on("</form>");
		ped.on("</table>");
	}

}
