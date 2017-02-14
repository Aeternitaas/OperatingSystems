import java.util.Arrays;
import java.util.Random;

class Barbershop {
	private int inBarberChair = 0, inCouch = 0, leaveCouch = 0, couch = 0, waiting = 0, barbers = 0, barberChairs = 3, out = 0, atReg = 0, cashiers = 0, count = 0, action = 0, finished = 0, payment = 0, receipt = 0, readyCust = 0;
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
	private void goToBarberChair() {
        for (int i = 0; i < couchBuffer.length; i++) 
            if (couchBuffer[i].equals(Thread.currentThread().getName()))
                couchBuffer[i] = "Empty";
		System.out.println( Thread.currentThread().getName() + " leaves the couch and moves to the barber chair: " + Arrays.toString(couchBuffer));
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
		while (action == 1) 
            try {
                wait();
            } catch (InterruptedException e) {
            }
        action = 1;
        System.out.println(Thread.currentThread().getName() + " accepts payment.");
        action = 0; 
        receipt = 1;
    }
	public synchronized void barbersBuffer(){
		System.out.println(Thread.currentThread().getName() + " is ready to cut hair.");
		while (readyCust == 0)
            try {
                System.out.println(Thread.currentThread().getName() + " has no customers and goes to sleep.");
                wait();
            } catch (InterruptedException e) {
            }
        readyCust = 0;
        while (action == 1)
            try {
                wait();
            } catch (InterruptedException e) {
            }
        action = 1;                                //Semaphores for cutting performing an action.
        cutHair();
        action = 0;
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
        while (count == 20)                   //If the waiting area is full.
			try {
				System.out.println(Thread.currentThread().getName() + " notices that the store is full and leaves.");
				wait();
			} catch (InterruptedException e) {
            }
		count++;                            //Increase count of people.
        while (couch == 4)                  //If the couch is full.
			try {
				System.out.println(Thread.currentThread().getName() + " waits in the waiting area.");
				wait();
			} catch (InterruptedException e) {
			}
        couch++;                            //Gets into couch.
        System.out.println(Thread.currentThread().getName() + " moves to the couch.");
        sitCouch();
        while (barberChairs == 0)            //If no barbers chairs are available.
			try {
				System.out.println(Thread.currentThread().getName() + " waits in the couch.");
				wait();
			} catch (InterruptedException e) {
			}
        couch--;                             //Gets out from couch.
        barberChairs--;                      //Signal ready for haircut
        goToBarberChair();
        sitBarberChair();
        readyCust = 1;
        notifyAll();
        while (barbers == 0)                 //If no barbers are available for cutting hair.
			try {
				System.out.println(Thread.currentThread().getName() + " waits in the barber chair.");
				wait();
			} catch (InterruptedException e) {
			}
        System.out.println(Thread.currentThread().getName() + " gets a haircut.");
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
        payment = 1;
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
