/*
	* sjohn@via-rs.net jan/2001 - 
*/
package br.org.guarani.servidor;

	import br.org.guarani.util.*;

	import java.net.*;
	import java.io.*;
	import java.lang.*;
	import java.util.*;
	import java.nio.charset.Charset;
	//import guarani.es.*;

//********************************
//********************************
class Http extends ProtocoloAbstrato {
	protected Hashtable pd;
	protected Leitor i;
	protected GravadorHttp o;
	boolean chunk = true;
 
	//public static String lf = "\r\n";
	public static final String httpVer = "HTTP/1.1";
	//public static final String movTemp = "<html><body>302 Moved Temporarily</body></html>"+lf+lf;
	public static final String charset = Guarani.getCfg("charset","iso-8859-1");
	public static final Charset charsetO = Charset.forName(charset);
	public static final String cabPrg = 
		"Cache-Control: private"+lf
		+"Cache-Control: no-store"+lf
		+"Content-Type: "+Guarani.tipos.getTipo(".html")+"; charset="+charset+lf
	;
	httpSessao sessaoN; //sessão não sessão

	protected boolean multiPart = false;

	protected int tBf = 1024*1024*4,timeoutUpload=-1;
	protected byte[] buf = new byte[tBf];

	protected String get, geto, param, term, ext, get1,permissao;
	protected Hashtable cnf;
	protected File arq;
	protected boolean classe,debugp=false,servlet;
	protected Pedido pedido;

	//servlet
	protected static Hashtable servlets;
	//tranf guarani = public static Hashtable sessoes;
	protected String servletN;

	String www_root;
	Hashtable www_vdirs=null;
 
	String dirIgnora[],dirClasse[][],naoSessao[];
	
