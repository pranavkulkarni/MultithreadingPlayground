// Demonstrating multithreading and thread synchronization in Java using Locks 
// Dining philosophers' problem
package section2;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ForkExampleChange4 {
	public static void main(String args[]) {
		Fork one = new Fork();
		Fork two = new Fork();
		Philosopher x = new Philosopher(one, two ,1);
		Philosopher y = new Philosopher(two, one, 2);
		(new Thread(x)).start();
		(new Thread(y)).start();
	}
}

class Philosopher implements Runnable {
	Fork left, right;
	int id;
	
	public Philosopher(Fork left, Fork right, int id) {
		this.left = left;
		this.right = right;
		this.id = id;
	}
	
	public void run() {
		while(true) {
			eat();
		}
		
	}
	
	public void eat() {
		if(pickBothForks()) {
			//both forks available to eat i.e. both locks obtained
			System.out.println("Philosopher " + id + " picked up both forks...");
			System.out.println("Philosopher " + id + " eating...");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				System.out.println("Philosopher " + id + " putting forks down...");
				left.putDown();
				right.putDown();
			}
		} 
	}
	
	public boolean pickBothForks() {
		if(!left.pickUp()) {
			return false;
		} 
		if(!right.pickUp()) {
			left.putDown();
			return false;
		}
		return true;
	}
}

class Fork {
	Lock lock = new ReentrantLock();
	
	public boolean pickUp() {
		return lock.tryLock();
	}
	
	public void putDown() {
		lock.unlock();
	}
}