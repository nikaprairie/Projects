package ca.mcgill.ecse420.a2;
import java.util.LinkedList;

class TesterThread implements Runnable {
	volatile private LinkedList<Integer> list;
	private MyLock lock;
	private Boolean result;

	public TesterThread(MyLock pLock, LinkedList<Integer> pList) {
		lock = pLock;
		list = pList;
		result = true;
	}
	
	public boolean getTestResult() {
		return result;
	}
	
	public void run()
	{
		int numIterations = 100;
		int numElements = 100;
		for(int j = 0; j < numIterations; j++ ) {
			try {
				lock.lock();
				//try {
				//Thread.sleep(5);

				for(int i = 0; i < numElements; i++) {
					list.add(i);
					if (list.size() != i+1) {
						//System.out.println("Mutual Exclusion error detected in addition!");
						result = false;
						return;
					}
				}
				for(int i = 0; i < numElements; i++) {
					list.remove();
					if (list.size() != numElements - i - 1) {
						//System.out.println("Mutual Exclusion error detected in removal!");
						result = false;
						return;
					}
				}
			}  catch(LockException e) {  
				//System.out.println("Mutual Exclusion violated!");
				result = false;
				return;
			}
			try {
				lock.unlock();
			} catch(LockException e) {
				//System.out.println("Mutual Exclusion violated!");
				result = false;
				return;
			}
		}
	}
}