//================================================================
//==Ket-Meng Cheng ~ Operating Systems and Computer Architecture==
//== ---------------------------------------------------------- ==
//==                      Program #1                            ==
//================================================================

import java.util.Arrays;

class Barbershop {
    private int inBarberChair = 0, couch = 4, barberChairs = 3, 
            hairCut = 0, count = 20, action = 3, finished = 0, 
            payment = 0, receipt = 0, readyCust = 0, finishedCut = 0,
            atReg = 0, sit = 0, sitCouch = 0, leaveCouch = 0;

    private String[] barberChairBuffer = new String[3];
    private String[] couchBuffer = new String[4];

    public Barbershop() {
        for (int i = 0; i < 3; i++)                 //3 barber chairs.  
            barberChairBuffer[i] = "Empty";
        for (int j = 0; j < 4; j++)                 //4 couch spots.
            couchBuffer[j] = "Empty";
    }
    /**
     * Sits the customer on the couch.
     * 
     * Removes people waiting in the waiting area and places them on the couch, 
     * adding them to the couchBuffer array.
     */
    private void sitCouch() { 
        couchBuffer[sitCouch] = Thread.currentThread().getName();
        sitCouch = (sitCouch + 1) % couchBuffer.length;
        System.out.println( Thread.currentThread().getName() + " sits on the couch: " + Arrays.toString(couchBuffer));
    }
    /**
     * Removes the customer from the couch.
     *
     * Removes the customer from the couchBuffer by finding a person and setting
     * it to empty.
     */
    private void leaveCouch() {
        couchBuffer[leaveCouch] = "Empty";
        leaveCouch = (leaveCouch + 1) % couchBuffer.length;
        System.out.println( Thread.currentThread().getName() + " leaves the couch: " + Arrays.toString(couchBuffer));
    }
    /**
     * Sits a customer from the couchBuffer onto a barberChair.
     * 
     * Looks for the last customer on the couchBuffer and removes them from
     * the chair, placing them onto the barberChair buffer.
     */
    private void sitBarberChair() {
        barberChairBuffer[inBarberChair] = Thread.currentThread().getName();
        inBarberChair = (inBarberChair + 1) % barberChairBuffer.length;
        System.out.println( Thread.currentThread().getName() + " sits in barber chair: " + Arrays.toString(barberChairBuffer));
    }
    /**
     * Cuts a customer's hair.
     *
     * Removes customer from the barberChairBuffer after cutting.
     */
    private void cutHair() {
        String cust = barberChairBuffer[hairCut];
        barberChairBuffer[hairCut] = "Empty";
        hairCut = (hairCut + 1) % barberChairBuffer.length;
        System.out.println( Thread.currentThread().getName() + " cuts " + cust + "'s hair " + Arrays.toString(barberChairBuffer) );
    }
    /**
     * Performs cashier operations.
     *
     * Operated by a free barber, performs transactions one at a time.
     */
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
        receipt++;			//semSignal(receipt);
        notifyAll();
    }
    /**
     * Performs barber's operations.
     *
     * Cuts customer's hair, otherwise sleep.
     */
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
        action--;                //Semaphores for cutting performing an action
        cutHair();
        action++;                //semSignal(action);
        notifyAll();
        finishedCut++;           //semSignal(finish);
        notifyAll();
        while (atReg == 0) {     //leave_b_chair
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
    /**
     * Performs customer's operations.
     *
     * Goes from the waiting area, to the couch, to the barber's chair,
     * performs transactions and leaves.
     */
    public synchronized void customersBuffer() {
        System.out.println(Thread.currentThread().getName() + " enters.");
        while (count == 0)        //If the waiting area is full.
            try {
                System.out.println(Thread.currentThread().getName() + " notices that the store is full and leaves.");
                wait();
            } catch (InterruptedException e) {
            }
        count--;                  //Increase count of people.
        System.out.println(Thread.currentThread().getName() + " waits in the waiting area.");
        while (couch == 0)        //If the couch is full.
            try {
                wait();
            } catch (InterruptedException e) {
            }
        couch--;                  //Gets into couch.
        System.out.println(Thread.currentThread().getName() + " moves to the couch and waits.");
        sitCouch();
        while (barberChairs == 0) //If no barbers chairs are available.
            try {
                wait();
            } catch (InterruptedException e) {
            }
        barberChairs--;           //semSignal(payment);
        leaveCouch();
        couch++;                  //Gets out from couch.
        notifyAll();
        sitBarberChair();
        readyCust++;
        notifyAll();
        while (finishedCut == 0)  //If no barbers are available for cutting hair.
            try {
                System.out.println(Thread.currentThread().getName() + " is getting their hair cut.");
                wait();
            } catch (InterruptedException e) {
            }
        finishedCut--;
        atReg++;                  //semSignal(leave_b_chair);
        notifyAll();
        System.out.println(Thread.currentThread().getName() + " hands over cash.");
        payment++;		          //semSignal(payment);
        notifyAll();
        while (receipt == 0)      //If no barbers are available for cutting hair.
            try {
                System.out.println(Thread.currentThread().getName() + " waits for receipt.");
                wait();
            } catch (InterruptedException e) {
            }
        receipt--;
        System.out.println(Thread.currentThread().getName() + " takes receipt and exits.");
        count--;
        if (count == 0)
            notifyAll();
    }
}
