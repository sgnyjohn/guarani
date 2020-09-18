package br.org.guarani.servidor;

import java.util.Hashtable;
import br.org.guarani.util.*;

public class Mime {

	static Hashtable h=null;
	//static Hashtable cgi;

	public Mime() {
		if (h==null) 
			init();
	}

	private static void init() {
  
  
  
		h = new Hashtable();

		h.put(".txt","text/plain");
		h.put(".css","text/css");
		h.put(".html","text/html");
		h.put(".htm","text/html");
		h.put(".gif","image/gif");
		h.put(".jpg","image/jpeg");
		h.put(".jpe","image/jpeg");
		h.put(".jpeg","image/jpeg");
		h.put(".java","text/plain");
		h.put(".body","text/html");
		h.put(".rtx","text/richtext");
		h.put(".tsv","text/tab-separated-values");
		h.put(".etx","text/x-setext");
		h.put(".ps","application/x-postscript");
		h.put(".class","application/java");
		h.put(".csh","application/x-csh");
		h.put(".sh","application/x-sh");
		h.put(".tcl","application/x-tcl");
		h.put(".tex","application/x-tex");
		h.put(".texinfo","application/x-texinfo");
		h.put(".texi","application/x-texinfo");
		h.put(".t","application/x-troff");
		h.put(".tr","application/x-troff");
		h.put(".roff","application/x-troff");
		h.put(".man","application/x-troff-man");
		h.put(".me","application/x-troff-me");
		h.put(".ms","application/x-wais-source");
		h.put(".src","application/x-wais-source");
		h.put(".zip","application/zip");
		h.put(".bcpio","application/x-bcpio");
		h.put(".cpio","application/x-cpio");
		h.put(".gtar","application/x-gtar");
		h.put(".shar","application/x-shar");
		h.put(".sv4cpio","application/x-sv4cpio");
		h.put(".sv4crc","application/x-sv4crc");
		h.put(".tar","application/x-tar");
		h.put(".ustar","application/x-ustar");
		h.put(".dvi","application/x-dvi");
		h.put(".hdf","application/x-hdf");
		h.put(".latex","application/x-latex");
		h.put(".bin","application/octet-stream");
		h.put(".oda","application/oda");
		h.put(".pdf","application/pdf");
		h.put(".ps","application/postscript");
		h.put(".eps","application/postscript");
		h.put(".ai","application/postscript");
		h.put(".rtf","application/rtf");
		h.put(".nc","application/x-netcdf");
		h.put(".cdf","application/x-netcdf");
		h.put(".cer","application/x-x509-ca-cert");
		h.put(".exe","application/octet-stream");
		h.put(".gz","application/x-gzip");
		h.put(".Z","application/x-compress");
		h.put(".z","application/x-compress");
		h.put(".hqx","application/mac-binhex40");
		h.put(".mif","application/x-mif");
		h.put(".ief","image/ief");
		h.put(".tiff","image/tiff");
		h.put(".tif","image/tiff");
		h.put(".ras","image/x-cmu-raster");
		h.put(".pnm","image/x-portable-anymap");
		h.put(".pbm","image/x-portable-bitmap");
		h.put(".pgm","image/x-portable-graymap");
		h.put(".ppm","image/x-portable-pixmap");
		h.put(".rgb","image/x-rgb");
		h.put(".xbm","image/x-xbitmap");
		h.put(".xpm","image/x-xpixmap");
		h.put(".xwd","image/x-xwindowdump");
		h.put(".au","audio/basic");
		h.put(".snd","audio/basic");
		h.put(".aif","audio/x-aiff");
		h.put(".aiff","audio/x-aiff");
		h.put(".aifc","audio/x-aiff");
		h.put(".wav","audio/x-wav");
		h.put(".mpeg","video/mpeg");
		h.put(".mpg","video/mpeg");
		h.put(".mpe","video/mpeg");
		h.put(".qt","video/quicktime");
		h.put(".mov","video/quicktime");
		h.put(".avi","video/x-msvideo");
		h.put(".movie","video/x-sgi-movie");
		h.put(".avx","video/x-rad-screenplay");
		h.put(".wrl","x-world/x-vrml");
		h.put(".mpv2","video/mpeg");
		h.put(".dww","application/snvj.dww");

		h.put(".php","text/html");
		h.put("dir","text/html");

		//cgi = new Hashtable();
		//cgi.put(".php","e:/php/php.exe");

		String l=Guarani.dirCfg+"/mime.types";
		arquivo a = new arquivo(l);
		if (!a.f.exists()) {
			logs.grava("ERRO, Atenção mime não existe: Não existe arq: "+l);
			return;
		}
		while ((l=a.leLinha())!=null) {
			l = str.trocaTudo(str.trimm(l),"\t\t","\t");
			if (l.indexOf("\t")!=-1 && !str.equals(l,"#")) {
				String v[] = str.palavraA(str.substrAt(l,"\t")," ");
				for (short i=0;i<v.length;i++) {
					v[i] = str.trimm(v[i]);
					if (v[i].length()>1) {
						h.put("."+v[i],str.trimm(str.leftAt(l,"\t")));
						//logs.grava("."+v[i]+"=="+str.trimm(str.leftAt(l,"\t")));
					}
				}
			}
		}

	}

	public static String getTipo(String a) {
		if (h==null) {
			init();
		}
		return (String)(h.get(a.toLowerCase()));
	}

	/*public static String getCgi(String a) {
		//logs.grava(a);
		return (String)(cgi.get(a));
	}
	*/

}
