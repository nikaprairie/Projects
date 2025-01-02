package ca.mcgill.ecse420.a3;
import java.util.concurrent.locks.Condition;
//import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.lang.reflect.Array;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Arrays;



public class BoundedQueueArrLocked<T> {
	ReentrantLock enqLock, deqLock;
	Condition notEmptyCondition, notFullCondition;
	AtomicInteger size;
	volatile int head, tail;
	final int capacity;
	T[] itemArray;

	public BoundedQueueArrLocked(Class<T> pDataType, int pCapacity) {
		capacity = pCapacity;
		head = 0;
		tail = -1;
		size = new AtomicInteger(0);
		enqLock = new ReentrantLock();
		notFullCondition = enqLock.newCondition();
		deqLock = new ReentrantLock();
		notEmptyCondition = deqLock.newCondition();
		itemArray = (T[]) java.lang.reflect.Array.newInstance(pDataType, pCapacity);

	}

	public void enq(T pItem) throws InterruptedException  {
		boolean mustWakeDequeuers = false;
		enqLock.lock();
		try {
			while (size.get() == capacity) {
				//System.out.println("QW");
				notFullCondition.await();
			}
			tail = (tail + 1) % capacity;
			itemArray[tail] = pItem;
			if (size.getAndIncrement() == 0) {
				mustWakeDequeuers = true;
			}
		} finally {
			enqLock.unlock();
		}
		if (mustWakeDequeuers) {
			deqLock.lock();
			try {
				notEmptyCondition.signalAll();
			} finally {
				deqLock.unlock();
			}
		}

	}

	public T deq() throws InterruptedException {
		T result;
		boolean mustWakeEnqueuers = false;
		deqLock.lock();
		try {
			while (0 == size.get()) {
				//System.out.println("DQW");
				notEmptyCondition.await();
			}
			result = itemArray[head];
			head = (head + 1) % capacity;
			if (size.getAndDecrement() == capacity) {
				mustWakeEnqueuers = true;
			}
		} finally {
			deqLock.unlock();
		}
		if (mustWakeEnqueuers) {
			enqLock.lock();
			try {
				notFullCondition.signalAll();
			} finally {
				enqLock.unlock();
			}
		}
		return result;
	}
	/*
	 * 
	 * 
	 * 
	 */
	public void printQueue(String pName) {
		System.out.println(pName);
		deqLock.lock();
		try {
			enqLock.lock();
			int curr = head;
			try {
				System.out.println("size: "+size.get());
				for(int i = 0; i < size.get(); i++) {
					System.out.println(curr+": "+itemArray[curr].toString());
					curr = (curr + 1) % capacity; 
				}
			} finally {
				enqLock.unlock();
			}
		} finally {
			deqLock.unlock();
		}
	}


}
