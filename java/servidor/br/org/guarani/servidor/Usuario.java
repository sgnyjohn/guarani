package br.org.guarani.servidor;

import java.util.*;

import br.org.guarani.util.*;


//********************************
//********************************
public class Usuario {
	//protected static long venceP = str.longo(Guarani.getCfg("logonSegs"),60*5)*1000L;  //5 minutos
	public static String tpPass = "MD5";
	static Hashtable usu=new Hashtable();
	public String dominio, ses;
	public String nome;
	private long dataV=0,dataC; //ultima validacao-criação
	private long dataA=0; //ultimo acesso validado
	private long vence;
	private long nInval=0,nVal=0,nAcess=0;
	protected boolean validoS = true;
	public String grupos,gruposS,gruposN;
	public Hashtable dados;
		
	//controle ver hash
	static long clVer=0L;
	//********************************
	String UsuarioV() {
		String r = nome
			+"~"+dataC
			+"~"+dataV
			+"~"+dataA
			+"~"+vence
			+"~"+nInval
			+"~"+nVal
			+"~"+nAcess
		;
		return r;
	}
	//********************************
	boolean UsuarioSet(String ln) {
		String v[] = ln.split("~");
		dataC = str.longo(v[1],-1);
		dataV = str.longo(v[2],-1);
		dataA = str.longo(v[3],-1);
		vence = str.longo(v[4],-1);
		nInval = str.longo(v[5],-1);
		nVal = str.longo(v[6],-1);
		nAcess = str.longo(v[7],-1);
		return true;
	}
	//**************************************************
	// retorna o valor de uma variavel
	public Object get(String Var) {
		return dados.get(Var);
	}
	//**************************************************
	// seta um valor para uma variavel
	public Object set(String Var,Object Val) {
		Object r = dados.get(Var);
		dados.put(Var,Val);
		return r;
	}
		
	//**************************************************
	public static void delUsu(String u) {
		for (Enumeration e = usu.keys();e.hasMoreElements();) {
			String k = (String)e.nextElement();
			if (str.equals(k,u+"~")) {
				logs.grava("removendo usu:"+k);
				usu.remove(k);
			}
		}
	}
	//**************************************************
	public static void log(String s) {
		logs.grava("val","Usuario: "+s);
	}
	//********************************
	public static void reset(String id) {
		for (Enumeration e = usu.keys(); e.hasMoreElements();) {
			String k = (String)e.nextElement();
			if (str.equals(k,id+"~")) {
				usu.remove(k);
			}
		}
	}
	//********************************
	public static Usuario get(String idSess,String id) {
		if (clVer!=Guarani.classVer) {
			usu = new Hashtable();
			clVer = Guarani.classVer;
		}
		String ch = id+"~"+idSess;
		Usuario r = (Usuario)usu.get(ch);
		if (r==null) {
			String nc = Guarani.getCfg("usuarioClass","br.org.guarani.bd.usuario");
			Class c = Guarani.findClass(nc);
			try {
				r = (Usuario)c.newInstance();
			} catch (Exception e) {
				log("ERRO: newInstance de "+nc+" "+str.erro(e));
				r = new Usuario();
			}
			r.init(idSess,id);
			//r = new Usuario(id);
			usu.put(ch,r);
		}
		return r;
	}

	//********************************
	public boolean validoS() {
		return validoS;
	}
	//********************************
	public boolean valido() {
		//logs.grava(nome+" "+ses+" valido? "+dataA+" "+vence+" "+data.ms());
		return data.ms()-dataA < vence;
	}
	//********************************
	public void valida() {
		nVal++;
		dataA = data.ms();
		//logs.grava(nome+" "+ses+" validado "+dataA);
		dataV = dataA;
		validoS = true;
	}
	//********************************
	public boolean compara(Usuario u) {
		return dominio.equals(u.dominio) && nome.equals(u.nome);
	}
	//********************************
	public void invalida() {
		//logs.grava("logon","LogOFF: usu="+this);
		nInval++;
		dataV = 0;
		dataA = 0;
	}
	//********************************
	public String toString() {
		long t = data.ms();
		return "(<b>usuario</b>: nome=<b><a href=\"usu.class?op=tabela&__tabela=Usuario&__modo=P&__form=f_1306868410366&__rel=1&__manda=Pesquisa&_identif_="+nome+"&_email_=\"><font color="+
			(valido()?"blue>":"red>")+nome+"</font></a></b>/"+dominio
			+" ultimo Acesso: "+(t-dataA)/1000+" segs - nro Acessos: "+nAcess
			+" - segundos logado: "+(t-dataV)/1000+" -  nro validacoes: "+nVal
			+" -  valInvalidas: "+nInval+" ss="+(validoS?"S":"N")
			+" vence="+(vence/1000)
			+")"
		;
		/*return "(<b>usuario</b>: nome=<b><font color="+
			(valido()?"blue>":"red>")+nome+"</font></b>/"+dominio
			+" Acess:t-na="+(t-dataA)/1000+"s-"+nAcess
			+" Val:t-nv-ni="+(t-dataV)/1000+"s-"+nVal+"-"+nInval+" ss="+(validoS?"S":"N")+")";
		*/
	}
	//********************************
	public String getNome() {
		return nome;
	}
	//********************************
	public String getDominio() {
		return dominio;
	}
	//********************************
	public long getDataV() {
		return dataV;
	}
	//********************************
	public long getDataA() {
		return dataA;
	}
	//********************************
	public void setDataA(long s) {
		nAcess++;
		//logs.grava("setDataA="+s);
		dataA = s;
	}
	//********************************
	public  void init(String idSess,String us) {
		//ip = pip;
		ses = idSess;
		dominio = str.leftAt(us,"\\\\");
		nome = str.substrAt(us,"\\\\");
		dataC = data.ms();
		dataV = 0;//data.ms();
		dataA = 0;//data.ms();
		vence = str.longo(Guarani.getCfg("logonSegs"),60*15)*1000L;//Usuario.venceP;
	}
	//********************************
	public Usuario() {
	}
}
