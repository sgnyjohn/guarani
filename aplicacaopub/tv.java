
import br.org.guarani.servidor.*;
import br.org.guarani.bd.*;
import br.org.guarani.util.*;

import java.io.*;
import java.util.*;

//***************************************
//***************************************
public class tv extends PagV {
	String pq;
	
	
	//***************************************
	public void strip() {
	}
	//***************************************
	public void det() {
	}
	//***************************************
	public void inicio() {
		convEncod.isoUtf("teste");
		on("<table><tr><td>"
			+"<form name=\"__formul\">"
			+"<script language=\"JavaScript\" src=\"/js/tecl.js\"></script>"
			+"<script></script>"
			+"procurar: <input size=30 value=\""+pq+"\" type=text name=p id=iPesq>"
			+"<img src=\"/imagens/sel.gif\" onclick=\"oTeclado.abre('iPesq')\">"
			+" <input type=submit>"
			+"</form>"
		);
		
		if (str.vazio(pq)) {
			return;
		}
		
		int num=str.inteiro(param("num","7"),-1);
		String start=param("start");
		String ed = "http://video.google.com/videosearch?"
			+(!str.vazio("pq")?"q="+str.troca(pq," ","+"):"")
			//+"?so=0"
			+"&num=7"
			+"&lr=lang_pt"
			+(start==null?"":"&start="+start)
		;
		//ed = "http://localhost/1.txt";
		
		
		String ar = "/tmp/"+str1.md5(ed)+".txt";
		arquivo aq = new arquivo(ar);
		String t = null;
		if (!aq.f.exists()) {
			on("<br>ed="+ed);
			web w = new web(ed);
			w.debug = true;
			w.leCab();
			on("<pre>"+w.tCab+"</pre>");
			w.setTimeOut(10000);
			
			t = w.lePag();
			aq.gravaTxt(t);
			on("<hr>"+(t==null?t:""+t.length())+"<hr>");
		} else {
			t = aq.leTxt();
		} 
		
		int nv = t.indexOf(" of about ");
		if (nv>0) {
			nv = str.inteiro(str.troca(str.substrAtAt(t.substring(nv,nv+20),">","<"),",",""),-1);
		}
		
		on("<td><h1>Total: "+nv+"</h1></table>");
		
		//ok ver...
		xmlParserHtml p = new xmlParserHtml(ar);
		xmlTag x = p.parse();
		if (x==null) {
			//on(p.sErro());
			aq.f.delete();
			return;
		}
		x.grava("/tmp/2.xml");
		
		xmlTag x1 = x.getElementsByTagNameClass("div","SearchResultItem");
		//on("txml="+x1.size());
		
		(x1.get(1)).grava("/tmp/1.xml");
		
		on("<table border=1>");
		for (int i=0;i<x1.size();i++) {
			xmlTag xa = x1.get(i);
			
			xmlTag xi = xa.getElementsByTagNameClass("div","Url");
			String url = xi.get(0).toText();
			
			
			xi = xa.getElementsByTagName("img");
			on("<tr><td><img src="+(xi.get(0)).getAtr("src")+"><td>");
			
			
			xi = xa.getElementsByTagNameClass("div","Title");
			on("<b>"+convEncod.utfIso(xi.get(0).toText())+"</b>");
			
			on("<br><a target=_blank href=\""+url+"\">"+url+"</a>");
			
			xi = xa.getElementsByTagNameClass("div","Details");
			String s = "<b>"+xi.get(0).toText();
			on("<br>"+str.troca(s,"-","</b>-"));
			
			xi = xa.getElementsByTagNameClass("div","Snippet");
			on("<br>"+convEncod.utfIso(xi.get(0).toText()));
			
		}
		
		if (nv>0) {
			on("<tr><td colspan=2 align=center>");
			for (int i=0;i<nv;i+=num) {
				on("<a href=\"?p="+pq+(i==0?"":"&start="+i)+"\">"+(i/num+1)+"</a>");
			}
		}
		
		
		on("</table>");
		
		
		
	}
	//***************************************
	public boolean run(Pedido pd) {
		super.run(pd);
		cab("");
		
		pq = param("p","");
		
		exec();
  
		rodap();
		return true;
	}
}
