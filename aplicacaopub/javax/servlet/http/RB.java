
package javax.servlet.http;

import java.util.*;

public class RB extends ResourceBundle {
	Hashtable attr = new Hashtable();
	public Object handleGetObject(String key) {
		// don't need okKey, since parent level handles it.
		if (key.equals("cancelKey")) return "Abbrechen";
		return null;
	}
	public ResourceBundle getBundle() {
		return this;
	}
	public Enumeration getKeys() {
		return attr.keys();
	}
	//public String getString(String nome) {
	// return "rb.getString()";
	//}
}
