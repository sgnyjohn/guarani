/*
	* Signey John jan/2001. 
*/

package br.org.guarani.util;

import java.util.StringTokenizer;
import java.io.*;
import java.security.*;

public class str {
	public final static String tb_trim = " \r\n\t";
	public final static String acentos  = "áéíóúüàâêôãõñçäÁÉÍÓÚÜÀÂÊÔÃÕÑÇÄ";
	public final static String acentost = "aeiouuaaeoaoncaAEIOUUAAEOAONCA";
	public final static String acentosDOS = 
		"\u00A0\u0082\u00A1\u00A2\u00A3\u0081\u0085"
		+"\u0083\u0088\u0093\u00C6\u00E4\u00A4\u0087"
		+"\u00B5\u0090\u00D6\u00E0\u00E9\u009A\u00B7"
		+"\u00B6\u00D2\u00E2\u00C7\u00E5\u00A5\u0080";

	//*********************************************
	public static String html(String r) {
		if (r.indexOf("&")!=-1) {
			r = str.troca(r,"&","&amp;");
		}
		if (r.indexOf("<")!=-1) {
			r = str.troca(r,"<","&lt;");
		}
		if (r.indexOf(">")!=-1) {
			r = str.troca(r,">","&gt;");
		}
		return r; 
	}
	//**************************************/
	public static String toDos(String s) {
		int p,t = s.length();
		byte b[] = new byte[t];

		for (int i=0;i<t;i++) {
			p = acentos.indexOf(s.charAt(i));
			if (p<0) {
				b[i] = (byte)s.charAt(i);
			} else {
				b[i] = (byte)acentosDOS.charAt(p);
			}
		}

		return new String(b);
	}
  
	//**************************************/
	public static String erro(Throwable e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}

	//**************************************/
	public static String[] erroA(Throwable e) {
		return str.palavraA(erro(e),"\n");
	}

	//*****************************************//
	public static int opcaoInt(String a,String op,int padrao) {
		String r = opcao(a,op);
		if (r==null) {
			return padrao;
		}
		return inteiro(r,padrao);
	}

	//*****************************************//
	public static int inteiro(String s,int padrao) {
		try {
			//logs.grava(s);
			if (s.indexOf(".")!=-1) {
				s = str.leftAt(s,".");
			}
			int i = Integer.parseInt(trimm(s));
			return i;
		} catch (Exception e) {
			return padrao;
		}
	}
	public static long longo(String s,long padrao) {
		try {
			long i = Long.parseLong(trimm(s));
			return i;
		} catch (Exception e) {
			return padrao;
		}
	}
	public static double duplo(String s,double padrao) {
		try {
			double i = Double.parseDouble(trimm(s));
			return i;
		} catch (Exception e) {
			return padrao;
		}
	}

	//*****************************************//
	public static String opcao(String a,String op,String padrao) {
		String r = opcao(a,op);
		if (r==null) {
			return padrao;
		}
		return r;
	}

	//*****************************************//
	public static String opcao(String a,String op) {
		op += "=";
		int pi = a.indexOf(op);
		if (pi==-1) {
			return null;
		}
		pi += op.length();
		if (a.substring(pi,pi+1).equals("'")) {
			pi++;
			int pf = a.indexOf("'",pi);
			if (pf==-1) {
				return null;
			}
			return a.substring(pi,pf);
		} else {
			int pf = a.indexOf(" ",pi);
			if (pf==-1) {
				pf = a.length();
			}
			return a.substring(pi,pf);
		}
	}

	//*****************************************//
	public static boolean equals(String maior,String menor) {
		if (menor.length()>maior.length()) return false;
		return (maior.substring(0,menor.length()).equals(menor));
	}
	//*****************************************//
	public static String strZero(long b,int t) {
		return str.right(str.repl("0",t)+b,t);
	}

	//*****************************************//
	public static String repl(String a,int b) {
		int t = a.length();
		byte c[] = new byte[t*b];
		for (int i=0;i<t*b;i+=t) 
			for (int x=0;x<t;x++) c[i+x] = (byte)a.charAt(x);
		return new String(c,0,t*b);
	}


	//*****************************************//
	public static String right(String a,int b) {
		int t = a.length();
		return a.substring(t-b,a.length());
	}

	//*****************************************//
	public static boolean vazio(String a) {
		if (a==null) return true;
		if (a.length()==0) return true;
		if (str.trimm(a).length()==0) return true;
		return false;
	}
	//*****************************************//
	public static String seVazio(String a,String b) {
		return (str.vazio(a)?b:a);
	}
	//*****************************************//
	public static String seVazio(String a,String b,String c) {
		return ((a==null || a.trim().length()==0) ? b : c+a );
	}
	//*****************************************//
	public static String seNull(String a,String b,String c) {
		return ((a==null) ? b : c+a );
	}
	//*****************************************//
	public static String seNull(String a,String b) {
		return ((a==null) ? b : a );
	}

