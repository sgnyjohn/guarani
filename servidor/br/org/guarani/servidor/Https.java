/*
* Signey John fev/2002. 
* 2016 set - https
*          e com - cliente key auth
* 
* 2020 user certif logon
* 
* celular android não importa certificados para o navegador
* a) usar vpn x509 ?
* 		android aceita PPTP,L2TP/IPSec PSK,L2TP/IPSec RSA,
* 			IPSec Xauth PSK, IPSec Xauth RSA, IPSec Hybrid RSA
* 		vantagens: poderia navegar conex casa com anon
* 		https://www.strongswan.org/download.html
* b) navegador próprio.
* 		webview
* 		https://medium.com/@vee.eye/supporting-x509-in-the-webview-c46c7c8d4a52
* 		inclusive o do chromium não aceita x509 
* c) webview + proxy x509?
* 
* toDo
* a)	classe que se comunica x509 com esta
* b)	opcional o tipo auth, https class setaria  
* 			? keyNeedClientAuth cfrme IP ?
* 			"SSL alert number 42" - servidor EXIGIU certificado cliente
* 			https://developer.android.com/training/articles/security-ssl
* 
* 
* ATENÇÃO:
* 	no raspberry o consumo de cpu do HTTPS (java SSLServerSocket) limita a transferencia a 2,26MB/s
* 		consumindo 100% cpu, no HTTP (java) a velocidade bate na velocidade REDE 11,2MB/s 
* 	a) testar https no lighttpd e comparar.
* 			lighttpd CRAVOU NO LIMITE REDE 52% cpu - classes SSLServerSocket JAVA ineficiente?
* 			JVM parece (lsof) ter codigo próprio TLS (não depende de openSSL)
* 	b) muitos flush? NÃO parece (estava em 0,5 segundos, passei para 20, velocidade =)
* 	https://www.google.com/search?client=firefox-b-d&q=%22SSLServerSocket%22+inefficiency
*/ 
	
	

package br.org.guarani.servidor; 

import br.org.guarani.util.*;

