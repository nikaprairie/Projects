package ca.mcgill.ecse420.a3;
import java.util.concurrent.locks.Condition;
//import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.lang.reflect.Array;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Arrays;



public class BoundedQueueArrLockFree<T> {
	AtomicInteger size;
	AtomicInteger commitedSize;
	AtomicInteger head;
	AtomicInteger tail;
	final int capacity;
	T[] itemArray;

	public BoundedQueueArrLockFree(Class<T> pDataType, int pCapacity) {
		capacity = pCapacity;
		// We use size and commitedSize to ensure that enqueueing
		// and dequeuing don't step on each others toes
		size = new AtomicInteger(0);
		commitedSize = new AtomicInteger(0);
		head = new AtomicInteger(0);
		tail = new AtomicInteger(-1);
		itemArray = (T[]) java.lang.reflect.Array.newInstance(pDataType, pCapacity);

	}

	public void enq(T pItem) {
		int presentSize = size.get();

		// Let's 'reserve' a spot for us to enqueue the item. We loop while
		// there is no space available to add an item, i.e. presentSize >= capacity
		// or we're not able to correctly increment by 1 the size, i.e.
		//    if the size stored is equal to presentSize, then increase the size
		//    by 1
		while ( (presentSize >= capacity) || 
				(!size.compareAndSet(presentSize, (presentSize+1)) ) ) {
			presentSize = size.get();
		}
		// The above code reserves a spot for us in the Q, now we can atomically 
		// increment the tail location we should use, again keeping in mind the
		// ring buffer
		int presentTail = tail.get();
		while (!tail.compareAndSet(presentTail,  (presentTail +1) % capacity)) {
			presentTail = tail.get();
		}

		itemArray[(presentTail+1)%capacity] = pItem;
		commitedSize.addAndGet(1);
	}

	public T deq() {
		T result;

		// Let's 'reserve' an item for us to dequeue. We loop while
		// there are no items i.e. presentSize >= capacity
		// Note, we use commitedSize to ensure that we only try to 
		// dequeue once the enque code has completed the addition of an item
		// and we're not able to correctly decrement the commitedSize by 1, i.e.
		//    if the size stored is equal to presentSize, then decrement
		//    the size by 1
		int presentSize = commitedSize.get();
		while ( (presentSize <= 0) || 
				(!commitedSize.compareAndSet(presentSize, (presentSize-1) )) ){
			presentSize = commitedSize.get();

		}

		//System.out.println("DEQ: "+presentSize);
		// We have now reserved an element for us to dequeue
		// Determine which element we'll dequeue
		int presentHead = head.get();
		while (!head.compareAndSet(presentHead,  (presentHead +1) % capacity)) {
			presentHead = head.get();
		}
		result = itemArray[presentHead];
		// Now that we have retrieved the result, decrement the size 
		// used as a gait by enque
		size.addAndGet(-1);

		if (null == result) {
			//System.out.println("Deq: Head: "+ presentHead+" size: "+presentSize);
		}
		return result;
	}
	/*
	 * 
	 * D O  N O T  R U N  W I T H  M U L T I P L E  T H R E A D S
	 * 
	 * ONLY FOR TEST PURPOSES
	 */
	public void printQueue(String pName) {
		System.out.println(pName);
		int curr = head.get();

		System.out.println("size: "+size.get());
		for(int i = 0; i < size.get(); i++) {
			System.out.println(curr+": "+itemArray[curr].toString());
			curr = (curr + 1) % capacity; 

		}


	}
}
