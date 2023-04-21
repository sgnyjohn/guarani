package bd;

/**
	* Interface para acesso a base de Dados
	*
	* @author signey
	* @version jul/2001
	* cliodbc em jun/2002

*/

/* PROBLEMAS

	nome table upper case?

*/
 

import java.util.*;
import java.sql.*;

import br.org.guarani.servidor.*;
import br.org.guarani.util.*;

//**********************************************
//**********************************************
public class Dados {
	static Hashtable oCfg;

	//con
	protected Object[] oC;
	public String[] sCon;	

	//bloqueio de base ou tudo
	private static boolean roTudo = false;
	private boolean ro = false;
	
	//aponta para conex ativa
	protected cliOdbc DB=null;
	protected boolean odbc=false;

	public String ch,nomeG,nomeInterno,Base;
	protected Connection con;
	longo uTest;
 
	public Pedido ped;
	public boolean erro = false;
	public boolean logErro = true;
	public String sErro = null;
	private int type = -1; //0=default mysql, 1=sqlite
	//**************************************
	// log
	public static void log(String s) {
		logs.grava("jdbc",s);
	}
	//**************************************
	// 0 - mysql 1 - sqlite
	public String textSql(String v) {
		return "'"+textEscape(v)+"'";
	}
	public String textEscape(String v) {
		if (v==null) return v;
		if (type()==1) {
			if (v.indexOf("'")!=-1) {
				v = str.troca(v,"'","''");
			}
		} else {
			//add contrabarra antes dos caracteres listados.
			v = strBuf.addContra(v,"\\'\n\r");
		}
		return v;
	}
	//**************************************
	// 0 - mysql 1 - sqlite
	public int type() {
		if (type==-1) type = str.equals(sCon[1],"jdbc:sqlite:")
			?1:
			(str.equals(sCon[1],"jdbc:hsqldb:")?2:0)
		;
		return type;
	}
	//**************************************
	//retorna expressao do bd - fazer outras q não mysql
	public String concatSql(String campos[]) {
		if (campos.length==1) {
			return campos[0];
		}
		String r = "";
		for (short i=0;i<campos.length;i++) {
			//String r1 = sql(campos[i]);
			r += ","+campos[i];
		}
		//mysql...
		r = "CAST(concat("+r.substring(1)+") as CHAR)";
		//logs.grava(r);
		return r;
	}	
	//*****************************************
	public String classe() {
		return sCon[0];
	}
	//*****************************************
	public int execUpdateSql(String sql) {
		return executeUpdate(sql);
	}
	//*****************************************
	public DadosSet execSql(String sql) {
		return executeQuery(sql);
	}
	//****************************************
	public boolean erro(Throwable e,String sql,int nv) {
		String er = "ERRO: "+this+" erro="+e+" nv="+nv+"\n\nsql="+sql+"\n\ntrace="+str.erro(e)+"";
		if (nv<1 && ((""+e).indexOf("CommunicationsException")!=-1 || (""+e).indexOf("ConnectionException")!=-1)) {
			//manda TENTAR NOVAMENTE
			log("try RE-connection "+e+"\n"+this+"\n"+sql);
			return false;
		}
		if (logErro) log(er);
		/*
		 * com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException: No operations allowed after connection closed.
		 * 
		 * com.mysql.jdbc.exceptions.jdbc4.CommunicationsException: The last packet 
					successfully received from the server was 62.425.959 milliseconds ago.  
			 The last packet sent successfully to the server was 62.425.960 milliseconds
					ago. is longer than the server configured value of 'wait_timeout'.
			 You should consider either expiring and/or testing connection validity before
					use in your application, increasing the server configured values
					for client timeouts, or using the Connector/J connection property 
					'autoReconnect=true' to avoid this problem.
		 * if (nv==1) {
			if (ped!=null) {
				ped.on(er);
			}
			logs.grava("erro",er);
			erro = true;
			sErro = er;
			return true;
		}
		*/
		erro = true;
		sErro = er;
		return true;
	}
	//****************************************
	public void erro(String s, Exception e) {
		erro = true;
		sErro = this+" "+s+"\r\n"+e;
		log(s+"<hr>"+this+"<hr>"+str.erro(e));
		if (ped!=null) {
			ped.erro(s,e);
		}
	}
	//*****************************************
	public DadosSet executeQuery(String sql) {
		if (type()==1 && sql.indexOf("concat(")!=-1) {
			logs.grava("concat","sql="+sql);
		}
				
		sErro = null;
		erro = false;
		if (odbc) {
			if (!DB.executeQuery(sql)) {
				sErro = DB.sErro;
				erro = true;
				return null;
			} else {
				return DB;
			}
		}
 
		if (!conecta()) {
			return null;
		}
		ResultSet rs;
		Statement stmt=null;

		int nv = 0;
		while (true) {
			try {
				stmt = con.createStatement(
					ResultSet.TYPE_FORWARD_ONLY //funciona no mysql.???
					//ResultSet.TYPE_SCROLL_INSENSITIVE  //ignora alterações de outros enquanto avança
					,ResultSet.CONCUR_READ_ONLY //não aceita alteração
				);
				rs = stmt.executeQuery(sql);
				//log("execSql-fim="+rs);
				//uTest.val = data.ms();
				if (rs!=null) {
					break;
				}
			} catch (java.lang.NoClassDefFoundError e) {
				if (erro(e,sql,nv)) {
					return null;
				}
			} catch (Throwable e) {
				if (erro(e,sql,nv)) {
					return null;
				}
			}
			//log("jdbc ERRO mas vai tentar novamente...");
			close();
			if (!conecta()) {
				//log("jdbc Não reconectou na 2 tentativa...");
				erro("ERRO na RE-conexção com a BASE 2a tentativa",new Exception("erro conectando na 2 tentativa"));
				return null;
			}
			nv++;
		
		} 
		//set tipo
		DadosSet re = type()==1
				?new DadosSetSqlite(ped,rs)
				:(type()==2?new DadosSetHsql(ped,rs):new DadosSetJdbc(ped,rs))
		;
		re.parent = this;
		re.sql = sql;
		return re;
	}
	//***********************************
	public String bloqueada() {
		return (String)oC[6];
	}
	//***********************************
	// bloqueia base para carga total....
	public static synchronized String block(String ch,String motivo) {
		//ogs("block: ch="+ch+":"+motivo+" e="+str.erro(new Exception()));
		
		Object o[] = (Object[])oCfg.get(ch);
		String r = (String)o[6];
		//quer bloquear?
		if (motivo!=null) {
			if (motivo.equals("")) {
				//so quer o status
				return r;
			} else if (str.vazio(r)) {
				log("jdbc bloqueando "+ch+" motivo: "+motivo);
				//hBlock.put(ch,motivo);
				o[6] = motivo;
				return null;
			} else {
				return r;
			}
		} else {
			log("jdbc liberando "+ch+" motivo: "+motivo);
			//hBlock.remove(ch);
			o[6] = "";
		}
		return r;
	}
	//*****************************************
	public void ro(PagV pg,boolean tudo,boolean val) {
		if (!pg.doGrupo("dev")) {
			return;
		}
		if (tudo) {
			roTudo = val;
		} else {
			ro = val;
		}
	}
	//*****************************************
	public boolean ro() {
		return roTudo || ro;
	}
	//*****************************************
	public DatabaseMetaData getMetaData() throws Exception {
		return con.getMetaData();
	}
	//*****************************************
	public Dados(Pedido pd,String BBase) {
		ped = pd;
		//logs.grava("b="+Base);
		init(BBase);
	}
	//*****************************************
	public Dados() {
		ped = null;
	}
	//*****************************************
	public Dados(String BBase) {
		ped = null;
		init(BBase);
	}
	//*****************************************
	public void setPed(Pedido pd) {
		ped = pd;
	}
	//*****************************************
	protected void init(String BBase) {
		// 2015/nov - add @ se não existe, assim não mexe nas
		// definicoes original do servidor 
		// a não ser que no servidor for especificado o @
		Base = (BBase.indexOf("@")==-1?BBase+"@":"")+BBase;
		if (oCfg==null) initCfg();

		ch = Base.toLowerCase();
		Object v[] = (Object[])oCfg.get(ch);
		//se não encontrado procura no padrão ou @indicado
		if (v==null) {
			//procura por conex nome depois do @
			nomeG = str.substrAt(Base,"@");
			String chD = nomeG.toLowerCase();
			v = (Object[])oCfg.get(chD);
			//procura na conex padrao *
			if (v==null) {
				chD = "*";
				v = (Object[])oCfg.get(chD);
			}
			//achou?
			if (v!=null) {
				//assume virtual e clona
				String s1[] = (String[])v[0];
				String s[] = new String[s1.length];
				for (int i=0;i<s1.length;i++) {
					s[i] = str.trimm(s1[i]);
				}
				s[1] = str.troca(s[1],"*",str.leftAt(Base,"@"));
				log("ch="+ch+" Base="+Base+" chD="+chD+" assume virtual e clona!");
				//0="+s[0]+" 1="+s[1]);
				//new Object[]{s,(Connection)null,nomeG,null,new longo(0),""};
				v = novaCon(nomeG,s);
				oCfg.put(ch,v);
			} else {
				log("Não encontrou a conexão padrão: "
					+chD+" para base "+Base);
			}
		}
		
		if (v==null) {
			erro(Base+", Base não definida!!",new Exception());
			return;
		}
		
		//ok 
		oC = v;
		nomeG = (String)oC[2];
		sCon = (String[])v[0];
		con = (Connection)v[1];
		uTest = (longo)v[4];
		ro = !str.vazio(""+v[5]);
		if (sCon[0].equals("bd.cliOdbc")) {
			odbc = true;
			DB = new cliOdbc(ped);
			if (!DB.conecta(sCon[1],sCon[2])) {
				log("cliOdbc:"+sCon[1]+" "+sCon[2]+" "+DB.sErro);
				sErro = DB.sErro;
				erro = true;
			}
		} else {
			conecta();
			nomeInterno = (String)oC[3];
		}
	}
	//*****************************************
	public DadosSet executeQuery(String sql,String v[]) {
		return executeQuery(subst(sql,v));
	}
	//*****************************************
	public static String subst(String sql,String v[]) {
		for (int i=0;i<v.length;i++) {
			if (v[i]==null) {
				sql = str.troca(sql,"'{"+i+"}'","null");
			} else {
				if (v[i].indexOf("'")!=-1) {
					v[i] = str.troca(v[i],"'","\\'");
				}
				sql = str.troca(sql,"{"+i+"}",v[i]);
			}
		}
		return sql;
	}
	//*****************************************
	public int executeUpdate(String sql,String v[]) {
		return executeUpdate(subst(sql,v));
	}
	//*****************************************
	public int executeUpdate(String sql[]) {
		int r = 0;
		for (int i=0;i<sql.length;i++) {
			r += executeUpdate(sql[i]);
		}
		return r;
	}
	//*****************************************
	public int executeUpdate(String sql) {
		erro = false;
		sErro = null;
		if (ro()) {
			erro = true;
			sErro = "somente para leitura, bloqueado pelo adm tudo?="+roTudo;
			return -1;
		}
		if (!conecta()) return -1;
		if (odbc) {
			return DB.executeUpdate(sql);
		}

		Statement stmt;
		int r = -1;
		int nv = 0;
		while (true) {
			try {
				stmt = con.createStatement();
				r = stmt.executeUpdate(sql);
				break;
			} catch (java.lang.NoClassDefFoundError e) {
				if (erro(e,sql,nv)) {
					return -1;
				}
			} catch (Exception e) {
				if (erro(e,sql,nv)) {
					return -1;
				}
			} catch (Throwable e) {
				if (erro(e,sql,nv)) {
					return -1;
				}
			}
			//log("jdbc ERRO mas vai tentar novamente...");
			close();
			if (!conecta()) {
				erro("ERRO na conexção com a BASE 2a tentativa",new Exception("erro conectando na 2 tentativa"));
				return r;
			}
			nv++;
		}
		return r;
	}
	//*****************************************
	static private synchronized boolean conecta1(Dados dl) {
		boolean r = dl.con!=null;
		if (!r) {
			try {
				log("Vai proc classe="+dl.sCon[0]);
				Class c = Guarani.findClass(dl.sCon[0]); //Class.forName(dl.sCon[0]);
				if (c==null) {
					log("não achou driver jdbc: "+dl.sCon[0]);
					return false;
				}
				log("Vai instanc classe="+dl.sCon[0]);
				Object o = c.newInstance();
				if (c==null) {
					log("não conseguiu instanciar driver jdbc: "+dl.sCon[0]);
					return false;
				}
			//} catch (Throwable e) {
			} catch (Exception e) {
				dl.erro("ERRO, problema no driver jdbc: "+dl.sCon[0],e);
				return false;
			}
			
			try {
				log("Vai "+((dl.con==null)?"":"RE")+
					"Conectar "+dl.sCon[0]+" ~ "+str.leftAt(dl.sCon[1],"?")+
					" "+dl.con
					+" Base="+dl.Base+" nomeG="+dl.nomeG+" ch="+dl.ch+" nomeInterno="+dl.nomeInterno //ch,nomeG,nomeInterno,Base;
					//+" sCon[]="+str.palavraA(dl.sCon," ~ ")
				);
				if (dl.sCon[1].indexOf("*")!=-1) {
					logs.grava("Trocando em "+dl.sCon[1]+" * -> "+dl.nomeG);
					dl.sCon[1] = str.troca(dl.sCon[1],"*",dl.nomeG);
				}
				Properties props = new Properties();
				//dl.type = str.equals(dl.sCon[1],"jdbc:sqlite:")?1:0;
				if (dl.type()==0) {
					props.put("useInformationSchema", "true");				
				}
				dl.con = DriverManager.getConnection(dl.sCon[1],props);
				dl.oC[3] = str.substrRatAt(dl.sCon[1],"/","?");
				r = true;
				log("conectou type="+dl.type+" str=("+dl.sCon[1]+")");
				//log("Cat="+vCon[nCon].getCatalog());
			} catch (Exception e) {
				dl.erro("conecta=",e);
			}
		}
  
		return r;
	}
	//*****************************************
	private boolean conecta0() {
		if (true) {
			return con!=null;
		}
		return true;
	}
	//*****************************************
	private boolean conecta() {
		boolean r = conecta0();
		if (!r) {
			r = conecta1(this);
			if (r) {
				//guarda conexção
				oC[1] = con;
				if (classe().indexOf("mysql")!=-1) {
					executeQuery("SET NAMES 'utf8mb4'"); 
				}
			}
		}
		erro = !r;
		return r;
	}
	//*****************************************
	public boolean fecha() {
		return close();
	}
	//*****************************************
	public boolean close() {
		boolean r = false;
		if (odbc && DB!=null) {
			DB.close();
			r = true;
		} else if (con!=null) {
			try {
				con.close();
				r = true;
			} catch (Exception e) {
			}
			con = null;
			oC[1] = null;
		}
		return r;
	}
	//****************************************
	public String toString() {
		return "Dados={ch="+ch+"/"+nomeInterno+",classe="+sCon[1]+"}";
	}
	//*****************************************
	public static synchronized void initCfg() {
		// oCfg é hashtable key lower case e value string[class driver,string con]
		if (oCfg!=null) return;
		if (Guarani.get("dados.oCfg")!=null) {
			try {
				oCfg = (Hashtable)Guarani.get("dados.oCfg");
				log("jdbc->Recuperada conexcoes...");
				return;
			} catch (Exception e) {
				log("jdbc->Erro recuperando de guarani="
					+str.erro(e)
				);
			}
		}
		
		//carrega configs do Guarani
		log("init cfg!!");
		Hashtable cg = new Hashtable();
		Hashtable c = (Hashtable)Guarani.cnf_jdbc;
		int i = c.size();
		String k;
		int pos=0;
		oCfg = new Hashtable();
		for (Enumeration e = c.keys() ; e.hasMoreElements() ;) {
			k = (String)e.nextElement();
			log("jdbc==>"+k); //+"="+c.get(k));
			String v[] = str.palavraA((String)c.get(k),"~");
			oCfg.put(k.toLowerCase()
				//,new Object[]{v,(Connection)null,k,null,new longo(0),""}
				,novaCon(k,v)
			);
			try {
				Class.forName(v[0]).newInstance();
			} catch (Exception ee) {
				log("JDBC Classe não existe: "+v[0]);
			}
			pos++;
		}
		Guarani.put("dados.oCfg",oCfg);
	}
	private static Object[] novaCon(String k,String v[]) {
		/* 
		0 = string[] vetor da conexão 0=classe do driver 1=url
		1 =  connection
		2 = a chave
		3 = nomeInterno
		4 = ultimo teste
		5 = read only
		*/
		return new Object[]{v,(Connection)null,k,null,new longo(0),"",""};
	}
}
