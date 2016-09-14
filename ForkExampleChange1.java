// Demonstrating deadlock
package section2;

public class ForkExampleChange1 implements Runnable {

	int i; // the ID of the thread, so we can control behavior
	boolean busy; // the flag, Thread 1 will wait until Thread 0 is no longer busy before continuing
	ForkExampleChange2 other; // reference to the other thread we will synchronize on. This is needed so we can control behavior.

	// create the runnable object
	public ForkExampleChange1(int i, ForkExampleChange2 other) {
		this.i = i; // set the thread ID (0 or 1)
		if(i==0) { busy = true; } // set the busy flag so Thread 1 waits for Thread 0
		else { this.other = other; }
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
					//busy = false; // must synchronize while editing the flag
					//check for other thread's busy state
					while(other.isBusy()) {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println("Thread 0: I am waiting for the other thread.");
					}
					busy = false;
					notify(); // notify() will only notify threads waiting on *this* object;
				}
			}
			catch(InterruptedException tie) { tie.printStackTrace(); }
		}
		else {
			while(other.isBusy()) { // check if other thread is still working
				System.out.println("Thread 1: I am waiting for the other thread.");
				System.out.println("Thread 1: Will wait for 2 seconds and put myself as busy if I am not already busy.");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				synchronized(this) {
					if(!busy) {
						busy = true;
						System.out.println("Thread 1: I have woken up and will put myself as busy.");
					}
				}
				// must sychnronize to wait on other object
				try { synchronized(other) {
					System.out.println("Thread 1: I will wait for thread 0 to notify me.");
					other.wait(); } } // note we have synchronized on the object we are going to wait on
				catch(InterruptedException tie) { tie.printStackTrace(); }
			}
			System.out.println("Finished!");
		}
	}

	public static void main(String[] args) {
		ForkExampleChange2 t1 = new ForkExampleChange2(0, null);
		ForkExampleChange2 t2 = new ForkExampleChange2(1, t1);
		t1.other = t2;
		(new Thread(t2)).start();
		(new Thread(t1)).start();
	}

}

/*
Write up:
In this program, I have created 2 threads - thread id 0 and thread id 1. Thread 1 is initially not busy 
and after 2 seconds puts itself as busy. In the meanwhile, thread 0 has already started executing and it checks for 
thread 1's busy state after 8 seconds by which time, thread 1 had become busy. Thread 1 keeps waiting for thread 0
to notify but thread 0 cannot notify until thread 1 comes out of busy state. Hence there is a deadlock.
*/
