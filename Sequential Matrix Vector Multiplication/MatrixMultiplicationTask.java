package ca.mcgill.ecse420.a3;

class MatrixMultiplicationTask implements Runnable {
	private Matrix a;
	private Vector b;
	private Vector result;
	//ExecutorService executor;

	MatrixMultiplicationTask(Matrix pA, Vector pB, Vector pResult) {
		a = pA;
		b = pB;
		result = pResult;
	}


	public void run() { 

		try {
		Matrix.parallelMultiplyMatrixVectorInternal(a, b, result);
		} catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
	}
}