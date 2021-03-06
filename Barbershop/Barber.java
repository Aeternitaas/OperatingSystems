class Barber extends Thread {
	private Barbershop barbershop;
	private int i;
	public Barber(int i, Barbershop bs) {
		super("Barber" + i);		//Names barber thread.
		barbershop = bs;
	}
	public void run() {
		while (true) {
			barbershop.barbersBuffer();
            barbershop.cashierBuffer();
			try {
				sleep(300);
			} catch (InterruptedException e) {
			}
		}
	}
}
