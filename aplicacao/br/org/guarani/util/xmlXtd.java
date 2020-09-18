package br.org.guarani.util;

/*
	signey nov/2006
	nome 1: //Easy Data Extractor = EDX
	nome 2: extractor of data = Xdt =  9
	nome 3: "data extractor" = dtX = 234.000 
*/


import java.util.*;
import java.text.*;
import java.io.*;
import br.org.guarani.util.*;
import java.lang.reflect.*;


//***************************************
//***************************************
public class xmlXtd extends xmlTag {
	public String xDel = "x:";
	public xmlTag dd;
		
	//OO
	String aqOO,dirT;
	//***************************************
	//informa erro
	public void erroSintax(xmlOut xO,String s) {
		xO.erro("sintax: "+s);
	}
	//***************************************
	//informa conteudo
	public void erroConteudo(xmlOut xO,String s) {
		xO.erro("conteudo: "+s);
	}
	//***************************************
	//executa
	public String execOO(String oo,xmlOut xO,xmlTag dd) {
		this.dd = dd;
		aqOO = xO.dirX+"/"+oo;
		
		dirT = arquivo.nomeTmp("1",".oo");
		(new File(dirT)).delete();
		(new File(dirT)).mkdirs();
		xO.on("<hr>"+dirT);
		arquivoZip z = new arquivoZip(aqOO);
		z.extrai(dirT);
		xO.on("<hr>"+z.sErro);
		
		
		
		String ax = dirT+"/content.xml";
		((new xmlParser(ax)).parse()).grava(ax+"-O");
		convEncod.convArq(ax,"utf-8","iso-8859-1");
		(new arquivo(ax)).gravaTxt(str.troca((new arquivo(ax)).leTxt(),"UTF-8","ISO-8859-1"));
		
		xO.on("<hr>"+dirT+" "+ax);
		xmlOO x = (xmlOO)(new xmlParser(ax)).parse("br.org.guarani.util.xmlOO");
		x.procOO(xO);
		
		for (int i=0;i<xO.pi.size();i++) {
			Object o[] = (Object[])xO.pi.get(""+i);
			xO.on("<hr>Ins: ");
			x.insSubTree((xmlOO)o[0],(xmlOO)o[1],((String)o[2]).equals("Before"));
		}
		
		
		x.gravaSubTags(new xmlOut("/tmp/res.xml"),0);
		x.gravaSubTags(new xmlOut(ax),0);
		
		
		/*if (nome==null) {
			gravaSubTags(xO,0);
		}
		gravaTag(xO,0);
		*/
		
		return ax;
		
	}
	//***************************************
	//executa
	public boolean exec(xmlOut xO) {
		boolean r = exec(xO,null);
		xO.close();
		return r;
	}
	//***************************************
	//executa
	public boolean exec(xmlOut xO,xmlTag dd) {
		this.dd = dd;
		boolean r = false;
		if (nome==null) {
			r = gravaSubTags(xO,0);
		} else {
			r = gravaTag(xO,0);
		}
		xO.close();
		return r;
	}
	//***************************************
	//formata
	public String Xformat(String s,String f) {
		if (f==null) {
			return s;
		}
		if (f.charAt(0)=='!') {
			if (f.equals("!m")) {
				s = s.toLowerCase();
			} else	if (f.equals("!M")) {
				s = s.toUpperCase();
			}
			return s;
		} else {
			String r = (new DecimalFormat(f)).format(str.duplo(s,-1));
			//logs.grava("f="+f+" v="+s+" r="+r);
			return r;
		}
	}
	//***************************************
	//calculadora
	public String Xcalc(xmlOut xO,String s) {
		//logs.grava("calc="+s);
		String v[] = str.palavraA(s," ");
		calc cc = new calc();
		String vv;
		for (int i=0;i<v.length;i++) {
			if (!str.vazio(v[i])) {
				char c = v[i].charAt(0);
				if (c=='$') {
					vv = (String)xO.get(v[i].substring(1));
				} else if ((c>='0' && c<='9') || c=='+' || c=='-' || c=='*' || c=='/' || c=='%') {
					vv = v[i];
				} else {
					vv = dd.getAtr(v[i]);
				}
				cc.empil(vv);
				//logs.grava(i+"==="+v[i]+"=="+vv);
			}
		}
		vv = ""+cc.res();
		//logs.grava(vv+" "+Xformat(vv,"0000.0000"));
		return vv;
	}
	//***************************************
	//testa if de tags
	public boolean Xif(xmlXtd m,xmlTag d,xmlOut xO) {
		xmlTag x = m.dd;
		m.dd = d;
		boolean r = m.Xif(xO);
		m.dd = x;
		return r;
	}
	//***************************************
	//testa if de tags
	public boolean Xif(xmlOut xO) {
		String k = getAtr(":name");
		if (k!=null && !k.equals(dd.nome)) {
			return false;
		}
		boolean not = false,r=true;
		String kc;
		for (Enumeration e=atr.keys();r && e.hasMoreElements();) {
			k = (String)e.nextElement();
			if (k.charAt(0)!=':') {
				kc = k;
				not = k.charAt(0)=='!';
				if (not) {
					kc = k.substring(1);
				}
				String p1 = kc;
				String p2 = getAtr(k);
				
				String v1 = Xatr(xO,p1);
				String v2 = Xatr(xO,p2);
				
				//xO.on("<br>p1="+p1+" v1="+v1+" p2="+p2+" v2="+v2);
				
				if (p2.length()>0 && p2.charAt(0)==':') {
					if (p2.indexOf(":"+v1+":")==-1) {
						r = false;
					}
				} else {
					if (v2==null || !v2.equals(v1)) {
						r = false;
					}
				}
				
				if (not) {
					r = !r;
				}
				
			}
			//logs.grava("v="+v+" v1="+v1+" k="+k+" kc="+kc+" r="+r);
		}
		return r;
	}
	//***************************************
	//testa if de tags
	public boolean lixo_Xif(xmlTag m,xmlTag d,xmlOut xO) {
		String k = m.getAtr(":name");
		if (k!=null && !k.equals(d.nome)) {
			return false;
		}
		boolean not = false,r=true;
		String kc;
		for (Enumeration e=m.atr.keys();r && e.hasMoreElements();) {
			k = (String)e.nextElement();
			if (k.charAt(0)!=':') {
				String v = m.getAtr(k);
				
				//falta - no if o nome pode ser var $ @ .. - usar xatr?
				if (v!=null && v.length()>0) {
					if (v.charAt(0)=='@') {
						v = xO.ped.getString(v.substring(1));
					}	if (v.charAt(0)=='$') {
						v = (String)xO.get(v.substring(1));
					}
				}
				kc = k;
				//not
				not = k.charAt(0)=='!';
				if (not) {
					kc = k.substring(1);
				}
				String v1=null;
				if (kc.charAt(0)=='@') {
					//parametro...
					v1 = ""+xO.ped.getString(kc.substring(1));
					if (!v1.equals(v)) {
						r = false;
					}
					//logs.grava("v="+v+" v1="+v1+" r="+r+" not="+not);
				} else {
					if (v.length()>0 && v.charAt(0)==':') {
						if (v.indexOf(":"+d.getAtr(k)+":")==-1) {
							r = false;
						}
					} else {
						v1 = d.getAtr(kc);
						if (v1==null || !v1.equals(v)) {
							r = false;
						}
					}
				}
				if (not) {
					r = !r;
				}
			}
			//logs.grava("v="+v+" v1="+v1+" k="+k+" kc="+kc+" r="+r);
		}
		return r;
	}
	//***************************************
	//retorna atr formatado
	public String Xatr(xmlOut xO,String n) {
		char c = n.charAt(0);
		String v=null;
		if (c=='@') {
			v = xO.ped.getString(n.substring(1));
		} else if (c=='$') {
			//xO.on(n+" "+xO.get(n.substring(1)));
			v = (String)xO.get(n.substring(1));
		} else if (c=='!') {
			if (n.length()==1) {
				v = " ";
			} else {
				v = n.substring(1);
			}
		} else if (c==':') {
			if (str.equals(n,":calc ")) {
				v = Xcalc(xO,str.substrAt(n," "));
			} else	if (str.equals(n,":concat ")) {
				String a[] = str.palavraA(str.substrAt(str.trimm(n)," ")," ");
				v = "";
				for (int i=0;i<a.length;i++) {
					if (a[i].equals("!")) {
						v += " ";
					} else if (!str.vazio(a[i])) {
						v += str.trimm(Xatr(xO,a[i]));
					}
				}
			} else	if (n.equals(":name")) {
				v = dd.nome;
			} else	if (str.equals(n,":exec ")) {
				v = str.substrAt(n," ");
				try {
					Method m = dd.getClass().getMethod(v,new Class[]{xmlTag.class});
					//deb(xO,"m="+m);
					v = (String)m.invoke(dd,new Object[]{dd});
				} catch (Exception e) {
					v = "#ERRO 1 Xatr:exec "+v+" "+e+"#";
					logs.grava("ERRO: "+v+" "+str.erro(e));
				}
			} else	if (str.equals(n,":execO ")) {
				v = str.substrAt(n," ");
				String vp[] = new String[]{"0","1"};
				Class vc[] = new Class[]{};
				if (v.indexOf(" ")!=-1) {
					vp = str.palavraA(str.substrAt(v," ")," ");
					vc = new Class[vp.length];
					for (int i=0;i<vp.length;i++) {
						vp[i] = Xatr(xO,vp[i]);
						vc[i] = String.class;
					}
					v = str.leftAt(v," ");
				}
				Method m = null;
				try {
					m = xO.getClass().getMethod(v,vc);
					//deb(xO,"m="+m);
					v = (String)m.invoke(xO,vp);
				} catch (Exception e) {
					v = "#ERRO 2 Xatr:execO m="+v+" er="+e+" mo="+m+"#";
					logs.grava("ERRO: "+v+" "+str.erro(e));
				}
			} else {
				v = "#"+n+"#";
			}

		} else if (dd!=null) {
			v = dd.getAtr(n);
		}
		if (v==null) {
			v = "#null#";//+dd.nome; //+dd.atr;
		}
		String f = getAtr(":format");
		if (f==null) {
			return v;
		}
		return Xformat(v,f);
	}
	//***************************************
	//processa 
	public boolean X(xmlOut xO,int nv) {
		if (nome==null || !str.equals(nome,xDel)) {
			//propaga x
			//logs.grava("X "+nome+" <> null");
			return false;
		}
		
		String n = str.substrAt(nome,":");
		if (n.equals("tags")) {
			String a = getAtr(":action","");
			int limite = str.inteiro(getAtr(":limit"),-1),tv=0;
			//deb(xO,"lim="+limite);
			/*if (dd==null) {
				logs.grava("erro dd: "+dd);
				return false;
			} if (dd.tg == null) {
				logs.grava("erro tg: "+dd.nome);
				return false;
			}
			*/
			for (int i=0;i<dd.tg.size();i++) {
				if (limite!=-1 && tv>limite) {
					//xO.on("<br>LIMITE EST="+limite);
					break;
				}
				xmlTag y = dd.get(i);
				//logs.grava("X tags "+getAtr("name")+" == "+y.nome);
				if (Xif(this,y,xO)) {
					tv++;
					if (a.equals("delete")) {
						y.tg = new Hashtable();
					} else {
						//pra cada subtag do modelo aplica a tag dados e mostra...?
						//logs.grava("X tags "+getAtr("name")+" == "+y.size());
						for (int i1=0;i1<tg.size();i1++) {
							xmlXtd y1 = (xmlXtd)get(i1);
							y1.dd = y;
							y1.gravaTag(xO,nv);
						}
					}
				}
			}
			
		} else if (n.equals("if")) {
			if (Xif(this,dd,xO)) {
				for (int i=0;i<tg.size();i++) {
					xmlXtd y = (xmlXtd)get(i);
					y.dd = dd;
					y.gravaTag(xO,nv);
				}
			}
		
		} else if (n.equals("attr")) {
			xO.on("<span class=\""+getAtr("name")+"\">"+Xatr(xO,getAtr("name"))+"</span>",nv);
			
		} else if (n.equals("expr")) {
			for (Enumeration e = atr.elements(); e.hasMoreElements(); ) {
				String s = (String)e.nextElement();
				xO.on(Xatr(xO,s),nv);
			}
			xO.on(texF);
			
		} else if (n.equals("tag")) {
			String s = getAtr("set");
			try {
				if (s!=null) {
					xO.buf(true);
					xO.o("<"+tex);
				} else {
					xO.on("<"+tex,nv);
				}
				for (int i=0;i<tg.size();i++) {
					xmlXtd y = (xmlXtd)get(i);
					y.dd = dd;
					y.gravaTag(xO,-99);
				}
				if (s!=null) {
					xO.o(">");
					String v = xO.buf(false);
					xO.set(s,v);
				} else {
					xO.o(">");
					if (!str.vazio(texF)) {
						xO.o(str.trimm(texF));
					}
				}
			} catch (Exception e) {
				logs.grava("xmlTag.X:tag ERRO: ",str.erro(e));
			}
		
		} else if (n.equals("set")) {
			for (Enumeration e=atr.keys();e.hasMoreElements();) {
				String k = (String)e.nextElement();
				if (!k.equals(":format")) {
					//2010 Xatr já formata = xO.set(k,Xformat(Xatr(xO,getAtr(k)),getAtr(":format")));
					xO.set(k,Xatr(xO,getAtr(k)));
				}
			}
			//xO.on(texF,nv);
			
		} else if (n.equals("setX")) {
			String no = getAtr(":name");
			String ac = getAtr(":action");
			Object ob[] = (Object[])xO.get(no);
			xmlTag xm = null;
			if (ob!=null) {
				xm = (xmlTag)ob[1];
			}
			if (ac==null) {
				xmlTag xu = (xmlTag)ob[2];
				if (getAtr(":modo")==null) {
					for (Enumeration e=atr.keys();e.hasMoreElements();) {
						String k = (String)e.nextElement();
						if (k.charAt(0)!=':') {
							//xu.putAtr(k,Xformat(Xatr(xO,getAtr(k)),getAtr(":format")));
							xu.putAtr(k,Xatr(xO,getAtr(k)));
						}
					}
				} else {
					for (Enumeration e=atr.keys();e.hasMoreElements();) {
						String k = (String)e.nextElement();
						if (k.charAt(0)!=':') {
							String v = xu.getAtr(k);
							xu.putAtr(k, ""+(str.duplo(Xatr(xO,getAtr(k)),0)+str.duplo(v,0)) );
						}
					}
				}
				if (!str.vazio(texF)) {
					xO.on(texF,nv);
				}
			} else if (ac.equals("tag")) {
				String n1 = getAtr(":tagName");
				ob[2] = new xmlTag(n1==null?no:n1);
				xm.put((xmlTag)ob[2]);
			} else if (ac.equals("new")) {
				//hash conteúdo 1=hash index 2=tag 3=ult tag
				xO.set(no,new Object[]{new Hashtable(),new xmlTag(no),null});
				//falta grv attr
			} else if (ac.equals("sort")) {
				xm.sort(getAtr(":atr"), getAtr(":order"));
			} else if (ac.equals("with")) {
				//xmlTag d = dd;
				//dd = xm;
				for (int d=0;d<xm.size();d++) {
					for (int i=0;i<tg.size();i++) {
						xmlXtd y = (xmlXtd)get(i);
						y.dd = xm.get(d);
						y.gravaTag(xO,nv);
					}
				}
				//dd = d;
			} else if (ac.equals("seek")) {
				Hashtable h = (Hashtable)ob[0];
				String k = getAtr(":key");
				if (k==null) {
					erroSintax(xO,"seek sem campo :key");
				}
				//recupera valor da chave
				String v = Xatr(xO,k);
				if (v==null) {
					erroConteudo(xO,"seek val :key=null");
				}
				ob[2] = h.get(v);
				if (ob[2]==null) {
					//xO.on("<br>chNova: "+v);
					String b = getAtr(":tagName");
					ob[2] = new xmlTag(b==null?no:b);
					h.put(v,ob[2]);
					xm.put((xmlTag)ob[2]);
				}
				
				xmlTag x = (xmlTag)ob[2];
				String s = getAtr(":set");
				if (s==null) {
					//por padrão seta todos attr da tag atual na tag encontrada/criada
					for (Enumeration e=atr.keys();e.hasMoreElements();) {
						String k1 = (String)e.nextElement();
						if (k1.charAt(0)!=':') {
							//xO.on("<br>"+k1+"="+Xatr(xO,getAtr(k1))+" "+x.atr+" k="+k+" v="+v);
							x.putAtr(k1,Xatr(xO,getAtr(k1)));
						}
					}
				} else if (s.equals("")) {
					//por padrão seta todos attr da tag encontrada/criada na tag atual
					for (Enumeration e=x.atr.keys();e.hasMoreElements();) {
						String k1 = (String)e.nextElement();
						if (k1.charAt(0)!=':') {
							//xO.on("<br>"+k1+"="+x.atr+" k="+k1+" v="+v);
							dd.atr.put(k1,x.atr.get(k1));
						}
					}
				} else {
					//xO.on("attr="+x.atr+" "+getAtr(":attr")+" v="+v);//+" h="+h);
					String at = getAtr(":attr");
					if (at==null) {
						erroSintax(xO,"seek faltou definir :attr");
					}
					try {
						xO.set(s,x.getAtr(at));
					} catch (Exception e) {
						xO.on(getAtr(":attr")+" v="+v+" attr="+x.atr);
					}
				}
				
			} else if (ac.equals("index")) {
				String k = getAtr(":key");
				Hashtable h = (Hashtable)ob[0];
				for (int i=0;i<xm.size();i++) {
					xmlTag x = xm.get(i);
					String s = x.getAtr(k);
					if (s!=null) {
						h.put(s,x);
					}
				}
				
			} else if (ac.equals("out")) {
				xm.gravaTag(xO,nv);
				
			} else {
				deb(xO,"xmlXtd:X:setX action="+ac);
				
			}
			
		} else if (n.equals("with")) {
			String ar = getAtr(":file");
			String a = getAtr(":action");
			//xO.on(f);
			if (ar!=null) {
				ar = Xatr(xO,ar);
				//deb(xO,"AR="+ar+" ac="+a);
				if (a!=null && a.equals("append")) {
					arquivo1 ap = new arquivo1(xO.nameXml(ar));
					ap.append(Xatr(xO,getAtr(":text"))+"\n");
					ap.fecha();
					return true;
				} else if (a!=null && a.equals("load")) {
					//2010 xmlTag x = (new xmlParser(xO.nameXml(ar))).parse(getAtr(":class"));
					xmlTag x = xO.xml(ar,getAtr(":class"));
					//logs.grava("load null arq="+ar+" "+xO.nameXml(ar));
					if (x==null) {
						//x = x.get(1);
						logs.grava("load null arq="+ar+" "+xO.nameXml(ar));
						//logs.grava("load="+x.nome+" t="+x.size());
					}
					//filtar tags?
					String tg = getAtr(":tags");
					if (tg!=null) {
						x = x.getElementsByTagName(tg);
					}
					xO.set(getAtr(":mem"),new Object[]{new Hashtable(),x,null});
					return true;
				} else if (a!=null && a.equals("delete")) {
					File dl = new File(xO.nameXml(ar));
					//xO.on("<br>Del: "+dl);
					if (dl.exists()) {
						return dl.delete();
					}
					return false;
				}
				//xmlTag aq = xO.xml(f);
				//File aq = new File(md?xO.nameXtd(f):xO.nameXml(f));
				File aq = new File(xO.nameXml(ar));
				//deb(xO,""+aq);
				xmlTag dx = dd;
				//logs.grava("a="+a);
				if (a!=null && a.equals("notExist")) {
					if (aq.exists()) {
						return true;
					}
					for (int i=0;i<tg.size();i++) {
						xmlXtd y = (xmlXtd)get(i);
						//y.dd = dx; //.get(d);
						y.gravaTag(xO,nv);
					}
					return true;
				} else {
					if (!aq.exists()) {
						if (a==null || !a.equals("exists")) {
							xO.on("<hr/>X:with nao existe Arq: <b>"+ar+"</b> = "+aq);
						}
						return true;
					}
					//if (md) {
					//	dx = xO.xtd(f);
					//} else {
						dx = xO.xml(ar,getAtr(":class"));
					//}
				}
				
				if (a==null || a.equals("exists")) {
					//processa com novos dados
					//xO.on("<br>processar Arq: "+f+" = "+xO.name);
					for (int i=0;i<tg.size();i++) {
						xmlXtd y = (xmlXtd)get(i);
						y.dd = dx; //.get(d);
						y.gravaTag(xO,nv);
					}
				} else if (a.equals("mem")) {
				} else {
					deb(xO,"ERRO x:with action="+a+" nao conhecida..."+ar);
				}
				
			} else if (getAtr(":mem")!=null) {
				xmlTag x = (xmlTag)((Object[])xO.get(getAtr(":mem")))[1];
				if (a==null) {
					//logs.grava("with mem="+x.nome+" s="+x.size()+" S1="+size());
					for (int i=0;i<tg.size();i++) {
						//logs.grava("with mem="+i);
						xmlXtd y = (xmlXtd)get(i);
						y.dd = x; //.get(d);
						y.gravaTag(xO,nv);
					}
				} else if (a.equals("save")) {
					ar = Xatr(xO,getAtr(":fileName"));
					String f = xO.dirD+"/"+ar;
					//logs.grava("save="+f);
					xmlOut xo = new xmlOut(f,xO.dirX,xO.dirD);
					if (xo.sErro==null) {
						if (getAtr(":root")!=null) {
							x.gravaTag(xo,0);
						} else {
							x.gravaSubTags(xo,0);
						}
					} else {
						xO.on("<br/>xmlTag.X:with:$:grava erro grav xml: "+f);
						logs.grava("xmlTag.X:with:$:grava erro grav xml: "+f);
					}
				} else {
					deb(xO,"x:with:mem not identif action="+a);
				}
			} else {
				deb(xO,"x:with não identif atr="+atr);
			}
			
			
		} else if (n.equals("!")) {
			
		} else if (n.equals("calc")) {
			String k = getAtr("expr");
			if (k!=null) {
				xO.on(Xformat(Xcalc(xO,k),getAtr(":format"))+texF,nv);
			} else {
				xO.on("<span class=\"erro\">x:calc no expr attribute!</span>"+texF,nv);
			}
			
		} else if (n.equals("sort")) {
			String k = getAtr("key");
			String o = getAtr("order");
			dd.sort(k,o);

		} else if (n.equals("save")) {
			xmlOut xo = new xmlOut(Xatr(xO,getAtr("file")),xO);
			xO.on("<hr>Salvando: "+Xatr(xO,getAtr("file")));
			if (getAtr("root")!=null) {
				dd.gravaTag(xo,0);
			} else {
				dd.gravaSubTags(xo,0);
			}
			
		} else if (n.equals("attrPrint")) {
			xO.on("<table>",nv);
			for (Enumeration e=dd.atr.keys();e.hasMoreElements();) {
				String k = (String)e.nextElement();
				xO.on("<tr><th>"+k+"</th><td>"+dd.getAtr(k)+"</td></tr>",nv+1);
				//l += " "+k+"=\""+getAtr(k)+"\"";
			}
			xO.on("</table>",nv);
			
		} else {
			//xO.on("<span class=\"erro\">cmd x: não processado "+n+"</span>");
			//xO.on("<span class=\""+n+"\">"+Xatr(xO,n)+"</span>"+texF,nv);
			//xO.on(Xatr(xO,n)+(str.vazio(texF)?"":str.trimm(texF)),nv);
			xO.o(Xatr(xO,n)+(str.vazio(texF)?"":str.trimm(texF,"\t\n\r")));

			//return false;
		}
		
		return true;
	}
	//***************************************
	public void deb(xmlOut xO,String s) {
		xO.on("<div class=\"xmlDebug\">"+s+"</div>");
	}
	//***************************************
	// metodos extendidos
	//***************************************
	//***************************************
	//***************************************
	public boolean gravaTag(xmlOut xO,int nv) {
		if (X(xO,nv)) {
			return true;
		}
		return super.gravaTag(xO,nv);
	}
	//***************************************
	public boolean gravaSubTags(xmlOut xO,int nv) {
		for (int i=0;i<size();i++) {
			xmlXtd xt = (xmlXtd)get(i);
			if (!xt.removida) {
				xt.dd = dd;
				xt.gravaTag(xO,nv);
			}
		}
		return true;
	}
}
