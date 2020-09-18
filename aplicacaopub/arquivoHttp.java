

import br.org.guarani.servidor.*;
import br.org.guarani.util.*;

public class arquivoHttp extends pagina {
	private arquivo arq;
	private Pedido ped;
	public arquivoHttp(Pedido pd,String aq) {
		ped = pd;
		arq = new arquivo(aq);
	}
 
}
