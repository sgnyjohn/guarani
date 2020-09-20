package br.org.guarani.util;

import java.io.*;


//***********************
//dados do sistema operarional
//***********************
public class so {
	private static String jvn,on,ggp,ggpDir,jit;
	private static boolean linux,sun;
	//***********************
	public static void init() {
		jvn = System.getProperty("java.vm.name");
		sun = jvn.toLowerCase().indexOf("gcj")==-1;

		on = System.getProperty("os.name");
		linux = on.toLowerCase().indexOf("linux")!=-1;

		if (!sun) {
			String ggp1 = System.getProperty("gnu.gcj.progname");
			File f = new File(ggp1);
			try {
				ggp = f.getCanonicalPath();
			} catch (Exception e) {
				System.out.println("soCFG não Existe!!");
				System.exit(2);
			}
			ggpDir = str.leftRat(ggp,System.getProperty("file.separator"));
		} else {
			ggp = ".\\";
			ggpDir = ".\\";
		}
  
		jit = System.getProperty("java.io.tmpdir");
	}
	//***********************
	public static boolean sun() {
		return sun;
	}
	//***********************
	public static boolean linux() {
		return linux;
	}
	//***********************
	public static String nomePrg() {
		return ggp;
	}
	//***********************
	public static String dirPrg() {
		return ggpDir;
	}
	//***********************
	public static String dirTmp() {
		return jit;
	}
}
