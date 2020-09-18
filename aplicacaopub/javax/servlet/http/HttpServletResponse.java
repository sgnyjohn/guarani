package javax.servlet.http;

import java.io.*;
import javax.servlet.*;
import br.org.guarani.servidor.*;
import br.org.guarani.util.*;

public class HttpServletResponse {
	Pedido ped;
	ServletOutputStream sos;
	public void addCookie(Cookie c) {
	}
	public void reset() {
	}
	public HttpServletResponse(Pedido ped) {
		this.ped = ped;
		//sos = new ServletOutputStream(ped.getOutputStream());
	}
	public void setContentType(String s) {
		if (!ped.setMime(s)) {
			logs.grava("direto",new Exception("erro="+s));
		}
	}
	public PrintWriter getWriter() {
		ped.o("");
		return ped.getWriter();
	}
	public String encodeURL(String s) {
		logs.grava("direto",new Exception("não def"));
		return "encodeURL()="+s;
	}
	public OutputStream getOutputStream() throws IOException {
		ped.o("");
		return ped.getOutputStream(); //sos;
	}
	//public OutputStream getOutputStream() throws IOException {
	// return ped.getOutputStream();
	//}
	//getContentType()
	public void setHeader(String a,String b) {
		logs.grava("direto",new Exception("não def"));
	}
	public void sendRedirect(String e) {
		//logs.grava("direto",new Exception("não def="+e));
		//rever
		ped.on("<script>");
		ped.on("window.location.href='"+e+"';");
		ped.on("</script>");
	}
	public HttpSession getSession(boolean a) {
		return HttpSession.getSession(ped,true);
	}

}
