/*
	sjohn@via-rs.net ago/2002 
	definições:
	pedido somente pode ser feito com sessão 
	válida, e browser aceitando cookie.
*/

/*
Limite usuários!!
Limite de usuários do servidor foi atingido...

Sua sessão foi invalidada.

<p class="p_titPag">Limite usuários!!</p>
<h1>Limite de usuários do servidor foi atingido...</h1>
<p>Sua sessão foi invalidada.</p>
*/
package br.org.guarani.servidor;

import br.org.guarani.util.*;
import java.util.*;
import java.net.*;

import java.security.cert.X509Certificate;

//********************************
//********************************
public class httpSessao implements ObjectOrd {
	public static final String nomeCook = "GSESSIONID";
	public static HashtableOrd sessoes = new HashtableOrd();
	static long dataFutura = data.ms()+data.ms();
	static int limpaNovas=0;
	protected static int nsLimite = -1;  //10 sessoes no máximo
	//static Hashtable ips = new Hashtable();
	static long vence = str.longo(Guarani.getCfg("logonSegs"),60*15)*1000L;


	//var sessoes
	protected boolean nova=true,conf=false;
	//private InetAddress ender;
	private Usuario usuario;
	protected String id,host,ip,browser;
	protected long datac,dataa,datav;
	private Hashtable dados=new Hashtable();

	//var estatisticas
	int nv=0,paramAuto=0;
	
	//lim sessao
	private static long limUTeste;
	private static String arqSessoes = Guarani.dirDados+"/sessoes.csv";
	private String valAnt="";
	private boolean valid = false;

