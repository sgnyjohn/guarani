/*
	Signey - mai/2003
*/

package br.org.guarani.util;

import java.lang.reflect.*;
import java.util.*;
import java.text.*;

import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.DayOfWeek;

//***************************************
//***************************************
public class data1 extends data {
	public static final long hora = 60L*60L*1000L; 
	public static final long dia = 24L*hora;
	public static String diaSemEN[] = str.palavraA("Sun,Mon,Tue,Wed,Thu,Fri,Sat",",");
	public static String mesEN = ",jan,feb,mar,apr,may,jun,jul,aug,sep,oct,nov,dec";
	public static String mesPT = ",jan,fev,mar,abr,mai,jun,jul,ago,set,out,nov,dez";
	public static DateFormat http = new SimpleDateFormat( "E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
	public static DateTimeFormatter weekParser = new DateTimeFormatterBuilder()
            .appendPattern("YYYY-ww")
            .parseDefaulting(ChronoField.DAY_OF_WEEK, DayOfWeek.SUNDAY.getValue())
            .toFormatter(Locale.getDefault())
  ;
	//***************************************
	// milisegundos as zero horas de hoje
  public static long msZeroHora(long ms) {
		Date d = new Date(ms);
		return new Date(d.getYear(),d.getMonth(),d.getDay()).getTime();
	}	
	//***************************************
	// milisegundos as zero horas de hoje
  public static long msDia() {
		return java.sql.Date.valueOf(LocalDate.now()).getTime();
	}	
	//***************************************
	// semana,ano
	public static String semanaAno() {
		return semana(data.ms());
	}
	public static String semana(LocalDate ld) {
		return ld.format( DateTimeFormatter.ISO_WEEK_DATE ).substring(0,8);
	}
	public static String semana(long v) {
		LocalDate ld = LocalDate.ofEpochDay(v/dia);
		return ld.format( DateTimeFormatter.ISO_WEEK_DATE ).substring(0,8);
	}
	public static LocalDate semanaToLocalDate2(String s) {
		DateTimeFormatter weekParser = new DateTimeFormatterBuilder()
							.appendPattern("YYYY-ww")
							.parseDefaulting(ChronoField.DAY_OF_WEEK, DayOfWeek.SUNDAY.getValue())
							.toFormatter(Locale.getDefault())
		;	
    return LocalDate.parse(s, weekParser);	
	}
	public static LocalDate semanaToLocalDate1(String s) {
    return LocalDate.parse(s, weekParser);	
	}
	public static LocalDate semanaToLocalDate(String s) {
		int ano = str.inteiro(str.leftAt(s,"-"),-1);
		int sem = str.inteiro(str.right(s,2),-1);
		LocalDate d = LocalDate.of(ano,1,1).minusDays(4-7*(sem-1));
		while (data1.semana(d).compareTo(s)<0) {
			d = d.plusDays(1);
		}
		return d;
	}
	public static long semanaToLong(String s) {
		return semanaToLocalDate(s).toEpochDay()*dia-21*hora;		
	}
	//***************************************
	// tempo
	public static String diasHMS(long dif) {
		dif = dif/1000;
		short sg = (short)(dif%60);
		dif = dif/60;
		short mi = (short)(dif%60);
		dif = dif/60;
		short hr = (short)(dif%24);
		long di = dif/24;
		return (di>0?di+"d ":"")
			+(hr>0?hr+"h ":"")
			+(mi>0?mi+"m ":"")
			+sg+"s "
		;
	}
	//***************************************
	public static String linux(String s) {
		return linux(dateSql(s),s.indexOf(" ")!=-1);
	}
	//***************************************
	public static String linux(Date d,boolean hora) {
		if (str.leftAt(data.strSql()," ").equals(str.leftAt(data.strSql(d)," "))) {
			return "Hoje";
		}
		return (hora?strSql(d):strSql(d).substring(0,10));
	}
	//***************************************
	public static String dataLog(String s) {
		String v[] = str1.palavraA(str.troca(s,"/",":"),":");
		// 30/Jul/2011:22:26:15
		if (v.length > 5) { 
			return v[2]+"-"+mes(v[1])+"-"+v[0]+" "+v[3]+":"+v[4]+":"+v[5]; 
		}
		return "";
	}
	//***************************************
	public static String mes(String s) {
		s = s.toLowerCase().substring(0,3);
		int i = mesPT.indexOf(","+s);
		if ( i == -1 ) {
			i = mesEN.indexOf(","+s);
		}
		return str.strZero( i / 4  + 1 ,2);
	}
	//***************************************
	public static String dataTwSql(String s) {
		String v[] = str.palavraA(s," ");
		//Sat Oct 24 20:53:34 +0000 2009
		return v[5]+"-"+str.strZero( mesEN.indexOf(v[1].toLowerCase())/4 + 1 ,2)+"-"+v[2]+" "+v[3];
	}
	//***************************************
	public static String http(Date d) {
		http.setTimeZone(TimeZone.getTimeZone("GMT"));
		return http.format(d);
	}
	//***************************************
	public static Date http(String s) {
		http.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date d = null;
		try {
			d = http.parse(s);
		} catch (Exception e) {
		}		
		return d;
	}
	//***************************************
	public static Date iDia(Date dt,int i) {
		GCalendar d = new GCalendar(dt);
		int ha = d.get(Calendar.HOUR_OF_DAY);
		//d.roll(Calendar.DAY_OF_WEEK,i);
		d.roll(Calendar.DATE,i);
		int hn = d.get(Calendar.HOUR_OF_DAY);
		if (hn<ha || (hn==23 && ha==0) ) {
			//dia com 25 horas
			d.roll(Calendar.HOUR_OF_DAY,1);
		}
		return d.getTime();
	}
	//***************************************
	public static String strSql(GCalendar g) {
		return data.strSql(g.getTime());
	}
	//***************************************
	public static Date somaMs(Date d,long dur) {
		return new Date(d.getTime()+dur);
	}
	//***************************************
	public static long duracao(String dur) {
		String v[] = str.palavraA(dur,":");
		return (str.longo(v[0],0)*24*60
			+str.longo(v[1],0)*60
			+str.longo(v[2],0))*60000;
	}
	//***************************************
	public static Date somaDur(Date d,String dur) {
		return somaMs(d,duracao(dur));
	}
	//**********************************
	public static Date resetHora(Date d) {
		GCalendar g = new GCalendar(d.getYear()+1900,d.getMonth(),d.getDate(),0,0,0);
		//logs.grava("d="+d+" r="+g.getTime());
		return g.getTime();
	}
	//*********************************
	public static long resetHora(long d) {
		return resetHora(new Date(d)).getTime();
		//return d/dia*dia+2*hora;
	}
	/*********************************
	public static Date resetHora(Date d) {
		return new Date(resetHora(d.getTime()));
	}
	//*********************************
	public static long resetHora(long d) {
		//return resetHora(new Date(d)).getTime();
		return d/dia*dia+2*hora;
	}
	*/
	//*********************************
	public static String ontem() {
		long d = (new Date()).getTime()-3600000*24;
		//Date d = data1.iDia(dateSql(data.strSql()),-1);
		return str.leftAt(data.strSql(d)," ");
	}
	//*********************************
	public static long longSql(String d) {
		return dateSql(d).getTime();
	}
	//*********************************
	public static Date dateSql(String d) {
		int dd[]=new int[6],p=0;
		StringTokenizer st = new StringTokenizer(d," -:");
		while (st.hasMoreTokens()) {
				dd[p++] = str.inteiro(st.nextToken(),-1);
		}
		GCalendar c = new GCalendar(
			dd[0],dd[1]-1,dd[2],dd[3],dd[4],dd[5]
		);
		return c.getTime();
	}
	//*********************************
	public static String sql(String d) {
		if (d==null || d.length()<10) {
			return "";
		}
		return d.substring(8,10)+"/"+d.substring(5,7)+"/"
			+d.substring(2,4)+d.substring(10);
	}
	//*********************************
	//
	public static String str() {
		String r;
		//SimpleDateFormat fus = new SimpleDateFormat ("EEE, dd MMM yyyy HH:mm:ss z");
		SimpleDateFormat fus = 
			new SimpleDateFormat ("EEE, dd/MM/yyyy HH:mm:ss");
		r = fus.format(new Date());
		return r;
	}
	public static final String nomeMes[] = 
		{"janeiro","fevereiro","março","abril",
			"maio","junho","julho","agosto","setembro","outubro",
			"novembro","dezembro"
		};
	//*************************************
	public static String incMes(String dt,int inc) {
		String r,del="";
		int a,m;
		if (dt.indexOf("-")!=-1) {
			String v[] = str.palavraA(dt,"-");
			a = str.inteiro(v[0],-1);
			m = str.inteiro(v[1],-1);
			del = "-";
		} else {
			a = str.inteiro(dt.substring(0,4),-1);
			m = str.inteiro(dt.substring(4,6),-1);
		}
  
		m += inc;
		if (m>12) {
			a += (m/12);
			m = (m % 12);
			if (m==0) m=12;
		}
  
		return a+del+str.strZero(m,2);
  
	}
	//*************************************
	public static String nomeMes(int m) {
		if (m<1 || m>12) return "mes: "+m;
		return nomeMes[m-1];
	}
}

