class Customer extends Thread {
	private Barbershop barbershop;
	public Customer(int i, Barbershop bs) {
		super("customer" + i); 		//Names customer thread.
		barbershop = bs;
	}
	private void consume(int item) {
		System.out.println(getName() + " consumes " + item);
	}
	public void run() {
		while (true) {
			int item = barbershop.removeFromBuffer();
			consume(item);
			try {
				sleep(300);
			} catch (InterruptedException e) {
			}
		}
	}
}
