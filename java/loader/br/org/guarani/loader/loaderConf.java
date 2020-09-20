/* ************************************
signey set/2020
*************************************/
package br.org.guarani.loader;

import java.util.*;
import java.io.*;


//***********************************
//***********************************
public class loaderConf {
	xmlTagL cfg;
	public String get(String ch) {
		return (String)cfg.getCh(ch);
	}
	boolean init(xmlTagL Cfg) {
		cfg = Cfg;
		return true;
	}
}

