package bd;
import java.sql.*;
import java.util.*;

import br.org.guarani.servidor.*;
import br.org.guarani.util.*;


/**
	* Write a description of class DadosSet here.
	*
	* @author signey
	* @version jul/2001
	*/
public class DadosSet {
	public Dados parent;
	public ResultSet rs = null;
	public boolean erro,fechado = false;
	//ResultSet rs;
	//Pedido ped;
	//public boolean erro;
	public static String dl[] = new String[]{"\n","\t","\\n","\\t"};
	public String sql;

	//****************************************
	public void log(String s) {
		logs.grava("jdbc",s);
	}
	//****************************************
	public void toCsv(Pag pg) {
		int i=0;
		//head
		int nc=contaCampos();
		pg.o(getNomeCampo(1));
		for (i=2;i<=nc;i++) {
			pg.o("\t"+getNomeCampo(i));
		}
		pg.on("");
		//dados
		while (next()) {
			pg.o(getString(1));
			for (i=2;i<=nc;i++) {
				pg.o("\t"+getString(i));
			}
			pg.on("");
		}
	}
	//****************************************
	public GregorianCalendar getCalendar(String nome) {
		return null;
	}
	//****************************************
	public boolean campoExiste(String nome) {
		return true;
	}
	//****************************************
	public boolean fim() {
		return false;
	}
	//****************************************
	public String sql() {
		return null;
	}
	//****************************************
	public boolean absolute(int reg) {
		return false;
	}
	//****************************************
	public int nRegs() {
		return -1;
	}
	//****************************************
	public boolean mostra(Pedido ped) {
		return false;
	}
	//****************************************
	public boolean mostra(String atalho,String op) {
		return false;
	}
	//****************************************
	public boolean next() {
		return false;
	}
	//****************************************
	public String getStringB(String c) {
		return null;
	}
	//****************************************
	public byte[] getBytes(String c) {
		return null;
	}
	//****************************************
	public double getDuplo(String s) {
		return -1;
	}
	//****************************************
	public double getDuplo(int s) {
		return -1;
	}
	//****************************************
	public java.sql.Timestamp getDateTime(String s) {
		return null;
	}
	//****************************************
	public java.sql.Date getDate(String s) {
		return null;
	}
	//****************************************
	public long getLongo(String s) {
		return -1;
	}
	//****************************************
	public long getLongo(int s) {
		return -1;
	}
	//****************************************
	public int getInt(String s) {
		return getInteiro(s);
	}
	//****************************************
	public int getInteiro(String s) {
		return -1;
	}
	//****************************************
	public int getInteiro(int s) {
		return -1;
	}
	//****************************************
	public String getString(String s) {
		return null;
	}
	//****************************************
	public String getString(String s,String s1) {
		return null;
	}
	//****************************************
	public String getString(int pos) {
		return null;
	}
	//****************************************
	public boolean close() {
		return false;
	}
	//****************************************
	public boolean erro() {
		return false;
	}
	//****************************************
	public void erro(String s, Exception e) {}
	//****************************************
	//public void RsMd();
	//****************************************
	public int contaCampos() {
		return -1;
	}
	//****************************************
	public Hashtable getHashtable() {
		return null;
	}
	//****************************************
	public String[] getVetor() {
		return null;
	}
	//****************************************
	public String getNomeCampo(int i) {
		return null;
	}
	//****************************************
	//public void estru();
}

