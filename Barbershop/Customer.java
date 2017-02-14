class Customer extends Thread {
	private Barbershop barbershop;
	public Customer(int i, Barbershop bs) {
		super("Customer" + i); 		//Names customer thread.
		barbershop = bs;
	}
	public void run() {
        barbershop.customersBuffer();
        try {
            sleep(3000);
        } catch (InterruptedException e) {
		}
	}
}
