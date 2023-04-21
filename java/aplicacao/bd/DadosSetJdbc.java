package bd;

import java.sql.*;
import java.util.*;

import br.org.guarani.servidor.*;
import br.org.guarani.util.*;

//import bd.tabela.*;


/**
	* Write a description of class DadosSet here.
	*
	* @author signey
	* @version jul/2001
	*/
 
//****************************
//****************************
public class DadosSetJdbc extends DadosSet {
	Pedido ped;
	public ResultSetMetaData oRsMd=null;
	boolean fim = false;
	int nRegs=-1;
	Hashtable cE;//campo existe
	//****************************************
	public GregorianCalendar getCalendar(String nome) {
		String s = getString(nome);
		if (s==null || str.equals(s,"0000-00-00 00:00:00")) {
			return null;
		}
		int v[] = str.palavraAInt(str.troca(str.troca(s," ","-"),":","-"),"-");
		return new GregorianCalendar(v[0],v[1],v[2],v[3],v[4],v[5]);
	}
	//****************************************
	public String getStringB(String c) {
		if (erro) return null;
		try {
			byte b[] = rs.getBytes(c);
			return b==null?null:new String(b);
		} catch (Exception e) {
			//logs.grava("erro pegando valor de "+s+" "+str.erro(e)+" "+sql());
			return "?ds: campo não existe: "+c+"?";
			//erro("getString("+s+")",e);
		}		
		
	}
	//****************************************
	public byte[] getBytes(String c) {
		if (erro) return null;
		try {
			return rs.getBytes(c);
		} catch (Exception e) {
			//logs.grava("erro pegando valor de "+s+" "+str.erro(e)+" "+sql());
			return (("?ds: campo não existe: "+c+"?").getBytes());
			//erro("getString("+s+")",e);
		}		
		
	}
	//****************************************
	public String export(String tab) {
		if (cE==null) {
			campoExiste("");
		}
		String r = "INSERT INTO "+tab+" VALUES (";
		return r;		
	}
	//****************************************
	public boolean campoExiste(String nome) {
		if (cE==null) {
			RsMd();
			cE = new Hashtable();
			try {
				for (int i=0;i<oRsMd.getColumnCount();i++) {
					cE.put(oRsMd.getColumnName(i+1),""+i);
				}
			} catch (Exception e) {
				logs.grava("erro",fechado+" vendo estrutura para campoExiste: "+e);
			}
		}
		return cE.get(nome)!=null;
	}
	//****************************************
	public boolean close() {
		try {
			if (cE!=null) {
				cE.clear();
				cE = null;
			}
			fechado = true;
			rs.close();
		} catch (Exception e) {
			erro("close",e);
			return false;
		}
		return true;
	}
	/****************************************
	public boolean campoExiste(String nome) {
		if (erro || fechado) return false;
		if (cE==null) {
			cE = new Hashtable();
		} else {
			String t = (String)cE.get(nome);
			if (t!=null) {
				return !str.vazio(t);
			}
		}
		try {
			rs.getString(nome);
			cE.put(nome,"1");
		} catch (Exception e) {
			logs.grava("e="+e.getClass().getName());
			cE.put(nome,"");
			return false;
		}
		return true;
	}
	*/
	//****************************************
	public boolean fim() {
		try {
			return rs.isAfterLast();
		} catch (Exception e) {
		}
		return fim;
	}
	//****************************************
	public String sql() {
		return ""+sql;
	}
	//****************************************
	public java.sql.Timestamp getDateTime(String s) {
		if (erro) return null;
		try {
			return rs.getTimestamp(s);
		} catch (Exception e) {
			//return "";
			erro("getDateTime("+s+")",e);
		}
		return null;
	}
	//****************************************
	public java.sql.Date getDate(String s) {
		if (erro) return null;
		try {
			return rs.getDate(s);
		} catch (Exception e) {
			//return "";
			erro("getDate("+s+")",e);
		}
		return null;
	}
	//****************************************
	public boolean absolute(int reg) {
		try {
			return rs.absolute(reg);
		} catch (Exception e) {
			logs.grava("jdbc","ERRO DadosSetJdbc.absolute(int reg): "+e);
		}
		return false;
	}
	//****************************
	public int nRegs() {
		if (nRegs!=-1) {
			return nRegs;
		}
		int nr=-1;
		try {
			if (false) {
				//nRegs = rs.count();
			} else {
				int ra = rs.getRow();
				rs.last();
				nr = rs.getRow();
				//volta ao reg original...
				if (ra==0) {
					rs.beforeFirst();
				} else {
					rs.absolute(ra);
				}
			}
		} catch (Exception e) {
			logs.grava("jdbc","ERRO DadosSetJdbc.nRegs(): "+e);
			nr = -1;
		}
		nRegs = nr;
		return nr;
	}
	//****************************
	public boolean mostra(Pedido ped) {
		ResultSetMetaData rmd; 
		try {
			rmd = rs.getMetaData();
		} catch (Exception e) {
			//sErro = "Erro medatada="+e;
			return false;
		}
  
		try {
			int nr=0,nc = rmd.getColumnCount();
			ped.on("<table border=1>");
			//cabec
			ped.on("<tr>");
			for (int i=0;i<nc;i++) {
				ped.on("<th>"+rmd.getColumnName(i+1));
			}
			//dados
			while (rs.next() && !ped.erro) {
				nr++;
				ped.on("<tr>");
				for (int i=0;i<nc;i++) {
					ped.on("<td>"+rs.getString(i+1));
				}
			}
   
		} catch (Exception e)  {
			ped.on("Erro rs="+e);
			return false;
		}
		ped.on("</table>");
		return true;
	}

