package ca.mcgill.ecse420.a3;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main2 {
	final static int maxElements = 100000;
	final static int numExecutionPasses = 1000;
	static FineGrainedSet<Integer> intSet;

	public static void main(String[] args) {
		AtomicBoolean toldToRun = new AtomicBoolean(true);

		class Adder implements Runnable {
			public Adder() {
				return;
			}
			public void run() {
				while(toldToRun.get()) {
					for(int i = maxElements/2; i < maxElements; i++) {
						intSet.add(i);
					}
				}

			}
		}

		class Remover implements Runnable {
			public Remover() {
				return;
			}
			public void run() {
				while(toldToRun.get()) {
					for(int i = maxElements/2; i < maxElements; i++) {
						intSet.remove(i);
					}
				}

			}
		}

		class Container implements Runnable {

			public boolean pass;
			public Container() {
				pass = true;
				return;
			}
			public void run() {
				for(int numRuns = 0; numRuns < numExecutionPasses; numRuns++) {
					for(int i = 0; i < maxElements; i++) {
						intSet.contains(i);
						if (!intSet.isConsistent()) {
							pass = false;
							System.out.println("Invariant NOT met during contains testing!");
						}
					}
				}
				toldToRun.set(false);
			}
		}


		intSet = new FineGrainedSet<Integer>();

		/* As per the text book The Art of Multiprocessor Programming
		 * by Herlihy M, page 337, we can show that a property is invariant
		 * by showing that:
		 *
		 * property holds when the object is created
		 * no thread can take a step that makes it false

		 *  The invariants for the set algorithm are:
		 *  
		 *  1. The key of any node in the list is less than they key of its successor if it has one
		 *  
		 *  2. The key of any item added, removed or searched for is greater then the key of head and less then the key of tail. 
		 *     Hence the sentinel nodes are neither added nor removed
		 *  
		 */


		// Created the object, verify that the invariants are satisfied:

		if (!intSet.isConsistent()) {
			System.out.println("Invariant NOT met for set creation!");
		} else {
			System.out.println("Invariant MET for set creation!");
		}

		// Now add items to the set
		// We optimize a bit so that elements are all added immediately
		// after the head so that we can get on with it more quickly
		for(int i = maxElements; i >0; i--) {
			intSet.add(i);
		}
		// Our goal here isn't to ensure that add meets the invariants
		// so we don't need to test with several threads. However
		// we do need to setup the pre-conditions before we test contains 
		if (!intSet.isConsistent()) {
			System.out.println("Invariant NOT met after init!");
		} else {
			System.out.println("Invariant MET for init!");
		}

		Adder adderThread = new Adder();
		Remover removerThread = new Remover();
		Container containerThread = new Container();
		Thread addThread = new Thread(adderThread);
		Thread removeThread = new Thread(removerThread);
		Thread containThread = new Thread(containerThread);

		addThread.start();
		removeThread.start();
		containThread.start();
		try {
			addThread.join();
			removeThread.join();
		} catch(InterruptedException e) {  
			System.out.println("Unable to join thread!");
		}
		if (containerThread.pass) {
			System.out.println("Container testing passed!");
		}

	}






}
