package br.org.guarani.servidor;

import br.org.guarani.dose.*;
import br.org.guarani.util.*;

//*********************************
//*********************************
public class gServico implements Runnable {
	//*********************************
	public void run() {
		Guarani.log("Rodando a Thread gServico");
		int n=0;
		while (!servico.running() && n<20) {
			Guarani.log("Rodando a Thread gServico="+n);
			try {
				Thread.sleep(200);
				n++;
			} catch (Exception e) {
			}
		}
		
		if (!servico.running()) {
			Guarani.log("ERRO running o SERVICO: "+servico.er);
			System.exit(9);
		}
		
		boolean bEr = true;
		try {
			Guarani g = Guarani.start();
			Guarani.log("running OK gServico");
		
			while (servico.running()) {
				try {
					Thread.sleep(500);
				} catch (Exception e) {
				}
			}
			Guarani.log("FIM running em gServico");
			bEr = false;
		} catch (Exception e) {
			Guarani.log("ERRO running em gServico="+str.erro(e));
		} catch (InternalError e) {
			Guarani.log("ERRO SAIDA1?: "+e);
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			Guarani.log("ERRO SAIDA2?: "+e);
			e.printStackTrace();
		} catch (StackOverflowError e) {
			Guarani.log("ERRO SAIDA3?: "+e);
			e.printStackTrace();
		} catch (UnknownError e) {
			Guarani.log("ERRO SAIDA4?: "+e);
			e.printStackTrace();
		} catch (VirtualMachineError e) {
			Guarani.log("ERRO SAIDA5?: "+e);
			e.printStackTrace();
		}
	
		Guarani.log("parando servico="+bEr);
		servico.stop();
		Guarani.log("parando guarani="+bEr);
		Guarani.stop(0);
	}
}