	boolean getIsDir = false;
	boolean https = false;
	//*************************************************
	public String detalhePedido() {
		return pedido.toString();
	}
	//********************************
	public Object getConf(String s) {
		Object r = cnf.get(s);
		//ogs.grava(s+"="+r+" "+cnf);
		return r;
	}
	//********************************
	public void movTemp(String url, String cb) {
		resp(httpVer+" 302 Moved Temporarily");
		o.print(cabPrg
			+(str.vazio(cb)?"":str.trimm(cb)+lf)
			//2020/set + "Content-Length: "+movTemp.length()+lf				
			+ "Content-Length: 0"+lf				
			+"Location: "+url+lf
			//2020/set +lf+movTemp
			+lf
		);
	}
	//********************************
	public String dirRoot() {
		return ""+www_root;
	}
	//********************************
	public void deb(String s) {
		logs.grava("debug",s);
	}
	//********************************
	// mpv mplayer não aceitam sessão
	boolean naoSessao() {
		if (naoSessao!=null) {
			String s = (String)pd.get("?endereco");
			for (short i=0;i<naoSessao.length;i++) {
				//ogs.grava(s+" "+naoSessao[i]);
				if (str.equals(s,naoSessao[i])) {
					return true;
				}
			}
		}
		return false;
	}
	//********************************
	public void run() {
		nPedidos++;
		in = System.currentTimeMillis();
		pd = new Hashtable();
		int x;
		
		String raizWeb="/";

		if (!erro) {
			abreStream();
		} else {
			logs.grava("servidor","erro antes de abrir stream!!");
		}


		if (!erro & rodando) {
			lePedido();
			if (pd.size()==0) {
				// 2017-dez o firefox? abre o socket  e fecha...
				erro = true;
				geto = "PEDIDO vazio!!";
			} else if ((get1 = (String)pd.get("?endereco"))==null) {
				erro = true;
				logs.grava("servidor",new Exception("pedido invalido!"),
					"sk="+sp+" pd="+pd);
			} else {
				//ogs.grava("g="+get1);
    
				//dirIgnora - ajp13 e apache proxy
				if (dirIgnora!=null) {
					for (short i=0;i<dirIgnora.length;i++) {
						//ogs.grava("dirIg="+dirIgnora[i]+" "+get1);
						if (str.equals(get1,dirIgnora[i])) {
							get1 = "/"+get1.substring(dirIgnora[i].length());
							//l ogs.grava("dirIgRes="+dirIgnora[i]+" "+get1);
							raizWeb = dirIgnora[i];
							break;
						}
					}
				}
				
				//dirClasse - para baixar arquivos validado com nome original
				// redireciona todos os pedidos para o dir, ou sub-dir deste, para uma classe
				// pode ser usado para cgi... --> dirClasse=/player/=/cgiBash.class
				if (dirClasse!=null) {
					for (short i=0;i<dirClasse.length;i++) {
						// 0="o dir"  1=antes'?' 2=apos'?'
						//ogs.grava(i+" geto="+geto+" get1="+get1+" d="+dirClasse[i][0]);
						if (str.equals(get1,dirClasse[i][0])) {
							int p = get1.indexOf("?");
							String arg;
							if (p==-1) {
								arg = dirClasse[i][2]+get1;
							} else {
								arg = dirClasse[i][2]+get1.substring(0,p)
									+"&"+get1.substring(p+1)
								;
							}
							//l ogs.grava("de: "+get1);
							get1 = dirClasse[i][1]+"?"+arg;
							//http://localhost/baixarwwws/Pessoa/Anexos/Pessoa/0%7e%7e75_safecor_proposta.sxw%7e%7e1097155648000%7e%7e13547/safecor_proposta.sxw
							//l ogs.grava("para: "+get1);
							break;
						}
					}
				}

				//l ogs.grava("g1="+get1);
    
			}
		}
  
		//POST
		if (!erro & rodando) {
			pedido = new Pedido(this,pd);
			pedido.raizWeb = raizWeb;
			pedido.setOut(o,sp);

			if (((String)pd.get("?")).equals("POST")) {
				lePost();
			}
		}

		//analisa PEDIDO
		if (!erro & rodando) {
			try {
				analizaPedido();
			} catch (Exception e) {
				erro = true;
				logs.grava("servidor",e,"analise pedido="+pd+" socket="+sp);
			}
		}

		//criar sessão?
		if (!erro && rodando && classe) {
			if (naoSessao()) {
				httpSessao sessao = sessaoN;
				sessao.dataa = data.ms();
				pedido.setSessao(sessao);
				pedido.naoSessao = true;
			} else {
				httpSessao sessao = httpSessao.getSessao(this);
				String dsv = (String)pd.get("?endereco");
				x = dsv.indexOf("_GS_");
				if (sessao==null) {
					deb("sessao null");
					//1o acesso
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
					}
					dsv = (dsv.indexOf("?")==-1 ?dsv+"?" :dsv+"&"+"_GSI_="+data.ms());
					movTemp(dsv,null);
				} else if (sessao.nova) {
					if (x!=-1) {
						deb("sessao nova -1");
						if (str.equals(get1,"/adm.class?op=stop")) {
							if ( (sessao.ip.equals("127.0.0.1")||sessao.ip.equals("0:0:0:0:0:0:0:1")) && !pedido.proxy ) {
								logs.grava("seguranca","stop aceito ip="+sessao.ip+" proxy="+(pedido.proxy?"true":"false"));
								Guarani.stop(0);
								rodando = false;
							} else {
								logs.grava("seguranca","tentativa inválida de stop ip="+sessao.ip
									+" proxy="+(pedido.proxy?"true":"false")
								);
							}
						} else {
							resp(httpVer+" 200 OK");
							o.print(cabPrg+lf
								+"<html><h1>Browser não aceita cookie"
								+"<br>em: "+data.strSql()
								+"<br>cookies: "+pedido.cook
								+"<br>"+httpSessao.nomeCook+": "+pedido.cook.get(httpSessao.nomeCook)
								+"</h1></html>"
							);
							logs.grava("servidor","não aceita cookie: "+pedido+"<br>pd="+pd);
						}
					} else {
						//deb("sessao nova setar cook");
						//resp(httpVer+" 302 Moved Temporarily");
						dsv = (dsv.indexOf("?")==-1?dsv+"?"	:dsv+"&")	+"_GS_="+data.ms();
						//l ogs.grava("sessao","set cook dsv="+dsv);
						/*o.print(cabPrg
							+"Set-Cookie: "+httpSessao.nomeCook+"="+sessao.getId()+";Path=/"+lf
							+ "Content-Length: "+movTemp.length()+lf				
							+"Location: "+dsv+lf
							+lf+movTemp
						);
						*/
						movTemp(dsv,
							"Set-Cookie: "+httpSessao.nomeCook+"="+sessao.getId()
							+";Expires="+data.strHttp(data.ms()+3600000l*24*365)
							+";Path=/"
						);
						//logs.grava("vcto sessão="+data.strHttp(data.ms()+3600000*24*365));
					}
					rodando = false;
				} else if (x!=-1) {
					//deb("sessao confirma");
					sessao.confirma();
					dsv = dsv.substring(0,x-1);
					//l ogs.grava("sessao","sessao confirma dsv="+dsv);
					/*resp(httpVer+" 302 Moved Temporarily");
					o.print("Location: "+dsv+lf
						+cabPrg
						+ "Content-Length: "+movTemp.length()+lf				
						+lf+movTemp
					);
					*/
					movTemp(dsv,null);
					rodando = false;
				} else {
					//setar sessão pedido...
					//deb("sessao ok");
					sessao.dataa = data.ms();
					pedido.setSessao(sessao);
				}
			}
		}

		//analiza se modificado
		if (!erro && rodando && !classe) {
			//cgi = Guarani.tipos.getCgi(ext); cgi==null & 
			if (!classe & !getIsDir) {
				arqModificado();
			}
		}

		if (!erro & rodando) {

			//monta cebecalho
			pedido.cab = httpVer+" 200 OK"+lf+respp();
			if (classe || getIsDir) { //|| cgi!=null
				pedido.cab += cabPrg
					+ "Connection: close"+lf;
			} else {
				//arquivo
				/*pedido.cab +=
							"Content-Type: "+Guarani.tipos.getTipo(ext)+lf
					+ "Last-Modified: "+data.strHttp(arq.lastModified())+lf
					+ "Content-Length: "+arq.length()+lf
					+ "Connection: close"+lf;
				*/
			}

			//gera a resposta
			if (classe) {
				//classes compiladas com serv start
				if (servlet) {
					pedido.servletP = new Hashtable();
					pedido.servletP.put("?",pd);
					pedido.servletP.put("Gconf",servlets.get(servletN));
				}
    
				if (geto.indexOf(".obj")!=-1) {
					geto = str.troca(geto,".obj",".class");
				}
				Guarani.execClasse(geto,pedido,o,0);

			} else if (getIsDir) {
				mostradir(get,geto);

			} else if ((ext.equals(".class") || ext.equals(".java"))
						& get.indexOf("applets")<0) {
				o.println("<html><h1>Extenssão invalida="+get+" "+ext+"</h1></html>");

			} else {
				//if (true)cgi==null) {
					//o.print(pedido.cab+lf);
					mandaArq(""+arq,null,false);
				//} else {
				//	execCgi(cgi);
				//}
			}
		}

