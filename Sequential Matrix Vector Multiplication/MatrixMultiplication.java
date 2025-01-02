package ca.mcgill.ecse420.a3;
/**

 * @author nikakp

 *

 */

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;  


public class MatrixMultiplication {


	public static void main(String[] args) throws InterruptedException, ExecutionException   {
		long cpuTimeStart;
		long cpuTimeEnd;
		long singleDeltaTime = 0;
		long multiDeltaTime = 0;

		int numIterations = 500;
		int matrixSize;
		ThreadPoolExecutor pool;


		if (args.length < 1) {
			System.out.println("Insufficient arguments passed, use <rows/cols>");
			System.exit(1);
		}

		matrixSize = Integer.parseInt(args[0]);

		pool = Matrix.getPool();

		System.out.println("Thread Pool Details Before Parallel Execution:");

		System.out.println("Core threads: " + pool.getCorePoolSize());
		System.out.println("Largest number of simultaneous executions: "
				+ pool.getLargestPoolSize());
		System.out.println("Maximum number of  allowed threads: "
				+ pool.getMaximumPoolSize());
		System.out.println("Current threads in the pool: "
				+ pool.getPoolSize());
		System.out.println("Currently executing threads: "
				+ pool.getActiveCount());
		System.out.println("Total number of threads ever scheduled: "
				+ pool.getTaskCount());

		System.out.println("\nStarting Multiplication of matrix "+matrixSize+"x"+matrixSize+" with vector of "+matrixSize+"\n");





		// Generate a random matrix
		Matrix aM = Matrix.generateRandomMatrix(matrixSize);

		// Generate a random vector
		Vector v = Vector.generateRandomVector(matrixSize);
		Vector vSequentialResult = new Vector(matrixSize);
		singleDeltaTime = 0;


		for(int i = 0; i< numIterations; i++) {
			vSequentialResult = new Vector(matrixSize);
			cpuTimeStart = System.nanoTime();
			Matrix.sequentialMultiplyMatrixVector(aM, v, vSequentialResult);
			cpuTimeEnd = System.nanoTime();
			singleDeltaTime += cpuTimeEnd - cpuTimeStart;
		}


		Vector vParallelResult = new Vector(matrixSize);
		for(int i = 0; i< numIterations; i++) {
			vParallelResult = new Vector(matrixSize);
			cpuTimeStart = System.nanoTime();
			try {
				Matrix.parallelMultiplyMatrixVector(aM, v, vParallelResult);
			} catch (InterruptedException e) {
				System.out.println("Interrupted Exception");
			} catch (ExecutionException e) {
				System.out.println("Execution Exception");
			}
			cpuTimeEnd = System.nanoTime();
			multiDeltaTime += cpuTimeEnd - cpuTimeStart;
		}

		System.out.println("\nThread Pool Details After Parallel Execution:");
		pool = Matrix.getPool();
		System.out.println("Core threads: " + pool.getCorePoolSize());
		System.out.println("Largest number of simultaneous executions: "
				+ pool.getLargestPoolSize());
		System.out.println("Maximum number of  allowed threads: "
				+ pool.getMaximumPoolSize());
		System.out.println("Current threads in the pool: "
				+ pool.getPoolSize());
		System.out.println("Currently executing threads: "
				+ pool.getActiveCount());
		System.out.println("Total number of threads ever scheduled: "
				+ pool.getTaskCount());

		if (vParallelResult.equals(vSequentialResult)) {
			System.out.println("\nSequential and Multithreaded matched!\n");
		} else {
			System.out.println("\nMultithreaded MISMATCH!!!\n");
		}

		System.out.println("Single: "+singleDeltaTime/numIterations+" ns");
		System.out.println("Multi:  "+multiDeltaTime/numIterations+" ns");
		System.out.println("Speed up: "+((double)singleDeltaTime/(double)multiDeltaTime));
		pool.shutdown();


	}

}

