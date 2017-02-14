import java.util.Random;

class Barber extends Thread {
	private Barbershop barbershop;
	private int i;
	public Barber(int i, Barbershop bs) {
		super("barber" + i);		//Names barber thread.
		barbershop = bs;
		this.i = i;
	}
	private int produce() {
		int item = (new Random()).nextInt(100);
		System.out.println(getName() + " produces " + item);
		return item;
	}
	public void run() {
		while (true) {
			barbershop.addToBuffer(i);
			try {
				sleep(300);
			} catch (InterruptedException e) {
			}
		}
	}
}
