package gnu.gcj.runtime;

import java.util.*;
import java.lang.reflect.*;
import java.net.URL;


public class teste {
	public ClassLoader setClSy1(ClassLoader a) {
		URL v[] = new URL[0];
		//return VMClassLoader.createSystemClassLoader(v,a);
		return null;
	}
	public ClassLoader setClSy(ClassLoader a) {
		URL v[] = new URL[0];
		ClassLoader cl = this.getClass().getClassLoader();
		//((VMClassLoader)cl).createSystemClassLoader(v,a);
		//return VMClassLoader.createSystemClassLoader(v,a);
		return null;
	}
	public void on(String s) {
		System.out.println(s);
	}
	public teste() {
		ClassLoader cl = this.getClass().getClassLoader();
		while (cl!=null) {
			on("parent: "+cl.getClass().getName());
			Method m[] = cl.getClass().getDeclaredMethods();
			for (int i=0;i<m.length;i++) {
				on("  method="+m[i].getName());
			}
			Field[] f =	cl.getClass().getDeclaredFields();
			for (int i=0;i<f.length;i++) {
				on("  field="+f[i].getName());
			}
			
			cl = cl.getParent();
		}
	}
}
