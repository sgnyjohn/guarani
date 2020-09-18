package br.org.guarani.loader;

import java.io.*;
import java.util.*;
import java.util.zip.*;

//***************************************
//***************************************
public class xmlOutL {
	String name;
	FileOutputStream f;
	byte[] buf;
	public int tBf;
	String sErro;
	public String nl = "\r\n",tb="\t";
	Hashtable dad = new Hashtable();
	boolean pd = false;
	
	static Hashtable hX=new Hashtable();
	public String dirD,dirX;
	//,type;
	//File file;
	//public boolean exists = true;
		
	String bf=null;
	
	//xmlOO
	int nt;
	public Hashtable pi=new Hashtable();
	
	//***************************************
	// inicia out em String / finaliza e retorna
	public String buf(boolean inicio) {
		String r = null;
		if (inicio) {
			if (bf!=null) {
				logsL.grava("xmlOutL.buf:true - buffer já ativo");
			}
			bf = "";
		} else {
			r = bf;
			bf = null;
		}
		return r;
	}
		
	//***************************************
	public Object get(String n) {
		return dad.get(n);
	}
	//***************************************
	public void set(String n,Object o) {
		dad.put(n,o);
	}
	//***************************************
	public boolean close() {
		try {
			f.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	//***************************************
	public boolean o(String s) {
		if (bf!=null) {
			bf += s;
			return true;
		} else if (pd) {
			//ped.o(s);
			return true;
		}
		try {
			int t,i=0,p = 0;
			t = s.length();
			while (p<t) {
				buf[i++] = (byte)s.charAt(p++);
				if (i==tBf || p==t) {
					f.write(buf,0,i);
					i = 0;
				}
			}
		} catch (Exception e) {
			sErro = "xmlOutL.o arq="+name+" "+strL.erro(e);
			logsL.grava("erro",sErro);
			return false;
		}
		return true;
	}
	//***************************************
	public boolean on(String s,int nv) {
		return o(nv<0?s:nl+strL.repl(tb,nv)+s);
	}
	//***************************************
	public boolean on(String s) {
		return o(s+nl);
	}
	/***************************************
	public xmlOutL(Pedido ped,String dx,String dd) {
		pd = true;
		dirX = dx;
		dirD = dd;
		this.ped = ped;
	}
	*/
	//***************************************
	public xmlOutL(String nome) {
		this(nome,2048);
	}
	//***************************************
	public xmlOutL(String nome,xmlOutL xo) {
		this(nome,2048);
		dirX = xo.dirX;
		dirD = xo.dirD;
	}
	//***************************************
	public xmlOutL(String nome,String dx,String dd) {
		this(nome,2048);
		dirX = dx;
		dirD = dd;
	}
	//***************************************
	public xmlOutL(String nome,int tBf) {
		this.name = nome;
		this.tBf = tBf;
		try {
			f = new FileOutputStream(nome);
			buf = new byte[tBf];
		} catch (Exception e) {
			sErro = "xmlOutL.cria arq="+nome+" "+strL.erro(e);
			logsL.grava("erro",sErro);
		}
	}
	//*************************************
	public xmlTagL xml(File f,String cl) {
		boolean oo = "~sxw~odt~".indexOf("~"+strL.substrRat(f.getName().toLowerCase(),".")+"~")!=-1;
		xmlTagL r=null;
		try {
			ZipFile zf = new ZipFile(f);
			for (Enumeration e = zf.entries() ; e.hasMoreElements() ;) {
				ZipEntry z = (ZipEntry)e.nextElement();
				String n = ""+z;
				if (z==null) {
				} else if ((!oo && n.indexOf(".xml")!=-1) || (oo && n.equals("content.xml")) ) {
					InputStream is = zf.getInputStream(z);
					if (is!=null) {
						xmlParserL x = new xmlParserL(is);
						r = x.parse(cl);
						if (x.hErr!=null) {
							on("<hr>ERRO no XML="+f+" "+z+"<br>"+x.hErr+"<hr>");
						}
					}
					//x.debug(ped);
				}
			}
		} catch (Exception e) {
			on("ERRO: "+strL.erro(e));
		}
		return r;
	}
	//*************************************
	public String name(String r,String n) {
		if (strL.equals(n,"dir://")) {
			r = "dir://"+r+"/"+strL.substrAt(n,"//");
			//logsL.grava(r);
			return r;
		} else if (strL.equals(n,"http://")) {
			return n;
		} else if (n.indexOf(".")!=-1 && strL.substrRat(n,".").length()==3) {
			return r+"/"+n;
		}
		String v[] = new String[]{"xml","zip","ok","odt","sxw"};
		//logsL.grava("r="+r+" n="+n);
		for (int i=0;i<v.length;i++) {
			String n1 = r+"/"+n+"."+v[i];
			if ((new File(n1)).exists()) {
				//logsL.grava(" ok "+n1);
				return n1;
			}
		}
		r = r+"/"+n+"."+v[0];
		//logsL.grava("cam="+r);
		return  r;
	}
	/*
	//*************************************
	public String nameXtd(String an) {
		return name(dirX,an);
	}
	//*************************************
	public xmlXtd xtd(String an) {
		return xtd(an,"br.org.guarani.util.xmlXtd");
	}
	//*************************************
	public xmlXtd xtd(String an,String cl) {
		return (xmlXtd)arq(nameXtd(an),cl);
	}
	*/
	//*************************************
	public String nameXml(String an) {
		return name(dirD,an);
	}
	//*************************************
	public xmlTagL xml(String an,String cl) {
		return arq(nameXml(an),cl);
	}
	//*************************************
	public xmlTagL arq(String an,String cl) {

		xmlTagL x;
		boolean cache = false;
		int tp = 0;
		Long dm = new Long(-1);
		
		//É Arquivo LOCAL...
		File f = new File(an);
		if (!f.exists()) {
			on("<hr>ERRO não EXISTE arq="+an);
			return null;
		}
		
		//está no cache?
		String ch = cl+"~"+an;
		Object[] o = (Object[])hX.get(ch);
		if (o!=null) {
			Long l = (Long)o[0];
			if (f.lastModified()==l.longValue()) {
				return (xmlTagL)o[1];
			}
		}
		dm = new Long(f.lastModified());
		
		
		
		//logsL.grava("an="+an+" cl="+cl);
		xmlParserL p = new xmlParserL(an);
		x = p.parse(cl);
		if (p.hErr!=null) {
			on("<hr>ERRO no XML="+an+"<br>"+p.hErr+"<hr>");
		}
		
		if (hX.size()>30) {
			hX = new Hashtable();
		}
		if (x!=null) {
			hX.put(ch,new Object[]{dm,x});
		}
		return x;
	}
}
