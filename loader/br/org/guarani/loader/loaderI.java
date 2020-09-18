package br.org.guarani.loader;

import java.util.Hashtable;
import java.io.File;

//**************************************************************
//**************************************************************
public class loaderI implements loaderInterface,Runnable {
	private int pos,sit=-1;
	xmlTagL cfg;
	//**************************************************************
	public void estat(Hashtable h) {
		load.estat(h);
	}
	//**************************************************************
	public void stop(int sinal) {
		System.out.println("***********************************");
		System.out.println("**   saindo sinal: "+sinal);
		System.exit(sinal);
	}
	//**************************************************************
	public void run() {
	}
	//**************************************************************
	public File resLoc(String ch) {
		ch = strL.troca(ch,".","/");
		return resLoc(ch,".res");
	}
	//**************************************************************
	public File resLoc(String ch,String ext) {
		for (int i=0;i<cfg.size();i++) {
			xmlTagL x = cfg.get(i);
			if (x.nome.equals("resource")) {
				//ogsL.grava("procurando em="+x.getAtr("path")+"/"+ch+ext);
				File f = new File(x.getAtr("path")+"/"+ch+ext);
				if (f.exists()) {
					if (f.isDirectory()) {
						//retorna a raiz...
						return new File(x.getAtr("path"));
					}
					return f;
				}
			}
		}
		if ("~relatorio~versao~".indexOf("~"+strL.substrRat(ch,"/")+"~")==-1) {
			//logsL.grava("n Achou RES: "+ch+" ext="+ext+" "+strL.erro(new Exception()));
		}
		return null;
	}
	//**************************************************************
	public boolean resExclui(String ch) {
		ch = strL.troca(ch,".","/");
		File f = resLoc(ch);
		f.delete();
		return !f.exists();
	}
	//**************************************************************
	public boolean resMove(String ch,String ch1) {
		ch = strL.troca(ch,".","/");
		File f = resLoc(ch);
		ch1 = strL.troca(ch1,".","/");
		File f1 = resLoc(ch1);
		if (f1.exists()) {
			logsL.grava("erro","loaderI.resMove: rename "+ch+" para "+ch1+" nome destino existe ");
			return false;
		}
		f.renameTo(f1);
		return f1.exists();
	}
	//**************************************************************
	//static int xx=0;
	public boolean resGrava(String ch,String tx) {
		ch = strL.troca(ch,".","/");
		File f = resLoc(ch);
		if (f!=null) {
			logsL.grava("res ALTEROU ch="+ch+" gr aq="+f);//+" tx="+tx);
			/*arquivoL a = new arquivoL("");
			a.gravaTxt(new File("/tmp/"+ch+"-"+dataL.ms()+"-"+(xx++)),
				"res ALTEROU ch="+ch+" gr aq="+f+" tx="+tx+"\n"+strL.erro(new Exception())
			);
			*/
			return load.arq.gravaTxt(f,tx);
		}
	
		//novo? - localiza dir
		String ch1 = ch;
		while (f==null && ch1.indexOf("/")!=-1) {
			logsL.grava("ch1="+ch1);
			ch1 = strL.leftRat(ch1,"/");
			f = resLoc(ch1,"");
		}
		//assume q está na RESource 0
		if (f==null) {
			f = new File(cfg.get(0).getAtr("path"));
			logsL.grava("Assumindo na RESOURCE 0: "+f+" "+ch);
		}
		if (f==null || !f.isDirectory()) {
			logsL.grava("não sei o q fazer, grava "+ch);
			//return false;
		}
		//retornou dir da raiz....
		File fd = new File(f+"/"+strL.leftRat(ch,"/"));
		if (!fd.exists()) {
			fd.mkdirs();
		}
		f = new File(f+"/"+ch+".res");
		logsL.grava("res NOVA ch="+ch+" gr aq="+f);
		return load.arq.gravaTxt(f,tx);
	}
	//**************************************************************
	public String resUrl(String ch) {
		return ""+resLoc(ch,"")+"/"+ch;
	}	
	//**************************************************************
	public Hashtable resCarregaS(String ch) {
		ch = strL.troca(ch,".","/");
		Hashtable r = new Hashtable();
		File fd = resLoc(ch,"");
		if (fd==null || !fd.isDirectory()) {
			return r;
		}
		fd = new File(""+fd+"/"+ch);
		File vf[] = fd.listFiles();
		if (vf==null) {
			//logs.grava("não achei="+fd);
			return r;
		}
		for (int i=0;i<vf.length;i++) {
			if (!vf[i].isDirectory()) {
				File f = vf[i];
				if (f.exists()) {
					String c = strL.leftRat(f.getName(),".");
					Hashtable d = new Hashtable();
					d.put("defs",load.arq.leTxt(f));
					d.put("ch",ch+"."+c);
					r.put(""+r.size(),d);
				}
			}
		}
		return r;
	}
	//**************************************************************
	public String resCarrega(String ch) {
		File f = resLoc(ch);
		if (f==null || f.isDirectory()) {
			return null;
		}
		return load.arq.leTxt(f);
	}
	//**************************************************************
	public Class loadClass(String nome) {
		//return null;
		try {
			return load.loadClass(pos,nome);
		} catch (Exception e) {
		}
		return null;
	}
	//**************************************************************
	public String compil() {
		return load.compila();
	}
	//**************************************************************
	public boolean reLoad() {
		return sit!=-1;
	}
	//**************************************************************
	public boolean reset() {
		sit = load.testa();
		if (sit==-1) { 
			//nada a fazer
		} else if (sit<=pos) {
			return true;
		}
		return false;
	}
	//**************************************************************
	loaderI(int Pos) {
		cfg = load.cfgR;
		//x.getElementsByTagName("resource").get(0);
		pos = Pos;
	}
}
