
/*
	Signey - mai/2005
	//importar para mudar senhas dominio
*/

package br.org.guarani.servidor;

import java.util.*;
import java.io.*;

import br.org.guarani.util.*;
import br.org.guarani.servidor.*;


//***************************************
//***************************************
public class gpg extends xmlTag {
	private static Hashtable bases = new Hashtable();
	Hashtable chaves = new Hashtable();
	String dirCh; // = "/home/signey/.gnupg";
	String apl;
	//***************************************
	public String getId(String nome) {
		for (Enumeration e = chaves.elements();e.hasMoreElements();) {
			chave c = (chave)e.nextElement();
			if (c.userId.toLowerCase().indexOf(nome.toLowerCase())!=-1) {
				return c.keyId; //"0x"+str.right(c.keyId,8);
			}
		}
		return null;
	}
	//***************************************
	public String getNome(String id) {
		chave c = (chave)chaves.get(id);
		return c.userId;
	}
	//***************************************
	public gpg(Pag pg,String dir) {
		//super(this.getClass().getName());
		dirCh = dir;
		apl = "/usr/bin/gpg --homedir "+dirCh;
		init(pg);
	}
	//***************************************
	public boolean assinaArq(Pag pg,String arq,String ch) {
		return assinaArq(pg,arq,ch,null);
	}
	//***************************************
	public boolean assinaArq(Pag pg,String arq,String ch,String ds) {
		//gpg -v -a --textmode --clearsign -u 0x04956069 -otmptkpgp.asc tmptkpgp.txt
		//--no-textmode
		String cmd = apl
			+" -v --yes -a --batch --passphrase-fd 0   -u 0x"
			+str.right(ch,8);
		if (ds==null) {
			cmd += " --textmode --clearsign "+arq;
		} else {
			ds = str.vazio(ds)?arq+".sig":ds;
			cmd += " --no-textmode -o"+ds+" --detach-sign "+arq;
		}
		pg.on("<hr>"+cmd+"<hr>");
		chave c = (chave)chaves.get(ch);
		execP(pg,cmd,c.senha+"\n");
		return true;
	}
	//***************************************
	public void nova(Pag pg,String tx) {
		//execP(pg,apl+" --gen-key --batch --armor",tx);
		execP(pg,apl+" --gen-key --batch ",tx);
		bases.remove(dirCh);
	}
	//***************************************
	public void init(Pag pg) {
		executa e = new executa();
		e.exec(pg.ped,apl+" --with-colons --list-keys");
		String c = e.getOut();
		//pg.on(c);
		String v1[] = str.palavraA(c,"\n");
		chave ch=null;
		for (int i=0;i<v1.length;i++) {
			//on("<br>"+str1.html(v1[i]));
			String l[] = str.palavraA(v1[i],":");
			if (l.length==1) {
			} else if (l[0].equals("pub")) {
				ch = new chave(l);
				chaves.put(ch.keyId,ch);
				//on("<br>"+l.length);
			} else if (l[0].equals("tru"))  {
			} else if (l[0].equals("uid"))  {
				//uid:-::::1999-03-13::6FD4D592311BFE757AC1C4376D55CFF20EBEC918::Martin Schulze <joey@infodrom.north.de>:
				//ids adicionais, 
			} else if (l[0].equals("sub"))  {
				ch.sub(l);
			} else {
				pg.on("<hr>Não sei o q é isto...="+l[0]+" "+l[1]+" "+v1[i]);
			}
		}
	}
	//***************************************
	public void toString(Pag pg) {
		pg.on("<table class=gpgChLista>");
		int nv=0;
		for (Enumeration en = chaves.elements();en.hasMoreElements();) {
			chave ch = (chave)en.nextElement();
			if (nv++==0) {
				pg.on(ch.mostraCab());
			}
			pg.on(ch.mostra());
		}
		pg.on("</table>");
		if (true) return;
		/*pg.on("<h2>Chaves</h2>"
			+"<pre>"+str1.html(c)+"</pre>"
		);
		*/
		String v[] = new String[]{"secring.gpg","pubring.gpg","trustdb.gpg"};
		for (int i=0;i<v.length;i++) {
			executa e = new executa();
			e.exec(pg.ped,apl+" --with-colons --list-packets "+dirCh+"/"+v[i]);
			pg.on("<h2>packets "+v[i]+"</h2>"
				+"<pre>"+str1.html(e.getOut())+"</pre>"
			);
		}
		executa e = new executa();
		e.exec(pg.ped,apl+" --with-colons --list-config");
		pg.on("<h2>Config</h2>"
			+"<pre>"+e.getOut()+"</pre>"
		);
	}
	//***************************************
	public void cryptSign(Pag pg) {
		String cm = apl
			//+" --no-tty"
      +" --no-secmem-warning"
      +" --no-options"
      +" --no-default-keyring"
      +" --quiet"
      +" --yes"
      //+" --homedir "+dirCh
		;
		
		//'--armor',  '--batch', '--always-trust', '--recipient ' . $email, $keyring,'--encrypt'
		cm = apl+" --passphrase-fd 0 --no-default-keyring --batch --always-trust ";
		//--homedir "+dirCh;
		
		//tkpg = gpg -v -a --textmode --encrypt --sign -u 0x04956069 -r 0x04956069 -otmptkpgp.asc tmptkpgp.txt
		String aDs = "/tmp/lixo.asc";
		File fDs = new File(aDs);
		if (fDs.exists()) {
			pg.on("<br>Deletando "+aDs+": "+fDs.delete());
		}

		cm += " -sea -r signey -o "+aDs+" /home/signey/unison.txt";
		pg.on("<br>"+cm);
		//if (true) return;
		execP(pg,cm,"vinteedois\n");
		if (fDs.exists()) {
			pg.on("<hr>Gerou: <pre>"+(new arquivo(aDs)).leTxt()+"</pre>");
		} else {
			pg.on("<hr>Arq Saida "+aDs+" não existe...");
		}
	}
	//***************************************
	public void execP(Pag pg,String cm,String digita) {
		try {
			//inicia processo
			Process p = Runtime.getRuntime().exec(cm,null);
			//lança thread de acompanhamento
			ByteArrayOutputStream rEr = new ByteArrayOutputStream(4096);
			ByteArrayOutputStream rIn = new ByteArrayOutputStream(4096);
			
			BufferedInputStream bEr = new BufferedInputStream(p.getErrorStream());
			BufferedInputStream bIn = new BufferedInputStream(p.getInputStream());
			OutputStream bOu = p.getOutputStream();
			
			gpg1 gEr = new gpg1(bEr,rEr);
			gpg1 gIn = new gpg1(bIn	,rIn);
			gpg1 gOu = new gpg1(bOu,digita);
			
			gEr.start();
			gIn.start();
			gOu.start();
			
			//espera processo terminar
			p.waitFor();
			Thread.sleep(1000);
			
			//fecha tudo
			gEr.join();bEr.close();
			gIn.join();	bIn.close();
			gOu.join();bOu.close();
			
			
			p.destroy();
			
			pg.on("<hr>saiu: "+p.exitValue()
				+"<hr>Er: "+gEr.toString()
				+"<hr>In: "+gIn.toString()
				+"<hr>Ou: "+gOu.toString()
			);
			
			
		} catch (Exception e) {
			pg.on("ERRO: "+e);
		}
	}
	//***************************************
	public static gpg getBase(Pag pg,String dir) {
		gpg g = (gpg)bases.get(dir);
		if (g==null) {
			logs.grava("NOVO "+dir);
			g = new gpg(pg,dir);
			bases.put(dir,g);
		}
		return g;
	}
	//***************************************
	//***************************************
	public class chave {
		String keyId,bits,importada,criada,vcto,userId,esc;
		String keyIdS,bitsS,importadaS,criadaS,vctoS;
		String senha="minhafrase";
		//***************************************
		public String mostra() {
			return "<tr>"
				+"<td rowspan=2 class=gpgI"+importada+">"+str1.html(userId)
					+"<br><div align=right>"
					+"<input onclick=desv('?op=_assinaArq&keyId="+keyId+"',event); type=button value=\"Assinar Arquivo\">"
					+"</div>"
				+"<td>"+keyId
				+"<td>"+criada+"<br>"+vcto+"<td>"+bits
				+(keyIdS!=null
					?"<tr><td>"+keyIdS
						+"<td>"+criadaS+"<br>"+vctoS+"<td>"+bitsS
				:"<tr>")
			;
		}
		//***************************************
		public String mostraCab() {
			//String r = "<table class=gpgLista>"
			return "<tr><th>user<th>id<th>criada<br>vcto<th>bits";
			//;
			//return r+"</table>";
		}
		//***************************************
		public void sub(String sub[]) {
			keyIdS = sub[4];
			bitsS = sub[2];
			importadaS = sub[1];
			criadaS = sub[5];
			vctoS = sub[6];
		}
		//***************************************
		public chave(String pub[]) {
			logs.grava("tam="+pub.length+" = "+str1.palavraA(pub," | "));
			keyId = pub[4];
			bits = pub[2];
			importada = pub[1];
			criada = pub[5];
			vcto = pub[6];
			userId = pub[9];
			esc = pub[11];
		}
	}
	
