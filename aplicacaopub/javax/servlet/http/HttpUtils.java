package javax.servlet.http;

import java.util.*;

import br.org.guarani.util.*;

public class HttpUtils {
	public static Hashtable parseQueryString(String s) {
		String b[] = str.palavraA(s,"&");
		//logs.grava("direto",b.length+" tam q="+s);
		Hashtable h = new Hashtable();
		for (int i=0;i<b.length;i++) {
			//logs.grava("direto",+i+"="+b[i]);
			h.put(str.leftAt(b[i],"="),str.UnEscape(str.substrAt(b[i],"=")));
		}
		return h;
	}
}