import java.io.*;
import java.net.*;
import java.security.*;
import java.security.Principal;
import java.security.cert.X509Certificate;
//import org.apache.tomcat.net.*;
import javax.net.ssl.*;

 
public class Https extends Http {
	void log(String s) {
		logs.grava("https",s);
	}
	//********************************
	//seta socket
	public void setSocket(Socket s) {
		super.setSocket(s);		
	}
	//********************************
	//retorna serversocket
	public ServerSocket criaServerSocket(int porta) throws IOException {
		https = true;
		//SSLSocketFactory ssl = new SSLSocketFactory();
		//ssl.setCfg(cnf);
		//return ssl.createSocket(porta);
		
		//***********************************
		/*SSLContext sslContext 
			= SSLConnections.getSSLContext(cnf.get("keystore"), cnf.get("keystorepass"));
		*/

		String keyFile = (String)cnf.get("keyFile");
		String keyFilePass = (String)cnf.get("keyFilePass");
		String keyFileClient = (String)cnf.get("keyFileClient");
		String keyFileClientPass = (String)cnf.get("keyFileClientPass");
		//SSLServerSocket.setWantClientAuth(keyWantClientAuth):
		boolean keyWantClientAuth = (""+cnf.get("keyWantClientAuth")).equals("true");
		// NeedClientAuth
		boolean keyNeedClientAuth = (""+cnf.get("keyNeedClientAuth")).equals("true");

		// You can't use ssl without a server certificate.
		// Create a KeyStore ( to get server certs )
		//KeyStore kstore = initKeyStore( keystoreFile, keyPass );
		try {
			KeyStore kstore=KeyStore.getInstance( "JKS" );
			kstore.load(new FileInputStream(keyFile), keyFilePass.toCharArray());

			// Key manager will extract the server key
			javax.net.ssl.KeyManagerFactory kmf = javax.net.ssl.KeyManagerFactory.getInstance("SunX509");
			kmf.init( kstore, keyFilePass.toCharArray());

			// localiza chave servidor.
			final String keyHost = (String)cnf.get("keyHost");
			final X509KeyManager orig = (X509KeyManager)kmf.getKeyManagers()[0];
			//X509Certificate xv[]	= ok.getCertificateChain(keyHost);
			//X509KeyManager kn = new X509KeyManager(xv);
			X509KeyManager km = new X509KeyManager() {
				public String chooseClientAlias(String[] keyType,Principal[] issuers, Socket socket) {
					// Implement your alias selection, possibly based on the socket
					// and the remote IP address, for example.
					log("chooseClientAlias keyType="+keyType
						+" issuers="+issuers+" socket="+socket
					);
					return orig.chooseClientAlias(keyType,issuers,socket);
				}
				public String[] getClientAliases(String keyType,Principal[] issuers) {
					log("getClientAliases keyType="+keyType
						+" issuers="+issuers
					);
					return orig.getClientAliases(keyType,issuers);
				}
				public String chooseServerAlias(String keyType,Principal[] issuers, Socket socket) {
					//aparece seguido, keytype RSA ou EC, inssuers null, e socket - Socket[addr=/10.10.10.1,port=54070,localport=8443]
					// log("chooseServerAlias keyType="+keyType
					//	+" issuers="+issuers+" socket="+socket
					//);
					return keyHost;
				}				
				public String[] getServerAliases(String keyType,Principal[] issuers) {
					log("getServerAliases keyType="+keyType
						+" issuers="+issuers
					);
					return new String[]{keyHost};
				}				
				public PrivateKey getPrivateKey(String alias) {
					//server aparece seguido log("getPrivateKey alias="+alias);
					return orig.getPrivateKey(alias);
				}
				public X509Certificate[] getCertificateChain(String alias) {
					//server aparece seguido log("getCertificateChain alias="+alias);
					return orig.getCertificateChain(alias);
				}
			};
			

			// If client authentication is needed, set up TrustManager
			//
			// aceita certificados de usuário assinados por ca diferente que a do servidor.
			//
			javax.net.ssl.TrustManager[] tm = null;
			if( keyFileClient != null ) {
				KeyStore kstorec=KeyStore.getInstance( "JKS" );
				kstorec.load(new FileInputStream(keyFileClient), keyFileClientPass.toCharArray());
				javax.net.ssl.TrustManagerFactory tmf = javax.net.ssl.TrustManagerFactory.getInstance("SunX509");
				tmf.init(kstorec);
				tm = tmf.getTrustManagers();
				//////////////////////////////////////////
				// debug...
				log("carregado certif auth clients n="+tm.length
					+" classe="+tm[0].getClass().getName() //=classe=sun.security.ssl.X509TrustManagerImpl
				); 
				X509Certificate v[] = ((X509TrustManager)tm[0]).getAcceptedIssuers();
				for (int i=0;i<v.length;i++) {
					log(i+" ==> "+v[i].getSubjectDN().getName());
				}
			}

			// Create a SSLContext ( to create the ssl factory )
			// This is the only way to use server sockets with JSSE 1.0.1
			javax.net.ssl.SSLContext context = javax.net.ssl.SSLContext.getInstance("TLS"); //SSL
			// init context with the key managers
			context.init(new KeyManager[] { km }, tm, null); //kmf.getKeyManagers()

			SSLServerSocketFactory sslProxy 
				= context.getServerSocketFactory();
			SSLServerSocket sslServerSocket
				= (SSLServerSocket) sslProxy.createServerSocket(porta);
			//config
			sslServerSocket.setWantClientAuth(keyWantClientAuth);
			sslServerSocket.setNeedClientAuth(keyNeedClientAuth);

			log("socket TLS ok");
			
			return sslServerSocket;		

		} catch (Exception ex) {
			log("ERRO criando HTTPS socket "+ keyFile + ": " + ex.getMessage()+" "+str.erro(ex));
			throw new IOException( "Exception trying to load keystore " + keyFile + ": " + ex.getMessage() ); 

		}
	}
}
