package ca.mcgill.ecse420.a3;
import java.util.concurrent.locks.ReentrantLock;

// package default

public class FineGrainedSet<T> {

	private class Node {
		T item;
		int key;
		Node next;
		ReentrantLock nodeLock;

		Node(T pItem, int pKey) {
			item = pItem;
			key = pKey;
			nodeLock = new ReentrantLock();
		}
		Node(T pItem) {
			item = pItem;
			key = item.hashCode();
			nodeLock = new ReentrantLock();
		}

		public void lock() {
			nodeLock.lock();
		}
		public void unlock() {
			nodeLock.unlock();
		}
	}

	Node head;
	Node tail;

	public FineGrainedSet() {
		head = new Node(null, Integer.MIN_VALUE);
		tail = new Node(null, Integer.MAX_VALUE);
		//head.key = Integer.MIN_VALUE;
		//tail.key = Integer.MAX_VALUE;
		head.next = tail;
		tail.next = null;
	}

	public int getHeadKey() {
		return head.key;

	}

	public int getTailKey() {
		return tail.key;
	}

	public boolean add(T item) {
		int key = item.hashCode();
		head.lock();
		Node pred = head;
		try {
			Node curr = pred.next;
			curr.lock();
			try {
				while (curr.key < key) {
					pred.unlock();
					pred = curr;
					curr = curr.next;
					curr.lock();
				}
				if (curr.key == key) {
					return false;
				}
				Node node = new Node(item);
				node.next = curr;
				pred.next = node;
				return true;
			} finally {
				curr.unlock();
			}
		} finally {
			pred.unlock() ;
		}
	}

	public boolean remove(T item) {
		int key = item.hashCode();
		head.lock();
		Node pred = head;
		try {
			Node curr = pred.next;
			curr.lock() ;
			try {
				while (curr.key < key) {
					pred.unlock();
					pred = curr;
					curr = curr.next;
					curr.lock();
				}
				if (curr.key == key) {
					pred.next = curr.next;
					return true;
				}
				return false;
			} finally {
				curr.unlock() ;
			}
		} finally {
			pred.unlock () ;
		}
	}

	public boolean contains(T item) {
		int key = item.hashCode();
		head.lock();
		Node pred = head;
		try {
			Node curr = pred.next;
			curr.lock();
			try {
				while (curr.key < key) {
					pred.unlock();
					pred = curr;
					curr = curr.next;
					curr.lock();
				}
				if (curr.key == key) {
					return true;
				}
				return false;
			} finally {
				curr.unlock();
			}
		} finally {
			pred. unlock() ;
		}
	}

	public boolean isConsistent() {
		
		head.lock();
		Node pred = head;
		try {
			Node curr = pred.next;
			curr.lock();
			try {
				while (curr.next != null) {
					if (pred.key >= curr.key) {
						return false;
					}
					pred.unlock();
					pred = curr;
					curr = curr.next;
					curr.lock();
				}
				if (pred.key >= curr.key) {
					return false;
				}
				return true;
			} finally {
				curr.unlock();
			}
		} finally {
			pred. unlock() ;
		}
	}

}
