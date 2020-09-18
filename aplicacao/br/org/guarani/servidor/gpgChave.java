
package br.org.guarani.servidor;

import br.org.guarani.util.*;


//***************************************
//***************************************
public class gpgChave {
	String keyId,bits,importada,criada,vcto,userId,esc;
	String keyIdS,bitsS,importadaS,criadaS,vctoS;
	String senha;
	//***************************************
	public String getNome() {
		return userId;
	}
	//***************************************
	public String mostra() {
		return "<tr>"
			+"<td rowspan=2 class=gpgI"+importada+">"+str1.html(userId)
				+"<br><div align=right>"
				+"<input onclick=desv('?op=_assinaArq&keyId="+keyId
					+"',event); type=button value=\"Assinar Arquivo1\">"
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
	public gpgChave(String pub[]) {
		//logs.grava("tam="+pub.length+" = "+str.dPalav(pub," | "));
		keyId = pub[4];
		bits = pub[2];
		importada = pub[1];
		criada = pub[5];
		vcto = pub[6];
		userId = pub[9];
		esc = pub[11];
	}
}