	//*****************************************//
	public static String leftAt(String g,String a) {
		int i = g.indexOf(a);
		if (i<0) return g;
		return g.substring(0,i);
	}

	//*****************************************//
	public static String substrAt(String g,String a) {
		int i = g.indexOf(a);
		if (i<0) return g;
		return g.substring(i+a.length(),g.length());
	}

	//*****************************************//
	public static String leftRat(String g,String a) {
		int i = g.lastIndexOf(a);
		if (i<0) return g;
		return g.substring(0,i);
	}

	//*****************************************//
	public static String substrRat(String g,String a) {
		int i = g.lastIndexOf(a);
		if (i<0) return g;
		return g.substring(i+a.length(),g.length());
	}

	//*****************************************//
	public static String rightAt(String g,String a) {
		int i = g.lastIndexOf(a);
		if (i<0) return g;
		return g.substring(i+a.length(),g.length());
	}

	//*****************************************//
	public static String substrAtAt(String g,String a,String b) {
		int i = g.indexOf(a);
		if (i<0) return "";
		int f = g.indexOf(b,i+a.length());
		if (f<0) return "";
		return g.substring(i+a.length(),f);
	}

	//*****************************************//
	public static String substrRatAt(String g,String a,String b) {
		int i = g.lastIndexOf(a);
		if (i<0) return "";
		int f = g.indexOf(b,i);
		if (f<0) return "";
		//System.out.println(i+a.length()+"="+f);
		return g.substring(i+a.length(),f);
	}

	//*****************************************//
	public static String substrAtRat(String g,String a,String b) {
		int i = g.indexOf(a);
		if (i<0) return "";
		int f = g.lastIndexOf(b);
		if (f<0) return "";
		//System.out.println(i+a.length()+"="+f);
		return g.substring(i+a.length(),f);
	}
	//*****************************************//
	public static String trocaTudo(String g,String a,String b) {
		while (!g.equals(g=str.troca(g,a,b))) {
		}
		return g;
	}

	//*****************************************//
	public static String troca(String g,String a,String b) {
		int i=0,p,ta,tb;

		ta = a.length();
		tb = b.length();

		while ( (p = g.indexOf(a,i)) > -1 )  {
			g = g.substring(0,p)+b+g.substring(p+ta,g.length());
			i = p - ta + tb + 1;
		}

		return g;

	}

	//*****************************************//
	public static int[] palavraAInt(String g,String a) {
		String x[] = str.palavraA(g,a);
		int t = x.length;
		int r[] = new int[t];
		for (int i=0;i<t;i++) {
			try {
				r[i] = Integer.parseInt(x[i]);
			} catch (Exception e) {
			}
		}
		return r;
	}

	//*****************************************//
	public static String palavraA(Object g[],String a) {
		String r = "";
		for (int i=0;i<g.length;i++) {
			r += g[i]+a;
		}
		if (r.length()>a.length()) {
			return r.substring(0,r.length()-a.length());
		}
		return r;
	}


	//*****************************************//
	public static String[] palavraA(String g,String a) {
		//if (true) { //a.length() > 1 || g.indexOf(a+a)!=-1 ) {
			int nv = strConta(g,a)+1;
			String r[] = new String[nv];
			if (nv==0) {
				r[0] = g;
				return r;
			} else {
				int p=0,pn,t=a.length(),pi=0;
				while ((pn=g.indexOf(a,p))!=-1) {
					r[pi++] = g.substring(p,pn);
					p = pn + t;
				}
				r[pi++] = g.substring(p,g.length());
				return r;
			}
		/*	return palavraA(g,a.substring(0,1));
		} else {
			StringTokenizer t = new StringTokenizer(g,a);
			String r[] = new String[t.countTokens()];
			int i=0;
			while (t.hasMoreTokens())
					r[i++] = t.nextToken();
			return r;
		}
		*/
	}
 
	//*****************************************//
	//conta número de ocorrencias de b em a
	public static int strConta(String a,String b) {
		int p=0,nv=0,pn,t=b.length();
		while ((pn=a.indexOf(b,p))!=-1) {
			nv++;
			p = pn + t;
		}
		return nv;
	}

	//*****************************************//
	public static String[][] palavraA(String g,String a,String b) {
		String t1[] = palavraA(g,a);
		int t = palavraA(t1[0],b).length;
		String r[][] = new String[t1.length][t];
		for (int i=0;i<t1.length;i++) {
			String r1[] = palavraA(t1[i],b);
			for (int ii=0;ii<Math.min(r1.length,t);ii++) {
				r[i][ii] = r1[ii];
			}
		}
		return r;
	}