		geto = data.strSql(in)+" "+geto+"<br>"+sp;
 
		//FLUSH
		try {
			o.flush();
		} catch (Exception e) {
			erro = true;
			logs.grava("servidor",geto+", o.flush(): "+e);
		}

		/*problema com ajp13
		//SHUTDOWN
		try {
			if (sp!=null) {
				sp.shutdownOutput();
			}
		} catch (IOException e) {
			//logs.grava("task",geto+", sp.shutdownOutput(): "+e);
		}

		try {
			if (sp!=null) {
				sp.shutdownInput();
			}
		} catch (IOException e) {
			//logs.grava("task",geto+", sp.shutdownInput(): "+e);
		}
		*/

		//CLOSE
		if (pedido!=null) {
			// null acontece qdo auth ssl falho...
			pedido.close();
			nBytes += pedido.getBytes();
		}
		try {
			o.close();
		} catch (Exception e) {
			//erro = true;
			//logs.grava("task",geto+", o.close(): "+e);
		}
  
		try {
			if (i!=null) i.close();
		} catch (Exception e) {
			//erro = true;
			//logs.grava("task",geto+", i.close(): "+e);
		}
   
		try {
			if (sp!=null) {
				sp.close();
			}
			sp = null;
		} catch (IOException e) {
			//logs.grava("task",geto+", sp.close(): "+e);
		}

