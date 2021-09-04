package br.org.guarani.util;

import java.util.*;
import java.text.*;

import  java.math.BigInteger;  
import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;  


//***********************************************
//***********************************************
public class str1 extends str {
	long z=(long)1055793802258.0;
	long z1=(long)1053979402258.0;
	static MessageDigest md = null;
	//**************************************/
	public static String palavraA(String v[][],String a,String b) {
		String r = "";
		for (int i=0;i<v.length;i++) {
			if (v[i]!=null) r += palavraA(v[i],b)+a;
		}
		return r;
	}
	//**************************************/
	public static String palavraA(String v[],String a) {
		String r = "";
		for (int i=0;i<v.length;i++) {
			r += v[i]+a;
		}
		return r;
	}
	//**************************************/
	public static String[] getChars(String s) {
		//https://www.guj.com.br/t/duvida-com-relacao-offsetbycodepoints-da-classe-string/337218
		//on("String: " + greeting,"p"); 
		//on("Number of code units in greeting is " + greeting.length(),"p");
		int n = s.codePointCount(0,s.length());
		//on("Number of code points " + n,"p");

		int ip;
		int ii=s.offsetByCodePoints(0,0);
		String r[] = new String[n];
		for(int i=0;i<n;i++){
			ip = s.offsetByCodePoints(0,i+1);
			r[i] = s.substring(ii,ii+ip-ii);
			ii = ip;
		}
		return r;
	}
	//**************************************/
	public static Date dateSql(String dt) {
		//Calendar calendar = Calendar.getInstance(); 
		//calendar.setTime(dt);
		try {
			Date r =  (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(dt);//.getTime();
			return r;
		} catch (Exception e) {
			return null;
		}
	}
	//**************************************/
	public static Map rotulos(String s,String del,String ignora) {
		//str.tb_trim
		Map<String,String> r = new HashMap();
		String v[] = s.split(del);
		String ch = str.trimm(v[0],ignora);
		String chn=null,a=null;
		for (int i=1;i<v.length;i++) {
			//retira prox rótulo do final
			a = str.trimm(v[i],ignora);
			if (i+1!=v.length) {
				chn = "";
				int p = a.length()-1;
				while (p>=0 && ignora.indexOf(a.substring(p,p+1))==-1) {
					chn = a.substring(p,p+1)+chn;
					p--;
				}
				a = str.trimm(a.substring(0,p),ignora);
			}
			r.put(ch,a);
			ch = chn;
		}
		return r;
	}	
	//**************************************/
	public static Map<String,String> map(String v[]) {
		Map<String,String> r = new HashMap();
		for (int i=0;i<v.length;i+=2) {
			r.put(v[i],v[i+1]);
		}
		return r;
	}
	//**************************************/
	public static Map<String,String> map(String t) {
		return rotulos(t,":",tb_trim);
	}
	//**************************************/
	public static String map(Map<String,String> m) {
		String t = "";
		for(Map.Entry e : m.entrySet()) {
			t += e.getKey()+": "+e.getValue()+";\n";
		}
		return t;
	}
	//**************************************/
	public static String trim(String s,int t) {
		s = str.trimm(s);
		if (s.length()>t) return s.substring(0,t-3)+"...";
		return s;
	}
	//**************************************/
	public static String quebra(String s,int t) {
		//otimiz
		String r = "";
		for (int i=0;i<s.length();i+=t) {
			r += "\n"+s.substring(i,Math.min(i+t,s.length()));
		}
		return r.substring(1);
	}
	//**************************************/
	public static int compareTo(String a,String b) {
		int ta = a.length();
		int tb = b.length();
		if (ta>tb) {
			return a.substring(0,tb).compareTo(b);
		} else if (tb>ta) {
			return a.compareTo(b.substring(0,ta));
		} else {
			return a.compareTo(b);
		}
	}
	//**************************************/
	public static String html(Throwable e) {
		String stv[] = str.erroA(e);
		String	st = "<p class=erroTr>"+stv[0]+"</p>";
		int ni=0;
		for (int i=1;i<stv.length;i++) {
			String a = str.leftAt(str.trimm(stv[i])," ");
			if (!a.equals("at")) {
				st += "<p class=erroTr>"+stv[i]+"</p>";
			} else {
				if (ni==0) {
				st += "<p class=erroTr1>"+stv[i]+"</p>";
				} else {
					st += "<p class=erroTr2>"+stv[i]+"<p>";
				}
				ni++;
			}
		}
		return st;
	}	
	//**************************************
	// byte para hexadecimal. base16
	private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
	public static String toHex(byte[] buf) {
		char[] chars = new char[2 * buf.length];
		for (int i = 0; i < buf.length; ++i) {
			chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
			chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
		}
		return new String(chars);
	}
	//**************************************
	public static String padr(String a,int t) {
		return str.repl(" ",Math.max(t-a.length(),0))+a;
	}
	//**************************************
	public static String padl(String a,int t) {
		return a+str.repl(" ",Math.max(t-a.length(),0));
	}
	//**************************************
	public static String seNulo(Object a,String b,String c) {
		if (a==null) return b;
		return c+a;
	}
	//**************************************
	public static String seNulo(Object a,String b) {
		if (a==null) return b;
		return ""+a;
	}
	
	//**************************************
	// procura por padroes como %HX \xHX \n \t \r
	public static String decodUrl(String a) {
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
				//%%%%%%%%%%%%%%%%%%%%
				if (i<t-1 && a.charAt(i+1)==s) {
					r[pos++] = s;
					r[pos++] = s;
					i++;
				} else if (i<t-2 
						&& (b=str.inteiroBase(a.substring(i+1,i+3),16,-1))!=-1) {
					r[pos++] = (char)b;
					i += 2;
				} else {
					r[pos++] = s;
				}
			} else if (s=='\\') {
				//\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\-
				if (i<t-1 && a.charAt(i+1)==s) {
					//se 2 seguidos mostra so 1
					r[pos++] = s;
					i++;
				} else if (i<t-1 
						&& (b="nrt".indexOf(a.charAt(i+1)))!=-1 ) {
					r[pos++] = "\n\r\t".charAt(b);
					i += 1;
				} else if (i<t-2 && a.charAt(i+1)=='x'
						&& (b=str.inteiroBase(a.substring(i+2,i+4),16,-1))!=-1) {
					r[pos++] = (char)b;
					i += 3;
				} else {
					r[pos++] = s;
				}
			//} else if (s=='Ã') { //problema UTF... abaixo não vai mais funcionar...
			} else if (false && s=='a') {
				// utf mal convertido...
				if (i<t-1 && a.charAt(i+1)==s) {
					//se 2 seguidos mostra so 1
					r[pos++] = s;
					i++;
				} else {
					r[pos++] = s;
				}
			} else {
				r[pos++] = s;
			}
		}