	//***************************************
	//***************************************
	public class gpg1 extends Thread {
		int tBf = 4096;
		byte[] buf;
		boolean fim = false;
		BufferedInputStream is;
		OutputStream out;
		InputStream inp;
		public boolean erro = false;
		public String sErro;
		int by = 0;
		//***************************************
		public String toString() {
			String r = "nGrv="+by+(erro?" ERRO: "+sErro:"");
			if (is!=null) {
				r += "<br>"+str.troca(out.toString(),"\n","<br>");
			}
			return r;
		}
		//***************************************
		void grava() {
			if (!fim) {
				try {
					out.write(buf,0,tBf);
					by = tBf;
					out.close();
				} catch (IOException e) {
					erro = true;
					sErro = "ERRO: "+str.erro(e);
				}
			}
		}
		//***************************************
		void le() {
			if (!fim) {
				try {
					int nb = is.read(buf,0,tBf);
					if (nb==-1) {
						fim = true;
					} else {
						by += nb;
						out.write(buf,0,nb);
					}
				} catch (IOException e) {
					erro = true;
					sErro = "ERRO: "+str.erro(e);
				}
			}
		}
		//***************************************
		public void run() {
			if (is!=null) {
				buf = new byte[tBf];
				while (!fim) {
					le();
				}
			} else {
				grava();
			}
				
		}
		//***************************************
		public gpg1(OutputStream out,String manda) {
			tBf = manda.length();
			buf = new byte[tBf];
			for (int i=0;i<tBf;i++) {
				buf[i] = (byte)manda.charAt(i);
			}
			this.out = out;
		}
		//***************************************
		public gpg1(BufferedInputStream is, OutputStream out) {
			this.is = is;
			this.out = out;
		}
	}
	/*
		signey@socrates:~$ gpg --gen-key
		gpg (GnuPG) 1.4.0; Copyright (C) 2004 Free Software Foundation, Inc.
		This program comes with ABSOLUTELY NO WARRANTY.
		This is free software, and you are welcome to redistribute it
		under certain conditions. See the file COPYING for details.
		
		Por favor selecione o tipo de chave desejado:
			 (1) DSA and Elgamal (default)
			 (2) DSA (apenas assinatura)
			 (5) RSA (apenas assinatura)
		Sua opÃ§Ã£o? 1
		DSA keypair will have 1024 bits.
		ELG-E keys may be between 1024 and 4096 bits long.
		What keysize do you want? (2048)
		O tamanho de chave pedido Ã© 2048 bits
		Por favor especifique por quanto tempo a chave deve ser vÃ¡lida.
						 0 = chave nÃ£o expira
					<n>  = chave expira em n dias
					<n>w = chave expira em n semanas
					<n>m = chave expira em n meses
					<n>y = chave expira em n anos
		A chave Ã© valida por? (0)
		A Key nÃ£o expira nunca
		Is this correct? (y/N) y
		
		You need a user ID to identify your key; the software constructs the user ID
		from the Real Name, Comment and Email Address in this form:
				"Heinrich Heine (Der Dichter) <heinrichh@duesseldorf.de>"
		
		Nome completo: 3W Sistemas
		O nome nÃ£o pode comeÃ§ar com um dÃ­gito
		Nome completo: T3W Sistemas
		EndereÃ§o de correio eletrÃ´nico: teste@3wsistemas.com.br
		ComentÃ¡rio:
		VocÃª selecionou este identificador de usuÃ¡rio:
				"T3W Sistemas <teste@3wsistemas.com.br>"
		
		Muda (N)ome, (C)omentÃ¡rio, (E)ndereÃ§o ou (O)k/(S)air? O
		VocÃª precisa de uma frase secreta para proteger sua chave.
		
		Precisamos gerar muitos bytes aleatÃ³rios. Ã uma boa idÃ©ia realizar outra
		atividade (digitar no teclado, mover o mouse, usar os discos) durante a
		geraÃ§Ã£o dos nÃºmeros primos; isso dÃ¡ ao gerador de nÃºmeros aleatÃ³rios
		uma chance melhor de conseguir entropia suficiente.
		.+++++++++++++++++++++++++..+++++++++++++++++++++++++.++++++++++..++++++++++..++++++++++++++++++++.+++++++++++++++++++++++++.+++++...++++++++++>+++++.+++++.........+++++
		Precisamos gerar muitos bytes aleatÃ³rios. Ã uma boa idÃ©ia realizar outra
		atividade (digitar no teclado, mover o mouse, usar os discos) durante a
		geraÃ§Ã£o dos nÃºmeros primos; isso dÃ¡ ao gerador de nÃºmeros aleatÃ³rios
		uma chance melhor de conseguir entropia suficiente.
		..++++++++++.+++++++++++++++++++++++++.+++++.+++++.++++++++++.+++++.+++++++++++++++.+++++.+++++.+++++++++++++++.+++++..+++++++++++++++..++++++++++++++++++++.+++++.+++++>.+++++.......s.df.o.ji...s.d.f..sd..f..............+++++^^^
		gpg: key A8EE6586 marked as ultimately trusted
		chaves pÃºblica e privada criadas e assinadas.
		
		gpg: a verificar a base de dados de confianÃ§a
		gpg: 3 marginal(s) needed, 1 complete(s) needed, PGP trust model
		gpg: depth: 0  valid:   2  signed:   0  trust: 0-, 0q, 0n, 0m, 0f, 2u
		pub   1024D/A8EE6586 2005-05-05
					Key fingerprint = 4AAD FB40 2518 8DD0 E6EC  B30B 96C8 59EA A8EE 6586
		uid                  T3W Sistemas <teste@3wsistemas.com.br>
		sub   2048g/F5A0527A 2005-05-05

	*/
}