	/*
	/*****************************************
	public static String UnEscape(String a) {
		String r = "";
		char s;

		for (int i=0;i<a.length();i++) {
			s = a.charAt(i);
			if (s=='+') {
				r += " ";
			} else if (s=='%') {
				r += (char)Integer.parseInt(a.substring(i+1,i+3),16);
				//(char) Integer.parseInt(str.substring(strPos + 1, strPos + 3), 16)
				i += 2;
			} else {
				r += s;
			}
		}

		return r;

	}
	*/

	//*****************************************//
	public static String rTrim(String a,String b) {
		//retira do fim
		int t = a.length();
		if (t<1) return a;
		int i = t-1;
		while (i>-1 && b.indexOf(a.substring(i,i+1))>-1) i--;
		if (i!=t-1) a = a.substring(0,i+1);
		return a;
	}

	//*****************************************//
	public static String lTrim(String a,String b) {
		//retira do inicio
		int t = a.length()-1;
		if (t<0) return a;
		int i = 0;
		while (i<t & b.indexOf(a.substring(i,i+1))>-1) i++;
		if (i!=0) a = a.substring(i,t);
		return a;
	}

	//*****************************************//
	public static String trimm(String a,String b) {
		int i,t;

		//retira do inicio
		t = a.length()-1;
		if (t<0) return a;
		i = 0;
		while (i<t & b.indexOf(a.substring(i,i+1))>-1) i++;
		if (i!=0) a = a.substring(i,t+1);

		//retira do fim
		t = a.length();
		if (t<1) return a;
		i = t-1;
		while (i>-1 && b.indexOf(a.substring(i,i+1))>-1) i--;
		if (i!=t-1) a = a.substring(0,i+1);

		return a;

	}
	//*****************************************//
	public static String trimm(String a) {
		return trimm(a,tb_trim);
	}

	//*****************************************//
	public static String tiraAcentos(String s) {
		int p,t = s.length();
		byte b[] = new byte[t];

		for (int i=0;i<t;i++) {
			p = acentos.indexOf(s.charAt(i));
			if (p<0) {
				b[i] = (byte)s.charAt(i);
			} else {
				b[i] = (byte)acentost.charAt(p);
			}
		}

		return new String(b);
	}

	public static String dir(String s) {
		String dl = (so.linux()?"/":"\\");
		if (s.substring(s.length()-1,s.length()).equals(dl)) {
			return s;
		} else {
			return s+dl;
		}
	}
	
 
	//**************************************
	// le pedido enviado por Browser e para
	// para caracteres BR
	//**************************************
	public static String UnEscape(String a) {
		try {
			return java.net.URLDecoder.decode(a);//, "UTF-8");
		} catch (Exception e) {
			logs.grava("UnEscape(String): erro "+e+" na string=("+a+")");
		}
		return a;
	}
	//**************************************
	public static String UnEscapeA(String a) {
		int pos=0,t = a.length(),b;
		char[] r = new char[t];
		char s;
		//logs.grava("unescape="+a);

		//rever 
		for (int i=0;i<t;i++) {
			s = a.charAt(i);
			if (s=='+') { //em ajax apos escape(s) os + devem ser substituidos por %2B
				r[pos++] = ' ';
			} else if (s=='%') {
				if (i<t-1 && a.charAt(i+1)==s) {
					r[pos++] = s;
					r[pos++] = s;
					i++;
				//ESTAVA IGNORANDO Ã
				//} else if (i<t-2 
				//  && (a.substring(i+1,i+3).toLowerCase().equals("c3"))) {
				// //ignora C3 que é prefixo de UTF-8?
				// i += 2;
				} else if (i<t-2 
						&& (b=str.inteiroBase(a.substring(i+1,i+3),16,-1))!=-1) {
					r[pos++] = (char)b;
					i += 2;
				} else {
					r[pos++] = s;
				}
			} else {
				r[pos++] = s;
			}
		}

		return new String(r,0,pos);

	}

	//**************************************/
	public static int inteiroBase(String a,int base,int pad) {
		try {
			int i = Integer.parseInt(trimm(a),base);
			return i;
		} catch (Exception e) {
			return pad;
		}
	}
	//**************************************/
	public static String strBase(int i,int base) {
		return strBase(i,base,-1);
	}
	//**************************************/
	public static String strBase(int i,int base,int tam) {
		if (i<0) i+= 255;
		byte rs,r[] = new byte[]{48,48,48,48,48,48,48,48,48,48,48,48,48,48,48,48};
		int t=r.length-1;
		while (i>0) {
			rs = (byte)(i%base + '0');
			if (rs>'9') {
				rs = (byte)(rs - '9' - 1 + 'a');
			}
			if (rs>'z') {
				rs = (byte)(rs - 'z' + 'A');
			}
			if (t<0) {
				r[0] = '*';
			} else {
				r[t--] = rs;
			}
			i = i/base;
		}
		if (tam==-1) {
			tam = r.length - t - 1;
		}
		return new String(r,r.length-tam,tam);
	}

}
