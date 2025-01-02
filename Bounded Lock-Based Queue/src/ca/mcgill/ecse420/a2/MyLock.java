package ca.mcgill.ecse420.a2;

public interface MyLock {
	public void lock () throws LockException ;
	public void unlock() throws LockException ;
	public void addThreadMapping(long threadID);
}