		return new String(r,0,pos);

	}	
	//**************************************
	public static String erro() {
		return str.erro(new Throwable(" sem erro "));
	}
	
	//**************************************
	public static String erroHtml(Throwable e) {
		String v[] = str.palavraA(str.erro(e),"\n");
		String r="", rf = "<hr><font color=red><b>ERRO "
					+e+"</b></font><br>"
		;
		for (int i = 0;i<v.length; i++ ) {
			if (str.equals(str.trimm(v[i]),"Caused by:")) {
				String tt = r;
				r = rf+"<br><font style=\"font-size:120%;\">"+v[i]+"</font><br>";
				rf = tt;
			} else {
				r += "<br>"+v[i];
			}
		}
		return r+"======="+rf;
	}
 	//***********************************************
	public  static String md5sum(String se) { 
		/* http://www.guj.com.br/posts/list/46888.java
			pode dar results #s cfrme SO ou config SO
			senha.getBytes("UTF-8")'
			senha.getBytes("ISO-8859-1")'
		
		*/
		String sen = "";  
		if (md == null) {
			try {  
				md = MessageDigest.getInstance("MD5");  
			} catch (NoSuchAlgorithmException e) {  
				e.printStackTrace(); 
				return null;
			}  
		}
		BigInteger hash = new BigInteger(1, md.digest(se.getBytes()));  
		sen = hash.toString(16); 
		while (sen.length()<32) {
			sen = "0"+sen;
		}
		return sen;  
	}
	//***********************************************
	public static String htmlStr(String a) {
		int pos=0,t = a.length(),b;
		char bHtml[] = new char[t];
		char s;
		for (int i=0;i<t;i++) {
			s = a.charAt(i);
			if ( s=='&' && t-i>3) {
				b=a.indexOf(";",i);
				if (b==-1) {
					bHtml[pos++] = s;
				} else if (a.charAt(i+1)=='#' && b<i+6 ) {
					//ogs.grava("ex="+a.substring(i+2,b));
					int c = str.inteiro(a.substring(i+2,b),-1);
					bHtml[pos++] = (char)c;
					i = b;
				}  else if (b<i+7) {
					//http://www.w3.org/MarkUp/draft-ietf-iiir-html-01.txt
					String ts = a.substring(i+1,b);
					if (ts.equals("gt")) {
						bHtml[pos++] = '>';i = b;
					} else if (ts.equals("lt")) {
						bHtml[pos++] = '<';i = b;
					} else if (ts.equals("amp")) {
						bHtml[pos++] = '&';i = b;
					} else if (ts.equals("quote")) {
						bHtml[pos++] = '"';i = b;
					} else {
						bHtml[pos++] = s;
					}
				} else {
					bHtml[pos++] = s;
				}
			} else {
				bHtml[pos++] = s;
			}
		}
		return new String(bHtml,0,pos);
	}
	//*****************************************//
	public static String[][] palavraA(String g,String a,String b) {
		String t1[] = palavraA(g,a);
		//int t = palavraA(t1[0],b).length;
		String r[][] = new String[t1.length][];
		for (int i=0;i<t1.length;i++) {
			r[i] = palavraA(t1[i],b);
			//for (int ii=0;ii<Math.min(r1.length,t);ii++) {
			//	r[i][ii] = r1[ii];
			//}
		}
		return r;
	}
	//**************************************/
	public static String concatDel(String s,String d,String s1) {
		if (str.vazio(s1)) {
			return s;
		} else if (str.vazio(s)) {
			return s1;
		}
		return s+d+s1;
	}
	//**************************************/
	public static String dHtml(String s) {
		return str.troca(str.troca(s,"&lt;","<"),"&gt;",">");
	}
	//****************************************************
	public static String seVazio(String s,String b) {
		return (str.vazio(s)?b:s);
	}
	//****************************************************
	public static String md5(String s) {
		return str1.toHexString(digito.md5(s));
	}
	//****************************************************
	// ignora sem espaços ou q já tenham algo minusculo ou @ (email)
	public static String capitalizeNome(String s) {
		if (s.indexOf(" ")==-1 || !s.toUpperCase().equals(s) || s.indexOf("@")!=-1) {
			return s;
		}
		return str1.capitalize(s);
	}
	//**************************************
	public static String troca(String s,String v[]) {
		for (int i=0;i<v.length;i+=2) {
			s = str.troca(s,v[i],v[i+1]);
		}
		return s;
	}
	//**************************************
	public static String Escape(String a) {
		int pos=0,t = a.length(),b;
		char[] r = new char[t*3];
		char s;

		for (int i=0;i<t;i++) {
			s = a.charAt(i);
			if ( (s>='0' && s<='9') ||  (s>='a' && s<='z')  ||  (s>='A' && s<='Z') ) {
				r[pos++] = s;
			} else {
				String s1 = str.right("00"+base16((int)s),2);
				r[pos++] = '%';
				r[pos++] = s1.charAt(0);
				r[pos++] = s1.charAt(1);
			}
		}
		return new String(r,0,pos);
	}

	//**************************//
	public static String capitalize(String s) {
		String tb = "~de~e~do~dos~no~nos~na~nas~da~das~em~rs~";
		String v[] = str.palavraA(s," "),r="";
		for (int i=0;i<v.length;i++) {
			if (!str.vazio(v[i])) {
				r +=( "(-".indexOf(v[i].substring(0,1))!=-1
					?v[i].toUpperCase()
					:(tb.indexOf("~"+v[i].toLowerCase()+"~")==-1
							?v[i].substring(0,1).toUpperCase()+v[i].substring(1).toLowerCase()
							:v[i].toLowerCase())
				)+" ";
			}
		}
		return str.trimm(r);
	}

	
	//**************************//
	public static String concat(String arr[]) {
		String r = arr[0];
		int u=-1;
		for (int i=1;i<arr.length;i+=2) {
			if (!str.vazio(arr[i+1])) {
				r += (str.vazio(r)?"":arr[i])+arr[i+1];
			}
		}
		return r;
	}
	//*****************************************//
	public static boolean equalsR(String maior,String menor) {
		int p = maior.length()-menor.length();
		if (p<0) {
			return false;
		}
		return maior.substring(p).equals(menor);
	}
	
	//***********************************
	public static Hashtable palavraH(String s,String d1,String d2) {
		String v[][] = str.palavraA(s,d1,d2);
		Hashtable h = new Hashtable();
		for (int i=0;i<v.length;i++) {
			h.put(v[i][0],v[i][1]);
		}
		return h;
	}
	
	//***********************************
	public static String retiraTags(String s,String tg) {
		String in="<",fi=">",r="",c,t="",t1;
		boolean v=false;
		for (short i=0;i<s.length();i++) {
			c = s.substring(i,i+1);
			if (v) {
				if (c.equals(fi)) {
					v = !v;
					t1 = str.leftAt(t+" "," ").toLowerCase();
					if (!t1.equals(tg) && !t1.equals("/"+tg)) {
						r += in+t+fi;
					}
				} else {
					t += c;
				}
			} else {
				if (c.equals(in)) {
					v = !v;
					t = "";
				} else {
					r += c;
				}
			}
		}
		return r;
	}
	//***********************************
	public static String atalhoHttp(String l) {
		String a,v[] = new String[]{
			"http://","mailto:","https://","ftp://"
		};
		int t = v.length,p,tl=l.length();
		char c;
		try {
		for (int i=tl-7;i>=0;i--) {
			for (int x=0;x<t;x++) {
				if (str1.equals(l,v[x],i)) {
					p = i+v[x].length();
					//ogs.grava(l.substring(i,i+20));
					while ( p<tl && (c=l.charAt(p))!=' ' && c!='\n' && c!='\r' ) {
						p++;
					}
					a = l.substring(i,p);
					//ogs.grava("at="+a);
					if (x==1) {
						a = "<a href="+a+">"+str.substrAt(a,":")+"</a>";
					} else {
						a = "<a target=_blank href="+a+">"
							+(a.length()>45?a.substring(0,45)+"...":a)+"</a>";
					}
					l = l.substring(0,i)
						+a
						+l.substring(p)
					; 
					i -= 6;
					break;
				}
			}
		}
		} catch (Exception e) {
		}
		return l;
	}
	//*****************************************//
	public static boolean equals(String maior,String menor) {
		return str1.equals(maior,menor,0);
	}
	//*****************************************//
	public static boolean equals(String maior,String menor,int desloc) {
		int ml = menor.length();
		int mL = maior.length();
		if (ml>mL-desloc || desloc<0 ) return false;
		for (short i=0;i<ml;i++) {
			if (maior.charAt(i+desloc)!=menor.charAt(i)) {
				return false;
			}
		}
		return true;
	}
	//***********************************
	public static String seIgual(String s,String isto,String aquilo) {
		return s.equals(isto)?aquilo:s;
	}
	//***********************************
	public static String capitalizeLIXO(String s) {
		if (str.vazio(s)) {
			return "";
		}
		String v[] = str.palavraA(s," ");
		for (int i=0;i<v.length;i++) {
			v[i] = v[i].substring(0,1).toUpperCase()+v[i].substring(1).toLowerCase();
		}
		return str1.palavraA(v," ");
	}

	//***********************************
	public static String nomeTabela(String nome) {
		boolean m = true;
		String r = "";
		for (short i=0;i<nome.length();i++) {
			String c = (nome.charAt(i)+"").toLowerCase();
			if ("0123456789".indexOf(c)!=-1
				|| (c.compareTo("a")>=0 && c.compareTo("z")<=0)) {
				r += (m?c.toUpperCase():c);
				m = false;
			} else {
				m = true;
			}
		}
		return r;
	}

	//*****************************************//
	public static String toHexString(byte b[]) {
		int t = b.length;
		char br[] = new char[t*2];
		String s;
		for (int i=0;i<t;i++) {
			s = Integer.toHexString(b[i]);
			if (s.length()!=2) s = "0"+s;
			br[i*2] = s.charAt(0);
			br[i*2+1] = s.charAt(1);
		}
		return new String(br);
	}
 
	//*****************************************//
	public static String troca(String a,String b[],String c[]) {
		for (int i=0;i<b.length;i++) {
			a = str.troca(a,b[i],c[i]);
		}
		return a;
	}
	//*****************************************//
	public static String troca(String a,String b[],String c) {
		for (int i=0;i<b.length;i++) {
			a = str.troca(a,b[i],c);
		}
		return a;
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
	//********************************//
	public static String encodeUrl(String s) {
		//s = str.troca(s," ","%20");
		String v[] = str.palavraA(s,"/");
		for (short i=0;i<v.length;i++) {
				v[i] = encodeParam(v[i]); //,"8859_1");
		}
		return str.palavraA(v,"/");
	}
	//********************************//
	public static String encodeParam(String s) {
		int p=0,t=s.length();
		char b[] = new char[t*3];
		boolean modif=false;
		for (short i=0;i<t;i++) {
			char c = s.charAt(i);
			if ((c>='a' && c<='z')
					|| (c>='A' && c<='Z')
					|| (c>='0' && c<='9')
					|| c=='.' || c=='-' || c=='*' || c=='_' ) {
				b[p++] = c;
			} else {
				b[p++] = '%';
				String h = Integer.toHexString(c);
				if (h.length()==1) {
					h = "0"+h;
				}
				b[p++] = h.charAt(0);
				b[p++] = h.charAt(1);
				modif = true;
			}
		}
		if (!modif) {
			return s;
		}
		return new String(b,0,p);
	}
	/**************************************
	public static String UnEscape(String a) {
		int pos=0,t = a.length(),b;
		char[] r = new char[t];
		char s;

		//rever 
		for (int i=0;i<t;i++) {
			s = a.charAt(i);
			if (s=='+') {
				r[pos++] = ' ';
			} else if (s=='%') {
				if (i<t-1 && a.charAt(i+1)==s) {
					r[pos++] = s;
					r[pos++] = s;
					i++;
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
	*/
	//@dt@
	//*********************************************
	public static String fixD(String r,int t) {
		return str.right(str.repl(" ",t)+r,t);
	}
	//*********************************************
	public static String fix(String r,int t) {
		return (r+str.repl(" ",t)).substring(0,t);
	}
	//*********************************************
	public static String grep(String tx,String s) {
		return grep(str.palavraA(tx,"\n"),s);
	}
	//*********************************************
	public static String grep(String v[],String s) {
		String r = "";
		for (short i=0;i<v.length;i++) {
			if (v[i].indexOf(s)!=-1) {
				r += v[i]+"\n";
			}
		}
		return r;
	}
 
	//*********************************************
	public static String base16(int x) {
		String r = "";
		int i,b=16;
		while (x>0) {
			i = x % b;
			r = "0123456789ABCDEF".charAt(i)+r;
			x = (x-i)/b;
		}
		return r;
	}
	//*********************************************
	public static String rel(String t1,String t2) {
		return " and "+t1+".C_"+t2+"="+t2+".C_"+t2;
	}
	//*********************************************
	public static String vazio(String a,String b) {
		return vazio(a,b,"");
	}
	//*********************************************
	public static String vazio(String a,String b,String c) {
		if (str.vazio(a)) return "";
		return b+a+c;
	}
	//***********************************************
	public static String strJava(String r) {
		return java(r);
	}
	//***********************************************
	public static String java(String r) {
		String r1 = r.toLowerCase();
		int p;
		while ((p=r1.indexOf("<scrip"))!=-1) {
			r = r.substring(0,p)+"<!scrip"+r.substring(p+6);
			r1 = r.toLowerCase();
		}
		while ((p=r1.indexOf("</scri"))!=-1) {
			r = r.substring(0,p)+"<!/scri"+r.substring(p+6);
			r1 = r.toLowerCase();
		}

		if (r.indexOf("\\")!=-1) {
			r = str.troca(r,"\\","\\\\");
		}
		if (r.indexOf("\'")!=-1) {
			r = str.troca(r,"\'","\\\'");
		}
		if (r.indexOf("\r\n")!=-1) {
			r = str.troca(r,"\r\n","\\n");
		}
		if (r.indexOf("\t")!=-1) {
			r = str.troca(r,"\t"," ");
		}
		if (r.indexOf("\n")!=-1) {
			r = str.troca(r,"\n","\\n");
		}
		if (r.indexOf("\r")!=-1) {
			r = str.troca(r,"\r","\\r");
		}
		return r;
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
			int i = Integer.parseInt(s);
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
	//*****************************************
	public static int[] palavraAInt(String g,String a) {
		String v[] = str.palavraA(g,a);
		int r[] = new int[v.length];
		for (short i=0;i<v.length;i++) {
			r[i] = str.inteiro(v[i],-1);
		}
		return r;
	}
 
	/*****************************************
	public static String[] palavraA(String g,String a) {
		if (a.length() > 1) {
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
			//return palavraA(g,a.substring(0,1));
		} else {
			StringTokenizer t = new StringTokenizer(g,a);
			String r[] = new String[t.countTokens()];
			int i=0;
			while (t.hasMoreTokens())
					r[i++] = t.nextToken();
			return r;
		}
	}
	*/
 
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

}


/*
                              ENTITIES
                                   
   The following entity names are used in HTML , always prefixed by
   ampersand (&) and followed by a semicolon as shown.  They represent
   particular graphic characters which have special meanings in places
   in the markup, or may not be part of the character set available to
   the writer.
   
  &lt;                    The less than sign <
                         
  &gt;                    The "greater than" sign >
                         
  &amp;                   The ampersand sign & itself.
                         
  &quot;                  The double quote sign "
                         
   Also allowed are references to any of the ISO Latin-1 alphabet,
   using the entity names in the following table.
   
ISO Latin 1 character entities

   This list is derived from "ISO 8879:1986//ENTITIES Added Latin
   1//EN".
   
  &AElig;                capital AE diphthong (ligature)
                         
  &Aacute;               capital A, acute accent
                         
  &Acirc;                capital A, circumflex accent
                         
  &Agrave;               capital A, grave accent



Berners-Lee and Connolly                                             28

  &Aring;                capital A, ring
                         
  &Atilde;               capital A, tilde
                         
  &Auml;                 capital A, dieresis or umlaut mark
                         
  &Ccedil;               capital C, cedilla
                         
  &ETH;                  capital Eth, Icelandic
                         
  &Eacute;               capital E, acute accent
                         
  &Ecirc;                capital E, circumflex accent
                         
  &Egrave;               capital E, grave accent
                         
  &Euml;                 capital E, dieresis or umlaut mark
                         
  &Iacute;               capital I, acute accent
                         
  &Icirc;                capital I, circumflex accent
                         
  &Igrave;               capital I, grave accent
                         
  &Iuml;                 capital I, dieresis or umlaut mark
                         
  &Ntilde;               capital N, tilde
                         
  &Oacute;               capital O, acute accent
                         
  &Ocirc;                capital O, circumflex accent
                         
  &Ograve;               capital O, grave accent
                         
  &Oslash;               capital O, slash
                         
  &Otilde;               capital O, tilde
                         
  &Ouml;                 capital O, dieresis or umlaut mark
                         
  &THORN;                capital THORN, Icelandic
                         
  &Uacute;               capital U, acute accent
                         
  &Ucirc;                capital U, circumflex accent
                         
  &Ugrave;               capital U, grave accent
                         
  &Uuml;                 capital U, dieresis or umlaut mark
                         
  &Yacute;               capital Y, acute accent
                         
  &aacute;               small a, acute accent



Berners-Lee and Connolly                                             29

  &acirc;                small a, circumflex accent
                         
  &aelig;                small ae diphthong (ligature)
                         
  &agrave;               small a, grave accent
                         
  &aring;                small a, ring
                         
  &atilde;               small a, tilde
                         
  &auml;                 small a, dieresis or umlaut mark
                         
  &ccedil;               small c, cedilla
                         
  &eacute;               small e, acute accent
                         
  &ecirc;                small e, circumflex accent
                         
  &egrave;               small e, grave accent
                         
  &eth;                  small eth, Icelandic
                         
  &euml;                 small e, dieresis or umlaut mark
                         
  &iacute;               small i, acute accent
                         
  &icirc;                small i, circumflex accent
                         
  &igrave;               small i, grave accent
                         
  &iuml;                 small i, dieresis or umlaut mark
                         
  &ntilde;               small n, tilde
                         
  &oacute;               small o, acute accent
                         
  &ocirc;                small o, circumflex accent
                         
  &ograve;               small o, grave accent
                         
  &oslash;               small o, slash
                         
  &otilde;               small o, tilde
                         
  &ouml;                 small o, dieresis or umlaut mark
                         
  &szlig;                small sharp s, German (sz ligature)
                         
  &thorn;                small thorn, Icelandic
                         
  &uacute;               small u, acute accent
                         
  &ucirc;                small u, circumflex accent



Berners-Lee and Connolly                                             30

  &ugrave;               small u, grave accent
                         
  &uuml;                 small u, dieresis or umlaut mark
                         
  &yacute;               small y, acute accent
                         
  &yuml;                 small y, dieresis or umlaut mark
                         
                             THE HTML DTD
                                   

*/
