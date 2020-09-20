/*
	signey ago/2010 - 
*/
package br.org.guarani.servidor;

	import br.org.guarani.util.*;

	import java.net.*;
	import java.io.*;
	import java.lang.*;
	import java.util.*;
	//import guarani.es.*;

/*config

[Proxy]
porta=8181
nTask=100
classe=br.org.guarani.servidor.Proxy
classeP=br.org.guarani.servidor.authProxy
proxyPorta=8888
proxyHost=127.0.0.1

*/


//********************************
//********************************
public class Proxy extends ProtocoloAbstrato {
	Hashtable conf;
	OutputStream o;
	InputStreamReader i;
	int tmbf = 1024*10;	
	byte bf[] = new byte[tmbf];
	// do codigo copiado
	final byte[] request = new byte[1024];
	byte[] reply = new byte[4096];
	String proxyHost,classeP;
	int proxyPorta;
	Class classePc;
	PrgProxy classePo;
	//
	public String ln1; //primeira linha do pedido
	public InputStream streamFromClient;
	public OutputStream streamToClient;
	public int tr,tw,tt;
	//********************************
	public boolean valida() {
		if (classeP==null) {
			return false;
		}
		//logs.grava("guarani.dev="+Guarani.dev+" "+classeP);
		if (Guarani.dev || classePc==null) {
			if (Guarani.dev) {
				Guarani.compilar();
			}
			classePc = Guarani.findClass(classeP,true);
			try {
				classePo = (PrgProxy)classePc.newInstance();
			} catch (Exception e) {
				logs.grava("Proxy.java erro instanciando "+classeP);
				return false;
			}
		}
	
		return classePo.run(this);
	
	}
	//********************************
	public void run() {
		nPedidos++;
		in = System.currentTimeMillis();
		nBytes = 0;
		tr = 0;
		tw = 0;
		
		// baseado em http://www.java2s.com/Code/Java/Network-Protocol/Asimpleproxyserver.htm
		Socket client = null, server = null;
		try {
			// Wait for a connection on the local port
			
			client = sp;

			streamFromClient = client.getInputStream();
			streamToClient = client.getOutputStream();
			
			//le primeira linha pedido
			ln1 = readLine(streamFromClient);
		
			//precisa validação?
			if (valida()) {
				fim();
				return;
			}
			

			// Make a connection to the real server.
			try {
				server = new Socket(proxyHost, proxyPorta);
			} catch (IOException e) {
				PrintWriter out = new PrintWriter(streamToClient);
				out.print("Proxy server cannot connect to " + proxyHost + ":"
						+ proxyPorta + ":\n" + e + "\n");
				out.flush();
				client.close();
				fim();
				return;
			}

			// Get server streams.
			final InputStream streamFromServer = server.getInputStream();
			final OutputStream streamToServer = server.getOutputStream();

			// a thread to read the client's requests and pass them
			// to the server. A separate thread for asynchronous.
			Thread t = new Thread() {
				public void run() {
					int bytesRead;
					try {
						//manda 1a linha
						streamToServer.write((ln1+"\n").getBytes(), 0, ln1.length()+1);
						while ((bytesRead = streamFromClient.read(request)) != -1) {
							tr += bytesRead;
							streamToServer.write(request, 0, bytesRead);
							streamToServer.flush();
						}
					} catch (IOException e) {
					}

					// the client closed the connection to us, so close our
					// connection to the server.
					try {
						streamToServer.close();
					} catch (IOException e) {
					}
				}
			};

			// Start the client-to-server request thread running
			t.start();

			// Read the server's responses
			// and pass them back to the client.
			int bytesRead;
			try {
				while ((bytesRead = streamFromServer.read(reply)) != -1) {
					tw += bytesRead;
					streamToClient.write(reply, 0, bytesRead);
					streamToClient.flush();
				}
			} catch (IOException e) {
			}

			// The server closed its connection to us, so we close our
			// connection to our client.
			streamToClient.close();
		} catch (IOException e) {
			System.err.println(e);
		} finally {
			try {
				if (server != null)
					server.close();
				if (client != null)
					client.close();
			} catch (IOException e) {
			}
		}
		
		fim();
	
	}
	//***************************//
	void fim() {
		fi = System.currentTimeMillis();
		nBytes += tr+tw;
		tt = (int)(fi-in);
		if (classePo!=null) {
			classePo.fim(this);
		}
		sp = null;
		tempo += fi-in;
		rodando = false;
		return;
	}
	//***************************//
	public String readLine(InputStream i) throws java.io.IOException {
		int c1=0,t=0;
		while (c1!=10 && t<tmbf) {
			c1 = i.read();
			if (c1==-1) {
				break;
			}
			bf[t++] = (byte)c1;
		}
		if (t>0 && bf[t-1]=='\n') t--;
		if (t>0 && bf[t-1]=='\r') t--;
		return new String(bf,0,t);
	}		
	//********************************
	//inicializa
	public void init(Hashtable c) {
		conf = c;
		proxyPorta = str.inteiro(""+c.get("proxyPorta"),8080);
		proxyHost = (c.get("proxyHost")==null?"127.0.0.1":""+c.get("proxyHost"));
		classeP = (String)c.get("classeP");
		//if (classeP!=null) {
		//	classeP = str.troca(classeP,".","/")+".class";
		//}
	}
	//********************************
	public String getPedido() {
		return "?";
	}
}

