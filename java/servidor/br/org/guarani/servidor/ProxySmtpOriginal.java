/*
http://www.salemioche.com/smtp_prog2.htm
© Nicolas JEAN (1998-2001)
protocole SMTP : proxy smtp
Un proxy pour espionner tous les messages envoyés par smtp.
Et hop, voila comment espionner tous ce qui passe.
--------------------------------------------------------------------------------
Il suffit de modifier SMTP SERVER, et de configurer votre client mail pour qu'il utilise votre machine comme serveur SMTP.



--------------------------------------------------------------------------------
*/

import java.io.*;
import java.net.*;

class OneServeur extends Thread{
		private Socket socket, socketToSMTP;
		private BufferedReader fromMailer;
		private PrintWriter toMailer;
		private PrintWriter toSMTP;
		private BufferedReader fromSMTP;

		public static final int PORT = 25;

		public OneServeur(Socket s) throws IOException {
								socket = s;
								fromMailer = new BufferedReader( new InputStreamReader ( socket.getInputStream()));
								toMailer = new PrintWriter( new BufferedWriter ( new OutputStreamWriter ( socket.getOutputStream())),true);

								socketToSMTP = new Socket(InetAddress.getByName("SMTP SERVEUR"),25);
								System.out.println(socketToSMTP);
								fromSMTP = new BufferedReader( new InputStreamReader ( socketToSMTP.getInputStream()));
								toSMTP = new PrintWriter( new BufferedWriter ( new OutputStreamWriter ( socketToSMTP.getOutputStream())),true);

								start();
		}

		public void run() {
				try {
								String str;
								while(true) {

													str = fromSMTP.readLine();
													if ( str == null ) break;
													toMailer.println(str);
													System.out.println(str);

													if ( str.startsWith("354") ) { // data

														do {
																str = fromMailer.readLine();
																if ( str == null ) break;
																toSMTP.println(str);
																System.out.println(str);
														} while ( !str.startsWith(".") );

														if ( str == null ) break;

													} else  {
																	str = fromMailer.readLine();
																	if ( str == null ) break;
																	toSMTP.println(str);
																	System.out.println(str);
													}
								}
								System.out.println("\nclosing ...");
				} catch ( IOException e) {
				} finally {
								try {
																socket.close();
								} catch ( IOException e ) {}
				}
		}

		public static void main(String args[]) throws IOException {
								ServerSocket s = new ServerSocket(PORT);
								System.out.println("Started " + s);

								try {
																while ( true ) {
																								Socket socket = s.accept();
																								try {
																																System.out.println("connection acceptee " + socket);
																																new OneServeur(socket);
																								} catch ( IOException e ) {
																																socket.close();
																								}
																}
								} finally {
																s.close();
								}
		}
}

