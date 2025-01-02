package ca.mcgill.ecse420.a2;
import java.util.concurrent.atomic.AtomicInteger;

public class LockTester implements MyLock {
	MyLock testedLock;
	AtomicInteger counter;
	
	public LockTester(MyLock ptestedLock) {
		testedLock = ptestedLock;
		counter = new AtomicInteger(0);
	}

	public void lock() throws LockException {

		testedLock.lock();

		if (1 != counter.incrementAndGet()) {
			throw new LockException("More than one thread has the lock in lock(), throwing an exception");
			
		}
		return;
	}
	
	public void unlock() throws LockException {

		if (0 != counter.decrementAndGet()) {
			throw new LockException("More than one thread has the lock in lock(), throwing an exception");
		}
		testedLock.unlock();
	}
	
	public void addThreadMapping(long threadID) {
		testedLock.addThreadMapping(threadID);
	}
}


