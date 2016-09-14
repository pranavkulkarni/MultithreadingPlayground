// Demonstrating Producer Consumer problem.
package section2;

import java.util.*;
public class ForkExampleChange3 {
	
	public static final int MAX_Q_SIZE = 5;
	
	public static void main(String[] args) {
		LinkedList<Integer> q = new LinkedList<Integer>();
		Producer p = new Producer(q);
		Consumer c = new Consumer(q);
		(new Thread(p)).start();
		(new Thread(c)).start();
	}
}

class Producer implements Runnable {
	Queue<Integer> q;
	int number = 1;
	
	public Producer(LinkedList<Integer> q) {
		this.q = q;
	}
	
	public void run() {
		while(number < 12) {
			
				if(q.size() < ForkExampleChange3.MAX_Q_SIZE) {
					System.out.println("Producer puts: " + produce());
					try {
						synchronized(q) {
							q.notify();
						}
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						synchronized(q) {
							q.wait();
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		}
	}
	
	public synchronized int produce() {
		q.add(number);
		return number++;
	}
}

class Consumer implements Runnable {
	Queue<Integer> q;
	
	public Consumer(LinkedList<Integer> q) {
		this.q = q;
	}
	
	public void run() {
		while(true) {
			
				if(q.size() > 0) {
					System.out.println("Consumer consumes: " + consume());
					synchronized(q) {
						q.notify();
					}
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						synchronized(q) {
							q.wait();
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		}
	}
	
	public synchronized int consume() {
		return q.remove();
	}
}


