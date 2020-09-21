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

//********************************
//********************************
public class httpSessao implements ObjectOrd {
	public static final String nomeCook = "GSESSIONID";
	public static HashtableOrd sessoes = new HashtableOrd();
	static long dataFutura = data.ms()+data.ms();
	static int limpaNovas=0;
	protected static int nsLimite = -1;  //10 sessoes no máximo
	//static Hashtable ips = new Hashtable();

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
	protected httpSessao(Http ht) {
		Socket sk = ht.sp;
		Pedido ped = ht.pedido;
		Hashtable hr = ped.getCab();
		datac = data.ms();
		//tipo de acesso
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
		//logs.grava("teste"+ip+browser+datac);
		id = httpSessao.novoId(ip+browser+datac);
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
	//********************************
	private boolean valida(String ipa,String br) {
		boolean r = (ip.equals(ipa) && browser.equals(br));
		if (!r) {
			logs.grava("sessao","ip("+ip+")=ipa("+ipa+")"
				+" browser("+browser+")=br("+br+")"
			);
		}
		return r;
	}
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
		return valida(ipn,brn);
	}
	//********************************
	//********************************
	protected static void setUsuario(String ip,String idSes,Usuario u) {
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
	//********************************
	public static void limpa(Pedido ped) {
		String k;
		httpSessao s;
		int nr=0;
		//padrão: limpa sessões de mais de 2 horas sem acesso
		//   ou criadas há 20seg s/nenhum acesso
		//   chamar limpeza automática a cada 20 novas sessões 
		long tv = (ped==null?1000*60*60*2:str.longo(ped.getString("t"),-1));
		long ta=data.ms();
		if (ped!=null) ped.on("<table border=1>"
			+"<tr><td aling=center colspan=4>Removendo maiores que "
			+(tv/60000)+" min");
		for (Enumeration e = sessoes.keys() ; e.hasMoreElements() ;) {
			k = (String)e.nextElement();
			s = (httpSessao)sessoes.get(k);
			if ((ta-s.dataa>tv && ta-s.datac>30000 && s.dataa!=0)
				|| (s.dataa==0 && ta-s.datac>20000) ) {
				nr++;
				if (ped!=null) ped.on("<tr><td>"+nr+s.mostra());
				sessoes.remove(k);
			}
		}
		if (ped!=null) ped.on("<tr><td aling=center colspan=4>Removidas="
			+nr+"</table>");
	}
	//********************************
	private static String novoId(String expr) {
		//logs.grava("mdr="+expr);
		byte r1[] = br.org.guarani.util.digito.md5(expr);
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
		//ja setou nro máximo de sessoes?
		if (nsLimite == -1) {
			nsLimite = str.inteiro((String)ht.cnf.get("maxSessoes"),10);
			logs.grava("Http maxSessoes="+nsLimite);
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
		} else if (r.nova) {
			r.nova = false;
		}
		httpSessao.limSessao();
		
		r.nv++;
		return r;
	}
	//********************************
	protected synchronized static void limSessao() {
		//testa LIMITE/limita SESSOES - de 10 em 10 segundos...
		if (data.ms()-limUTeste<10000) {
			return;
		}
		limUTeste = data.ms();
		//se necessario invalida sessoes com ultimo acesso + antigo
		Object o[] = sessoes.Ordena("dataa");
		int ns=0;
		for (int i=0;i<o.length;i++) {
			httpSessao s = (httpSessao)sessoes.get(o[i]);;
			Usuario u = s.getUsuario();
			if ( u!=null && u.valido() && u.validoS() ) {
				ns++;
				if (ns>nsLimite) {
					u.validoS = false;
				}
			}
		}
	}
}