		fi = System.currentTimeMillis();
		tempo += fi-in;
		rodando = false;
		return;

	}

	//********************************
	//atende pedido
	protected void abreStream() {
		try {
			String chs = "iso-8859-1";
			chs = charset;
			i = new Leitor(new InputStreamReader(sp.getInputStream(),charset));
			OutputStream out = sp.getOutputStream();
			//OutputStreamWriter outw = new OutputStreamWriter(out,"UTF-8");
			//OutputStream out = sp.getOutputStream();
			o = new GravadorHttp(out);
			//o = new GravadorHttp(sp.getOutputStream());
		} catch( final IOException ioe ) {
			logs.grava("servidor","Abrindo Stream sp="+sp);
			erro = true;
		}
	}

	//********************************
	//inicializa - meleca, executa para apenas uma das tarefas...?
	public void init(Hashtable conf) {
		//l ogs.grava("***********************http init");
		rodando = false;
		erro = false;
		cnf = conf;
		timeoutUpload = str.inteiro(""+cnf.get("timeoutUpload"),15000);
		tempo = 0;
		nPedidos = 0;
		nBytes = 0;
		
		chunk = (cnf.get("chunk")==null?"1":cnf.get("chunk")).equals("1");

		//cabHtml = "Content-Type: "+Guarani.tipos.getTipo(".html")+lf;

		permissao = " "+str.seNull((String)cnf.get("permissao"),"*")+" ";
  
		www_root = ""+(new File(Guarani.dir((String)cnf.get("www_root")))).getAbsolutePath();
		String s = (String)cnf.get("www_vdirs");
		debugp = str.seNull((String)cnf.get("debugp"),"").equals("on");
  
		if (s!=null) {
			www_vdirs = new Hashtable();
			String v[] =  str.palavraA(str.troca(s,"\\","/"),";");
			for (int i=0;i<v.length;i++) {
				if (v[i].indexOf("=")>0)
					www_vdirs.put(str.trimm(str.leftAt(v[i],"="))+"/",
						str.trimm(str.substrAt(v[i],"="))
					);
			}
		}
  
		//servlet
		if (servlets==null) {
			int i=0;
			Guarani.sessoes =  new Hashtable();
			servlets = new Hashtable();
			while ((s=(String)cnf.get("servlet"+i))!=null) {
				//l ogs.grava("servlet",i+"="+s);
				servlets.put(str.leftAt(s,"~"),str.palavraA(s,"~"));
				i++;
			}
		}
  
		//if (cnf.get("dirClasse")!=null) {
		//pega da conf geral do Guranai
		if (Guarani.getCfg("dirClasse")!=null) {
			String v[] = str.palavraA(str.trocaTudo(str.trimm(""+Guarani.getCfg("dirClasse")),"  "," ")," ");
			dirClasse = new String[v.length][3];
			for (short i=0;i<v.length;i++) {
				//ogs.grava(i+" v="+v[i]);
				dirClasse[i][0] = str.leftAt(v[i],"=");
				String dc = str.substrAt(v[i],"=");
				if (dc.indexOf("?")==-1) {
					dirClasse[i][1] = dc;
					dirClasse[i][2] = "dir=";
				} else {
					dirClasse[i][1] = str.leftAt(dc,"?");
					dirClasse[i][2] = str.substrAt(dc,"?");
				}
				//ogs.grava("**********************"
				//	+dirClasse[i][0]+"="+dirClasse[i][1]+" 2="+dirClasse[i][2]
				//);
			}
		} else {
			//l ogs.grava("*******************Dir CLASSE=null");
		}
		if (cnf.get("dirIgnora")!=null) {
			dirIgnora=str.palavraA(str.trocaTudo(str.trimm(""+cnf.get("dirIgnora")),"  "," ")," ");
			for (int i=0;i<dirIgnora.length;i++) {
				dirIgnora[i] = "/"+str.trimm(dirIgnora[i],"/")+"/";
			}
		}
		if (cnf.get("naoSessao")!=null) { 
			naoSessao=str.palavraA(str.trocaTudo(str.trimm(""+cnf.get("naoSessao")),"  "," ")," ");
			for (int i=0;i<naoSessao.length;i++) {
				naoSessao[i] = "/"+str.trimm(naoSessao[i],"/")+"/";
			}
			sessaoN = new httpSessao();
		}		
	}
	//********************************
	//seta socket
	public void setSocket(Socket s) {
		sp = s;
		erro = false;
		classe = false;
		servlet = false;
		geto = "";
		get1 = "";
		get = "";

		//l ogs.grava(sp.getLocalAddress().getHostAddress()); 
		InetAddress i = sp.getInetAddress();
		String ip = i.getHostAddress();
		if (!permissao.equals(" * ") &&
			permissao.indexOf(" "+ip+" ")==-1){
			logs.grava("servidor","Http: sem permissão "+permissao+"=="+sp);
			erro = true;
			return;
		}

	}
	//********************************
	//le pedido
	protected void lePedido() {
		String sPedido = " ",np,pr;
		int nl = 0;

		try {
			while (sPedido.length()!=0 & rodando & !erro) {
				//pedido = new String(i.readLine().getBytes());
				//sPedido = i.readLine();
				sPedido = i.readLine();
				if (debugp) {
					logs.grava("debug",sPedido);
				}
				nl++;
				if (sPedido==null) {
					erro = true;
					sPedido = "";

				} else if (nl==1) {
					try {
						pd.put("?",str.leftAt(sPedido," "));
						pd.put("?endereco",str.substrAtRat(sPedido," "," "));
						pd.put("?protocolo",str.rightAt(sPedido," "));
					} catch (Throwable e) {
						logs.grava("seguranca","pedido invalido tam="+sPedido.length()+
							" ped="+sPedido
						);
					}

				} else if (sPedido.length()!=0) {
					np = str.leftAt(sPedido,":").toLowerCase();
					pr = str.substrAt(sPedido," ");
					if (np.equals("keep-alive")) {
						keepAlive++;
					}
					pd.put(np,pr);
				}
			}

		} catch (IOException ioe) {
			//2017-dez - ssh para cada pedido faz um vazio ?
			if (nl!=0) {
				logs.grava("servidor",ioe,"lendo Pedido, ln lidas="+nl
					+"<br>sp="+sp+"<br>ped="+pd);
				erro = true;
				geto = "ERRO LENDO PEDIDO!!";
			}
		}
  
		if (debugp) {
			logs.grava("debug",""+pd);
		}

	}

	//********************************
	//le POST
	protected void lePost() {
		long t = System.currentTimeMillis();

		int read;
		String ln1="",l;
		//Content-Type: application/x-www-form-urlencoded
		//Content-Type: multipart/form-data; boundary=---------------------------7d1295376039c
		String tipo = (String)pd.get("content-type");
		int nr, tm = Integer.parseInt((String)pd.get("content-length"));

		multiPart = tipo.substring(0,10).equals("multipart/");
		if (multiPart) {
			i.restaPost = tm;
			pedido.io = i;
			try {
				sp.setSoTimeout(timeoutUpload);
			} catch (Exception e) {
			}
			LeitorMP lt = new LeitorMP(pedido);
			if (!lt.setPedido()) {
				logs.grava("servidor","ERRO MultiPart tm="+tm);
			}
			return;
		}
		//char[] cbuf = new char[1024];
		//ogs.grava("charsetO="+charsetO);
		try {
			if (!multiPart) {
				byte[] cbuf = new byte[1024];
				while (tm > 0 ) {
					nr = i.read(cbuf, 0, ((tm>1024) ? 1024 : tm));
					if (nr>0) {
						tm -= nr;
						ln1 += new String(cbuf,0,nr);
					} else {
						tm = 0;
					}
				}
				pedido.setPost(ln1);
				/*
				String s;
				while (tm > 0 ) {
					s = i.readLine();
					if (s==null) break;
					ln1 += s+"\r\n";
					tm -= s.length()+2;
				}
				pedido.setGet(ln1);
				*/

			} else {
				byte[] cbuf = new byte[1024];
				//parece que o tomcat não trata isso, e sim permite passar
				//para o httpservlet o inputstream...
				logs.grava("servidor","tipo post não implementado!!");
				//int nr,s = 0;
				ln1="";
				while (tm > 0 ) {
					nr = i.read(cbuf, 0, ((tm>1024) ? 1024 : tm));
					//nr = i.read(cbuf, 0, tm);
					//l ogs.grava("leu:"+nr+" falta: "+tm);
					if (nr>0) {
						tm -= nr;
						ln1 += new String(cbuf,0,nr,charsetO);
					} else {
						tm = 0;
					}
				}
				arquivo a = new arquivo("/tmp/teste.post");
				a.gravaTxt(ln1);
				if (true) return;

				String nm="",vr="",aq="",aqt="";
				String bd = "--"+str.substrAtAt(tipo+" ","boundary="," ");
				int s=0;
				logs.grava("servidor","multipart="+bd);
				while (tm > 0 ) {
					l = i.readLine();
					nr = l.length()+2;
					tm -= nr;
					//logs.grava("leu:"+nr+" falta: "+tm+" "+l);
					if (l.equals(bd) | l.equals(bd+"--")) {
						//logs.grava("SEPARADOR s="+s+" nm="+nm);
						if (s==0) {
							s = 1;
						} else if (s==4) {
							ln1 += "&"+nm+"="+vr.substring(0,vr.length()-2);
							s = 1;
						} else {
							logs.grava("servidor","ERRO bd não esperado no post s="+s);
						}

					} else if (s==1) {
						//nome, etc..
						if (str.equals(l,"Content-Disposition:")) {
							nm = str.substrAtAt(l," name=\"","\"");
							aq = null;
							vr = "";
							if (l.indexOf(" filename=\"")>-1) {
								aq=str.substrAtAt(l," filename=\"","\"");
								logs.grava("servidor","AQ="+aq);
								s = 2;
							} else {
								s = 3;
							}
						} else {
							logs.grava("servidor","ERRO esperado content disp="+l);
						}

					} else if (s==2) {
						//arquivo = tipo
						if (str.equals(l,"Content-Type:")) {
							aqt = str.substrAt(l,"Content-Type: ");
						} else {
							logs.grava("servidor","ERRO esperado tipo arq="+l);
						}
						s = 3;

					} else if (s==3) {
						//linha em branco
						if (l.length()!=0) {
							logs.grava("servidor","Esperado linha em branco");
						}
						s = 4;

					} else if (s==4) {
						//dados
						vr += l+lf;

					} else {
						if (str.equals(l,"--")) {
							logs.grava("servidor","=="+bd+"="+bd.length()
								+"="+lf+"=="+l+"="+l.length()+"=");
						}

					}
				}
				logs.grava("servidor",ln1);
				pedido.setPost(ln1);
			}

		} catch (Exception e) {
			logs.grava("servidor", "Http.class: Error. lendo Post="
				+e+lf+"=="+sp+lf+"=="+pd );
			erro = true;
			geto = "ERRO LENDO POST!!";
		}

		//l ogs.grava("tempo post="+(System.currentTimeMillis()-t));

	}
	//********************************
	//analiza pedido 2020/set
	void analizaPedido() {
		getIsDir = false;
		get = ""+get1; //(String)pd.get("?endereco");
		//fazer isto no caso de ser arquivo,
		//senão os parametros devem ser decoded 1 a 1 
		//get = URLDecoder.decode(get); 
		int i = get.indexOf('?');
		if (i!=-1) {
			pedido.setGet(get.substring(i+1,get.length()));
			get = get.substring(0,i);
		}
		
		get = str.UnEscape(get);

		//segurança, descoberta fábio
		//não permitir endereços com ..
		//o que permite ler toda a unidade
		//em caso do serv ter permissão para isto
		//talvez tenha outras possibilidades... 
		if (get.indexOf("..")>-1) {
			o.println("Erro, pedido inválido...");
			geto = "ERRO, uso de .. "+get;
			erro = true;
			return;
		}

		//ver extenção
		ext = "";
		i = get.lastIndexOf(".");
		int i1 = get.lastIndexOf("/");
		if (i>-1 & i1<i) {
			ext = get.substring(i,get.length());
			//l ogs.grava("ext -> "+ext);
		}

		geto = get;
		get = www_root+get;
		//diretório virtual?
		if (www_vdirs!=null) {
			String s = geto.substring(1);
			//l ogs.grava(s);
			s = "/"+str.leftAt(s,"/")+"/";
			//l ogs.grava(s);
			String s1 = (String)www_vdirs.get(s);
			//l ogs.grava(s1);
			if (s1!=null) get = s1+geto.substring(s.length()-1);
		}

		//é servlet...?
		for (Enumeration e = servlets.elements() ; e.hasMoreElements() ;) {
			String s[] = (String[])e.nextElement();
			//l ogs.grava("servlet","TESTANDO geto="+geto);
			if (str.equals(geto,s[1])) {
				//l ogs.grava("servlet","detect servlet="+get);
				ext = ".class";
				geto = s[2];
				servlet = true;
				servletN = s[0];
				break;
			}
		}

		classe = ((ext.equals(".class")||ext.equals(".obj")) 
			&& get.indexOf("applets")<0);
		if (classe) return;

		//arq = new Arq(URLDecoder.decode(get));
		arq = new File(get);
		if (!arq.exists() || !arq.canRead()) {
			String ss = str.UnEscape(get)+": Arquivo não existe\t'"
				+geto+"'\t'"+get+"'\t"+pd.get("referer")+"\t"+sp;
			logs.grava("servidor","nexiste="+ss);
			resp(httpVer+" 404 Not Found");
			o.println();
			o.println("<html><body>Http: Não existe!!</body></html>");

			rodando = false;
			erro = true;
		} if (!arq.isDirectory()) {
			return;
		}
		
		//é diretório
		//ogs.grava("é diretório "+get+" o="+geto);
		if ( get.charAt(get.length()-1) != '/' ) {
			//ogs.grava("vai redir "+get);
			get = get.substring(www_root.length())+"/";
			movTemp(get,null);
			rodando = false;
			return;
		}
		//ogs.grava("dir ok, tem index? "+get+" ="+arq+"/index.html");
		if ((arq=new File(arq+"/index.html")).exists()) {
			//ogs.grava("TEM "+get+" ="+arq);
			get = get+"/index.html";
			ext = ".html";
			return;
		}

		//vai mostrar dir
		//ogs.grava("vai mostrar dir "+get+" ="+arq);
		getIsDir = true;
		
	}
	/********************************
	//analiza pedido - ate 2020/set
	void analizaPedido() {
		get = ""+get1; //(String)pd.get("?endereco");
		//fazer isto no caso de ser arquivo,
		//senão os parametros devem ser decoded 1 a 1 
		//get = URLDecoder.decode(get); 
		int i = get.indexOf('?');
		if (i!=-1) {
			pedido.setGet(get.substring(i+1,get.length()));
			get = get.substring(0,i);
		}

		//segurança, descoberta fábio
		//não permitir endereços com ..
		//o que permite ler toda a unidade
		//em caso do serv ter permissão para isto
		//talvez tenha outras possibilidades... 
		if (get.indexOf("..")>-1) {
			o.println("Erro, pedido inválido...");
			geto = "ERRO, uso de .. "+get;
			erro = true;
			return;
		}

		//ver extenção
		ext = "";
		i = get.lastIndexOf(".");
		int i1 = get.lastIndexOf("/");
		if (i>-1 & i1<i) {
			ext = get.substring(i,get.length());
			//l ogs.grava("ext -> "+ext);
		}

		geto = get;
		get = www_root+get;
		//diretório virtual?
		if (www_vdirs!=null) {
			String s = geto.substring(1);
			//l ogs.grava(s);
			s = "/"+str.leftAt(s,"/")+"/";
			//l ogs.grava(s);
			String s1 = (String)www_vdirs.get(s);
			//l ogs.grava(s1);
			if (s1!=null) get = s1+geto.substring(s.length()-1);
		}

		if (ext=="") {
			if ((new File(get + "/index.html")).exists()) {
				ext = ".html";
				get = get + ((get.substring(get.length()-1,get.length()).equals("/")) ? "" : "/")+"index.html";
			} else {
				//é servlet...?
				for (Enumeration e = servlets.elements() ; e.hasMoreElements() ;) {
					String s[] = (String[])e.nextElement();
					//l ogs.grava("servlet","TESTANDO geto="+geto);
					if (str.equals(geto,s[1])) {
						//l ogs.grava("servlet","detect servlet="+get);
						ext = ".class";
						geto = s[2];
						servlet = true;
						servletN = s[0];
						break;
					}
				}
				if (ext=="") {
					ext = "dir";
				}
			}
		}

		classe = ((ext.equals(".class")||ext.equals(".obj")) 
			&& get.indexOf("applets")<0);

		//existe arquivo?
		if (!classe) {
			//arq = new Arq(URLDecoder.decode(get));
			arq = new File(str.UnEscape(get));
			if (!arq.exists() || !arq.canRead()) {
				String ss = str.UnEscape(get)+": Arquivo não existe\t'"
					+geto+"'\t'"+get+"'\t"+pd.get("referer")+"\t"+sp;
				logs.grava("servidor","nexiste="+ss);
				resp(httpVer+" 404 Not Found");
				o.println();
				o.println("<html><body>Http: Não existe!!</body></html>");

				rodando = false;
				erro = true;
			}
		}
	}*/
	//********************************
	//arq Modificado
	private void arqModificado() {
		String s = (String)pd.get("if-modified-since");
		if (s!=null) {
			if (s.indexOf(";")>-1) s = str.leftAt(s,";");
			//rever falta verif tamanho
			if (s.equals(data.strHttp(arq.lastModified()))) {
				resp(httpVer+" 304 Not Modified");
				o.println();

				rodando = false;
			}
		}
	}
	//********************************
	//resposta padrao
	void resp(String ln1) {
		o.println(ln1);
		o.print(respp());
	}
	//********************************
	//resposta padrao
	protected String respp() {
		return //"Server: Guarani 1.1"+lf+
									"Date: "+data.strHttp()+lf;
	}
	//********************************
	//mostra dir
	private void mostradir(String dir,String geto) {
		String d = dir.substring(www_root.length());
		pedido.on("<html><head><title>Diretório: "+d+"</title>"
			+"\n<script src=\"/js/func.js\"></script>"
			+"\n<script src=\"/js/funcoes.js\"></script>"
			+"\n<script src=\"/js/funcoes1.js\"></script>"
			+"\n<script>setTimeout(function(){var t=document.querySelector('table',['ad','da','da']);(new tabelaSort(t)).sort(2);},100);</script>"
			+"\n<script src=/js/jsCSSEditor/jsCSSEditor.js></script>"
			+"\n</head>"
			+"\n<LINK REL=\"StyleSheet\" HREF=\"/estilos/acessorios.css\">"
			+"\n<body onClick=\"jsCSSEditor(this,event);\">"
			+"\n<h3>Diretório: "+d+"</h3>"
			+"<hr>"
			+"\n<table class=\"httpMostraDir\"><tr><th>nome<th>tam<th>data"
		);

		File[] f = (new File(dir)).listFiles();
		if (f == null) {
			pedido.println("<h1>ERRO dir ("+dir+") não existe!</h1>");
			return;
		}
		int tm=60;
		for (int i=0;i<f.length;i++) {
			String e = str.dir(geto)+f[i].getName();
			String e1 = str.substrRat(e,"/");
			if (e.length()>tm) {
				String s = e1;
				e1 = "";
				for (int p=0;p<s.length();p+=tm) { 
					e1 += "<br>"+s.substring(p,Math.min(p+tm,s.length()));
				}
				e1 = e1.substring(4);
			}
			if (f[i].isDirectory()) {
				pedido.println("<tr><td><a href=\""+e+"\">"+e1+"</a>"
					+"<td align=right>0<td align=center>dir"
				);
			} else {
				pedido.println("<tr><td><a href=\""+e+"\">"+e1+"</a>"
					+"<td align=right>"+f[i].length()
					+"<td>"+data.strSql(f[i].lastModified())
				);
			}

			//finalizar?
			if (!rodando) {
				break;
			}

		}

		pedido.println("</table><hr></body></html>");
	}
	//********************************
	//manda htm
	// java parece ineficiente no IO... rede ou disco ? linux ou windows ? open ou oracle ?
	// talvez pondo um sleep que é mal feito no IO
	public boolean mandaArq(String nArq,String nome,boolean attach) {
		/* 	If-Range
			Content-Range
			Content-Type
			206 Partial Content
			416 Range Not Satisfiable
			===> uteis
			Accept-Ranges: bytes
			Content-Range: bytes 0-2472168733/2472168734
			Content-Range: bytes 2472157854-2472168733/2472168734
			...
			Range: bytes=0-
			Range: bytes=2472157854-
		 */
		if (nome==null) {
			nome = str.substrRat(nArq,"/");
		}
		if (nArq.indexOf("..")!=-1) {
			logs.grava("seguranca","ataque-ERRO, Http.mandaArq "+nArq+" ped="+pedido);
			logs.grava("baixa","ataque-ERRO, Http.mandaArq "+nArq+" ped="+pedido);
			return false;
		}
		String ext = "."+(str.substrRat(nome,".").toLowerCase());
		File arq = new File(nArq);
		if (!arq.exists()) {
			resp(httpVer+" 404 Not Found");
			o.println();
			o.println("<html><body>Http: Não existe!!</body></html>");
			return false;
		}
		//mime type
		String tp = ""+Guarani.tipos.getTipo(ext);
		if (str.equals(tp,"text/")) {
			tp += "; charset="+charset;
		}
		//range?
		String rg = (String)pd.get("range");
		long pi=0,pf=0;
		if (rg!=null) {
			rg = str.trimm(str.substrAt(rg,"="));
			pi = str.longo(str.leftAt(rg,"-"),0);
			pf = str.longo(str.substrAt(rg,"-"),arq.length());
		}
		pedido.cab = httpVer+(rg==null?" 200 OK":" 206 Partial Content")+lf
			+ "Content-Type: "+tp+lf
			+ (attach?"Content-Disposition: attachment; filename=\""+nome+"\";"+lf:"")
			+ "Last-Modified: "+data.strHttp(arq.lastModified())+lf
			+ "Accept-Ranges: bytes"+lf
			+ (rg==null
				? 	"Content-Length: "+arq.length()+lf
				:	"Content-Length: "+(pf-pi+1)+lf
					+"Content-Range: bytes "+pi+"-"+(pf-1)+"/"+arq.length()+lf
			  )
			+ "Connection: close"+lf
		;
		//ogs.grava(ext+"="+pedido.cab+"\n===>"+pd);
		o.print(pedido.cab+lf);
		int env=0,nv=0;
		long t = data.ms();
		try {
			//OutputStream oo = sp.getOutputStream();
			InputStream r = new FileInputStream(""+arq);
			if (pi!=0) {
				r.skip(pi);
			}
			int read = 0;
			if (rg==null) {
				while (!o.checkError() & (read = r.read(buf,0,tBf)) != -1) {
					//oo.write(buf, 0, read);
					//oo.flush();
					//logs.grava("vai ARQ tam="+read);
					//o.print(new String(buf,0,read));
					env += read;
					nv++;
					o.write(buf,0,read);
				}
			} else {
				long te = pf-pi+1;
				while (!o.checkError() & env<te & (read = r.read(buf,0,tBf)) != -1) {
					env += read;
					nv++;
					if (env>te) {
						o.write(buf,0,(int)(te-env+read));
						break;
					} else {
						o.write(buf,0,read);
					}
				}
			}
			r.close();
		} catch (IOException e ) {
			nBytes += env;
			logs.grava("baixa","ERRO, Http.mandaArq "+nArq
				+" e="+e+" bytes Env="+env+" usu="+pedido //str.erro(e)
			);
			o.println("ERRO ARQ "+nArq+"<br>");
			//o.println(e.toString());
			return false;
		}
		//logs.grava("baixa","env="+env+" nv="+nv+" bloco="+(env/nv));
		/*logs.grava("baixa","OK, Http.mandaArq "
			+" by/segs: "+num.format(env*1.0/(data.ms()-t),1)
			+" by len / env: "+num.format(arq.length(),0)+" / "+num.format(env,0)
			+" "+nArq
			+ " - Last-Modified: "+data.strHttp(arq.lastModified())
			+" ped="+pedido.ip+" "
		);
		*/ 
		return true;
	}
	/********************************
	//exec cgi
	private void execCgi(String cgi) {
		executa e = new executa();
		o.println(str.substrAt(e.getOut(),lf+lf));
	}
	*/
	//********************************
	public String getPedido() {
		String r = "";
		if (rodando) {
			if (geto.length()==0) {
				r =  get1+"<br>"+sp+"<br>"+getSocketStatus();
			} else {
				r = geto+"<br>"+sp+"<br>"+getSocketStatus();
			}
		} else {
			r = geto;
		}
		if (pedido!=null) {
			r += " pedido.h="+pedido.h;
		}
		return r;
	}
	//********************************
	private String getSocketStatus() {
		String r = "";
		try {
			//gcc##
			//r += " getKeepAlive()="+sp.getKeepAlive();
			if (sp!=null) {
				r =" getReceiveBufferSize()="+sp.getReceiveBufferSize()
						+" getSendBufferSize()="+sp.getSendBufferSize()
						+" getSoLinger()="+sp.getSoLinger()
						+" getSoTimeout()="+sp.getSoTimeout()
						+" getTcpNoDelay()="+sp.getTcpNoDelay();
						//gcj+" getKeepAlive()="+sp.getKeepAlive();
			} else {
				r = "auth ssl? sp==null";
			}
		} catch (java.net.SocketException e) {
			r += "<br>ERRO: "+e;
		}
		return r;
	}
}





