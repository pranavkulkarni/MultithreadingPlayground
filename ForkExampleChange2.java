// Demonstrating waited chain interaction. Thread 3 waits for thread 2 which in turn waits for thread 1
package section2;

public class ForkExampleChange2 implements Runnable {

	int i; // the ID of the thread, so we can control behavior
	boolean busy; // the flag, Thread 1 will wait until Thread 0 is no longer busy before continuing
	ForkExampleChange2 other; // reference to the other thread we will synchronize on. This is needed so we can control behavior.

	// create the runnable object
	public ForkExampleChange2(int i, ForkExampleChange2 other) {
		this.i = i; // set the thread ID (0 or 1)
		busy = true;  // set the busy flag so Thread 1 waits for Thread 0 and Thread 2 waits for Thread 1
		this.other = other;
	}

	// synchronized method to test if thread is busy or not
	public synchronized boolean isBusy() { return busy; } // What happens if this isn't synchronized? 

	// run method needed by runnable interface
	public void run() {
		if(i==0) { // 1st thread, sleep for a while, then notify threads waiting
			try {
				Thread.sleep(4000); // What happens if you put this sleep inside the synchronized block?
				synchronized(this) {
					notify(); // notify() will only notify threads waiting on *this* object;
				}
				Thread.sleep(4000); // What happens if you put this sleep inside the synchronized block?
				synchronized(this) {
					busy = false; // must synchronize while editing the flag
					notify(); // notify() will only notify threads waiting on *this* object;
				}
			}
			catch(InterruptedException tie) { tie.printStackTrace(); }
		}
		else {
			while(other.isBusy()) { // check if other thread is still working
				System.out.println("Thread " + i + " Waiting!");
				// must sychnronize to wait on other object
				try { synchronized(other) { other.wait(); } } // note we have synchronized on the object we are going to wait on
				catch(InterruptedException tie) { tie.printStackTrace(); }
			}
			synchronized(this) {
				busy = false; // must synchronize while editing the flag
				notify(); // notify() will only notify threads waiting on *this* object;
			}
			
			System.out.println("Thread " + i + " finished!");
		}
	}

	public static void main(String[] args) {
		ForkExampleChange2 t0 = new ForkExampleChange2(0, null);
		ForkExampleChange2 t1 = new ForkExampleChange2(1, t0);
		ForkExampleChange2 t2 = new ForkExampleChange2(2, t1);
		(new Thread(t2)).start();
		(new Thread(t1)).start();
		(new Thread(t0)).start();
	}
}

/*
In this program, I am trying to create a dependency of threads on each other by introducing a chain like waiting
structure. Thread 0 waits for no one and keeps executing. Thread 1 waits for Thread 0 to release busy-wait and 
finish and Thread 2 waits for Thread 1 to release busy-wait and finish. In this way there is a dependency like
Thread 2 --> Thread 1 --> Thread 0. In general this can be chained to n number of threads.  
*/
