package javax.servlet.http;

import java.util.*;
import br.org.guarani.servidor.*;
import br.org.guarani.util.*;

public class HttpSession {
	httpSessao gSessao;
	//*************************************/
	public boolean remove(String chave) {
		return gSessao.remove(chave);
	}
	//*************************************/
	public static synchronized HttpSession getSession(Pedido pd,boolean cria) {
		HttpSession sessao = new HttpSession(pd.getSessao());
		return sessao;
	}
	//*************************************/
	public HttpSession(httpSessao gS) {
		gSessao = gS;
	}
	//*************************************/
	public String getId() {
		return gSessao.getId();
	}
	//*************************************/
	public long getCreationTime() {
		return gSessao.getCreationTime();
	}
	//*************************************/
	public long getLastAccessedTime() {
		return gSessao.getLastAccessedTime();
	}
	//*************************************/
	public void setAttribute(Object nome,Object valor)  {
		gSessao.put(nome,valor);
	}
	//*************************************/
	public Object getAttribute(Object nome)  {
		return gSessao.get(nome);
	}
	//*************************************/
	public Enumeration getAttributeNames() {
		return gSessao.getAttributeNames();
	}
	//*************************************/
	public void invalidate() {
		gSessao.invalidate();
	}
}