	//*****************************************//
	public boolean validaX509(Pedido ped) {
		X509Certificate x509 = ped.getCliCert();		
		if (x509==null) {
			logs.grava("x509","pedido não retornou cert, usuário sem cert?");
			return false;
		}
		String a = x509.getSubjectDN().getName();
		String usu = str.substrAtAt(a,"CN=",",");		
		Object o = Usuario.get(getId(),usu);
		usuario = (Usuario)o;//sessao.getUsuario();
		if (usuario==null) {
			logs.grava("x509","usuario="+usu+"= não validado class Usuario");
			usu = "?";
			return false;
		}
		usuario.valida();
		//setUsuario(usuario);
		return true;
	}
	//********************************
	public static void load() {
		arquivo aq = new arquivo(arqSessoes);
		String ln;
		int nv=0;
		while ((ln=aq.leLinha())!=null) {
			httpSessao s = new httpSessao(ln);
			if (s.usuario!=null) {
				sessoes.put(s.id,s);
				nv++;
			}
		}
		aq.fecha();
		logs.grava("fim","sessoes carregadas "+nv);
	}
	//********************************
	public static void salva() {
		arquivo aq = new arquivo(arqSessoes);
		int nv=0;
		for (Enumeration e = sessoes.elements();e.hasMoreElements();) {
			httpSessao s = (httpSessao)e.nextElement();
			if (s.valid) {
				aq.gravaLinha(s.httpSessaoV());
				nv++;
			}
		}
		aq.fecha();
		logs.grava("fim","sessoes salvas "+nv);
	}
	//********************************
	public static void sessoesInit() {
		//salva ao shutdown
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				//salva sessoes
				logs.grava("fim","Thread salvando...");
				salva();
			}
		});
		logs.grava("fim","add shutdown hook");
		load();
		
		//salva periodicamente
		(new Thread() {
			public void run() {
				//salva a cada 10 minutos
				try {
					Thread.sleep(60000*10);
				} catch (Exception e) {
				}
				salva();
			}
		}).start();
	}
	//********************************
	public static void resetUsuario(String u) {
		//limpa nas SESSOES
		httpSessao s;
		Object o[] = sessoes.Ordena("");
		for (int i=0;i<o.length;i++) {
			s = (httpSessao)sessoes.get(o[i]);
			if (s.usuario!=null && s.usuario.nome.equals(u)) {
				s.usuario = null;
			}
		}
		//limpa no OBJ Usuarios
		Usuario.delUsu(u);
	}
	//********************************
	public static void mostra(Pedido ped) {
		httpSessao s;
		String l,filtro=ped.getString("filtro","").toLowerCase();
		Object o[] = sessoes.Ordena(ped.getString("ord",""));
		ped.on("<table border=1><tr><td colspan=3 align=center>"
			+"Total: "+sessoes.size()
			+" <a href=?op=limpaSess&t="+(24*3600000)+">Limpa</a>"
			+"- <a href=?op=limpaSess&t="+(00*3600000)+">tudo</a>"
			+"<tr><th>Acess<br>Cria<th>nv/ts<th>id host<br>brow");
		for (int i=0;i<o.length;i++) {
			s = (httpSessao)sessoes.get(o[i]);
			l = "<tr>"+s.mostra();
			if (filtro=="" || l.toLowerCase().indexOf(filtro)!=-1) {
				ped.on(l);
			}
		}
		ped.on("</table>");
	}
	//********************************
	public void invalidate() {
		dataa = 0;
	}

	//********************************
	public Object getOrdem(String o) {
		if (o.equals("ip")) {
			return ip; 	
		} else if (o.equals("datav")) {
			return usuario==null ? ""+dataFutura : ""+(dataFutura-usuario.getDataV());
		} else if (o.equals("datac")) {
			return ""+(dataFutura-datac);
		} else if (o.equals("browser")) {
			return browser;
		} else if (o.equals("usuario")) {
			return usuario==null ? "ZZZ" : usuario.getNome();
		} else if (o.equals("nv")) {
			return ""+(dataFutura-nv);
		}
		return ""+(dataFutura-dataa);
	}
	//********************************
	protected void confirma() {
		conf = true;
	}

	//********************************
	public String toString() {
		return "(<b>sessao</b>: id="+id+" ip="+ip
			+(!valid||data.ms()-dataa>vence?"<p style=\"color:red;\">sessão  I N V Á L I D A !</p>":"")
			+((usuario!=null)?" usu="+usuario:"")+")";
	}
	//********************************
	public String getIp() {
		return ip;
	}
	//********************************
	public String getBrowser() {
		return browser;
	}
	//********************************
	public Usuario getUsuario() {
		return usuario;
	}
	//********************************
	//reseta dados da sessão
	public void reset() {
		dados = new Hashtable();
	}
	//********************************
	public boolean remove(String s) {
		boolean r = dados.get(s)!=null;
		dados.remove(s);
		return r;
	}
	//********************************
	public String put(Object s,Object o) {
		if (s==null) {
			String s1 = "_auto_"+paramAuto++;
			dados.put(s1,o);
			return s1;
		} else {
			dados.put(s,o);
		}
		return null;
	}
	//********************************
	public Enumeration getAttributeNames() {
		return dados.keys();
	}
	//********************************
	public Object get(Object s) {
		return dados.get(s);
	}
	//********************************
	public long getCreationTime() {
		return datac;
	}
	//********************************
	public long getLastAccessedTime() {
		return dataa;
	}
	//********************************
	public String getId() {
		return id;
	}
	//********************************
	protected httpSessao() {
	}
	//********************************
	private httpSessao(String ln) {
		String v[] = str.palavraA(ln,"\t");
		id = v[0];
		nova = v[1].equals("true");
		conf = v[2].equals("true");
		usuario = Usuario.get(id,str.leftAt(v[3],"~"));
		usuario.UsuarioSet(v[3]);
		host = v[4];
		//valid = v[5].equals("true");
		//ip = v[5];
		//browser = v[6];
		datac = str.longo(v[5],-1);
		dataa = str.longo(v[6],-1);
		datav = str.longo(v[7],-1);		
		nv = str.inteiro(v[8],-1);
		valid = data.ms()-dataa<vence;
	}
	//********************************
	private String httpSessaoV() {
		return id
			+"\t"+nova
			+"\t"+conf
			+"\t"+usuario.UsuarioV()
			+"\t"+host
			//+"\t"+ip
			//+"\t"+valid
			+"\t"+datac
			+"\t"+dataa
			+"\t"+datav
			+"\t"+dados
			+"\t"+nv		
		;
	}	
	//********************************
	private void init(Http ht) {
		//
		Socket sk = ht.sp;
		Pedido ped = ht.pedido;
		Hashtable hr = ped.getCab();
		if (hr.get("x-forwarded-server")!=null) {
			//por proxy ou apache proxy
			ip = (String)hr.get("x-forwarded-for");
			host = ip;
			//regrava o host
			//hr.put("host",hr.get("x-forwarded-host"));
		} else if (hr.get("o_ssl")==null) {
			//direta
			InetAddress ender = sk.getInetAddress();
			ip = ender.getHostAddress();
			host = ender.getHostName();
		} else {
			//ajp13
			ip = (String)hr.get("remote_addr");
			host = (String)hr.get("remote_host");
		}
		if (str.vazio(host)) {
			host = ip;
		}
		browser = str.seVazio((String)hr.get("user-agent"),"?");
	}
	//********************************
	protected httpSessao(Http ht) {
		datac = data.ms();
		//init
		init(ht);
		//logs.grava("teste"+ip+browser+datac);
		id = novoId(ip,browser);
		//logs.grava("id="+id);
		nova = true;
		logs.grava("sessao","NOVA: "+this);
	}
	//********************************
	private String mostra() {
		return "<td>"+data.strSql(dataa)+"<br>"+
			data.strSql(datac)+"<td align=right>"+nv+
			"/"+((dataa-datac)/60000)+"<td>"+id+" <b>"+ip+"</b>"+
			"<br>"+usuario+"<br><font size=1>"+browser+
			"<br>dadS="+dados.size()+"<br>"+dados;
	}
	/* *******************************
	private boolean valida(String ipa,String br) {
		boolean r = (ip.equals(ipa) && browser.equals(br));
		if (!r) {
			logs.grava("sessao","ip("+ip+")=ipa("+ipa+")"
				+" browser("+browser+")=br("+br+")"
			);
		}
		return r;
	}
	*/
	private boolean valida(Socket sk,Hashtable pd) {
		String ipn,brn;
		brn = str.seVazio((String)pd.get("user-agent"),"?");
		if (pd.get("x-forwarded-server")!=null) {
			//por proxy ou apache proxy
			ipn = (String)pd.get("x-forwarded-for");
		} else if (pd.get("o_ssl")==null) {
			ipn = sk.getInetAddress().getHostAddress();
		} else {
			ipn = (String)pd.get("remote_addr");
		}
		if (valAnt.equals(ipn+brn+datac)) {
			return true;
		} else {
			valid = id.equals(novoId(ipn,brn));
			if (valid) valAnt = ipn+brn+datac;
			return valid;
		}
	}
	//********************************
	//********************************
	protected void setUsuario(Usuario u) {
		usuario = u;
	}
	/* *******************************
	//********************************
	protected static void setUsuario1(String ip,String idSes,Usuario u) {
		httpSessao r = (httpSessao)sessoes.get(idSes);
		if (r !=null && r.ip.equals(ip)) {
			if (r.usuario!=null && r.usuario.compara(u)) {
				//logs.grava("usuario igual="+r);
				//r.usuario.valida();
			} else {
				//logs.grava("usuario setando="+r);
			}
			r.usuario = u;
		} else {
			logs.grava("sessao","IP # ou sess não existe="
				+ip+" idSess="+idSes+" não fecha "+r);
		}
	}
	*/	
	//********************************
	public static void limpa(Pedido ped) {
		String k;
		httpSessao s;
		int nr=0;
		//padrão: limpa sessões de mais logonSegs sem acesso
		//   ou criadas há 20seg s/nenhum acesso
		//   chamar limpeza automática a cada 20 novas sessões 
		
		// 2horas ou "t"? do pedido? ===> adm class?
		long tv = Math.max(
					ped==null?0:str.longo(ped.getString("t"),-1)
					,str.longo(Guarani.getCfg("logonSegs"),60*15)*1000
				);
		long ta=data.ms();
		if (ped!=null) ped.on("<table border=1>"
			+"<tr><td aling=center colspan=4>Removendo maiores que "
			+(tv/60000)+" min");
		for (Enumeration e = sessoes.keys() ; e.hasMoreElements() ;) {
			k = (String)e.nextElement();
			s = (httpSessao)sessoes.get(k);
			if	(
					(s.dataa!=0 && ta-s.dataa>tv)
				||	(s.dataa==0 && ta-s.datac>20000)
				) {
				nr++;
				if (ped!=null) ped.on("<tr><td>"+nr+s.mostra());
				sessoes.remove(k);
			}
		}
		if (ped!=null) ped.on("<tr><td aling=center colspan=4>Removidas="
			+nr+"</table>");
	}
	//********************************
	private String novoId(String ip,String nav) {
		//toDo - país ip (mas se for interna)...
		String v[] = nav.replace('/',' ').split(" ");
		String expr = "";
		for (short i=0;i<v.length;i++) {
			expr += v[i].length()==0?"?":v[i].charAt(0);
		}
		//logs.grava("mdr="+expr);
		byte r1[] = br.org.guarani.util.digito.md5(expr+data.strSql(datac));
		//logs.grava("mdr="+(new String(r1)));
		int t = r1.length;
		char r[] = new char[t];
		String tbc = "0123456789abcdef";
		for (int i=0;i<t;i++) {
			if (r1[i]<0) {
				r[i] = tbc.charAt(-1*r1[i]/16);
			} else {
				r[i] = tbc.charAt(r1[i]%16);
			}
		}
		return new String(r);
	}
	//********************************
	public static httpSessao getSessao(String id) {
		return (httpSessao)sessoes.get(id);
	}
	//********************************
	protected synchronized static httpSessao getSessao(Http ht) {
		
		//executa apenas 1 vez
		if (nsLimite == -1) {
			nsLimite = str.inteiro((String)ht.cnf.get("maxSessoes"),100);
			logs.grava("Http maxSessoes="+nsLimite);
			//Runtime.getRuntime().addShutdownHook(new salva());
			sessoesInit();
		}
		
		Socket sk = ht.sp;
		Pedido ped = ht.pedido;
		Hashtable pd = ped.getCab();
		httpSessao r = null;
		String id = (String)ped.cook.get(nomeCook);
		if (id!=null) {
			r = (httpSessao)sessoes.get(id);
			if (r!=null && !r.valida(sk,ped.getCab())) {
				logs.grava("sessao","ip invalido sessao.id="+id+" sk="+sk);
				r = null;
			}
			//qual ip
		}
		if (r==null) {
			//logs.grava("sk="+sk+" pd="+pd);
			if (limpaNovas++>19) {
				httpSessao.limpa(null);
				limpaNovas = 0;
			}
			r = new httpSessao(ht);
			id = r.getId();
			sessoes.put(id,r);
		} else {
			if (r.nova) r.nova = false;
			//se sessao foi recuperada de arquivo não há ip
			if (r.ip == null) {
				r.init(ht);
			}
		}
		httpSessao.limSessao();
		
		
		r.nv++;
		return r;
	}
	//********************************
	protected synchronized static void limSessao() {
		//testa LIMITE/limita SESSOES validadas - a cada 10min
		if (data.ms()-limUTeste<600000) {
			return;
		}
		limUTeste = data.ms();
		//se necessario invalida sessoes com ultimo acesso + antigo
		// toDo - não tem sentido invalidar sessões usuário válido...,
		// 	deveria excluir sessões sem usuário.
		Object o[] = sessoes.Ordena("dataa");
		int ns=0;
		for (int i=0;i<o.length;i++) {
			httpSessao s = (httpSessao)sessoes.get(o[i]);
			Usuario u = s.getUsuario();
			if ( u!=null && u.valido() && u.validoS() ) {
				ns++;
				if (ns>nsLimite) {
					//u.validoS = false;
				}
			}
		}
	}
}
