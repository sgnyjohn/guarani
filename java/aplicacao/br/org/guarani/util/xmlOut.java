package br.org.guarani.util;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import br.org.guarani.servidor.*;

//***************************************
//***************************************
public class xmlOut {
	String name;
	FileOutputStream f;
	byte[] buf;
	public int tBf;
	String sErro;
	public String nl = "\r\n",tb="\t";
	public Pedido ped;
	public Pag pg;
	Hashtable dad = new Hashtable();
	boolean pd = false;
	public String proxy=null;
	
	static Hashtable hX=new Hashtable();
	web w;
	public String dirD,dirX;
	//,type;
	//File file;
	//public boolean exists = true;
		
	String bf=null;
	
	//xmlOO
	int nt;
	public Hashtable pi=new Hashtable();
	//***************************************
	// executa outro x?l
	public void exec(Hashtable h) {
		logs.grava("ped="+ped.h);
		Hashtable hk = ped.h;
		h.put("obj","1");
		ped.h = h;
		try {
			pg.initPed(ped);
			pg.run();
		} catch (Exception e) {
		}
		ped.h = hk;
	}
	//***************************************
	// mostra erro no xml
	public void erro(String s) {
		ped.o(s);
	}
	//***************************************
	// inicia out em String / finaliza e retorna
	public String buf(boolean inicio) {
		String r = null;
		if (inicio) {
			if (bf!=null) {
				logs.grava("xmlOut.buf:true - buffer já ativo");
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
		if (f!=null) {
			try {
				f.close();
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}
	//***************************************
	public boolean o(String s) {
		if (bf!=null) {
			bf += s;
			return true;
		} else if (f==null && pd) {
			ped.o(s);
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
			sErro = "xmlOut.o arq="+name+" "+str.erro(e);
			logs.grava("erro",sErro);
			return false;
		}
		return true;
	}
	//***************************************
	public boolean on(String s,int nv) {
		return o(nv<0?s:nl+str.repl(tb,nv)+s);
	}
	//***************************************
	public boolean on(String s) {
		return o(s+nl);
	}
	//***************************************
	public xmlOut(Pag pg,String dx,String dd) {
		pd = true;
		dirX = dx;
		dirD = dd;
		this.pg = pg;
		this.ped = pg.ped;
	}
	//***************************************
	public xmlOut(String nome) {
		this(nome,2048);
	}
	//***************************************
	public xmlOut(String nome,xmlOut xo) {
		this(nome,2048);
		dirX = xo.dirX;
		dirD = xo.dirD;
	}
	//***************************************
	public xmlOut(String nome,String dx,String dd) {
		this(nome,2048);
		dirX = dx;
		dirD = dd;
	}
	//***************************************
	public xmlOut(String nome,int tBf) {
		this.name = nome;
		this.tBf = tBf;
		try {
			f = new FileOutputStream(nome);
			buf = new byte[tBf];
		} catch (Exception e) {
			sErro = "xmlOut.cria arq="+nome+" "+str.erro(e);
			logs.grava("erro",sErro);
		}
	}
	//*************************************
	public xmlTag xml(File f,String cl) {
		boolean oo = "~sxw~odt~".indexOf("~"+str.substrRat(f.getName().toLowerCase(),".")+"~")!=-1;
		xmlTag r=null;
		try {
			ZipFile zf = new ZipFile(f);
			for (Enumeration e = zf.entries() ; e.hasMoreElements() ;) {
				ZipEntry z = (ZipEntry)e.nextElement();
				String n = ""+z;
				//logs.grava("z="+z);
				if (z==null) {
				} else if ((!oo && n.indexOf(".xml")!=-1) || (oo && n.equals("content.xml")) ) {
					InputStream is = zf.getInputStream(z);
					if (is!=null) {
						xmlParser x = new xmlParser(is);
						r = x.parse(cl);
						if (x.hErr!=null) {
							on("<hr>ERRO no XML="+f+" "+z+"<br>"+x.hErr+"<hr>");
						}
					}
					//x.debug(ped);
				}
			}
		} catch (Exception e) {
			on("ERRO: "+str.erro(e));
		}
		return r;
	}
	//*************************************
	public String name(String r,String n) {
		if (str.equals(n,"dir://")) {
			r = "dir://"+r+"/"+str.substrAt(n,"//");
			//logs.grava(r);
			return r;
		} else if (str.equals(n,"http://")) {
			return n;
		} else if (n.indexOf(".")!=-1 && str.substrRat(n,".").length()<=3) {
			return r+"/"+n;
		}
		String v[] = new String[]{"xml","zip","ok","odt","sxw"};
		//logs.grava("r="+r+" n="+n);
		for (int i=0;i<v.length;i++) {
			String n1 = r+"/"+n+"."+v[i];
			if ((new File(n1)).exists()) {
				//logs.grava(" ok "+n1);
				return n1;
			}
		}
		r = r+"/"+n+"."+v[0];
		//logs.grava("cam="+r);
		return  r;
	}
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
		return (xmlXtd)arq(nameXtd(an),cl,false);
	}
	//*************************************
	public String nameXml(String an) {
		return name(dirD,an);
	}
	//*************************************
	public xmlTag xml(String an,String cl) {
		return arq(nameXml(an),cl,true);
	}
	//*************************************
	public xmlTag arq(String an,String cl,boolean Cache) {

		xmlTag x;
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
		if (Cache) {
			Object[] o = (Object[])hX.get(ch);
			if (o!=null) {
				Long l = (Long)o[0];
				if (f.lastModified()==l.longValue()) {
					return (xmlTag)o[1];
				}
			}
		}
		
		
		dm = new Long(f.lastModified());
		xmlParser p = null;
		
		//é zip?
		if (str.substrRat(an,".").toLowerCase().equals("zip")) {
			arquivoZip az = new arquivoZip(an);
			InputStream i = az.inputStream(str.substrRat(str.leftRat(an,"."),"/"));
			if (i==null) {
				i = az.inputStream(str.substrRat(str.leftRat(an,"."),"/")+".xml");
			}
			if (i==null) {
				on("<hr>ERRO no zip "+an+", arq "+str.substrRat(str.leftRat(an,"."),"/")+" ou .xml não existe!");
			}
			p = new xmlParser(i);
		} else {
			p = new xmlParser(an);
		}
	
		//logs.grava("an="+an+" cl="+cl);
		x = p.parse(cl);
		if (p.hErr!=null) {
			on("<hr>ERRO no XML="+an+"<br>"+p.hErr+"<hr>");
		}
		
		if (hX.size()>30) {
			hX = new Hashtable();
		}
		if (Cache && x!=null) {
			hX.put(ch,new Object[]{dm,x});
		}
		return x;
	}
}
