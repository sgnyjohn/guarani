package br.org.guarani.util;

import java.io.*;
import java.util.*;

import br.org.guarani.servidor.*;

//***************************************
//***************************************
public class xmlParserDir extends xmlParser {
	//***************************************
	public xmlTag parse(Class cl) {
		this.cl = cl;
		//logs.grava("url dir="+url);
		xmlTag r = newTag();
		r.nome = "xmlParserDir";
		String fil = null;
		if (url.indexOf("*")!=-1) {
			fil = str.substrAt(url,"*").toLowerCase();
			url = str.leftAt(url,"*");
		}
		xmlTag r1 = newTag(new File(url));
		r1.putAtr("path",url);
		r.put(r1);
		//r.putAtr("file",url);
		File v[] = (new File(url)).listFiles();
		for (int i=0;i<v.length;i++) {
			if (fil==null || v[i].getName().toLowerCase().indexOf(fil)!=-1) {
				r1.put(newTag(v[i]));
			}
		}
		return r;
	}
	//***************************************
	xmlTag newTag(File f) {
		xmlTag r = newTag();
		r.nome = f.isDirectory()?"dir":"file";
		r.putAtr("name",f.getName());
		r.putAtr("lastModified",data.strSql(f.lastModified()));
		r.putAtr("size",""+f.length());
		return r;
	}
	//***************************************
	void erro(String s) {
		super.erro(s);
		//onD("<hr>aaa="+hErr.get(""+(hErr.size()-1)));
	}
	//***************************************
	public xmlParserDir(InputStream i) {
		hi = i;
		fechar = false;
	}
	//***************************************
	public xmlParserDir() {
	}
	/***************************************
	public xmlParserDir(String aq) {
		this(new File(aq));
	}
	//***************************************
	public xmlParserDir(File f) {
		url = ""+f;
		this.f = f;
	}
	*/
}
