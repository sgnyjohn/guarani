/*

signey jul/2015

*/

package br.org.guarani.servidor;

import java.util.*;
import java.io.*;

import java.net.InetAddress;
import java.util.Properties;
import java.util.Date;
import javax.mail.*;
import javax.mail.internet.*;
import com.sun.mail.smtp.*;


public class logMail {
	static String de = "signey.john@al.rs.gov.br";
	static String smtpHost = "correio.al.rs.gov.br"; //correio.al.rs.gov.br,ptservidor, 172.30.1.40
	static String smtpT = "smtp";//"smtp" smtps
	static String smtpPort = "25";//465 1465 25
	static String smtpUser = "signey.john";
	static String smtpPass = "xy350y350";
	static String dirMsg = "/mnt/web/pag/correio/conf";
	static String sErro;

	//**************************************************
	// log
	public static boolean log(String assunto,String texto) {
		return envia(de,assunto,texto);
	}
	//**************************************************
	// envia email
	public static boolean envia(String para,String assunto,String texto) {
		boolean r = true;
		try {
			Properties props = System.getProperties();
			props.put("mail."+smtpT+".host",smtpHost);
			props.put("mail."+smtpT+".port",smtpPort);
			props.put("mail."+smtpT+".auth","false");
			props.put("mail.smtp.socketFactory.checkserveridentity", "false");
			props.put("mail.smtps.socketFactory.checkserveridentity", "false");
			/*props.put("mail.smtp.ssl.checkserveridentity", "false");
			props.put("mail.smtps.ssl.checkserveridentity", "false");
			props.put("mail.smtp.ssl.trust", "*");
			*/
			Session session = Session.getInstance(props, null);
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(de));;
			msg.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(para, false)
			);
			msg.setSubject(assunto);
			if (false) {
				msg.setText(texto);
			} else {
				//msg.setContent(texto,"text/html");
				msg.setContent(texto,"text/html; charset=UTF-8");
			}
			msg.setHeader("X-Mailer", "ptsul.com.br");
			//msg.setHeader("Content-Type: text/html; charset=UTF-8");
			msg.setSentDate(new Date());
			SMTPTransport t =	(SMTPTransport)session.getTransport(smtpT);
			t.connect(smtpHost, smtpUser, smtpPass);
			t.sendMessage(msg, msg.getAllRecipients());
			//System.out.println("Response: " + t.getLastServerResponse());
			t.close();
		} catch (Exception e) {
			r = false;
			sErro = "ERRO envianto email "+e;
		}
		
		return r;
	}
}
