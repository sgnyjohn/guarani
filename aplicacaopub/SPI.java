/*
	*  signey 2001
	*/

import java.net.*;
import br.org.guarani.servidor.*;
import br.org.guarani.util.*;

import java.io.*;
import java.text.*;
import java.util.*;

public class SPI implements Prg {

	public boolean run(Pedido ped)  {

		String spi_nro,post;

		spi_nro = str.troca(str.troca(str.seNull(ped.getString("spi_nro"),"243481204969"),"-",""),"/","");
		post = "T=govrs_processos_e_con_001.html&P=govrs_processos_s_con_001.html&N13-PROC="+spi_nro+"&x=39&y=15";


		try {

			Socket sk = new Socket("bogota.procergs.com.br",80);
			sk.setSoTimeout(5000);
			OutputStreamWriter ou = new OutputStreamWriter(sk.getOutputStream());
			String a  = "POST /cgi-bin/webgen.exe HTTP/1.1\n";

			a += "Accept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-excel, application/msword, application/vnd.ms-powerpoint, */*\n";
			a += "Referer: http://bogota.procergs.com.br/govrs/govrs_processos_e_con_001.html\n";
			a += "Accept-Language: pt-br\n";
			a += "Content-Type: application/x-www-form-urlencoded\n";
			a += "Accept-Encoding: gzip, deflate\n";
			a += "User-Agent: Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)\n";

			//s
			a += "Host: bogota.procergs.com.br\n";
			//s
			a += "Content-Length: "+post.length()+"\n";

			a += "Connection: Keep-Alive\n";
			a += "\n";
			//243481204969
			a += post;

			ou.write(a,0,a.length());
			ou.flush();


			BufferedReader in = new BufferedReader(new InputStreamReader(sk.getInputStream()));

			String inputLine;
			int cab = 0;

			while ((inputLine = in.readLine()) != null) {
				if (cab!=1) {
					cab += ((inputLine.length() == 0) ? 1 : 0);
				} else {
					ped.println(inputLine);
				}
			}


			in.close();

		} catch (IOException e) {
		}

		return true;

		}

}
