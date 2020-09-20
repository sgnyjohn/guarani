package br.org.guarani.util;

//import br.org.guarani.util.*;

import java.text.*;

//***************************************
//***************************************
public class extenso {
	static String sDec = ""+ 
		(new DecimalFormatSymbols()).getDecimalSeparator();
	static String sMil = ""+ 
		(new DecimalFormatSymbols()).getGroupingSeparator();
	static NumberFormat nf = NumberFormat.getInstance();
	final static String centenas[] = 
		{"?","cento", "duzentos", "trezentos",
			"quatrocentos", "quinhentos", "seicentos", "setecentos",
				"oitocentos", "novecentos"
		};
	final static String dezena1[] = 
		{"?","onze", "doze", "treze", "quatroze", "quinze",
			"dezesseis", "dezessete", "dezoito", "dezenove"
		};
	final static String dezenas[] =
		{"?","dez", "vinte", "trinta", "quarenta",
			"cinqüenta", "sessenta", "setenta", "oitenta", "noventa"
		};
	final static String unidade[] =
		{"?","um", "dois", "três", "quatro", "cinco",
			"seis", "sete", "oito", "nove"
		};
	final static String mils[] = 
		{"mil", "milhões", "bilhões", "trilhões"};
	final static String mil[] =
		{"mil", "milhão", "bilhão", "trilhão"}; 
	//***************************************
	public static String moeda(double db) {
		String vVar = extenso.str(db,15);
		double in = str.duplo(vVar,0);
		String vVar1 = extenso.extInt(db);
		if (!str.vazio(vVar1)) {
			vVar1 += (vVar.substring(9).equals("000000")?"de ":"")
				+ (in>1?"reais ":"real ");
		}
		int c = (int)Math.round((db-in)*100);
		if (c!=0) {
			vVar1 += (in!=0?"e ":"") 
				+ extenso.ext3(extenso.str(c, 3))
				+ (c>1?"centavos":"centavo");
			if (in==0) {
				vVar1 += " de real";
			}
		}
		return str.trimm(vVar1);
	}
	//***************************************
	public static String str(double db,int tam) {
		return str.right("               "
			+str.troca(str.leftAt(nf.format(db)+sDec,sDec),sMil,""),tam);
	}
	//***************************************
	public static String extInt(double db) {
		String _Def = str.troca(extenso.str(db,15)," ","0");
		String _Def1;
		int nNum = 1;
		String vVar = "";
		String cStr;

		while (nNum < 6) {
			cStr = _Def.substring(nNum*3-3,nNum*3);
			vVar += extenso.ext3(cStr);
			if (str.inteiro(cStr,0)!=0 && nNum!=5) {
				if (str.inteiro(cStr,0)>1) {
					vVar += mils[4-nNum];
				} else {
					vVar += mil[4-nNum];
				}
				if (str.duplo(_Def.substring(nNum*3),0)!=0) {
					if (str.duplo(_Def.substring(nNum*3+3),0)!=0) {
						vVar += ", ";
					} else {
						vVar += " e ";
					}
				} else {
					vVar += " ";
				}
			}
			nNum++;
		}
		return vVar;
	}
	//***************************************
	public static String ext3(String cStr) {
		int nNum, nNum1, nNum2;
		String cStr1;

		cStr = cStr.length()!=3?str.right("   "+cStr,3):cStr;
		cStr1 = "";
		if (str.inteiro(cStr,0)!=0) {
			nNum = str.inteiro(""+cStr.charAt(0),0);
			nNum1 = str.inteiro(""+cStr.charAt(1),0);
			nNum2 = str.inteiro(""+cStr.charAt(2),0);

			if (nNum!=0) {
				if (nNum1 + nNum2==0 && nNum == 1) {
					cStr1 += "cem ";
				} else {
					cStr1 += centenas[nNum] + " ";
				}

				if (nNum1 + nNum2 != 0) {
					cStr1 += "e ";
				}
			}

			if (nNum1==1 && nNum2!=0) {
				cStr1 += dezena1[nNum2] + " ";
			} else {
				if (nNum1!=0)  {
					cStr1 += dezenas[nNum1] + " ";
					if (nNum2!=0) {
						cStr1 += "e ";
					}
				}
				if (nNum2!=0) {
					cStr1 += unidade[nNum2] + " ";
				}
			}
		}
		return cStr1;
	}
}

