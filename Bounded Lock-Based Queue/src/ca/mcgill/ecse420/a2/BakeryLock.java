package ca.mcgill.ecse420.a2;
import java.util.concurrent.*;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;

public class BakeryLock implements MyLock {

	private volatile boolean[] flag;
	private volatile int[] label;
	int presentCount;
	int numLevels;
	private HashMap<Long, Integer> threadIDToIndex;

	public BakeryLock(int n) {
		flag = new boolean[n];
		label = new int[n];
		for (int i = 0; i < n; i++) {
			flag[i] = false;
			label[i] = 0;
		}
		presentCount = 0;
		threadIDToIndex = new HashMap<Long, Integer>();
		numLevels = n;
	}

	private int getMyIndex() {
		Long myThreadID = Thread.currentThread().getId();
		int myIndex;


		// We don't have an entry in the hash yet, add it
		if (threadIDToIndex.containsKey(myThreadID)) {
			myIndex = threadIDToIndex.get(myThreadID);
			//System.out.println("Existing Mapping "+myThreadID+":"+ myIndex);
		} else {
			System.out.println("Mapping missing"+myThreadID);
			return -1;
		}
		return myIndex;
	}

	public void addThreadMapping(long pThreadID) {
		threadIDToIndex.put(pThreadID, presentCount);
		presentCount++;
	}


	private int getMaxLabel() {
		int max = label[0];
		for(int i = 1; i < label.length; i++) {
			if (label[i] > max) {
				max = label[i];
			}
		}
		return max;
	}

	public void lock() {

		int me = getMyIndex();
		//System.out.println("LOCK REQ: "+me);
		flag[me] = true;
		label[me] = (getMaxLabel()+1);
		for (int k = 0; k < numLevels; k++) {
			while ((k != me) && flag[k]&&
					( (label[k] < label[me] ) || 
					  ( (label[k] == label[me]) && 
									( k < me))) ) {
				// spin
			}
		}
		//System.out.println("IN "+me);
	}
	public void unlock() {
		int me = getMyIndex();
		flag[me] = false;
		//System.out.println("OUT "+me);
	}
}
