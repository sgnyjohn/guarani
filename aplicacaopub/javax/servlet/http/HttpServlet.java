package javax.servlet.http;

import java.io.*;
import java.util.*;
import javax.servlet.*;


import br.org.guarani.servidor.*;
import br.org.guarani.util.*;


public class HttpServlet implements Prg {
	Hashtable po;
	Pedido ped;
	HttpServletRequest in;
	HttpServletResponse out;
	static boolean iniciado = false;
	//ResourceBundle ResourceB; 
	public void init(ServletConfig c) throws ServletException {
	}
	public boolean run(Pedido pPed) {
		//ResourceBundle.getBundle("servlet.LocalStrings");
		ped = pPed;
		long ims = data.ms();
		po = (Hashtable)ped.servletP.get("?");
  
		in = new HttpServletRequest(ped);
		out = new HttpServletResponse(ped);
		String s = "init()";
		try {
			if (!iniciado) {
				iniciado = true;
				logs.grava("servlet","inicio CFG init()....");
				init(new ServletConfig());
			}
			if (po.get("?").equals("POST")) {
				s = "doPost()";
				doPost(in,out);
			} else {
				s = "doGet()";
				doGet(in,out);
			}
		} catch (Exception e) {
			//logs.grava("servlet",e);
			ped.erro("ERRO "+s+": ",e);
		}
		/*} catch (IOException ioe) {
			logs.grava("servlet","ERRO "+ioe);
		} catch (ServletException se) {
			logs.grava("servlet","ERRO "+se);
		}
		*/
		ims = data.ms()-ims;
		ped.on(ims+" ms");
		logs.grava("direto-d",in.cl+"\t"+in.query+"\t"+ims);
		return true;
	}
 
	public void doGet(HttpServletRequest request,HttpServletResponse response)
		throws IOException, ServletException {
		ped.on("SERVLET não definiu doGet");
	}

	public void doPost(HttpServletRequest request,HttpServletResponse response)
		throws IOException, ServletException {
		ped.on("SERVLET não definiu doPost");
	}

}
