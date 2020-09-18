package javax.servlet;

import br.org.guarani.util.*;

public class ServletConfig {
	public String getInitParameter(String s) {
		//logs.grava("servlet","ServletConfig param=? "+s);
		if (s.equals("path")) {
			return "/home/guarani/conf/servlet/direto/";
		} else {
			logs.grava("servlet","ServletConfig param=? "+s);
			return null;
		}
	}
}
