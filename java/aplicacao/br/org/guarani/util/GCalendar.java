package br.org.guarani.util;
import java.util.*;
public class GCalendar extends GregorianCalendar {
	public GCalendar() {
		super();
	}
	public GCalendar(long d) {
		this(new Date(d));
	}
	public GCalendar(Date d) {
		super();
		setTime(d);
	}
	public GCalendar(TimeZone zone) {
		super(zone);
	}
	public GCalendar(Locale locale) {
		super(locale);
	}
	public GCalendar(TimeZone zone, Locale locale) {
		super(zone,locale);
	}
	public GCalendar(int year, int month, int day) {
		super(year, month, day);
	}
	public GCalendar(int year, int month, int day, int hour, int minute) {
		super(year, month, day, hour, minute);
	}
	public GCalendar(int year, int month, int day,int hour, int minute, int second) {
		super(year, month, day, hour, minute, second);
	}
}
