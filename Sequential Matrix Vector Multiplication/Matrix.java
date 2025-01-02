package ca.mcgill.ecse420.a3;

import java.util.concurrent.Future;

import java.util.concurrent.ThreadPoolExecutor;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;  
import java.util.concurrent.Executors;  
import java.util.Random;

public class Matrix {
	static final int THRESHOLD = 1024;
	protected static ExecutorService executor = Executors.newCachedThreadPool();

	static Random generator = new Random(0);

	int realRows;
	int rows;
	int cols;
	double[][] data;
	int rowDisplace;
	int colDisplace; 

	public static ThreadPoolExecutor getPool() {
		return (ThreadPoolExecutor) executor;
	}

	Matrix(int pRows, int pCols) {

		int powerOfTwo = (int)(Math.log(pRows) / Math.log(2));

		boolean adjustmentNecessary = !(pRows == 1 << powerOfTwo);
		int numRows = pRows;
		if (adjustmentNecessary) {
			numRows = (int)Math.pow(2, powerOfTwo+1);
		} /*else {
			newVector = new Vector(pRows);
		}*/
		realRows = pRows;
		rows = numRows;
		cols = numRows;
		rowDisplace = 0;
		colDisplace = 0;
		if (1 == pCols) {
			cols= 1;

		}
		data = new double[numRows][cols];

	}

	Matrix(double[][] matrix, int x, int y, int pRows, int pCols)
	{
		data = matrix;
		rowDisplace = x;
		colDisplace = y;
		rows = pRows;
		cols = pCols;
	}

	double get(int pRow, int pCol) {
		return data[pRow + rowDisplace][pCol+ colDisplace];
	}

	void set(int pRow, int pCol, double pValue) {
		data[pRow + rowDisplace][pCol+ colDisplace] = pValue;
	}

	int getRows() {
		return rows;
	}

	int getCols() {
		return cols;
	}

	public static void sequentialAddMatrix(Matrix a, Matrix b, Matrix result) {

		assert(a.cols == b.cols);
		assert(a.rows == b.rows);
		assert(result.rows == b.rows);
		assert(result.cols == b.cols);

		for(int i = 0; i < a.rows; i++) {
			for(int j = 0; j < b.cols; j++) {
				result.data[result.rowDisplace+i][result.colDisplace+j] = a.data[a.rowDisplace+i][a.colDisplace+j]+b.data[b.rowDisplace+i][b.colDisplace+j];
			}
		}
		return;
	}
	
	
	

	public static void sequentialMultiplyMatrix(Matrix a, Matrix b, Matrix result) {

		assert(a.cols == b.rows);
		assert(a.rows == result.rows);
		assert(b.cols == result.cols);

		for(int i = 0; i < a.rows; i++) {
			for(int j = 0; j < b.cols; j++) {
				for(int k = 0; k < b.rows; k++) {
					result.set(i, j, result.get(i,j) + (a.get(i,j) * b.get(k,j)));
				}
			}
		}
		return;
	}

	public static void sequentialMultiplyMatrixVector(Matrix a, Vector b, Vector result) {

		assert(a.cols == b.rows);
		assert(a.rows == result.rows);

		for(int i = 0; i < a.rows; i++) {
			for(int j = 0; j < b.rows; j++) {
				result.data[result.rowDisplace+i][0] += a.data[a.rowDisplace+i][a.colDisplace+j]*b.data[b.rowDisplace+j][0];
			}
		}
		return;
	}

	
	
	public static void parallelMultiplyMatrixVector(Matrix a, Vector b, Vector result) 
			throws InterruptedException, ExecutionException	{

		parallelMultiplyMatrixVectorInternal(a, b, result);

		return;
	}
	
	public static void parallelMultiplyMatrixVectorInternal(Matrix a, Vector b, Vector result) 
			throws InterruptedException, ExecutionException  {


		assert(a.cols == b.rows);
		assert(a.rows == result.rows);

		if (result.getRows() <= Matrix.THRESHOLD) {
			Matrix.sequentialMultiplyMatrixVector(a, b, result);
			return;
		}


		Vector[] term = new Vector[]{new Vector(result.getRows()), new Vector(result.getRows())};
		Future<?> futures[] = new Future[4];
		MatrixMultiplicationTask tasks[] = new MatrixMultiplicationTask[4];
		int taskCount = 0;
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 2; j++) {

				Matrix aSplit = a.split(j, i);

				Vector bSplit = b.split(i);


				Vector termSplit = term[i].split(j);


				tasks[taskCount] = new MatrixMultiplicationTask(aSplit, bSplit, termSplit);
				futures[taskCount] = executor.submit(tasks[taskCount]);

				taskCount++;

			}
		}
		for(int i = 0; i < futures.length; i++) {
			futures[i].get();
		}
		// TODO: Try // add in the mix
		Vector.parallelAddVector(term[0],term[1],result);
		//Matrix.sequentialAddMatrix(term[0], term[1], result);
		//for(int i=0; i < result.rows; i++) {
		//result.set(i, term[0].get(i) + term[1].get(i));
		//}
		return;
	}


	Matrix split(int i, int j) {
		int newRows = rows /2;
		int newCols = cols /2;
		
		return new Matrix(data,
				rowDisplace + (i * newRows),
				colDisplace + (j * newCols),
				newRows,
				newCols);
	}


	/**

	 * Populates a matrix of given size with randomly generated integers between 0-10.
	 * We ensure that the matrix is a factor of a power of 2

	 *

	 * @param numRows number of rows

	 * @param numCols number of cols

	 * @return matrix

	 */

	public static Matrix generateRandomMatrix(int numRows) {
		Matrix newMatrix;

		int powerOfTwo = (int)(Math.log(numRows) / Math.log(2));

		boolean adjustmentNecessary = !(numRows == 1 << powerOfTwo);

		if (adjustmentNecessary) {
			newMatrix = new Matrix((int)Math.pow(2, powerOfTwo+1), (int)Math.pow(2, powerOfTwo+1));
		} else {
			newMatrix = new Matrix(numRows, numRows);
		}

		for (int row = 0; row < newMatrix.rows; row++) {

			for (int col = 0; col < newMatrix.cols; col++) {

				newMatrix.data[row][col] = (double) ((int) (generator.nextDouble() * 10.0));

			}

		}

		return newMatrix;

	}

	/** 

	 * Print out a matrix

	 * @param a is the matrix to be printed
	 */
	public void print() {
		System.out.println(this.toString());

		return;
	}

	public String toString() {
		String result = "rowDisplace: "+ rowDisplace + ", colDisplace: " + colDisplace + ", rows: "+rows+" cols:"+ cols +"\n{";
		boolean firstLine = true;

		for(int i =0; i < this.realRows; i++) {
			if (!firstLine) {
				result = result + " (";
			} else {
				firstLine = false;
				result = result + "(";
			}
			for(int j = 0; j < this.realRows; j++) {
				if (j != (this.cols -1)) {
					result = result +this.data[i][j]+",";
				} else {
					if (i == realRows  -1) {
						result = result + this.data[i][j]+")}\n";
					} else {
						result = result + this.data[i][j]+"),\n";
					}
				}

			}
		}
		return result;
	}

	public static Boolean Compare(Matrix a, Matrix b) {
		if ( (null == a) ||
				(null == b) ||
				(a.rows != b.rows) ||
				(a.cols != b.cols) ) {
			return false;
		}
		for(int i = 0; i < a.rows; i++) {
			for(int j = 0; j < a.cols; j++) {
				if (a.data[i][j] != b.data[i][j]) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean equals(Matrix b) {
		return Compare(this, b);
	}

}