	//****************************
	public boolean erro() {
		return erro;
	}
	//****************************
	public boolean mostra(String atalho,String op) {
		String ign=str.opcao(op,"/ign","");
		int lim = str.opcaoInt(op,"/lim",300),nr=0;
		boolean mlinha = str.opcao(op,"/mlin")!=null;
		ResultSetMetaData rmd; 
		try {
			rmd = rs.getMetaData();
		} catch (Exception e) {
			//sErro = "Erro medatada="+e;
			return false;
		}
  
		try {
			int nc = rmd.getColumnCount();
			ped.on("<table border=1>");
			//cabec
			ped.on("<tr>");
			if (mlinha) {
				ped.on("<th align=right>#");
			}
			for (int i=0;i<nc;i++) {
				if (ign.indexOf("-"+rmd.getColumnName(i+1)+"-")==-1) {
					ped.on("<th>"+rmd.getColumnName(i+1));
				}
			}
			//dados
			while (rs.next() && nr<lim && !ped.erro) {
				nr++;
				ped.on("<tr>");
				if (mlinha) {
					ped.on("<td align=right>"+nr);
				}
				int p=0;
				for (int i=0;i<nc;i++) {
					if (ign.indexOf("-"+rmd.getColumnName(i+1)+"-")==-1) {
						p++;
						if (p==1 && atalho!="") {
							ped.on("<td>"+str.troca(atalho,"@@",rs.getString(i+1)));
						} else {
							ped.on("<td>"+rs.getString(i+1));
						}
					}
				}
			}
   
		} catch (Exception e)  {
			ped.on("Erro rs="+e);
			return false;
		}
		ped.on("</table>");
		return true;
	}


	//*****************************************
	public DadosSetJdbc(Pedido p, ResultSet r) {
		rs = r;
		ped = p;
	}

	//****************************************
	public DadosSetJdbc(ResultSet r) {
		rs = r;
		ped = null;
	}

	//****************************************
	public boolean next() {
		if (erro) return false;
		try {
			fim = !rs.next();
			return !fim;
		} catch (Exception e) {
			erro("next()",e);
		}
		return false;
	}

