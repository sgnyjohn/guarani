package javax.servlet.http;

import java.util.*;
import br.org.guarani.servidor.*;
import br.org.guarani.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

public class HttpServletRequest {
	Pedido ped;
	Hashtable servletP;
	String cl,query;
	public Cookie[] getCookies() {
		logs.grava("direto",new Exception("não def"));
		return null;
	}
	public String getRequestURI() {
		logs.grava("direto",new Exception("não def"));
		return null;
	}
	public String getHeader(String s) {
		String r = (String)servletP.get(s.toLowerCase());
		//logs.grava("direto",new Exception("ok?="+s+"="+r));
		return r;
	}
	public ServletInputStream getInputStream() {
		if (ped.io==null) {
			logs.grava("direto",new Exception("ped.io=null"));
		}
		return ped.io;
	}
	public String[] getParameterValues(String ch) {
		String r[] = new String[1];
		r[0] = ped.getString(ch);
		//logs.grava("direto",new Exception("ok?="+ch+"="+r[0]));
		return r;
	}
	public String getQueryString() {
		String r = query;
		//servletP.get("?endereco")+"?"+
		//logs.grava("direto",new Exception("ok? ="+getMethod()+"=<hr>"+r));
		//rever
		return r;
	}
	public HttpServletRequest(Pedido ped) {
		this.ped = ped;
		servletP = (Hashtable)ped.servletP.get("?");
		String a = (String)servletP.get("?endereco");
		int i = a.indexOf("?");
		if (i==-1) {
			cl = a;
		} else {
			cl = a.substring(0,i);
			query = a.substring(i+1,a.length());	
		}
	}
	public Locale getLocale() {
		return Locale.US; 
		//new Locale("pt","BR");
	}
	public HttpSession getSession() {
		return HttpSession.getSession(ped,true);
	}
	public HttpSession getSession(boolean b) {
		return HttpSession.getSession(ped,b);
	}
 
	public String getParameter(String nome) {
		//logs.grava("direto","ped.getString("+nome+")"+
		// ((nome.indexOf("senha")==-1)?ped.getString(nome):"????"));
		return ped.getString(nome);
	}
	public String getContentType() {
		String r = (String)servletP.get("content-type");
		//logs.grava("direto",new Exception("ok?="+r));
		return r;
	}
	public String getPathInfo() {
		String r = "/"+str.substrRat(cl,"/");
		//rever logs.grava("direto",new Exception("ret ok?="+r));
		return r;
	}
	public String getRemoteAddr() {
		return ped.getSessao().getIp();
	}
	public Enumeration getParameterNames() {
		return ped.getParametros().keys();
	}
	public String getMethod() {
		String r = (String)servletP.get("?");
		//logs.grava("direto",new Exception("não ok="+r));
		return r; //ped.pd.get("?");
	}
}
