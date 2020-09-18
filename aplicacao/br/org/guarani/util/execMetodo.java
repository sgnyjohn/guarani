/*
mar/2009

permite driblar a nececidade de interface
ou super classe
*/
package br.org.guarani.util;


import java.lang.reflect.*;


//**************************************************
//**************************************************
public class execMetodo {
	Object o;
	String metodo;
	Method m;
	String sErro;
	//**************************************************
	public boolean exec(Object o[]) {
		try {
			m.invoke(o,o);
			return true;
		} catch (InvocationTargetException e) {
			sErro = ("ERRO, execMethod() no Método <b>"+metodo+"()</b>"+str.erro(e));
		} catch (Exception e) {
			sErro = "ERRO, execMethod() exec método <b>"+metodo+"()</b> "+e;
		}
		return false;
	}
	//**************************************************
	public void exec(Object o,Object o1,Object o2) {
		exec(new Object[]{o,o1,o2});
	}
	//**************************************************
	public void exec(Object o,Object o1) {
		exec(new Object[]{o,o1});
	}
	//**************************************************
	public void exec(Object o) {
		exec(new Object[]{o});
	}
	//**************************************************
	public execMetodo(Object o,String metodo) {
		this.o = o;
		this.metodo = metodo;
		this.m = getMethod(o,metodo);
	}
	//**************************************
	Method getMethod(Object o,String nome) {
		Class c = o.getClass();
		while (c!=null) {
			Method m[] = c.getDeclaredMethods();
			for (short i=0;i<m.length;i++) {
				if (m[i].getName().equals(nome)) {
					return m[i];
				}
			}
			//logs.grava("c="+c.getName());
			c = c.getSuperclass();
		}
		return null;
	}
}