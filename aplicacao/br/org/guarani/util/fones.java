package br.org.guarani.util;

import java.util.*;
import java.io.*;

import br.org.guarani.servidor.*;
import br.org.guarani.bd.*;
import bd.*;

//***************************************
//***************************************
public class fones {
	String dl[] = new String[]{";"};//," ou "," e "," E "," OU "," Ou "};
	public HashIndex hfone = new HashIndex();
	public boolean erro = false;
	//***************************************
	public fones(String s) {
		if (!str.vazio(s)) {
			for (int i=1;i<dl.length;i++) {
				s = str.troca(s,dl[i],dl[0]);
			}
			//logs.grava(s);
			
			String v[] = str.palavraA(s,dl[0]);
			fone pd=null;
			for (int i=0;i<v.length;i++) {
				//logs.grava(v[i]);
				if (!str.vazio(v[i])) {
					fone fn = new fone(v[i],pd);
					hfone.put(""+hfone.size(),fn);
					if (fn.sErro.length()!=0) {
						erro = true;
					}
					if (hfone.size()>0) {
						pd = (fone)hfone.get(hfone.size()-1);
					}
				}
			}
		}
	}
}

/*
SELECT *,length(PessoaEnd) as tm FROM `PessoaEnd`
WHERE C_PessoaEndTipo=2 ORDER BY tm desc

update PessoaEnd SET PessoaEnd=trim(PessoaEnd) WHERE PessoaEnd<>trim(PessoaEnd)
*/