/*
http://www.java2s.com/Code/Java/Network-Protocol/Asimpleproxyserver.htm


import java.io.*;
import java.net.*;

public class SimpleProxyServer {
  public static void main(String[] args) throws IOException {
    try {
      String host = "your Proxy Server";
      int remoteport = 100;
      int localport = 111;
      // Print a start-up message
      System.out.println("Starting proxy for " + host + ":" + remoteport
          + " on port " + localport);
      // And start running the server
      runServer(host, remoteport, localport); // never returns
    } catch (Exception e) {
      System.err.println(e);
    }
  }

  // **
   * runs a single-threaded proxy server on
   * the specified local port. It never returns.
   * //
  public static void runServer(String host, int remoteport, int localport)
      throws IOException {
    // Create a ServerSocket to listen for connections with
    ServerSocket ss = new ServerSocket(localport);

    final byte[] request = new byte[1024];
    byte[] reply = new byte[4096];

    while (true) {
      Socket client = null, server = null;
      try {
        // Wait for a connection on the local port
        client = ss.accept();

        final InputStream streamFromClient = client.getInputStream();
        final OutputStream streamToClient = client.getOutputStream();

        // Make a connection to the real server.
        // If we cannot connect to the server, send an error to the
        // client, disconnect, and continue waiting for connections.
        try {
          server = new Socket(host, remoteport);
        } catch (IOException e) {
          PrintWriter out = new PrintWriter(streamToClient);
          out.print("Proxy server cannot connect to " + host + ":"
              + remoteport + ":\n" + e + "\n");
          out.flush();
          client.close();
          continue;
        }

        // Get server streams.
        final InputStream streamFromServer = server.getInputStream();
        final OutputStream streamToServer = server.getOutputStream();

        // a thread to read the client's requests and pass them
        // to the server. A separate thread for asynchronous.
        Thread t = new Thread() {
          public void run() {
            int bytesRead;
            try {
              while ((bytesRead = streamFromClient.read(request)) != -1) {
                streamToServer.write(request, 0, bytesRead);
                streamToServer.flush();
              }
            } catch (IOException e) {
            }

            // the client closed the connection to us, so close our
            // connection to the server.
            try {
              streamToServer.close();
            } catch (IOException e) {
            }
          }
        };

        // Start the client-to-server request thread running
        t.start();

        // Read the server's responses
        // and pass them back to the client.
        int bytesRead;
        try {
          while ((bytesRead = streamFromServer.read(reply)) != -1) {
            streamToClient.write(reply, 0, bytesRead);
            streamToClient.flush();
          }
        } catch (IOException e) {
        }

        // The server closed its connection to us, so we close our
        // connection to our client.
        streamToClient.close();
      } catch (IOException e) {
        System.err.println(e);
      } finally {
        try {
          if (server != null)
            server.close();
          if (client != null)
            client.close();
        } catch (IOException e) {
        }
      }
    }
  }
}

*/