/*
 

continue download file 
 

 GET /player/dados/fila/1169~Neobratimost.2002.BDRip-AVC_Sborka_grab777.mkv HTTP/1.1
User-Agent: Wget/1.20.3 (linux-gnu)
Accept: *'/*
Accept-Encoding: identity
Host: 10.11.12.5:8080
Connection: Keep-Alive

HTTP/1.1 200 OK
Content-Type: video/x-matroska
Accept-Ranges: bytes
ETag: "2909173415"
Last-Modified: Sat, 31 Oct 2020 09:47:48 GMT
Content-Length: 2472168734
Date: Thu, 05 Nov 2020 13:36:07 GMT
Server: lighttpd/1.4.53
=======================================================================================
GET /player/dados/fila/1169~Neobratimost.2002.BDRip-AVC_Sborka_grab777.mkv HTTP/1.1
Range: bytes=128148333-
User-Agent: Wget/1.20.3 (linux-gnu)
Accept: *'/*
Accept-Encoding: identity
Host: 10.11.12.5:8080
Connection: Keep-Alive

HTTP/1.1 206 Partial Content
Content-Type: video/x-matroska
Accept-Ranges: bytes
ETag: "2909173415"
Last-Modified: Sat, 31 Oct 2020 09:47:48 GMT
Content-Range: bytes 128148333-2472168733/2472168734
Content-Length: 2344020401
Date: Thu, 05 Nov 2020 13:38:20 GMT
Server: lighttpd/1.4.53
==========
mpv
=========
GET /player/dados/fila/1169~Neobratimost.2002.BDRip-AVC_Sborka_grab777.mkv HTTP/1.1
User-Agent: libmpv
Accept: *'/*
Range: bytes=0-
Connection: close
Host: 10.11.12.5:8080
Icy-MetaData: 1
==========
HTTP/1.1 206 Partial Content
Content-Type: video/x-matroska
Accept-Ranges: bytes
ETag: "2909173415"
Last-Modified: Sat, 31 Oct 2020 09:47:48 GMT
Content-Range: bytes 0-2472168733/2472168734
Content-Length: 2472168734
Connection: close
Date: Thu, 05 Nov 2020 14:34:07 GMT
Server: lighttpd/1.4.53
=====
GET /player/dados/fila/1169~Neobratimost.2002.BDRip-AVC_Sborka_grab777.mkv HTTP/1.1
User-Agent: libmpv
Accept: *'/*
Range: bytes=2472157854-
Connection: close
Host: 10.11.12.5:8080
Icy-MetaData: 1

HTTP/1.1 206 Partial Content
Content-Type: video/x-matroska
Accept-Ranges: bytes
ETag: "2909173415"
Last-Modified: Sat, 31 Oct 2020 09:47:48 GMT
Content-Range: bytes 2472157854-2472168733/2472168734
Content-Length: 10880
Connection: close
Date: Thu, 05 Nov 2020 14:43:34 GMT
Server: lighttpd/1.4.53

 
 
 
 
 
 
 
 
 
 
 	ver página: http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.txt

	The individual values of the numeric status codes defined for
			HTTP/1.1, and an example set of corresponding Reason-Phrase's, are
			presented below. The reason phrases listed here are only
			recommended -- they may be replaced by local equivalents without
			affecting the protocol. These codes are fully defined in Section 9.

							Status-Code    = "100"   ; Continue
																						| "101"   ; Switching Protocols
																						| "200"   ; OK
																						| "201"   ; Created
																						| "202"   ; Accepted
																						| "203"   ; Non-Authoritative Information
																						| "204"   ; No Content
																						| "205"   ; Reset Content
																						| "206"   ; Partial Content
																						| "300"   ; Multiple Choices
																						| "301"   ; Moved Permanently
																						| "302"   ; Moved Temporarily
																						| "303"   ; See Other
																						| "304"   ; Not Modified
																						| "305"   ; Use Proxy
																						| "400"   ; Bad Request
																						| "401"   ; Unauthorized
																						| "402"   ; Payment Required
																						| "403"   ; Forbidden
																						| "404"   ; Not Found
																						| "405"   ; Method Not Allowed
																						| "406"   ; None Acceptable
																						| "407"   ; Proxy Authentication Required
																						| "408"   ; Request Timeout
																						| "409"   ; Conflict
																						| "410"   ; Gone
																						| "411"   ; Length Required
																						| "412"   ; Unless True
																						| "500"   ; Internal Server Error
																						| "501"   ; Not Implemented
																						| "502"   ; Bad Gateway
																						| "503"   ; Service Unavailable
																						| "504"   ; Gateway Timeout
																						| extension-code


	*/
