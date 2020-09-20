package br.org.guarani.servidor;

import br.org.guarani.util.*;

class fim extends Thread {
	long minPrime;

	public fim() {
	}

	public void run() {
		logs.grava("FIIMM fim..");
	}

}
