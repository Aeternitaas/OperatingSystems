import java.util.Arrays;
import java.util.Random;

class Barbershop {
	private int inBarberChair = 0, inCouch = 0, leaveCouch = 0, couch = 4,
		waiting = 0, barbers = 0, barberChairs = 3, out = 0, atReg = 0,
		cashiers = 0, count = 20, action = 3, finished = 0, payment = 0,
		receipt = 0, readyCust = 0;
	private String[] barberChairBuffer = new String[3];
	private String[] couchBuffer = new String[4];
	private String[] registerBuffer = new String[20];

	public Barbershop() {
		for (int i = 0; i < 3; i++)                 //3 barber chairs.  
			barberChairBuffer[i] = "Empty";
		for (int j = 0; j < 4; j++)                 //4 couch spots.
			couchBuffer[j] = "Empty";
		for (int k = 0; k < 20; k++)                //20 potential register slots.
			registerBuffer[k] = "Empty";
	}
	private void sitCouch() { 
		for (int i = 0; i < couchBuffer.length; i++) 
			if (couchBuffer[i].equals("Empty")) {
				couchBuffer[i] = Thread.currentThread().getName();
				break;
			}
		System.out.println( Thread.currentThread().getName() + " sits on the couch: " + Arrays.toString(couchBuffer));
	}
	private void sitBarberChair() {
		barberChairBuffer[inBarberChair] = Thread.currentThread().getName();
		inBarberChair = (inBarberChair + 1) % barberChairBuffer.length;
		System.out.println( Thread.currentThread().getName() + " sits in barber chair: " + Arrays.toString(barberChairBuffer));
	}
	private void leaveCouch() {
		for (int i = 0; i < couchBuffer.length; i++) 
			if (couchBuffer[i].equals(Thread.currentThread().getName()))
				couchBuffer[i] = "Empty";
		System.out.println( Thread.currentThread().getName() + " leaves the couch: " + Arrays.toString(couchBuffer));
	}
	private void cutHair() {
		String cust = barberChairBuffer[out];
		barberChairBuffer[out] = "Empty";
		out = (out + 1) % barberChairBuffer.length;
		System.out.println( Thread.currentThread().getName() + " cuts " + cust + "'s hair " + Arrays.toString(barberChairBuffer) );
	}
	public synchronized void cashierBuffer(){
		while (payment == 0) 
			try {
				System.out.println(Thread.currentThread().getName() + " waits for customer to pay.");
				wait();
			} catch (InterruptedException e) {
			}
		payment--;
		while (action == 0) 
			try {
				wait();
			} catch (InterruptedException e) {
			}
		action--;				//semWait(action);
		System.out.println(Thread.currentThread().getName() + " accepts payment.");
		action++;				//semSignal(action);
		notifyAll();
		receipt = 1;				//semSignal(receipt);
		notifyAll();
	}
	public synchronized void barbersBuffer(){
		System.out.println(Thread.currentThread().getName() + " is ready to cut hair.");
		while (readyCust == 0)
			try {
				System.out.println(Thread.currentThread().getName() + " has no customers and goes to sleep.");
				wait();
			} catch (InterruptedException e) {
			}
		readyCust--;
		while (action == 0)
			try {
				wait();
			} catch (InterruptedException e) {
			}
		action--;                                //Semaphores for cutting performing an action
		cutHair();
		action++;				 //semSignal(action);
		notifyAll();
		finished = 1;
		barbers = 1;
		notifyAll();
		while (atReg == 0) {                      //leave_b_chair
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		atReg--;
		barberChairs++;
		if (couch == 1)
			notifyAll();
	}
	public synchronized void customersBuffer() {
		System.out.println(Thread.currentThread().getName() + " enters.");
		while (count == 0)                   //If the waiting area is full.
			try {
				System.out.println(Thread.currentThread().getName() + " notices that the store is full and leaves.");
				wait();
			} catch (InterruptedException e) {
			}
		count--;                            //Increase count of people.
		System.out.println(Thread.currentThread().getName() + " waits in the waiting area.");
		while (couch == 0)                  //If the couch is full.
			try {
				wait();
			} catch (InterruptedException e) {
			}
		couch--;                            //Gets into couch.
		System.out.println(Thread.currentThread().getName() + " moves to the couch and waits.");
		sitCouch();
		while (barberChairs == 0)            //If no barbers chairs are available.
			try {
				wait();
			} catch (InterruptedException e) {
			}
		barberChairs--;                      //Signal ready for haircut		              //semSignal(payment);
		leaveCouch();
		couch++;                             //Gets out from couch.
		notifyAll();
		sitBarberChair();
		readyCust++;
		notifyAll();
		while (barbers == 0)                 //If no barbers are available for cutting hair.
			try {
				System.out.println(Thread.currentThread().getName() + " waits in the barber chair.");
				wait();
			} catch (InterruptedException e) {
			}
		while (finished == 0)                 //If no barbers are available for cutting hair.
			try {
				System.out.println(Thread.currentThread().getName() + " waits to pay.");
				wait();
			} catch (InterruptedException e) {
			}
		notifyAll();
		atReg = 1;                            //Leaves chair -> Register
		notifyAll();
		System.out.println(Thread.currentThread().getName() + " hands over cash.");
		payment = 1;		              //semSignal(payment);
		notifyAll();
		while (receipt == 0)                 //If no barbers are available for cutting hair.
			try {
				System.out.println(Thread.currentThread().getName() + " waits for receipt.");
				wait();
			} catch (InterruptedException e) {
			}
		System.out.println(Thread.currentThread().getName() + " takes receipt and exits.");
		count--;
		if (count == 0)
			notifyAll();
	}
}
