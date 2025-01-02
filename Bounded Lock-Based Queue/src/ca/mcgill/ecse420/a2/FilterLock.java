package ca.mcgill.ecse420.a2;
//package default;
import java.util.concurrent.*;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FilterLock implements MyLock {
	private volatile int[] level;
	private volatile int[] victim;
	private int numLevels;
	private HashMap<Long, Integer> threadIDToIndex;
	private int presentCount;

	public FilterLock(int n) {
		
		level = new int[n];
		victim = new int[n];
		numLevels = n;
		presentCount = 0;
		threadIDToIndex = new HashMap<Long, Integer>();
		for (int i = 0; i < n; i++) {
			level[i] = 0;
			victim[i] = 0;
		}
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

	public void lock() {
		int me = getMyIndex();
		//System.out.println("LOCK REQ: "+me);
		for (int i = 1; i < numLevels; i++) {
			level[me] = i;
			victim[i] = me;
			// spin while conflicts exist
			for (int k = 0; k < numLevels; k++) {
				while ((k != me) && (level[k] >= i && victim[i] == me)) {
					// spinning here
				}
			}
			//while (( Exists k != me ) (level[k] >= i && victim[i] = me)) {};
		}
		//System.out.println("IN "+me);
	}

	public void unlock() {
		int me = getMyIndex();
		level[me] = 0;
		//System.out.println("OUT "+me);
	}
}
