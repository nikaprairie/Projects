package ca.mcgill.ecse420.a3;
class VectorAddTask implements Runnable {
	private Vector a;
	private Vector b;
	private Vector result;

	VectorAddTask(Vector pA, Vector pB, Vector pResult) {
		a = pA;
		b = pB;
		result = pResult;
	}


	public void run() { 

		try {
			Vector.parallelAddVectorPrivate(a, b, result);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}