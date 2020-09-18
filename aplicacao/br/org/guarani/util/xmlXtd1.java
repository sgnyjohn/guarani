package br.org.guarani.util;

/*
	signey nov/2006
	nome 1: //Easy Data Extractor = EDX
	nome 2: extractor of data = Xdt =  9
	nome 3: "data extractor" = dtX = 234.000 
	nome 4: xsl - microsoft
*/

/*
cria var...
	<x:setX :name="reg" :action="new"/>
add tag:
	<x:setX :name="reg" :action="tag" :tagName="mun"/>
seek
	<x:setX :name="regT" :action="seek" :key="codigo" :set="rg" :attr="reg"/>


*/



import java.util.*;
import java.text.*;
import java.io.*;
import br.org.guarani.util.*;
import java.lang.reflect.*;


//***************************************
//***************************************
public class xmlXtd1 extends xmlXtd {
	//public String xDel = "x:";
	//public xmlTag dd;
		
	//OO
	//String aqOO,dirT;
	boolean downParar = false;
	//***************************************
	//recupera attributos xst em arquivo
	public void xstAttr(xmlOut xO,xmlTag dd,xmlTag x,xmlTag xu) {
		int t = dd.size();
		for (int i=0;i<t;i++) {
			xmlTag xa = dd.get(i);
			if (str.equals(xa.nome,"xsl:")) {
				if (xa.nome.equals("xsl:for-each")) {
					String e = xa.getAtr("select");
					xmlTag v = (xmlTag)x.get(str.leftRat(e,"/"));
					if (v!=null) {
						e = str.substrRat(e,"/");
						int nv = 0;
						//guarda um clone
						xmlTag xc  = xa.Clone();
						//varre subtags dos dados
						for (int y=0;y<v.size();y++) {
							if (v.get(y).nome.equals(e)) {
								//tem q duplicar 
								if (nv++==0) {
									xstAttr(xO,xa,v.get(y),xu);
								} else {
									//aplica no clone
									xstAttr(xO,xc,v.get(y),xu);
									//add filhos clonados do clone dentro xsl:for-etch
									for (int cc=0;cc<xc.size();cc++) {
										xa.put(xc.get(cc).Clone());
									}
								}
							}
						}
					}
					//regrava put(i,
				} else if (xa.nome.equals("xsl:if")) {
					//String an = xa.getAtr("test");
					//if (x.get(an)!=null) 
						xstAttr(xO,xa,x,xu);
					// else 
					//	logs.grava("não achei "+an);
					//
				} else if (xa.nome.equals("xsl:attribute")) {
					String an = xa.getAtr("name");
					xa = xa.get(0);
					if (xa.nome.equals("xsl:value-of")) {
						//String v = (String)x.getCh(str.troca(xa.getAtr("select"),"@",""),"/");
						String v = (String)x.get(xa.getAtr("select"));
						if (v!=null) {
							xu.putAtr(an,v);
						}
					}
				} else {
					xO.on("#ERRO "+xa.nome+" cmd xsl nao conhecido#");
				}
			} else {
				xstAttr(xO,xa,x,xa);
			}
		}
	}
	public void xstAttr(xmlOut xO) {
		if (dd.getAtr("xst:executado")!=null) {
			logs.grava("xst ja processado...");
			return;
		}
		String aq = getAtr(":file");
		aq = XcAtr(xO,aq);
		xmlTag x = xO.xml(aq,null);
		dd.putAtr("xst:executado","1");
		xstAttr(xO,dd,x,dd);
	}
	//***************************************
	//executa
	public String execOO(String oo,xmlOut xO,xmlTag dd) {
		this.dd = dd;
		aqOO = xO.dirX+"/"+oo;
		
		dirT = arquivo.nomeTmp("0",".oo");
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
	/***************************************
	//executa
	public boolean exec(xmlOut xO) {
		return exec(xO,null);
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
	//executa
	public boolean exec(xmlOut xO,xmlTag dd) {
		this.dd = dd;
		if (nome==null) {
			return gravaSubTags(xO,0);
		}
		return gravaTag(xO,0);
	}
	*/
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
			} else	if (str.equals(f,"!z")) {
				//logs.grava("zeros.. "+s+" f="+f);
				s = str.strZero(str.inteiro(s,0),str.inteiro(f.substring(2),5));
			}
			return s;
		} else {
			return (new DecimalFormat(f)).format(str.duplo(s,-1));
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
				if (v[i].length()==1 || (c>='0'&&c<='9') || c=='-' ) {
					//tam 1 ou nro e negativo
					cc.empil(v[i]);
				} else {
					String e = Xatr(xO,v[i]);
					//logs.grava(i+" "+v[i]+" "+e);
					cc.empil(e);
				}
			}
		}
		vv = ""+cc.res();
		if (vv.length()>2 && str.right(vv,2).equals(".0")) {
			vv = str.leftAt(vv,".");
		}
		return vv;
	}
	/***************************************
	//calculadora
	public String XcalcLIXO(xmlOut xO,String s) {
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
	*/
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
				
				String v1 = XatrF(xO,p1);
				String v2 = XatrF(xO,p2);
				
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

				//logs.grava("<br>r="+r+" not="+not+" p1="+p1+" v1="+v1+" p2="+p2+" v2="+v2);

			
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
	//retorna valor atributo de controle de tag x (nome iniciado por :)
	//se o valor dele começa com : aplica o Xatr
	public String XcAtr(xmlOut xO,String valor) {
		if (valor==null) {
			return valor;
		}
		if (valor.charAt(0)==':') {
			return XatrF(xO,valor.substring(1));
		}
		return valor;
	}
	//***************************************
	//retorna atr formatado - n é o VALOR do atr
	public String XatrF(xmlOut xO,String valor) {
		String r = Xatr(xO,valor);
		String f = getAtr(":format");
		if (f==null) {
			return r;
		}
		return Xformat(r,f);
	}
	//***************************************
	//retorna atr formatado - n é o VALOR do atr
	public String Xatr(xmlOut xO,String valor) {
		if (valor==null) {
			return "#?null?#";
		} else if (valor.length()==0) {
			return "";
		}
			
		char c = valor.charAt(0);
		String v=null;
		if (c=='@') {
			v = xO.ped.getString(valor.substring(1));
		} else if (c=='$') {
			//xO.on(n+" "+xO.get(n.substring(1)));
			v = (String)xO.get(valor.substring(1));
		} else if (c=='!') {
			if (valor.length()==1) {
				v = " ";
			} else {
				v = valor.substring(1);
			}
		} else if (c==':') {
			if (str.equals(valor,":calc ")) {
				v = Xcalc(xO,str.substrAt(valor," "));
			} else	if (str.equals(valor,":concat ")) {
				//logs.grava("conc "+valor);
				String a[] = str.palavraA(str.substrAt(str.trimm(valor)," ")," ");
				v = "";
				for (int i=0;i<a.length;i++) {
					if (a[i].equals("!")) {
						v += " ";
					} else {
						v += str.trimm(XatrF(xO,a[i]));
					}
				}
				//logs.grava("conc "+v);
			} else	if (valor.equals(":name")) {
				v = dd.nome;
			} else	if (valor.equals(":data")) {
				v = data.strSql();
			} else	if (valor.equals(":datan")) {
				v = str.troca(data.strSql(),"-","");
				v = str.troca(v,":","");
				v = str.troca(v," ","");
			} else	if (valor.equals(":text")) {
				v = dd.tex;
			} else	if (valor.equals(":textDad")) {
				v = dd.textDad();
			} else	if (str.equals(valor,":exec ")) {
				v = str.substrAt(valor," ");
				try {
					Method m = dd.getClass().getMethod(v,new Class[]{xmlTag.class});
					//deb(xO,"m="+m);
					v = (String)m.invoke(dd,new Object[]{dd});
				} catch (Exception e) {
					v = "#ERRO Xatr:exec "+v+" "+e+"#";
					logs.grava("ERRO: "+v+" "+str.erro(e));
				}
			} else	if (str.equals(valor,":execO ")) {
				v = str.substrAt(valor," ");
				String vp[] = new String[]{"0","1"};
				Class vc[] = new Class[]{};
				if (v.indexOf(" ")!=-1) {
					vp = str.palavraA(str.substrAt(v," ")," ");
					vc = new Class[vp.length];
					for (int i=0;i<vp.length;i++) {
						vp[i] = XatrF(xO,vp[i]);
						vc[i] = String.class;
					}
					v = str.leftAt(v," ");
				}
				Method m = null;
				try {
					m = xO.getClass().getMethod(v,vc);
					//logs.grava("m="+m);
					v = (String)m.invoke(xO,vp);
				} catch (Exception e) {
					v = "#ERRO Xatr:execO m="+v+" er="+e+" mo="+m+"#";
					logs.grava("ERRO: "+v+" "+str.erro(e));
				}
			} else {
				v = "#?"+valor+"?#";
			}

		} else if (dd!=null) {
			v = dd.getAtr(valor);
		}
		if (v==null) {
			//v = "#null#";//+dd.nome; //+dd.atr;
			v = "#?tag.nome="+(dd==null?"?":dd.nome)+"."+valor+"?#";//+dd.nome; //+dd.atr;
		}
		return v;
	}
	//***************************************
	// executa nas subtag c/dad...
	public void execSTags(xmlOut xO,xmlXtd xtd,xmlTag xtag,int nv) {
		if (xtag!=null) {
			for (int i=0;i<xtd.tg.size();i++) {
				xmlXtd y = (xmlXtd)xtd.get(i);
				y.dd = xtag; 
				y.gravaTag(xO,nv);
			}
		} else {
			for (int i=0;i<xtd.tg.size();i++) {
				xmlXtd y = (xmlXtd)xtd.get(i);
				y.gravaTag(xO,nv);
			}
		}
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
		if (n.charAt(0)=='!') {
		} else if (n.equals("tags")) {
			String a = getAtr(":action","");
			int limite = str.inteiro(getAtr(":limit"),-1),tv=0;
			//deb(xO,"lim="+limite);
			//deb(xO,"dd="+dd+" nome="+nome);
			
			xmlTag dd1 = dd;
			String p = getAtr(":path");
			if (p!=null) {
				dd1 = (xmlTag)dd.get(p);
				if (dd1==null) {
					xO.erro("#?path "+p+" não encontrada?#");
					dd1 = dd;
				}
			}
			
			for (int i=0;i<dd1.tg.size();i++) {
				if (limite!=-1 && tv>limite) {
					//xO.on("<br>LIMITE EST="+limite);
					break;
				}
				xmlTag y = dd1.get(i);
				//logs.grava("X tags "+getAtr("name")+" == "+y.nome);
				if (Xif(this,y,xO)) {
					tv++;
					if (a.equals("delete")) {
						y.tg = new Hashtable();
					} else {
						//pra cada subtag do modelo aplica a tag dados e mostra...?
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
			xO.on("<span class=\""+getAtr("name")+"\">"+XatrF(xO,getAtr("name"))+"</span>",nv);
			
		} else if (n.equals("expr")) {
			for (Enumeration e = atr.elements(); e.hasMoreElements(); ) {
				String s = (String)e.nextElement();
				xO.on(XatrF(xO,s),nv);
			}
			xO.on(texF);
			
		} else if (n.equals("tag")) {
			String no = XcAtr(xO,getAtr(":name"));
			if (no!=null) {
				String t = XcAtr(xO,getAtr(":text"));
				String t1 = "";
				//pega atr <> :
				for (Enumeration e = atr.keys(); e.hasMoreElements(); ) {
					String s = (String)e.nextElement();
					if (s.charAt(0)!=':') {
						t1 += " "+s+"=\""+XatrF(xO,getAtr(s))+"\"";
					}
				}
				xO.on("<"+no+t1+(t==null?"/>":">"+t+"</"+no+">"),nv);
				xO.on(str.trimm(tex+texF),nv);
			} else {
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
			}
		
		} else if (n.equals("set")) {
			for (Enumeration e=atr.keys();e.hasMoreElements();) {
				String k = (String)e.nextElement();
				if (k.charAt(0)!=':') {
					xO.set(k,XatrF(xO,getAtr(k)));
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
				//se não tem action faz...?
				xmlTag xu = (xmlTag)ob[2];
				if (getAtr(":modo")==null) {
					for (Enumeration e=atr.keys();e.hasMoreElements();) {
						String k = (String)e.nextElement();
						if (k.charAt(0)!=':') {
							xu.putAtr(k,Xformat(XatrF(xO,getAtr(k)),getAtr(":format")));
						}
					}
				} else {
					for (Enumeration e=atr.keys();e.hasMoreElements();) {
						String k = (String)e.nextElement();
						if (k.charAt(0)!=':') {
							String v = xu.getAtr(k);
							xu.putAtr(k, ""+(str.duplo(XatrF(xO,getAtr(k)),0)+str.duplo(v,0)) );
						}
					}
				}
				if (!str.vazio(texF)) {
					xO.on(texF,nv);
				}
			} else if (ac.equals("@")) {
				//explode expr param em tags de n niveis cfrme delimitadores dl1 e dl2
				String v0[] = str.palavraA(XcAtr(xO,getAtr(":val")),""+(getAtr(":delimitadores").charAt(0)));
				xmlTag x0 = new xmlTag("m");
				for (int i=0;i<v0.length;i++) {
					xmlTag x1 = new xmlTag(no);
					x0.put(x1);
					String v1[] = str.palavraA(v0[i],""+(getAtr(":delimitadores").charAt(1)));
					for (int a=0;a<v1.length;a++) {
						x1.putAtr("a"+a,v1[a]);
					}
				}
				xO.set(no,new Object[]{new Hashtable(),x0,null});
			} else if (ac.equals("locTag")) {
				//cria var mem :name com todas as tags de nome :tag
				xmlTag x = dd.getElementsByTagName(getAtr(":tag"));
				xO.set(no,new Object[]{new Hashtable(),x,null});
			} else if (ac.equals("tag")) {
				//add nova tag
				String n1 = getAtr(":tagName");
				//guarda nova como ultima tag
				ob[2] = new xmlTag(n1==null?no:n1);
				xm.put((xmlTag)ob[2]);
			} else if (ac.equals("new")) {
				//hash conteúdo 0=hash index 1=tag 2=ult tag
				xO.set(no,new Object[]{new Hashtable(),new xmlTag(no),null});
				//falta grv attr
			} else if (ac.equals("sort")) {
				xm.sort(getAtr(":atr"), getAtr(":order"));
			} else if (ac.equals("with")) {
				//pq não usar o cmd with normal...
				for (int d=0;d<xm.size();d++) {
					for (int i=0;i<tg.size();i++) {
						xmlXtd y = (xmlXtd)get(i);
						y.dd = xm.get(d);
						y.gravaTag(xO,nv);
					}
				}
			} else if (ac.equals("seek")) {
				//da seek, mas qdo indexa e qual attr?
				Hashtable h = (Hashtable)ob[0];
				String k = getAtr(":key");
				if (k==null) {
					erroSintax(xO,"seek sem campo :key");
				}	
				//recupera valor da chave
				String v = XatrF(xO,k);
				if (v==null) {
					erroConteudo(xO,"seek val :key=null");
				}			
				String seM = getAtr(":seMaior");
				if (seM==null) {
					ob[2] = h.get(v);
					if (ob[2]==null) {
						//se não encontrou, cria e grava chave
						String b = getAtr(":tagName");
						ob[2] = new xmlTag(b==null?no:b);
						h.put(v,ob[2]);
						xm.put((xmlTag)ob[2]);
					}
				} else {
					//procura por > varrendo as tags ordenadas
					double vvv = str.duplo(v,-1);
					for (int i=0;i<xm.size();i++) {
						xmlTag xa = xm.get(i);
						if (str.duplo(xa.getAtr(seM),-1)>=vvv) {
							ob[2] = xa;
							break;
						}
					}
				}
				
				//apos seta na tag encontrada todos attr sem :
				// 	OU seta uma var mem definica em :attr
				xmlTag x = (xmlTag)ob[2];
				String s = getAtr(":set");
				if (s==null) {
					//por padrão seta todos attr da tag atual na tag encontrada/criada
					for (Enumeration e=atr.keys();e.hasMoreElements();) {
						String k1 = (String)e.nextElement();
						if (k1.charAt(0)!=':') {
							String vl = XatrF(xO,getAtr(k1));
							if (k1.charAt(0)=='$') {
								k1 = (String)xO.get(k1.substring(1));
							}
							//logs.grava("k1="+k1+" vl="+vl);
							x.putAtr(k1,vl);
						}
					}
				} else if (s.equals("")) {
					//seta todos attr da tag encontrada/criada na tag atual
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
						xO.on("<br>setX :set :attr "+at+" v="+v+" attr="+x.atr);
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
					} else {
						//logs.grava("index, "+k+" cha não encontrada: "+x.tg);
					}
				}
				//logs.grava("index "+k+" size="+h.size()+" "+h);
				
			} else if (ac.equals("out")) {
				xm.gravaTag(xO,nv);
				
			} else {
				deb(xO,"xmlXtd:X:setX action="+ac);
				
			}
			
		} else if (n.equals("with")) {
			String ar = getAtr(":file");
			String a = getAtr(":action");
			//OPERAÇOES com arquivo não XML
			if (ar!=null) {
				ar = XcAtr(xO,ar);
				//deb(xO,"AR="+ar+" ac="+a+" at="+atr);
				//arquivo TXT
				if (a!=null && a.equals("append")) {
					arquivo1 ap = new arquivo1(xO.nameXml(ar));
					ap.append(XatrF(xO,getAtr(":text"))+"\n");
					ap.fecha();
					return true;
				} else if (a!=null && a.equals("delete")) {
					File dl = new File(xO.nameXml(ar));
					//xO.on("<br>Del: "+dl);
					if (dl.exists()) {
						return dl.delete();
					}
					return false;
				} else if (a!=null && a.equals("mkdir")) {
					File dl = new File(xO.dirD+"/"+ar);
					if (!dl.exists()) {
						logs.grava("mkdir: "+dl+" atr="+atr);
						dl.mkdirs();
						return false;
					}
					return true;
				} else if (a!=null && a.equals("baixa")) {
					if (downParar) {
						xO.erro(" todos down bloqueados por erro ");
						return true;
					}
					File dl = new File(xO.dirD+"/"+ar);
					arquivo1 arg = new arquivo1(""+dl);
					arg.gravaTxt("?");
					String url = getAtr(":url");
					url = XatrF(xO,url);
					//logs.grava("url="+url);
					if (xO.w == null) {
						xO.w = new web();
					}
					//verific PROXY
					String prx = (String)xO.get("proxy");
					if (str.vazio(prx) || prx.indexOf("?")!=-1) {
						//logs.grava("prx er="+prx);
						prx = xO.proxy;
					}
					//inicializa
					xO.w.init(url,prx);
					//atualiza ref c este endereço?
					xO.w.setRef = getAtr(":setRef","true").equals("true");
					//usa ref especifica?
					String refa=null,ref=getAtr(":ref");
					if (ref!=null) {
						ref = XatrF(xO,ref);
						//logs.grava("ref "+ref);
						refa = xO.w.ref;
						xO.w.ref = ref;
					}
					String s = xO.w.lePag();
					if (ref!=null) {
						xO.w.ref = refa;
					}
					//logs.grava("dl="+dl+" url="+url+" s="+s);
					if (s==null) {
						s = xO.w.lePag();
						if (s==null) {
							s = xO.w.lePag();
						}
					}
				
					//testa se ARQ Válido?
					String t = getAtr(":testa");
					if (s==null || (t!=null && s.indexOf(t)==-1)) {
						(new arquivo1("/tmp/el"+data.ms())).gravaTxt(""+s);
						xO.set("erroBx","1");
						dl.delete();
						downParar = true;
						return false;
					}
					//logs.grava("dl="+dl);
					arg = new arquivo1(""+dl);
					if (!arg.gravaTxt(s)) {
						xO.erro("#?arq="+dl+" e="+arg.sErro+"?#");
					}
					t = getAtr(":sleep");
					if (t!=null) {
						if (t.indexOf("rand")!=-1) {
							t = str.trimm(str.troca(t,"rand",""));
							t = ""+((int)Math.floor(Math.random()*str.inteiro(t,2000)));
							//logs.grava("sleep rand: "+t);
						}
						try { 
							Thread.sleep(str.inteiro(t,200));
						} catch (Exception e) {
						}
					}
					return true;
				}
				
				//arquivo XML
				xmlTag dx = dd;
				String are = xO.nameXml(ar);
				if (a!=null && a.equals("notExist")) {
					File f = new File(are);
					//logs.grava(are);
					if (f.exists()) {
						return true;
					}
					//executa sub tags...
					execSTags(xO,this,dd,nv+1);
					return true;
				}
				xmlTag x =  xO.xml(ar,getAtr(":class"));
	
				
				if (a==null && x==null) {
					xO.on("<hr/>X:with nao existe Arq: "+are+" = "+ar);
					return true;
				} else if (a==null) {
					//executa
					dx = x;
				} else if (a.equals("exists")) {
					if (x==null) {
						//se arq existe e não carregado dados...
						return true;
					}
				} else if (a.equals("load")) {
					if (x==null && getAtr(":action1","").equals("new") ) {
						x = new xmlTag(getAtr(":mem"));
					}
					if (x==null) {
						logs.grava(new Exception("load mem arq não existe: "+ar));
						return false;
					}
					//filtar tags?
					String tg = getAtr(":tags");
					if (tg!=null) {
						x = x.getElementsByTagName(tg);
					}
					xO.set(getAtr(":mem"),new Object[]{new Hashtable(),x,null});
					return true;
				} else {
					//não deveria chegar aqui
					logs.grava(new Exception("acao inválida: "+a));
					return false;
				}
				
				if (a==null || a.equals("exists")) {
					//processa com novos dados
					//deb(xO,"<br>processar Arq: "+are+" = "+dx.nome);
					for (int i=0;i<tg.size();i++) {
						xmlXtd y = (xmlXtd)get(i);
						//deb(xO,"nome="+y.nome);
						y.dd = dx; //.get(d);
						//deb(xO,"<br> = "+y.dd.nome);
						y.gravaTag(xO,nv);
					}
				} else if (a.equals("mem")) {
				} else {
					deb(xO,"ERRO x:with action="+a+" nao conhecida...");
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
					ar = XcAtr(xO,getAtr(":fileName"));
					//logs.grava("ar="+ar);
					String f = xO.dirD+"/"+ar;
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
			
		} else if (n.equals("xstAttr")) {
			xstAttr(xO);
			
		} else if (n.equals("attrs")) {
			//varre os atributos da tag seta :mem com nome attr
			//     e :memVal com o valor
			String m = getAtr(":mem");
			String mv = getAtr(":memVal");
			for (Enumeration e=dd.atr.keys();e.hasMoreElements();) {
				String k = (String)e.nextElement();
				xO.set(m,k);
				xO.set(mv,dd.atr.get(k));
				for (int i=0;i<tg.size();i++) {
					//logs.grava("with mem="+i);
					xmlXtd y = (xmlXtd)get(i);
					//y.dd = x; //.get(d);
					y.gravaTag(xO,nv);
				}
			}
			
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
			xmlOut xo = new xmlOut(XatrF(xO,getAtr("file")),xO);
			xO.on("<hr>Salvando: "+XatrF(xO,getAtr("file")));
			if (true) {
				//return true;
			}
			if (getAtr("root")!=null) {
				dd.gravaTag(xo,0);
			} else {
				dd.gravaSubTags(xo,0);
			}
			
		} else if (n.equals("exec")) {
			Hashtable h = new Hashtable();
			for (Enumeration e=atr.keys();e.hasMoreElements();) {
				String k = (String)e.nextElement();
				h.put(k,XatrF(xO,getAtr(k)));
			}
			//logs.grava("h="+h);
			xO.exec(h);
			
			
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
			//xO.on("<span class=\""+n+"\">"+XatrF(xO,n)+"</span>"+texF,nv);
			//xO.on(XatrF(xO,n)+(str.vazio(texF)?"":str.trimm(texF)),nv);
			xO.o(XatrF(xO,n)+(str.vazio(texF)?"":str.trimm(texF,"\t\n\r")));

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
