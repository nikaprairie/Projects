package ca.mcgill.ecse420.a2;

import java.util.LinkedList;


public class Main {

	public static void main(String[] args) {
		System.out.println("Lock testing, NoLock, verify we can detect a failure");
		//NoLock lock;
		LockTester noLockTestLock;
		Boolean result = true;
		int numThreads = 20;
		TesterThread[] testers = new TesterThread[numThreads];
		Thread threads[] = new Thread[numThreads];
		//lock = new NoLock(numThreads);
		noLockTestLock = new LockTester(new NoLock(numThreads));
		LinkedList<Integer> list = new LinkedList<Integer>();

		
		for(int i = 0; i < numThreads; i++) {
			testers[i] = new TesterThread(noLockTestLock, list);
			threads[i] = new Thread(testers[i]);
			noLockTestLock.addThreadMapping(threads[i].getId());
			threads[i].start();
		}
		
		for(int i = 0; i < numThreads; i++) {
			try {   
				threads[i].join();  
				result = result && testers[i].getTestResult();
			} catch(InterruptedException e) {  
				System.out.println("Unable to join thread!");
			}
		}
		
		if (result) {
			System.out.println("No lock test FAILED");
		} 
		else {
			System.out.println("No lock test PASSED");
		}
		result = true;
		
		System.out.println("\nLock testing, FilterLock");
		//FilterLock lock2;
		testers = new TesterThread[numThreads];
		threads = new Thread[numThreads];
		//lock2 = new FilterLock(numThreads);
		LockTester lockTesterFilter = new LockTester(new FilterLock(numThreads));
		list = new LinkedList<Integer>();
		for(int i = 0; i < numThreads; i++) {
			testers[i] = new TesterThread(lockTesterFilter, list);
			threads[i] = new Thread(testers[i]);
			lockTesterFilter.addThreadMapping(threads[i].getId());
			threads[i].start();
		}
		
		for(int i = 0; i < numThreads; i++) {
			try {   
				threads[i].join();  
				result = result && testers[i].getTestResult();
			} catch(InterruptedException e) {  
				System.out.println("Unable to join thread!");
			}
		}
		
		if (result) {
			System.out.println("FilterLock test PASSED");
		} 
		else {
			System.out.println("FilterLock test FAILED");
		}
		result = true;
		
		
		System.out.println("\nLock testing, BakeryLock");
		//BakeryLock lock3;
		testers = new TesterThread[numThreads];
		threads = new Thread[numThreads];
		//lock3 = new BakeryLock(numThreads);
		LockTester lockTesterBaker = new LockTester(new BakeryLock(numThreads));
		list = new LinkedList<Integer>();
		for(int i = 0; i < numThreads; i++) {
			testers[i] = new TesterThread(lockTesterBaker, list);
			threads[i] = new Thread(testers[i]);
			lockTesterBaker.addThreadMapping(threads[i].getId());
			threads[i].start();
		}
		
		for(int i = 0; i < numThreads; i++) {
			try {   
				threads[i].join();  
				result = result && testers[i].getTestResult();
			} catch(InterruptedException e) {  
				System.out.println("Unable to join thread!");
			}
		}
		
		if (result) {
			System.out.println("BakeryLock test PASSED");
		} 
		else {
			System.out.println("BakeryLock test FAILED");
		}
		result = true;
		
		System.out.println("Tests Completed");

	}

}
