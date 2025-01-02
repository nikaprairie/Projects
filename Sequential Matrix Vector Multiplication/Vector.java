package ca.mcgill.ecse420.a3;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Vector extends Matrix {

	public Vector(int pRows) {

		super(pRows, 1);

	}

	public Vector(double[][] data, int x, int pRows)
	{
		super(data, x, 0, pRows, 1);
		assert(data.length > x);
		assert(data[0].length == 1);
	}

	void set(int pRow, double value) {
		data[pRow + rowDisplace][0] = value;
	}

	double get(int pRow) {
		return data[pRow + rowDisplace][0];
	}

	Vector split(int i) {
		int newRows = rows /2;
		return new Vector(data,
				rowDisplace + (i * newRows),
				newRows);
	}

	Vector quarterSplit(int i) {
		int newRows = rows /4;

		return new Vector(data,
				rowDisplace + (i * newRows),
				newRows);

	}

	/**

	 * Populates a Vector of given size with randomly generated integers between 0-10.
	 * We ensure that the matrix is a factor of a power of 2

	 *

	 * @param numRows number of rows

	 * @param numCols number of cols

	 * @return matrix

	 */

	public static Vector generateRandomVector(int numRows) {
		Vector newVector;

		int powerOfTwo = (int)(Math.log(numRows) / Math.log(2));

		boolean adjustmentNecessary = !(numRows == 1 << powerOfTwo);

		if (adjustmentNecessary) {
			newVector = new Vector((int)Math.pow(2, powerOfTwo+1));
		} else {
			newVector = new Vector(numRows);
		}

		for (int row = 0; row < newVector.rows; row++) {
			newVector.data[row][0] = (double) ((int) (generator.nextDouble() * 10.0));
		}

		return newVector;

	}
	
	public static void parallelAddVector(Vector a, Vector b, Vector result) 
			throws InterruptedException, ExecutionException	{

		parallelAddVectorPrivate(a, b, result);

		return;
	}
	
	public static void parallelAddVectorPrivate(Vector a, Vector b, Vector result) 
			throws InterruptedException, ExecutionException  {


		assert(a.cols == b.rows);
		assert(a.rows == result.rows);

		if (result.getRows() <= Matrix.THRESHOLD) {
			Vector.sequentialAddVector(a, b, result);
			return;
		}


		Future<?> futures[] = new Future[4];
		VectorAddTask tasks[] = new VectorAddTask[4];
		int taskCount = 0;
		for(int i = 0; i < 4; i++) {

				Vector aSplit = /*a.split(i);*/a.quarterSplit(i);

				Vector bSplit = /*b.split(i);*/b.quarterSplit(i);


				Vector termSplit = /*result.split(i);*/ result.quarterSplit(i);


				tasks[taskCount] = new VectorAddTask(aSplit, bSplit, termSplit);
				futures[taskCount] = Matrix.executor.submit(tasks[taskCount]);

				taskCount++;

		}
		for(int i = 0; i < futures.length; i++) {
			futures[i].get();
		}
		// TODO: Try // add in the mix
		//Matrix.sequentialAddMatrix(term[0], term[1], result);
		//for(int i=0; i < result.rows; i++) {
		//result.set(i, term[0].get(i) + term[1].get(i));
		//}
		return;
	}
	
	public static void sequentialAddVector(Vector a, Vector b, Vector result) {

		assert(a.cols == b.cols);
		assert(a.rows == b.rows);
		assert(result.rows == b.rows);
		assert(result.cols == b.cols);

		if (result.rowDisplace+result.rows-1 > result.data.length) {
			System.out.println("issue");
		}
		
		for(int i = 0; i < result.rows; i++) {
				result.data[result.rowDisplace+i][0] = a.data[a.rowDisplace+i][0]+b.data[b.rowDisplace+i][0];
		}
		return;
	}

	public void print() {

		System.out.println(this.toString());
	}

	public String toString() {
		String result = "rowDisplace: "+ rowDisplace + ", rows: " + rows + "\n{";

		for(int i =0; i < this.realRows; i++) {
			if (i == this.realRows-1) {
				result = result + this.data[i][0]+"}";
			}
			else {
				result = result + this.data[i][0]+",";
			}
		}
		return result;
	}


	public static Boolean Compare(Vector a, Vector b) {
		if ( (null == a) ||
				(null == b) ||
				(a.rows != b.rows) ||
				(a.cols != b.cols) ) {
			return false;
		}
		for(int i = 0; i < a.rows; i++) {
			if (a.data[i][0] != b.data[i][0]) {
				return false;
			}
		}
		return true;
	}

	public boolean equals(Vector b) {
		return Compare(this, b);
	}

}
