import java.util.Arrays;
import java.util.Random;

class ProducerConsumer {
	private int in = 0, sz = 3, out = 0, count = 0;
	private int[] buffer = new int[sz];

	public ProducerConsumer() {
		for (int i = 0; i < sz; i++)
			buffer[i] = -1;
	}
	public ProducerConsumer(int s) {
		sz = s;
		buffer = new int[sz];
		for (int i = 0; i < sz; i++)
			buffer[i] = -1;
	}
	private void add(int item) {
		buffer[in] = item;
		in = (in + 1) % buffer.length;
		System.out.println( Thread.currentThread().getName() + " adds " + item + ": " + Arrays.toString(buffer));
	}
	private int remove() {
		int item = buffer[out];
		buffer[out] = -1;
		out = (out + 1) % buffer.length;
		System.out.println( Thread.currentThread().getName() + " gets " + item + ": " + Arrays.toString(buffer));
		return item;
	}
	public synchronized void addToBuffer(int item) {
		System.out.println(Thread.currentThread().getName() + " enters with " + item);
		while (count == buffer.length)
			try {
				System.out.println(Thread.currentThread().getName() + " goes to sleep.");
				wait();
			} catch (InterruptedException e) {
			}
		add(item);
		count++;
		if (count == 1)
			notifyAll();
	}
	public synchronized int removeFromBuffer() {
		System.out.println(Thread.currentThread().getName() + " enters.");
		while (count == 0)
			try {
				System.out.println(Thread.currentThread().getName() + " goes to sleep.");
				wait();
			} catch (InterruptedException e) {
			}
		int item = remove();
		count--;
		if (count == buffer.length - 1)
			notifyAll();
		return item;
	}
}