	//****************************************
	public int getInteiro(String s) {
		if (erro) return -1;
		try {
			return rs.getInt(s);
		} catch (Exception e) {
			//return "";
			erro("getInt("+s+")",e);
		}
		return -1;
	}
	//****************************************
	public int getInteiro(int s) {
		if (erro) return -1;
		try {
			return rs.getInt(s);
		} catch (Exception e) {
			//return "";
			erro("getInt("+s+")",e);
		}
		return -1;
	}
	//****************************************
	public double getDuplo(String s) {
		if (erro) return -1;
		try {
			return rs.getDouble(s);
		} catch (Exception e) {
			//return "";
			erro("getInt("+s+")",e);
		}
		return -1;
	}
	//****************************************
	public double getDuplo(int s) {
		if (erro) return -1;
		try {
			return rs.getDouble(s);
		} catch (Exception e) {
			//return "";
			erro("getInt("+s+")",e);
		}
		return -1;
	}
	//****************************************
	public long getLongo(String s) {
		if (erro) return -1;
		try {
			return rs.getLong(s);
		} catch (Exception e) {
			//return "";
			erro("getLong("+s+")",e);
		}
		return -1;
	}
	//****************************************
	public long getLongo(int s) {
		if (erro) return -1;
		try {
			return rs.getLong(s);
		} catch (Exception e) {
			//return "";
			erro("getLong("+s+")",e);
		}
		return -1;
	}

	//****************************************
	public String getString(String s,String s1) {
		String s2=getString(s);
		if (s2==null) {
			return s1;
		}
		return s2;
	}
	//****************************************
	public String getString(String s) {
		if (erro) return null;
		try {
			return rs.getString(s);//new String(rs.getBytes(s));
		} catch (Exception e) {
			String se = ""+e;
			if (se.indexOf("not be represented")!=-1) {
				return "";
			}
			//logs.grava("erro pegando valor de "+s+" "+str.erro(e)+" "+sql());
			return "?nExist."+s;
			//erro("getString("+s+")",e);
		}
	}
	//****************************************
	public String getString(int pos) {
		if (erro) return null;
		try {
			return rs.getString(pos);
		} catch (Exception e) {
			String se = ""+e;
			if (se.indexOf("not be represented")!=-1) {
				return "";
			}
			//logs.grava("erro pegando valor de "+s+" "+str.erro(e)+" "+sql());
			return "?nExist"+pos;
			//erro("getString("+s+")",e);
		}
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

	//****************************************
	public void RsMd() {
		if (oRsMd!=null) return;
		try {
			oRsMd = rs.getMetaData();
		} catch (Exception e) {
			erro("RsMd",e);
			logs.grava("erro","rs.getMetaData(): "+e);
		}
	}

	//****************************************
	public int contaCampos() {
		RsMd();
		try {
			return oRsMd.getColumnCount();
		} catch (Exception e) {
			log("contaCampos "+str.erro(e));
		}
		return 0;
	}

	//****************************************
	public Hashtable getHashtable() {
		int i=0;
		Hashtable r = new Hashtable();
		try {
			for (i=1;i<=contaCampos();i++) {
				r.put(getNomeCampo(i),str.seNull(getString(i),""));
			}
		} catch (Exception e) {
			logs.grava(i+" - erro hashtable!!");
		}
		return r;
	}

	//****************************************
	public String[] getVetor() {
		int i=0;
		String r[] = new String[contaCampos()];
		try {
			for (i=0;i<contaCampos();i++) {
				r[i] = str.seNull(getString(i+1),"");
			}
		} catch (Exception e) {
			logs.grava(i+" - erro Vetor!!");
		}
		return r;
	}

	//****************************************
	public String getNomeCampo(int i) {
		RsMd();
		try {
			return oRsMd.getColumnName(i);
		} catch (Exception e) {
			log("?getNomeCampo:"+i+" "+e);
		}
		return null;
	}

	//****************************************
	public void estru() {
		RsMd();
		try {
			for (int i=0;i<oRsMd.getColumnCount();i++) {
				if (ped==null) {
					logs.grava("CMP="+oRsMd.getColumnName(i+1));
				} else {
					ped.on("<br>"+oRsMd.getColumnName(i+1));
				}
			}
		} catch (Exception e) {
			erro("estru",e);
		}
	}

}
