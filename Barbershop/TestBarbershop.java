//================================================================
//==Ket-Meng Cheng ~ Operating Systems and Computer Architecture==
//== ---------------------------------------------------------- ==
//==                      Program #1                            ==
//================================================================

import java.util.Arrays;

class TestBarbershop {
	static public void main(String[] args) {
		Barbershop bs = new Barbershop();
		for (int i = 0; i <= 2; i++) 
			(new Barber(i, bs)).start();
		for (int i = 0; i <= 50; i++) 
			(new Customer(i, bs)).start();
		//Thread a[] = new Thread[Thread.activeCount()];
		//Thread.enumerate(a);
		//System.out.println(Arrays.toString(a));
	}
}
